package dev.fulmineo.companion_bats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	@Shadow
	protected PlayerEntity lastHurtByPlayer;
	@Shadow
	protected int lastHurtByPlayerTime;

	protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(at = @At("TAIL"), method = "setLastHurtByMob(Lnet/minecraft/entity/LivingEntity;)V")
	public void setLastHurtByMob(@Nullable LivingEntity attacker, CallbackInfo info) {
		if (attacker != null && attacker instanceof CompanionBatEntity){
			CompanionBatEntity batEntity = (CompanionBatEntity)attacker;
			this.lastHurtByPlayerTime = 100;
			LivingEntity livingEntity = batEntity.getOwner();
			if (livingEntity != null && livingEntity.getType() == EntityType.PLAYER) {
			   this.lastHurtByPlayer = (PlayerEntity)livingEntity;
			} else {
			   this.lastHurtByPlayer = null;
			}
		}
	}
}
