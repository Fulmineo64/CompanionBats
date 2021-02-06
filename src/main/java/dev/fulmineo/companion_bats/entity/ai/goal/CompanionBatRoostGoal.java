package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.Random;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CompanionBatRoostGoal extends Goal {
    private final CompanionBatEntity entity;
    private final double speed;
    private final EntityNavigation navigation;
    private final float maxDistanceToBegin;
    protected final Random random;
    private int canStartCountdownTicks;
	private int timeoutTicksToBegin;

    public CompanionBatRoostGoal(CompanionBatEntity entity, double speed, float maxDistanceToBegin, int timeoutTicksToBegin) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
		this.maxDistanceToBegin = maxDistanceToBegin;
		this.timeoutTicksToBegin = timeoutTicksToBegin;
		this.canStartCountdownTicks = this.timeoutTicksToBegin;
        this.random = new Random();
     }

    public boolean canStart() {
		if (!this.navigation.isIdle()) {
			this.canStartCountdownTicks = this.timeoutTicksToBegin;
			return false;
		}
		if (--this.canStartCountdownTicks <= 0) {
            this.canStartCountdownTicks = this.timeoutTicksToBegin;
			LivingEntity livingEntity = this.entity.getOwner();
			if (this.entity.isFleeing() || this.entity.isRoosting() || this.entity.isLeashed() || this.entity.hasVehicle() || this.entity.getTarget() != null){
				return false;
			} else if (livingEntity != null && (livingEntity.isSpectator() || this.entity.squaredDistanceTo(livingEntity) > (double)(this.maxDistanceToBegin * this.maxDistanceToBegin))) {
				return false;
			} else  {
				return this.isBundleEmpty();
			}
		}
        return false;
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
        if (!this.entity.isRoosting() && this.entity.hangingPosition != null && this.isBundleEmpty()){
            BlockPos blockPos = this.entity.getBlockPos();
            BlockPos blockPos2 = blockPos.up();
            if (this.entity.world.isAir(this.entity.hangingPosition)){
                this.entity.hangingPosition = null;
            } else {
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
        } else {
			this.entity.hangingPosition = null;
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

	private boolean isBundleEmpty(){
		ItemStack bundleStack = this.entity.getBundle();
		return !bundleStack.isOf(Items.BUNDLE) || BundleItem.getAmountFilled(bundleStack) == 0;
	}
}