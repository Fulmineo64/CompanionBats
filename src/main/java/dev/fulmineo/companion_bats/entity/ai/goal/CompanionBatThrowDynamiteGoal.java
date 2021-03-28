package dev.fulmineo.companion_bats.entity.ai.goal;

import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.entity.DynamiteEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CompanionBatThrowDynamiteGoal extends Goal {
	private int dynamiteCooldown;
	private CompanionBatEntity entity;
	private LivingEntity owner;
	private int dynamiteTicks;
	private EntityNavigation navigation;
	private int updateCountdownTicks;
	private double minDistanceSquared;
	private double maxDistanceSquared;

	public CompanionBatThrowDynamiteGoal(CompanionBatEntity entity, float minDistance, float maxDistance, int dynamiteCooldown) {
		this.entity = entity;
        this.minDistanceSquared = (double)(minDistance * minDistance);
        this.maxDistanceSquared = (double)(maxDistance * maxDistance);
		this.dynamiteCooldown = dynamiteCooldown;
		this.dynamiteTicks = dynamiteCooldown;
        this.navigation = entity.getNavigation();
	}

	public boolean canStart() {
        LivingEntity livingEntity = this.entity.getOwner();
		if (livingEntity == null || livingEntity.isSpectator() || this.entity.isRoosting()) {
            return false;
        } else {
			if (this.dynamiteTicks > 0) this.dynamiteTicks--;
			if (this.dynamiteTicks == 0){
				this.owner = livingEntity;
				return this.entity.getTarget() != null;
			}
			return false;
        }
    }

    public boolean shouldContinue() {
		return !this.navigation.isIdle() && this.entity.getTarget() != null;
    }

    public void start() {
    }

    public void stop() {
    }

    public void tick() {
        if (--this.updateCountdownTicks <= 0) {
			this.updateCountdownTicks = 10;
            if (!this.entity.isLeashed() && !this.entity.hasVehicle()) {
				LivingEntity target = this.entity.getTarget();
				if (target != null && target.isAlive()){
					double distance = this.entity.squaredDistanceTo(target);
					double ownerDistance = target.squaredDistanceTo(this.owner);
					if (distance > this.minDistanceSquared) {
						Direction direction = this.entity.getHorizontalFacing();
						// TODO: Here disable temporarily melee attack
						this.entity.setTarget(null);
						this.navigation.startMovingTo(this.entity.getX() - direction.getOffsetX(), this.entity.getY() - direction.getOffsetY(), this.entity.getZ() - direction.getOffsetZ(), 1.0F);
					} else if (distance < this.maxDistanceSquared && ownerDistance > this.minDistanceSquared){
						Vec3d vec3d = target.getVelocity();
						double d = target.getX() + vec3d.x - this.entity.getX();
						double e = target.getEyeY() - 1.100000023841858D - this.entity.getY();
						double f = target.getZ() + vec3d.z - this.entity.getZ();
						float g = MathHelper.sqrt(d * d + f * f);

						DynamiteEntity dynamite = new DynamiteEntity(this.entity.world, this.entity);
						dynamite.pitch -= -20.0F;
						dynamite.setPower(this.entity.abilities.getValue(CompanionBatAbility.DYNAMITE));
						dynamite.setVelocity(d, e + (double)(g * 0.2F), f, 0.75F, 8.0F);

						if (!this.entity.isSilent()) {
							this.entity.world.playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 1.0F, 1F + this.entity.world.random.nextFloat() * 0.4F);
						}

						this.entity.world.spawnEntity(dynamite);
						this.dynamiteTicks = this.dynamiteCooldown;


						this.entity.setVelocity(vec3d.negate());
					}
				}
            }
        }
	}
}
