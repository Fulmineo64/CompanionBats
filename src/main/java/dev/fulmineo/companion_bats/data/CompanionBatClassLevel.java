package dev.fulmineo.companion_bats.data;

import net.minecraft.nbt.NbtCompound;

public class CompanionBatClassLevel {
	public int totalExp;
	public CompanionBatAbility ability;
	public boolean permanent;

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
		if (this.ability != null) nbt.put("ability", this.ability.writeNbt(new NbtCompound()));
		nbt.putBoolean("permanent", this.permanent);
		return nbt;
	}

	public static CompanionBatClassLevel fromNbt(NbtCompound nbt) {
		CompanionBatClassLevel level = new CompanionBatClassLevel();
		level.totalExp = nbt.getInt("totalExp");
		if (nbt.contains("ability")) level.ability = CompanionBatAbility.fromNbt(nbt.getCompound("ability"));
		level.permanent = nbt.getBoolean("permanent");
		return level;
	}
}