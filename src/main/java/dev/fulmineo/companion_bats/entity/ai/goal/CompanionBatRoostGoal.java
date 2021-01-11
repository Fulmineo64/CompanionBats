package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.Random;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CompanionBatRoostGoal extends Goal {
    private final CompanionBatEntity entity;
    private final double speed;
    private final EntityNavigation navigation;
    private final float maxDistanceToBegin;
    protected final Random random;

    public CompanionBatRoostGoal(CompanionBatEntity entity, double speed, float maxDistanceToBegin) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
        this.maxDistanceToBegin = maxDistanceToBegin;
        this.random = new Random();
     }
  
    public boolean canStart() {
        LivingEntity livingEntity = this.entity.getOwner();
        if (this.entity.age < 50 || this.entity.isRoosting() || this.entity.isLeashed() || this.entity.hasVehicle() || !this.navigation.isIdle()){
            return false;
        } else if (livingEntity != null && (livingEntity.isSpectator() || this.entity.squaredDistanceTo(livingEntity) > (double)(this.maxDistanceToBegin * this.maxDistanceToBegin))) {
            return false;
        } else {
            return true;
        }
    }
  
    public boolean shouldContinue() {
        return !this.entity.isRoosting() && this.entity.hangingPosition != null;
    }
  
    public void start() {
        BlockPos pos = this.entity.getBlockPos();
        for (int y = 1; y <= 5; y++){
            pos = pos.up();
            if (this.validateAndSetPos(pos.north((int)(y / 1.5)))) return;
            if (this.validateAndSetPos(pos.east((int)(y / 1.5)))) return;
            if (this.validateAndSetPos(pos.south((int)(y / 1.5)))) return;
            if (this.validateAndSetPos(pos.west((int)(y / 1.5)))) return;
            if (this.validateAndSetPos(pos)) return;
        }
    }
  
    public void stop() {
        this.navigation.stop();
    }

    public void tick() {
        if (!this.entity.isRoosting() && this.entity.hangingPosition != null){
            BlockPos blockPos = this.entity.getBlockPos();
            BlockPos blockPos2 = blockPos.up();
            if (this.entity.world.isAir(this.entity.hangingPosition)){
                this.entity.hangingPosition = null;
            } else {
                /*double d = (double)this.entity.hangingPosition.getX() + 0.5D - this.entity.getX();
                double e = (double)this.entity.hangingPosition.getY() + 0.1D - this.entity.getY();
                double f = (double)this.entity.hangingPosition.getZ() + 0.5D - this.entity.getZ();
                Vec3d vec3d = this.entity.getVelocity();
                Vec3d vec3d2 = vec3d.add((Math.signum(d) * 0.5D - vec3d.x) * 0.10000000149011612D, (Math.signum(e) * 0.699999988079071D - vec3d.y) * 0.10000000149011612D, (Math.signum(f) * 0.5D - vec3d.z) * 0.10000000149011612D);
                this.entity.setVelocity(vec3d2);
                float g = (float)(MathHelper.atan2(vec3d2.z, vec3d2.x) * 57.2957763671875D) - 90.0F;
                float h = MathHelper.wrapDegrees(g - this.entity.yaw);
                this.entity.forwardSpeed = 0.5F;
                this.entity.yaw += h;*/
                this.navigation.startMovingTo(this.entity.hangingPosition.getX(), this.entity.hangingPosition.getY(), this.entity.hangingPosition.getZ(), this.speed);
                if (this.navigation.isIdle()){
                    double e = (double)this.entity.hangingPosition.getY() + 0.1D - this.entity.getY();
                    Vec3d vec3d = this.entity.getVelocity();
                    Vec3d vec3d2 = vec3d.add(vec3d.x, (Math.signum(e) * 0.699999988079071D - vec3d.y) * 0.10000000149011612D, vec3d.z);
                    this.entity.setVelocity(vec3d2);
                }
                if (this.entity.world.getBlockState(blockPos2).isSolidBlock(this.entity.world, blockPos2)) {
                    this.entity.setRoosting(true);
                    this.entity.yaw = (float)this.random.nextInt(360);
                    this.entity.headYaw = this.entity.yaw;
                }
            } 
        }
    }

    private boolean isValidHangingPos(BlockPos pos){
        return this.entity.world.getBlockState(pos).isSolidBlock(this.entity.world, pos) && this.entity.world.isAir(pos.down());
    }

    private boolean validateAndSetPos(BlockPos pos){
        if (this.isValidHangingPos(pos)) {
            this.entity.hangingPosition = pos;
            return true;
        }
        return false;
    }
}