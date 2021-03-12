package dev.fulmineo.companion_bats.mixin;

import org.spongepowered.asm.mixin.Mixin;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.world.World;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;


@Mixin(BatEntity.class)
public abstract class BatEntityMixin extends AmbientEntity {
    public BatEntityMixin(EntityType<? extends BatEntity> entityType, World world) {
        super(entityType, world);
    }

    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.world.isClient){
            return CompanionBatEntity.IS_FOOD_ITEM.test(itemStack) ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            if (CompanionBatEntity.IS_FOOD_ITEM.test(itemStack)) {
                if (!player.abilities.creativeMode) {
                    itemStack.decrement(1);
                }

				this.remove();
				ItemStack batItemStack = new ItemStack(CompanionBats.BAT_ITEM);
				if (!player.giveItemStack(batItemStack)){
					ItemEntity itemEntity = player.dropItem(batItemStack, false);
					if (itemEntity != null) {
						itemEntity.resetPickupDelay();
						itemEntity.setOwner(player.getUuid());
					}
				}
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
