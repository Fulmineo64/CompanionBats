package dev.fulmineo.companion_bats.data;

import net.minecraft.nbt.NbtCompound;

public class CompanionBatClassLevel {
	public int totalExp;
	public String abilityType;
	public String ability;
	public boolean permanent;
	public int abilityIncrement;
	public int duration;

	public int getAbilityIncrement() {
		return this.abilityIncrement == 0 ? 1 : this.abilityIncrement;
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
		if (this.abilityType != null) nbt.putString("abilityType", this.abilityType);
		if (this.ability != null) nbt.putString("ability", this.ability.toString());
		nbt.putBoolean("permanent", this.permanent);
		nbt.putInt("abilityIncrement", this.abilityIncrement);
		nbt.putInt("duration", this.duration);
		return nbt;
	}

	public static CompanionBatClassLevel fromNbt(NbtCompound nbt) {
		CompanionBatClassLevel level = new CompanionBatClassLevel();
		level.totalExp = nbt.getInt("totalExp");
		if (nbt.contains("abilityType")) level.abilityType = nbt.getString("abilityType");
		if (nbt.contains("ability")) level.ability = nbt.getString("ability");
		level.permanent = nbt.getBoolean("permanent");
		level.abilityIncrement = nbt.getInt("abilityIncrement");
		level.duration = nbt.getInt("duration");
		return level;
	}
}