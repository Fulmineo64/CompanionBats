package dev.fulmineo.companion_bats.entity.ai.goal;

import dev.fulmineo.companion_bats.data.CompanionBatAbilityType;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;

public class CompanionBatTargetSelectorGoal extends Goal {
	private CompanionBatEntity entity;
	private CompanionBatAbilityType ability;

	public CompanionBatTargetSelectorGoal(CompanionBatEntity entity, CompanionBatAbilityType ability) {
		this.entity = entity;
		this.ability = ability;
	}

	public boolean canStart() {
		return this.entity.getTarget() == null || this.entity.getTarget().isDead();
	}

	public boolean shouldContinue() {
		return false;
	}

	public void start() {
		LivingEntity owner = this.entity.getOwner();
		TargetPredicate predicate = TargetPredicate.createAttackable().setPredicate((livingEntity) -> !livingEntity.isSpectator() && livingEntity != owner && this.entity.canAttackWithOwner(livingEntity, owner));
		this.entity.setTarget(this.entity.world.getClosestEntity(this.getTargetClass(), predicate, this.entity, this.entity.getX(), this.entity.getY(), this.entity.getZ(), this.entity.getBoundingBox().expand(10.0D, 10.0D, 10.0D)));
	}

	private Class<? extends LivingEntity> getTargetClass(){
		switch (this.ability){
			case ATTACK_HOSTILES: {
				return HostileEntity.class;
			}
			case ATTACK_PASSIVE: {
				return PassiveEntity.class;
			}
			default: {
				return LivingEntity.class;
			}
		}
	}
}
