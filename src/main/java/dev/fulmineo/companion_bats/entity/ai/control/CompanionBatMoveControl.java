package dev.fulmineo.companion_bats.entity.ai.control;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;

public class CompanionBatMoveControl extends MoveControl {
    private final int maxPitchChange;

    public CompanionBatMoveControl(MobEntity entity, int maxPitchChange) {
        super(entity);
        this.maxPitchChange = maxPitchChange;
    }

    public void tick() {
		if (this.state == MoveControl.State.MOVE_TO) {
			this.state = MoveControl.State.WAIT;
			// Entity is set on ground movement because otherwise its flying speed would be capped.
			this.entity.setOnGround(true);
			this.entity.setNoGravity(true);
			double d = this.targetX - this.entity.getX();
			double e = this.targetY - this.entity.getY();
			double f = this.targetZ - this.entity.getZ();
			double g = d * d + e * e + f * f;
			if (g < 2.500000277905201E-7D) {
				// this.entity.setUpwardSpeed(0.0F);
				this.entity.setForwardSpeed(0.0F);
				return;
			}

			float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875D) - 90.0F;
			this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), h, 90.0F));
			float j = (float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
			if (this.entity.isSubmergedIn(FluidTags.LAVA)) {
				this.entity.setMovementSpeed(j / 3);
			} else if (this.entity.isSubmergedIn(FluidTags.WATER)) {
				this.entity.setMovementSpeed(j / 2);
			} else {
				this.entity.setMovementSpeed(j);
			}
			double k = Math.sqrt(d * d + f * f);
			float l = (float)(-(MathHelper.atan2(e, k) * 57.2957763671875D));
			this.entity.setPitch(this.wrapDegrees(this.entity.getPitch(), l, (float)this.maxPitchChange));
			if (Math.abs(e) < 0.25) {
				j = j / 4;
			} else if (Math.abs(e) < 0.5) {
				j = j / 3;
			} else {
				j = j / 2;
			}
			this.entity.setUpwardSpeed(e > 0.0D ? j : -j);
		} else {
			this.entity.setUpwardSpeed(0.0F);
			this.entity.setForwardSpeed(0.0F);
		}
    }
}
