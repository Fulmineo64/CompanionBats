package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.EnumSet;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;

public class CompanionBatTrackOwnerAttackerGoal extends TargetGoal {
	private final CompanionBatEntity entity;
	private LivingEntity attacker;
	private int lastAttackedTime;

	public CompanionBatTrackOwnerAttackerGoal(CompanionBatEntity entity) {
		super(entity, false);
		this.entity = entity;
		this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	public boolean canUse() {
		if (this.entity.isTame() && !this.entity.isInSittingPose()) {
			LivingEntity owner = this.entity.getOwner();
			if (owner == null) {
				return false;
			} else {
				Byte guardMode = this.entity.getGuardMode();
				if (guardMode == 0 || (guardMode == 1 && owner.getHealth() > (owner.getMaxHealth() / 2))){
					return false;
				}
				this.attacker = owner.getLastHurtByMob();
				int i = owner.getLastHurtByMobTimestamp();
				return i != this.lastAttackedTime && this.canAttack(this.attacker, new EntityPredicate().allowUnseeable()) && this.entity.canAttackWithOwner(this.attacker, owner);
			}
		} else {
			return false;
		}
	}

	public void start() {
		this.mob.setTarget(this.attacker);
		LivingEntity livingEntity = this.entity.getOwner();
		if (livingEntity != null) {
			this.lastAttackedTime = livingEntity.getLastHurtByMobTimestamp();
		}

		super.start();
	}
}
