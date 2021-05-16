package dev.fulmineo.companion_bats.entity.ai.goal;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathNavigator;

public class CompanionBatRangedAttackGoal extends Goal {
	private CompanionBatEntity entity;
	private PathNavigator navigation;
	private int canStartCountdownTicks;
	private int updateCountdownTicks;
	private double minDistanceSquared;
	private double maxDistanceSquared;
	private int rangedAttackCooldown;

	public CompanionBatRangedAttackGoal(CompanionBatEntity entity, float minDistance, float maxDistance, int rangedAttackCooldown) {
		this.entity = entity;
        this.minDistanceSquared = (double)(minDistance * minDistance);
        this.maxDistanceSquared = (double)(maxDistance * maxDistance);
		this.rangedAttackCooldown = rangedAttackCooldown;
		this.canStartCountdownTicks = rangedAttackCooldown;
        this.navigation = entity.getNavigation();
	}

	public boolean canUse() {
        if (--this.canStartCountdownTicks <= 0) {
			this.canStartCountdownTicks = 10;
			LivingEntity livingEntity = this.entity.getOwner();
			if (livingEntity == null || livingEntity.isSpectator() || this.entity.isRoosting()) {
				return false;
			} else {
				return this.entity.getTarget() != null && this.entity.getTarget().isAlive() && this.isWithinDistanceToAttack(this.entity.getTarget());
			}
		}
		return false;
    }

    public boolean canContinueToUse() {
		return this.canStartCountdownTicks <= 0 && !this.navigation.isDone() && this.entity.getTarget() != null && this.entity.getTarget().isAlive() && this.isWithinDistanceToAttack(this.entity.getTarget());
    }

    public void start() {
		this.updateCountdownTicks = 0;
    }

    public void stop() {
    }

    public void tick() {
        if (--this.updateCountdownTicks <= 0) {
			this.updateCountdownTicks = 10;
            if (!this.entity.isLeashed() && !this.entity.isPassenger()) {
				LivingEntity target = this.entity.getTarget();
				if (target != null && target.isAlive() && this.entity.getSensing().canSee(target) && this.entity.tryRangedAttack(target)){
					this.canStartCountdownTicks = this.rangedAttackCooldown;
				}
            }
        }
	}

	private boolean isWithinDistanceToAttack(LivingEntity target){
		double distance = this.entity.distanceToSqr(target);
		return distance > this.minDistanceSquared && distance < this.maxDistanceSquared;
	}
}
