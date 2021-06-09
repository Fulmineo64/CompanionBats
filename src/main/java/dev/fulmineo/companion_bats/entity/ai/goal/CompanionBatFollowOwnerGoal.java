package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.EnumSet;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathNavigator;

public class CompanionBatFollowOwnerGoal extends Goal {
    private final CompanionBatEntity entity;
    private LivingEntity owner;
    private final double speed;
    private final PathNavigator navigation;
    private int updateCountdownTicks;
    private final double maxDistanceSquared;
    private final double minDistanceSquared;

    public CompanionBatFollowOwnerGoal(CompanionBatEntity entity, double speed, float minDistance, float maxDistance) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
        this.minDistanceSquared = (double)(minDistance * minDistance);
        this.maxDistanceSquared = (double)(maxDistance * maxDistance);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
     }

    public boolean canUse() {
        LivingEntity livingEntity = this.entity.getOwner();
        if (livingEntity == null || livingEntity.isSpectator() || this.entity.isDigging() || !this.isWithinDistanceToStart(livingEntity)) {
            return false;
        } else {
           this.owner = livingEntity;
           return true;
        }
    }

    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
           	return false;
        } else {
			double distance = this.entity.distanceToSqr(this.owner);
           	return distance > 7 && distance < (double)(this.maxDistanceSquared);
        }
    }

    public void start() {
        if (this.entity.isRoosting()){
            this.entity.setRoosting(false);
        }
        this.updateCountdownTicks = 0;
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
    }

    public void tick() {
        this.entity.getLookControl().setLookAt(this.owner, 10.0F, (float)this.entity.getMaxHeadXRot());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.entity.isLeashed() && !this.entity.isPassenger()) {
                double distance = this.entity.distanceToSqr(this.owner);
                if (distance >= this.maxDistanceSquared * 1.15){
                    this.entity.returnToPlayerInventory();
                } else {
                    this.navigation.moveTo(this.owner, distance > (this.maxDistanceSquared * 10 / 100) ? this.speed : this.speed * 0.75);
                    this.navigation.getPath().setNextNodeIndex(1);
                }
            }
        }
    }

    private boolean isWithinDistanceToStart(LivingEntity owner){
        if (this.entity.isRoosting() || this.entity.isAboutToRoost() || this.entity.isFleeing()){
            return this.entity.distanceToSqr(owner) > this.maxDistanceSquared * 0.7D;
        } else {
            return this.entity.distanceToSqr(owner) > this.minDistanceSquared;
        }
    }
}
