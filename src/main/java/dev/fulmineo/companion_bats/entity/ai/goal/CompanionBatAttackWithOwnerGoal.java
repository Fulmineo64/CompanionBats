package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.EnumSet;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;

import net.minecraft.entity.passive.ParrotEntity;


public class CompanionBatAttackWithOwnerGoal extends TargetGoal {
	private final CompanionBatEntity entity;
	private LivingEntity attacking;
	private int lastAttackTime;

	public CompanionBatAttackWithOwnerGoal(CompanionBatEntity entity) {
	   super(entity, false);
	   this.entity = entity;
	   this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	public boolean canUse() {
		if (this.entity.isTame() && !this.entity.isInSittingPose()) {
			LivingEntity livingEntity = this.entity.getOwner();
			if (livingEntity == null) {
				return false;
			} else {
				this.attacking = livingEntity.getLastHurtMob();
				int i = livingEntity.getLastHurtMobTimestamp();
				return i != this.lastAttackTime && this.canAttack(this.attacking, new EntityPredicate().allowUnseeable()) && this.entity.canAttackWithOwner(this.attacking, livingEntity);
			}
		} else {
			return false;
		}
	}

	public void start() {
		this.entity.setTarget(this.attacking);
		LivingEntity livingEntity = this.entity.getOwner();
		if (livingEntity != null) {
			this.lastAttackTime = livingEntity.getLastHurtByMobTimestamp();
		}
		super.start();
	}
}

