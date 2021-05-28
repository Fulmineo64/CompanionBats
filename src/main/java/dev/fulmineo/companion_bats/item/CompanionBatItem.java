package dev.fulmineo.companion_bats.item;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBatAbilities;
import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels;
import dev.fulmineo.companion_bats.nbt.EntityData;
import dev.fulmineo.companion_bats.screen.CompanionBatScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CompanionBatItem extends Item {
    public CompanionBatItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (world instanceof ServerWorld) {
			EntityData.createIfMissing(itemStack);
			EntityData entityData = new EntityData(itemStack);
            if (user.isSneaking()){
                SimpleInventory inventory = new SimpleInventory(3);
                inventory.setStack(0, ItemStack.fromNbt(entityData.getAccessory()));
                inventory.setStack(1, ItemStack.fromNbt(entityData.getArmor()));
				inventory.setStack(2, ItemStack.fromNbt(entityData.getBundle()));
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
                float entityHealth = entityData.getHealth();
                if (entityHealth == 0){
                    List<ItemEntity> list = world.getEntitiesByClass(ItemEntity.class, user.getBoundingBox().expand(8.0D, 8.0D, 8.0D), CompanionBatEntity.IS_FOOD_ITEM_ENTITY);
                    if (list.size() > 0){
                        ItemEntity foodItemEntity = list.get(0);
                        ItemStack stack = foodItemEntity.getStack();
                        entityHealth += CompanionBatEntity.getItemHealAmount(stack);
                        entityData.putHealth(entityHealth);
                        stack.decrement(1);
                    }
                }
                if (entityHealth > 0){
					CompanionBatEntity batEntity = CompanionBatEntity.spawnFromItemStack((ServerWorld)world, itemStack, user);
                    ItemStack fluteItemStack = new ItemStack(CompanionBats.BAT_FLUTE_ITEM);
					NbtCompound tag = fluteItemStack.getOrCreateTag();
					tag.putUuid("EntityUUID", batEntity.getUuid());
					Text customName = batEntity.getCustomName();
					if (customName != null){
						tag.putString("EntityName", customName.asString());
						fluteItemStack.setCustomName(new TranslatableText("item.companion_bats.bat_flute.custom_name", customName.asString()));
					}
                    return TypedActionResult.success(fluteItemStack, world.isClient());
                } else {
                    user.sendMessage(new TranslatableText("item.companion_bats.bat_item.exausted", itemStack.hasCustomName() ? itemStack.getName() : new TranslatableText("entity.companion_bats.bat.your_bat")), false);
                    return TypedActionResult.fail(itemStack);
                }
            }
        }
        return TypedActionResult.success(itemStack);
    }

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		CompanionBatAbilities abilities = new CompanionBatAbilities();
		abilities.setFromNbt(new EntityData(stack));
		Set<Entry<CompanionBatAbility, Integer>> entrySet = abilities.entrySet();
		if (entrySet.size() > 0){
			tooltip.add(new TranslatableText("item.companion_bats.bat_item.abilities").formatted(Formatting.AQUA));
			for (Map.Entry<CompanionBatAbility, Integer> entry : entrySet) {
				tooltip.add(entry.getKey().toTranslatedText().append(" " + entry.getValue()).formatted(Formatting.GRAY));
			}
		}
	}

    public boolean isUsedOnRelease(ItemStack stack) {
        return true;
    }

    public Rarity getRarity(ItemStack stack) {
		EntityData entityData = new EntityData(stack);
		int level = CompanionBatLevels.getLevelByExp(entityData.getExp());
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