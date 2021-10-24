package dev.fulmineo.companion_bats.data;

import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.nbt.NbtCompound;

public class CompanionBatCombatLevel {
	public int totalExp;
	public float healthBonus;
	public float attackBonus;
	public float speedBonus;

	CompanionBatCombatLevel(int totalExp, float healthBonus, float attackBonus, float speedBonus) {
		this.totalExp = totalExp;
		this.healthBonus = healthBonus;
		this.attackBonus = attackBonus;
		this.speedBonus = speedBonus;
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putInt("totalExp", this.totalExp);
		nbt.putFloat("healthBonus", this.healthBonus);
		nbt.putFloat("attackBonus", this.attackBonus);
		nbt.putFloat("speedBonus", this.speedBonus);
		return nbt;
	}

	public static CompanionBatCombatLevel fromNbt(NbtCompound nbt) {
		return new CompanionBatCombatLevel(nbt.getInt("totalExp"), nbt.getFloat("healthBonus"), nbt.getFloat("attackBonus"), nbt.getFloat("speedBonus"));
	}

	public static int getLevelByExp(CompanionBatCombatLevel[] combatLevels, int exp) {
		for (int i = combatLevels.length - 1; i >= 0; i--) {
			if (combatLevels[i].totalExp <= exp) {
				return i;
			}
		}
		return combatLevels.length - 1;
	}


	public static float getMaxLevelHealth(CompanionBatCombatLevel[] combatLevels) {
		return CompanionBats.CONFIG.baseHealth + getLevelHealthBonus(combatLevels, combatLevels.length - 1);
	}

	public static float getLevelHealth(CompanionBatCombatLevel[] combatLevels, int level) {
		return CompanionBats.CONFIG.baseHealth + getLevelHealthBonus(combatLevels, level);
	}

	public static float getLevelAttack(CompanionBatCombatLevel[] combatLevels, int level) {
		return CompanionBats.CONFIG.baseAttack + getLevelAttackBonus(combatLevels, level);
	}

	public static float getLevelSpeed(CompanionBatCombatLevel[] combatLevels, int level) {
		return CompanionBats.CONFIG.baseSpeed + getLevelSpeedBonus(combatLevels, level);
	}

	public static float getLevelHealthBonus(CompanionBatCombatLevel[] combatLevels, int level) {
		return combatLevels[level].healthBonus;
	}

	public static float getLevelAttackBonus(CompanionBatCombatLevel[] combatLevels, int level) {
		return combatLevels[level].attackBonus;
	}

	public static float getLevelSpeedBonus(CompanionBatCombatLevel[] combatLevels, int level) {
		return combatLevels[level].speedBonus;
	}
}