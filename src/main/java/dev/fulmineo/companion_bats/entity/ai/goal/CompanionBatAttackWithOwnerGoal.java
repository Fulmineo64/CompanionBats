package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.EnumSet;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

public class CompanionBatAttackWithOwnerGoal extends TrackTargetGoal {
	private final CompanionBatEntity entity;
	private LivingEntity attacking;
	private int lastAttackTime;

	public CompanionBatAttackWithOwnerGoal(CompanionBatEntity entity) {
	   super(entity, false);
	   this.entity = entity;
	   this.setControls(EnumSet.of(Goal.Control.TARGET));
	}

	public boolean canStart() {
		if (this.entity.isTamed() && !this.entity.isSitting()) {
			LivingEntity livingEntity = this.entity.getOwner();
			if (livingEntity == null) {
				return false;
			} else {
				this.attacking = livingEntity.getAttacking();
				int i = livingEntity.getLastAttackTime();
				return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.createAttackable().visibleOnly()) && this.entity.canAttackWithOwner(this.attacking, livingEntity);
			}
		} else {
			return false;
		}
	}

	public void start() {
		this.entity.setTarget(this.attacking);
		LivingEntity livingEntity = this.entity.getOwner();
		if (livingEntity != null) {
			this.lastAttackTime = livingEntity.getLastAttackTime();
		}
		super.start();
	}
}

