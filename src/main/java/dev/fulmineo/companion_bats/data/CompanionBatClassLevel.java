package dev.fulmineo.companion_bats.data;

import dev.fulmineo.companion_bats.CompanionBatAbility;
import net.minecraft.nbt.NbtCompound;

public class CompanionBatClassLevel {
	public int totalExp;
	public CompanionBatAbility ability;
	public boolean permanent;
	public int abilityLevelIncrease;

	CompanionBatClassLevel(int totalExp) {
		this(totalExp, null, false, 0);
	}

	CompanionBatClassLevel(int totalExp, CompanionBatAbility ability) {
		this(totalExp, ability, false, 1);
	}

	CompanionBatClassLevel(int totalExp, CompanionBatAbility ability, boolean permanent) {
		this(totalExp, ability, permanent, 1);
	}

	CompanionBatClassLevel(int totalExp, CompanionBatAbility ability, boolean permanent, int abilityLevelIncrease) {
		this.totalExp = totalExp;
		this.ability = ability;
		this.permanent = permanent;
		this.abilityLevelIncrease = abilityLevelIncrease;
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putInt("totalExp", this.totalExp);
		return nbt;
	}

	public static CompanionBatClassLevel fromNbt(NbtCompound nbt) {
		return new CompanionBatClassLevel(nbt.getInt("totalExp"));
	}
}