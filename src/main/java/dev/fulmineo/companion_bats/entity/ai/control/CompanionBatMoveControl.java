package dev.fulmineo.companion_bats.entity.ai.control;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.MathHelper;

public class CompanionBatMoveControl extends MovementController {
    private final int maxPitchChange;

    public CompanionBatMoveControl(CompanionBatEntity entity, int maxPitchChange) {
        super(entity);
        this.maxPitchChange = maxPitchChange;
    }

    public void tick() {
        if (this.operation == MovementController.Action.MOVE_TO) {
            this.operation = MovementController.Action.WAIT;
            // Entity is set on ground movement because otherwise its flying speed would be capped.
            this.mob.setOnGround(true);
            this.mob.setNoGravity(true);
            double d = this.wantedX - this.mob.getX();
            double e = this.wantedY - this.mob.getY();
            double f = this.wantedZ - this.mob.getZ();
            // CompanionBats.info("d = " + d + " e = " + e + " f = " + f);
            double g = d * d + e * e + f * f;
            if (g < 2.500000277905201E-7D) {
                // this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
                return;
            }

            float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875D) - 90.0F;
			this.mob.yRot = this.rotlerp(this.mob.yRot, h, 90.0F);
            float j = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.mob.setSpeed(j);
            double k = (double)MathHelper.sqrt(d * d + f * f);
            float l = (float)(-(MathHelper.atan2(e, k) * 57.2957763671875D));
            this.mob.xRot = this.rotlerp(this.mob.xRot, l, (float)this.maxPitchChange);
            if (Math.abs(e) < 0.25) {
                j = j / 4;
            } else if (Math.abs(e) < 0.5) {
                j = j / 3;
            } else {
                j = j / 2;
            }
            this.mob.setYya(e > 0.0D ? j : -j);
        } else {
            /*if (!this.noGravity) {
                this.entity.setNoGravity(false);
            }*/

            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
        }
    }

    public void setWantedPosition(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_) {
        this.wantedX = p_75642_1_;
        this.wantedY = p_75642_3_;
        this.wantedZ = p_75642_5_;
        this.speedModifier = p_75642_7_;
        if (this.operation != MovementController.Action.JUMPING) {
            this.operation = MovementController.Action.MOVE_TO;
        }

    }

}
