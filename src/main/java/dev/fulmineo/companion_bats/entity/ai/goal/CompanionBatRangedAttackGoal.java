package dev.fulmineo.companion_bats.entity.ai.goal;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;

public class CompanionBatRangedAttackGoal extends Goal {
	private CompanionBatEntity entity;
	private EntityNavigation navigation;
	private int updateCountdownTicks;
	private double minDistanceSquared;
	private double maxDistanceSquared;
	private RangedAttackFunction attackFunction;

	public CompanionBatRangedAttackGoal(CompanionBatEntity entity, float minDistance, float maxDistance, RangedAttackFunction attackFunction) {
		this.entity = entity;
        this.minDistanceSquared = (double)(minDistance * minDistance);
        this.maxDistanceSquared = (double)(maxDistance * maxDistance);
		this.attackFunction = attackFunction;
        this.navigation = entity.getNavigation();
	}

	public boolean canStart() {
        if (--this.entity.rangedAttackTicks <= 0) {
			this.entity.rangedAttackTicks = 10;
			LivingEntity livingEntity = this.entity.getOwner();
			if (livingEntity == null || livingEntity.isSpectator() || this.entity.isRoosting()) {
				return false;
			} else {
				return this.entity.getTarget() != null && this.entity.getTarget().isAlive() && this.isWithinDistanceToAttack(this.entity.getTarget());
			}
		}
		return false;
    }

    public boolean shouldContinue() {
		return this.entity.rangedAttackTicks <= 0 && !this.navigation.isIdle() && this.entity.getTarget() != null && this.entity.getTarget().isAlive() && this.isWithinDistanceToAttack(this.entity.getTarget());
    }

    public void start() {
		this.updateCountdownTicks = 0;
    }

    public void stop() {
    }

    public void tick() {
        if (--this.updateCountdownTicks <= 0) {
			this.updateCountdownTicks = 10;
            if (!this.entity.isLeashed() && !this.entity.hasVehicle()) {
				LivingEntity target = this.entity.getTarget();
				if (target != null && target.isAlive() && this.entity.getVisibilityCache().canSee(target)){
					this.attackFunction.rangedAttack(target);
				}
            }
        }
	}

	private boolean isWithinDistanceToAttack(LivingEntity target){
		double distance = this.entity.squaredDistanceTo(target);
		return distance > this.minDistanceSquared && distance < this.maxDistanceSquared;
	}

	public interface RangedAttackFunction {
		void rangedAttack(Entity target);
	}
}

