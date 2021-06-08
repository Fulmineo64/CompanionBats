package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.EnumSet;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

public class CompanionBatTrackOwnerAttackerGoal extends TrackTargetGoal {
	private final CompanionBatEntity entity;
	private LivingEntity attacker;
	private int lastAttackedTime;

	public CompanionBatTrackOwnerAttackerGoal(CompanionBatEntity entity) {
		super(entity, false);
		this.entity = entity;
		this.setControls(EnumSet.of(Goal.Control.TARGET));
	}

	public boolean canStart() {
		if (this.entity.isTamed() && !this.entity.isSitting()) {
			LivingEntity owner = this.entity.getOwner();
			if (owner == null) {
				return false;
			} else {
				Byte guardMode = this.entity.getGuardMode();
				if (guardMode == 0 || (guardMode == 1 && owner.getHealth() > (owner.getMaxHealth() / 2))){
					return false;
				}
				this.attacker = owner.getAttacker();
				int i = owner.getLastAttackedTime();
				return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.createAttackable().ignoreVisibility()) && this.entity.canAttackWithOwner(this.attacker, owner);
			}
		} else {
			return false;
		}
	}

	public void start() {
		this.mob.setTarget(this.attacker);
		LivingEntity livingEntity = this.entity.getOwner();
		if (livingEntity != null) {
			this.lastAttackedTime = livingEntity.getLastAttackedTime();
		}

		super.start();
	}
}
