package dev.fulmineo.companion_bats.mixin;

import org.jetbrains.annotations.Nullable;
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

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	@Shadow
	protected PlayerEntity attackingPlayer;
	@Shadow
	protected int playerHitTimer;

	protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(at = @At("TAIL"), method = "setAttacker(Lnet/minecraft/entity/LivingEntity;)V")
	public void setAttackerMixin(@Nullable LivingEntity attacker, CallbackInfo info) {
		if (attacker != null && attacker instanceof CompanionBatEntity){
			CompanionBatEntity batEntity = (CompanionBatEntity)attacker;
			this.playerHitTimer = 100;
			LivingEntity livingEntity = batEntity.getOwner();
			if (livingEntity != null && livingEntity.getType() == EntityType.PLAYER) {
			   this.attackingPlayer = (PlayerEntity)livingEntity;
			} else {
			   this.attackingPlayer = null;
			}
		}
	}
}
