package dev.fulmineo.companion_bats.entity.ai.goal;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.CompanionBatAbility;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;

public class CompanionBatTargetSelectorGoal extends Goal {
	private CompanionBatEntity entity;
	private CompanionBatAbility ability;

	public CompanionBatTargetSelectorGoal(CompanionBatEntity entity, CompanionBatAbility ability) {
		this.entity = entity;
		this.ability = ability;
	}

	public boolean canUse() {
		return this.entity.getTarget() == null || this.entity.getTarget().isDeadOrDying();
	}

	public boolean canContinueToUse() {
		return false;
	}

	public void start() {
		LivingEntity owner = this.entity.getOwner();
		EntityPredicate predicate = new EntityPredicate().selector((livingEntity) -> !livingEntity.isSpectator() && livingEntity != owner && this.entity.canAttackWithOwner(livingEntity, owner));
		this.entity.setTarget(this.entity.level.getNearestEntity(this.getTargetClass(), predicate, this.entity, this.entity.getX(), this.entity.getY(), this.entity.getZ(), this.entity.getBoundingBox().inflate(10.0D, 10.0D, 10.0D)));
	}

	private Class<? extends LivingEntity> getTargetClass(){
		switch (this.ability){
			case ATTACK_HOSTILES: {
				return MonsterEntity.class;
			}
			case ATTACK_PASSIVE: {
				return AgeableEntity.class;
			}
			default: {
				return LivingEntity.class;
			}
		}
	}
}
