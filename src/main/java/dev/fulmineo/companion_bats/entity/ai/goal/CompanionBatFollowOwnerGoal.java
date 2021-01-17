package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.EnumSet;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
// import net.minecraft.entity.ai.pathing.PathNodeType;

public class CompanionBatFollowOwnerGoal extends Goal {
    private final CompanionBatEntity entity;
    private LivingEntity owner;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final double maxDistanceSquared;
    private final double minDistanceSquared;

    public CompanionBatFollowOwnerGoal(CompanionBatEntity entity, double speed, float minDistance, float maxDistance) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
        this.minDistanceSquared = (double)(minDistance * minDistance);
        this.maxDistanceSquared = (double)(maxDistance * maxDistance);
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
     }

    public boolean canStart() {
        LivingEntity livingEntity = this.entity.getOwner();
         if (livingEntity == null || livingEntity.isSpectator() || !this.isWithinDistanceToStart(livingEntity)) {
            return false;
        } else {
           this.owner = livingEntity;
           return true;
        }
    }

    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
           return false;
        } else {
           return this.entity.squaredDistanceTo(this.owner) > (double)(this.maxDistanceSquared);
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
        this.entity.getLookControl().lookAt(this.owner, 10.0F, (float)this.entity.getLookPitchSpeed());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.entity.isLeashed() && !this.entity.hasVehicle()) {
                double distance = this.entity.squaredDistanceTo(this.owner);
                if (distance >= this.maxDistanceSquared + (this.maxDistanceSquared * 15 / 100)){
                    this.tryTeleport();
                } else {
                    this.navigation.startMovingTo(this.owner, distance > (this.maxDistanceSquared * 10 / 100) ? this.speed : this.speed * 0.75);
                }
            }
        }
    }

    private void tryTeleport() {
        this.entity.returnToPlayerInventory();
    }

    private boolean isWithinDistanceToStart(LivingEntity owner){
        if (this.entity.isRoosting() || this.entity.isAboutToRoost()){
            return this.entity.squaredDistanceTo(owner) > this.maxDistanceSquared * 0.8D;
        } else {
            return this.entity.squaredDistanceTo(owner) > this.minDistanceSquared;
        }
    }
}
