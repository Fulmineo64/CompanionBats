package dev.fulmineo.companion_bats.item;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import dev.fulmineo.companion_bats.screen.CompanionBatContainerProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import dev.fulmineo.companion_bats.CompanionBatAbilities;
import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels;
import dev.fulmineo.companion_bats.nbt.EntityData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CompanionBatItem extends Item {
    public CompanionBatItem(Properties settings) {
        super(settings);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (world instanceof ServerWorld) {
			EntityData.createIfMissing(itemStack);
			EntityData entityData = new EntityData(itemStack);
            if (user.isCrouching()){
                Inventory inventory = new Inventory(3);
                inventory.setItem(0, ItemStack.of(entityData.getAccessory()));
                inventory.setItem(1, ItemStack.of(entityData.getArmor()));
				inventory.setItem(2, ItemStack.of(entityData.getBundle()));

                NetworkHooks.openGui((ServerPlayerEntity) user, new CompanionBatContainerProvider(this, inventory, hand), buffer -> {
                    buffer.writeEnum(hand);
                });
            } else {
                float entityHealth = entityData.getHealth();
                if (entityHealth == 0){
                    List<ItemEntity> list = world.getEntitiesOfClass(ItemEntity.class, user.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), (itemEntity) -> true);
                    if (list.size() > 0){
                        ItemEntity foodItemEntity = list.get(0);
                        ItemStack stack = foodItemEntity.getItem();
                        entityHealth += CompanionBatEntity.getItemHealAmount(stack);
                        entityData.putHealth(entityHealth);
                        stack.shrink(1);
                    }
                }
                if (entityHealth > 0){
					CompanionBatEntity batEntity = CompanionBatEntity.spawnFromItemStack((ServerWorld)world, itemStack, user);
                    ItemStack fluteItemStack = new ItemStack(CompanionBats.BAT_FLUTE_ITEM.get());
					CompoundNBT tag = fluteItemStack.getOrCreateTag();
					tag.putUUID("EntityUUID", batEntity.getUUID());
					ITextComponent customName = batEntity.getCustomName();
					if (customName != null){
						tag.putString("EntityName", customName.getString());
						fluteItemStack.setHoverName(new TranslationTextComponent("item.companion_bats.bat_flute.custom_name", customName.getString()));
					}
                    return ActionResult.sidedSuccess(fluteItemStack, world.isClientSide());
                } else {
                    user.displayClientMessage(new TranslationTextComponent("item.companion_bats.bat_item.exausted", itemStack.hasCustomHoverName() ? itemStack.getHoverName() : new TranslationTextComponent("entity.companion_bats.bat.your_bat")), false);
                    return ActionResult.fail(itemStack);
                }
            }
        }
        return ActionResult.success(itemStack);
    }

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag context) {
		CompanionBatAbilities abilities = new CompanionBatAbilities();
		abilities.setFromNbt(new EntityData(stack));
		Set<Entry<CompanionBatAbility, Integer>> entrySet = abilities.entrySet();
		if (entrySet.size() > 0){
			tooltip.add(new TranslationTextComponent("item.companion_bats.bat_item.abilities").withStyle(TextFormatting.AQUA));
			for (Map.Entry<CompanionBatAbility, Integer> entry : entrySet) {
				tooltip.add(entry.getKey().toTranslatedText().append(" " + entry.getValue()).withStyle(TextFormatting.GRAY));
			}
		}
	}

    public boolean useOnRelease(ItemStack stack) {
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