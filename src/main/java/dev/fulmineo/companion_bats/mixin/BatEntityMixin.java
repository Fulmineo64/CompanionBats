package dev.fulmineo.companion_bats.mixin;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AmbientEntity;
import net.minecraft.util.ActionResultType;
import org.spongepowered.asm.mixin.Mixin;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.nbt.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.world.World;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;


@Mixin(BatEntity.class)
public abstract class BatEntityMixin extends AmbientEntity {
    public BatEntityMixin(EntityType<? extends BatEntity> entityType, World world) {

        super(entityType, world);
        CompanionBats.info("test");
    }

    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (this.level.isClientSide){
            return CompanionBatEntity.IS_FOOD_ITEM.test(itemStack) ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            if (CompanionBatEntity.IS_FOOD_ITEM.test(itemStack)) {
                if (!player.abilities.invulnerable) {
                    itemStack.shrink(1);
                }

				ItemStack batItemStack = new ItemStack(CompanionBats.BAT_ITEM.get());
				EntityData.fromRegularBatEntity(this).toStack(batItemStack);

				this.remove();
				if (!player.addItem(batItemStack)){
					ItemEntity itemEntity = player.drop(batItemStack, false);
					if (itemEntity != null) {
                        itemEntity.setDefaultPickUpDelay();
                        itemEntity.setOwner(player.getUUID());
					}
				}
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
}
