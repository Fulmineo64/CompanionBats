package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.Random;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.item.CompanionBatPouchItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class CompanionBatRoostGoal extends Goal {
    private final CompanionBatEntity entity;
    private final double speed;
    private final PathNavigator navigation;
    private final float maxDistanceToBegin;
    protected final Random random;
	private int timeoutTicks;

    public CompanionBatRoostGoal(CompanionBatEntity entity, double speed, float maxDistanceToBegin, int timeoutTicks) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
		this.maxDistanceToBegin = maxDistanceToBegin;
		this.timeoutTicks = timeoutTicks;
		this.resetTimeoutTicks();
        this.random = new Random();
     }

    public boolean canUse() {
		if (this.entity.isRoosting()) return false;
		if (!this.navigation.isDone()) {
			this.resetTimeoutTicks();
			return false;
		}
		if (--this.entity.roostTicks <= 0) {
            this.resetTimeoutTicks();
			LivingEntity livingEntity = this.entity.getOwner();
			if (this.entity.isFleeing() || this.entity.isRoosting() || this.entity.isLeashed() || this.entity.isPassenger() || this.entity.getTarget() != null){
				return false;
			} else if (livingEntity != null && (livingEntity.isSpectator() || this.entity.distanceToSqr(livingEntity) > (double)(this.maxDistanceToBegin * this.maxDistanceToBegin))) {
				return false;
			} else  {
				return this.isBundleEmpty();
			}
		}
        return false;
    }

	private void resetTimeoutTicks(){
		float healthPercentage = this.entity.getHealth() / this.entity.getMaxHealth();
		if (healthPercentage < 1.0F){
			this.entity.roostTicks = this.timeoutTicks;
		} else {
			this.entity.roostTicks = this.timeoutTicks * 3;
		}
	}

    public boolean canContinueToUse() {
        return !this.entity.isRoosting() && this.entity.hangingPosition != null;
    }

    public void start() {
        BlockPos pos = this.entity.blockPosition();
        for (int y = 1; y <= 10; y++){
            pos = pos.above();
            if (this.validateAndSetPos(pos.north((int)(y / 1.75)))) return;
            if (this.validateAndSetPos(pos.east((int)(y / 1.75)))) return;
            if (this.validateAndSetPos(pos.south((int)(y / 1.75)))) return;
            if (this.validateAndSetPos(pos.west((int)(y / 1.75)))) return;
            if (this.validateAndSetPos(pos)) return;
		}
    }

    public void stop() {
		this.navigation.stop();
    }

    public void tick() {
        if (!this.entity.isRoosting() && this.entity.hangingPosition != null && this.isBundleEmpty()){
            BlockPos blockPos = this.entity.blockPosition();
            BlockPos blockPos2 = blockPos.above();
            if (this.entity.level.isEmptyBlock(this.entity.hangingPosition)){
                this.entity.hangingPosition = null;
            } else {
                this.navigation.moveTo(this.entity.hangingPosition.getX(), this.entity.hangingPosition.getY(), this.entity.hangingPosition.getZ(), this.speed);
                if (this.navigation.isDone()){
                    double e = (double)this.entity.hangingPosition.getY() + 0.1D - this.entity.getY();
                    Vector3d Vector3d = this.entity.getDeltaMovement();
                    Vector3d Vector3d2 = Vector3d.add(Vector3d.x, (Math.signum(e) * 0.699999988079071D - Vector3d.y) * 0.10000000149011612D, Vector3d.z);
                    this.entity.setDeltaMovement(Vector3d2);
                }
                if (this.entity.level.getBlockState(blockPos2).isRedstoneConductor(this.entity.level, blockPos2)) {
                    this.entity.setRoosting(true);
                    this.entity.yRot = (float)this.random.nextInt(360);
                    this.entity.yHeadRot = this.entity.yRot;
                }
            }
        } else {
			this.entity.hangingPosition = null;
		}
    }

    private boolean isValidHangingPos(BlockPos pos){
        return this.entity.level.getBlockState(pos).isRedstoneConductor(this.entity.level, pos) && this.entity.level.isEmptyBlock(pos.below());
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
		return bundleStack.getItem() != CompanionBats.BAT_POUCH_ITEM.get() || CompanionBatPouchItem.isEmpty(bundleStack);
	}
}