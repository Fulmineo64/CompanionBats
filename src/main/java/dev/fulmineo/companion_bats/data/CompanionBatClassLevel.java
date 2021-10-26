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

	public int getAbilityLevelIncrease() {
		return this.abilityLevelIncrease == 0 ? 1 : this.abilityLevelIncrease;
	}

	public static int getClassLevelByExp(CompanionBatClassLevel[] classLevels, int exp) {
		for (int i = classLevels.length - 1; i >= 0; i--) {
			if (classLevels[i].totalExp <= exp) {
				return i;
			}
		}
		return classLevels.length - 1;
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putInt("totalExp", this.totalExp);
		if (this.ability != null) nbt.putString("ability", this.ability.toString());
		return nbt;
	}

	public static CompanionBatClassLevel fromNbt(NbtCompound nbt) {
		if (nbt.contains("ability")) {
			return new CompanionBatClassLevel(nbt.getInt("totalExp"), CompanionBatAbility.valueOf(nbt.getString("ability")));
		} else {
			return new CompanionBatClassLevel(nbt.getInt("totalExp"));
		}
	}
}