package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.TameableEntity;

public class CompanionBatTrackOwnerAttackerGoal extends TrackTargetGoal {
	private final TameableEntity entity;
	private LivingEntity attacker;
	private int lastAttackedTime;

	public CompanionBatTrackOwnerAttackerGoal(TameableEntity entity) {
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
				this.attacker = owner.getAttacker();
				int i = owner.getLastAttackedTime();
				return i != this.lastAttackedTime && owner.getHealth() < (owner.getMaxHealth() / 2) && this.canTrack(this.attacker, new TargetPredicate().includeHidden()) && this.entity.canAttackWithOwner(this.attacker, owner);
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
