package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.List;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class CompanionBatAvoidHostilesGoal extends Goal {
	protected final CompanionBatEntity entity;
	private final double speed;
	protected HostileEntity targetEntity;
	protected final float fleeDistance;
	protected Path fleePath;
	protected final EntityNavigation navigation;
	private final TargetPredicate withinRangePredicate;

	private static final Direction[] DIRECTIONS_X;
	private static final Direction[] DIRECTIONS_Z;

	public CompanionBatAvoidHostilesGoal(CompanionBatEntity entity, float distance, float speed) {
		this.entity = entity;
		this.speed = speed;
		this.fleeDistance = distance;
        this.navigation = entity.getNavigation();
		this.withinRangePredicate = (new TargetPredicate()).setBaseMaxDistance((double)distance);
	}

	public boolean canStart() {
		LivingEntity owner = this.entity.getOwner();
		if (owner == null || owner.isSpectator()) return false;
		List<HostileEntity> entityList = this.entity.world.getEntitiesByClass(HostileEntity.class, this.entity.getBoundingBox().expand((double)this.fleeDistance, 3.0D, (double)this.fleeDistance), (livingEntity) -> true);
		this.targetEntity = this.entity.world.getClosestEntity(entityList, this.withinRangePredicate, this.entity, this.entity.getX(), this.entity.getY(), this.entity.getZ());
		if (this.targetEntity == null) {
			return false;
		} else {
			Direction ownerFacing = owner.getHorizontalFacing();
			Direction[] availableDirections;

			double x = this.entity.getX();
			double y = this.entity.getY();
			double z = this.entity.getZ();

			if (DIRECTIONS_X[0] == ownerFacing || DIRECTIONS_X[1] == ownerFacing){
				availableDirections = DIRECTIONS_Z;
				Direction direction = availableDirections[this.entity.world.random.nextInt(2)];
				z += this.fleeDistance * (direction == Direction.NORTH ? -1 : 1);
				x += this.fleeDistance * (ownerFacing == Direction.WEST ? 1 : -1);
			} else {
				availableDirections = DIRECTIONS_X;
				Direction direction = availableDirections[this.entity.world.random.nextInt(2)];
				x += this.fleeDistance * (direction == Direction.WEST ? -1 : 1);
				z += this.fleeDistance * (ownerFacing == Direction.NORTH ? 1 : -1);
			}

			this.entity.fleeingPosition = new BlockPos(new Vec3d(x, y, z));
			this.fleePath = this.navigation.findPathTo(x, y, z, 0);
			return this.fleePath != null;
		}
	}

	public boolean shouldContinue() {
		boolean bl = !this.navigation.isIdle();
	   	return bl;
	}

	public void start() {
	   this.navigation.startMovingAlong(this.fleePath, this.speed);
	}

	public void stop() {
		this.entity.fleeingPosition = null;
	   	this.targetEntity = null;
	}

	public void tick() {
	}

	static {
		DIRECTIONS_X = new Direction[2];
		DIRECTIONS_X[0] = Direction.WEST;
		DIRECTIONS_X[1] = Direction.EAST;

		DIRECTIONS_Z = new Direction[2];
		DIRECTIONS_Z[0] = Direction.NORTH;
		DIRECTIONS_Z[1] = Direction.SOUTH;

	}
}
