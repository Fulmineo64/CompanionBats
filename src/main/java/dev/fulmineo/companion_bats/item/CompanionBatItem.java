package dev.fulmineo.companion_bats.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels;
import dev.fulmineo.companion_bats.screen.CompanionBatScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CompanionBatItem extends Item {
    public CompanionBatItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (world instanceof ServerWorld) {
            CompoundTag entityData = getOrCreateEntityData(itemStack);
            if (user.isSneaking()){
                SimpleInventory inventory = new SimpleInventory(3);
                inventory.setStack(0, ItemStack.fromNbt(entityData.getCompound("armor")));
				inventory.setStack(1, ItemStack.fromNbt(entityData.getCompound("bundle")));
                user.openHandledScreen(new ExtendedScreenHandlerFactory() {
                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                        packetByteBuf.writeEnumConstant(hand);
                    }

                    @Override
                    public Text getDisplayName() {
                        return new TranslatableText(CompanionBatItem.this.getTranslationKey());
                    }

                    @Override
                    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return new CompanionBatScreenHandler(syncId, inv, inventory, hand);
                    }
                });
            } else {
                float entityHealth = entityData.getFloat("health");
                if (entityHealth == 0){
                    List<ItemEntity> list = world.getEntitiesByClass(ItemEntity.class, user.getBoundingBox().expand(8.0D, 8.0D, 8.0D), CompanionBatEntity.IS_FOOD_ITEM_ENTITY);
                    if (list.size() > 0){
                        ItemEntity foodItemEntity = list.get(0);
                        ItemStack stack = foodItemEntity.getStack();
                        entityHealth += CompanionBatEntity.getItemHealAmount(stack);
                        entityData.putFloat("health",entityHealth);
                        stack.decrement(1);
                    }
                }
                if (entityHealth > 0){
					Vec3d pos = user.getPos();
                    CompanionBatEntity batEntity = (CompanionBatEntity)CompanionBats.COMPANION_BAT.spawnFromItemStack((ServerWorld)world, itemStack, user, new BlockPos(pos.x, Math.ceil(pos.y), pos.z), SpawnReason.SPAWN_EGG, false, false);
                    batEntity.fromItem(user, entityData);
                    ItemStack fluteItemStack = new ItemStack(CompanionBats.BAT_FLUTE_ITEM);
                    fluteItemStack.getOrCreateTag().putUuid("entityUuid", batEntity.getUuid());
                    return TypedActionResult.success(fluteItemStack, world.isClient());
                } else {
                    user.sendMessage(new TranslatableText("item.companion_bats.bat_item.exausted", itemStack.hasCustomName() ? itemStack.getName() : new TranslatableText("entity.companion_bats.bat.your_bat")), false);
                    return TypedActionResult.fail(itemStack);
                }
            }
        }
        return TypedActionResult.success(itemStack);
    }

    public boolean isUsedOnRelease(ItemStack stack) {
        return true;
    }

    public static CompoundTag getEntityData(ItemStack stack) {
        return stack.getSubTag("entityData");
    }

    public static CompoundTag createEntityData(ItemStack stack) {
        CompoundTag subTag = new CompoundTag();
        stack.putSubTag("entityData", subTag);
        CompanionBatEntity.setDefaultEntityData(subTag);
        return subTag;
    }

    public static CompoundTag getOrCreateEntityData(ItemStack stack) {
        stack.getOrCreateTag(); // Insures that the tag exists
        CompoundTag subTag = getEntityData(stack);
        if (subTag == null) {
            subTag = createEntityData(stack);
        }
        return subTag;
    }

    static {

    }

    public Rarity getRarity(ItemStack stack) {
		CompoundTag entityData = getOrCreateEntityData(stack);
		int level = CompanionBatLevels.getLevelByExp(entityData.getInt("exp"));
		float levelProgression = level / CompanionBatLevels.LEVELS.length;
		if (levelProgression < 0.5) {
			return Rarity.COMMON;
		} else if (levelProgression < 1) {
			return Rarity.RARE;
		} else {
			return Rarity.EPIC;
		}
    }
}