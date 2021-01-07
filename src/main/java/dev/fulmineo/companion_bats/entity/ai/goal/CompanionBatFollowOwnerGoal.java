package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.EnumSet;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
// import net.minecraft.entity.ai.pathing.PathNodeType;

public class CompanionBatFollowOwnerGoal extends Goal {
    private final CompanionBatEntity tameable;
    private LivingEntity owner;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;

    public CompanionBatFollowOwnerGoal(CompanionBatEntity tameable, double speed, float minDistance, float maxDistance) {
        this.tameable = tameable;
        this.speed = speed;
        this.navigation = tameable.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(tameable.getNavigation() instanceof MobNavigation) && !(tameable.getNavigation() instanceof BirdNavigation)) {
           throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
     }
  
    public boolean canStart() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
           return false;
        } else if (livingEntity.isSpectator()) {
           return false;
        } else if (this.tameable.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
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
           return this.tameable.squaredDistanceTo(this.owner) > (double)(this.maxDistance * this.maxDistance);
        }
    }
  
    public void start() {
        this.updateCountdownTicks = 0;
    }
  
    public void stop() {
        this.owner = null;
        this.navigation.stop();
    }

    public void tick() {
        this.tameable.getLookControl().lookAt(this.owner, 10.0F, (float)this.tameable.getLookPitchSpeed());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.tameable.isLeashed() && !this.tameable.hasVehicle()) {
                double distance = this.tameable.squaredDistanceTo(this.owner);
                if (distance >= 288.0D) {
                    this.tryTeleport();
                } else {
                    this.navigation.startMovingTo(this.owner, distance > 28.0D ? this.speed : this.speed * 0.75);
                }
            }
        }
    }

    private void tryTeleport() {
        this.tameable.returnToPlayerInventory();
    }
}
