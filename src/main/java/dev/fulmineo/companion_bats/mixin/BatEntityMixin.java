package dev.fulmineo.companion_bats.mixin;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;

import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.entity.EntityType;
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
            CompanionBats.log(Level.INFO, "interact (client)");
            return itemStack.isOf(Items.PUMPKIN_PIE) /*&& !this.isTamed()*/ ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            CompanionBats.log(Level.INFO, "interact (server)");
            if (itemStack.isOf(Items.PUMPKIN_PIE)) {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                if (this.random.nextInt(3) == 0) {
                    CompanionBats.log(Level.INFO, "tamed! (server)");
                    this.discard();
                    // Create item in player's inventory
                    player.giveItemStack(new ItemStack(CompanionBats.BAT_ITEM));
                    this.world.sendEntityStatus(this, (byte)7);
                } else {
                    CompanionBats.log(Level.INFO, "try again (server)");
                    this.world.sendEntityStatus(this, (byte)6);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
