package dev.fulmineo.companion_bats.entity.ai.goal;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class CompanionBatMeleeAttackGoal extends MeleeAttackGoal {

	public CompanionBatMeleeAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
    }

	public void tick() {
		if (this.mob.getTarget() != null) {
			super.tick();
		}
	}
}
