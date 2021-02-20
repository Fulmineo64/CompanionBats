package dev.fulmineo.companion_bats.mixin;

import org.spongepowered.asm.mixin.Mixin;

import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.world.World;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
            return itemStack.getItem() == Items.PUMPKIN_PIE ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            if (itemStack.getItem() == Items.PUMPKIN_PIE) {
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
