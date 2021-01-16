package dev.fulmineo.companion_bats.mixin;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;

import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.world.World;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
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

                if (this.random.nextInt(1) == 0) {
                    this.discard();
                    player.giveItemStack(new ItemStack(CompanionBats.BAT_ITEM));
                } else {
                    this.world.sendEntityStatus(this, (byte)6);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Environment(EnvType.CLIENT)
    protected void companionBatsShowEmoteParticle(boolean positive) {
        ParticleEffect particleEffect = ParticleTypes.HEART;
        if (!positive) {
            particleEffect = ParticleTypes.SMOKE;
        }

        for(int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.01D;
            double e = this.random.nextGaussian() * 0.01D;
            double f = this.random.nextGaussian() * 0.01D;
            this.world.addParticle(particleEffect, this.getParticleX(0.50D), this.getRandomBodyY() + 0.5D, this.getParticleZ(1.0D), d, e, f);
        }
    }

    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 6) {
            this.companionBatsShowEmoteParticle(false);
        } else {
            super.handleStatus(status);
        }
    }
}
