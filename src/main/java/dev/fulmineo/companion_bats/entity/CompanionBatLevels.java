package dev.fulmineo.companion_bats.entity;

import java.util.HashMap;
import java.util.Map;

import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBatClass;

public class CompanionBatLevels {
	public static final CompanionBatLevel[] LEVELS;

	public static final Map<CompanionBatClass, CompanionBatClassLevel[]> CLASS_LEVELS = new HashMap<>();

	public static class CompanionBatLevel {
		public int totalExpNeeded;
		public float healthBonus;
		public float attackBonus;
		public float speedBonus;

		CompanionBatLevel(int totalExpNeeded, float healthBonus, float attackBonus, float speedBonus) {
			this.totalExpNeeded = totalExpNeeded;
			this.healthBonus = healthBonus;
			this.attackBonus = attackBonus;
			this.speedBonus = speedBonus;
		}
	}

	public static class CompanionBatClassLevel {
		public int totalExpNeeded;
		public CompanionBatAbility ability;
		public boolean permanent;
		public int abilityLevelIncrease;

		CompanionBatClassLevel(int totalExpNeeded) {
			this(totalExpNeeded, null, false, 0);
		}

		CompanionBatClassLevel(int totalExpNeeded, CompanionBatAbility ability) {
			this(totalExpNeeded, ability, false, 1);
		}

		CompanionBatClassLevel(int totalExpNeeded, CompanionBatAbility ability, boolean permanent) {
			this(totalExpNeeded, ability, permanent, 1);
		}

		CompanionBatClassLevel(int totalExpNeeded, CompanionBatAbility ability, boolean permanent, int abilityLevelIncrease) {
			this.totalExpNeeded = totalExpNeeded;
			this.ability = ability;
			this.permanent = permanent;
			this.abilityLevelIncrease = abilityLevelIncrease;
		}
	}

	public static float getLevelHealth(int level) {
		return LEVELS[level].healthBonus;
	}

	public static float getLevelAttack(int level) {
		return LEVELS[level].attackBonus;
	}

	public static float getLevelSpeed(int level) {
		return LEVELS[level].speedBonus;
	}

	public static int getLevelByExp(int exp) {
		for (int i = CompanionBatLevels.LEVELS.length - 1; i >= 0; i--) {
			if (CompanionBatLevels.LEVELS[i].totalExpNeeded <= exp) {
				return i;
			}
		}
		return CompanionBatLevels.LEVELS.length - 1;
	}

	public static int getClassLevelByExp(CompanionBatClass batClass, int exp) {
		CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(batClass);
		for (int i = classLevels.length - 1; i >= 0; i--) {
			if (classLevels[i].totalExpNeeded <= exp) {
				return i;
			}
		}
		return classLevels.length - 1;
	}

	static {
		LEVELS = new CompanionBatLevel[20];
		LEVELS[0] = new CompanionBatLevel(0, 0F, 0F, 0F);
		LEVELS[1] = new CompanionBatLevel(50, 1F, 0F, 0F); 			// Health
		LEVELS[2] = new CompanionBatLevel(150, 2F, 0F, 0F); 		// Health
		LEVELS[3] = new CompanionBatLevel(300, 2F, 0F, 0.03F); 		// Speed
		LEVELS[4] = new CompanionBatLevel(500, 2F, 1F, 0.03F); 		// Attack
		LEVELS[5] = new CompanionBatLevel(750, 3F, 1F, 0.03F); 		// Health
		LEVELS[6] = new CompanionBatLevel(1050, 4F, 1F, 0.03F); 	// Health
		LEVELS[7] = new CompanionBatLevel(1400, 4F, 1F, 0.06F); 	// Speed
		LEVELS[8] = new CompanionBatLevel(1800, 4F, 2F, 0.06F); 	// Attack
		LEVELS[9] = new CompanionBatLevel(2250, 5F, 2F, 0.06F);	 	// Health
		LEVELS[10] = new CompanionBatLevel(2750, 6F, 2F, 0.06F); 	// Health
		LEVELS[11] = new CompanionBatLevel(3300, 6F, 2F, 0.09F); 	// Speed
		LEVELS[12] = new CompanionBatLevel(3900, 6F, 3F, 0.09F); 	// Attack
		LEVELS[13] = new CompanionBatLevel(4550, 7F, 3F, 0.09F); 	// Health
		LEVELS[14] = new CompanionBatLevel(5250, 8F, 3F, 0.09F); 	// Health
		LEVELS[15] = new CompanionBatLevel(6000, 8F, 3F, 0.12F); 	// Speed
		LEVELS[16] = new CompanionBatLevel(6800, 9F, 3F, 0.12F); 	// Health
		LEVELS[17] = new CompanionBatLevel(7650, 10F, 3F, 0.12F); 	// Health
		LEVELS[18] = new CompanionBatLevel(8550, 10F, 3F, 0.15F); 	// Speed
		LEVELS[19] = new CompanionBatLevel(9550, 10F, 4F, 0.15F); 	// Attack

		CompanionBatClassLevel[] INFERNO_LEVELS = new CompanionBatClassLevel[10];
		INFERNO_LEVELS[0] = new CompanionBatClassLevel(0);
		INFERNO_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.INCREASED_ATTACK);
		INFERNO_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.BURN);
		INFERNO_LEVELS[3] = new CompanionBatClassLevel(300, CompanionBatAbility.INCREASED_ATTACK);
		INFERNO_LEVELS[4] = new CompanionBatClassLevel(500, CompanionBatAbility.BURN);
		INFERNO_LEVELS[5] = new CompanionBatClassLevel(750, CompanionBatAbility.INCREASED_ATTACK);
		INFERNO_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.BURN);
		INFERNO_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.INCREASED_ATTACK);
		INFERNO_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.FIRE_RESISTANCE);
		INFERNO_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.INCREASED_ATTACK, true);

		CLASS_LEVELS.put(CompanionBatClass.INFERNO, INFERNO_LEVELS);

		CompanionBatClassLevel[] VAMPIRE_LEVELS = new CompanionBatClassLevel[10];
		VAMPIRE_LEVELS[0] = new CompanionBatClassLevel(0);
		VAMPIRE_LEVELS[1] = new CompanionBatClassLevel(50);
		VAMPIRE_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.INCREASED_ARMOR);
		VAMPIRE_LEVELS[3] = new CompanionBatClassLevel(300, CompanionBatAbility.LIFESTEAL);
		VAMPIRE_LEVELS[4] = new CompanionBatClassLevel(500);
		VAMPIRE_LEVELS[5] = new CompanionBatClassLevel(750, CompanionBatAbility.INCREASED_ARMOR);
		VAMPIRE_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.LIFESTEAL);
		VAMPIRE_LEVELS[7] = new CompanionBatClassLevel(1400);
		VAMPIRE_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.INCREASED_ARMOR);
		VAMPIRE_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.LIFESTEAL, true);

		CLASS_LEVELS.put(CompanionBatClass.VAMPIRE, VAMPIRE_LEVELS);

		CompanionBatClassLevel[] LOOTER_LEVELS = new CompanionBatClassLevel[10];
		LOOTER_LEVELS[0] = new CompanionBatClassLevel(0);
		LOOTER_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.INCREASED_SPEED);
		LOOTER_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.LOOTING);
		LOOTER_LEVELS[3] = new CompanionBatClassLevel(300, CompanionBatAbility.INCREASED_SPEED);
		LOOTER_LEVELS[4] = new CompanionBatClassLevel(500, CompanionBatAbility.LOOTING);
		LOOTER_LEVELS[5] = new CompanionBatClassLevel(750, CompanionBatAbility.INCREASED_SPEED);
		LOOTER_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.LOOTING);
		LOOTER_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.INCREASED_SPEED);
		LOOTER_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.ADVENTURER_AURA);
		LOOTER_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.LOOTING, true);

		CLASS_LEVELS.put(CompanionBatClass.LOOTER, LOOTER_LEVELS);

		CompanionBatClassLevel[] KNIGHT_LEVELS = new CompanionBatClassLevel[10];
		KNIGHT_LEVELS[0] = new CompanionBatClassLevel(0);
		KNIGHT_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.INCREASED_ARMOR);
		KNIGHT_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.BLOCK_ATTACK);
		KNIGHT_LEVELS[3] = new CompanionBatClassLevel(300, CompanionBatAbility.INCREASED_ARMOR);
		KNIGHT_LEVELS[4] = new CompanionBatClassLevel(500, CompanionBatAbility.BLOCK_ATTACK);
		KNIGHT_LEVELS[5] = new CompanionBatClassLevel(750, CompanionBatAbility.INCREASED_ARMOR);
		KNIGHT_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.BLOCK_ATTACK);
		KNIGHT_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.INCREASED_ARMOR);
		KNIGHT_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.BLOCK_ATTACK);
		KNIGHT_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.INCREASED_ARMOR, true);

		CLASS_LEVELS.put(CompanionBatClass.KNIGHT, KNIGHT_LEVELS);

		CompanionBatClassLevel[] ALCHEMIST_LEVELS = new CompanionBatClassLevel[10];
		ALCHEMIST_LEVELS[0] = new CompanionBatClassLevel(0);
		ALCHEMIST_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.EFFECT_POTION);
		ALCHEMIST_LEVELS[2] = new CompanionBatClassLevel(150);
		ALCHEMIST_LEVELS[3] = new CompanionBatClassLevel(300, CompanionBatAbility.EFFECT_POTION);
		ALCHEMIST_LEVELS[4] = new CompanionBatClassLevel(500, CompanionBatAbility.INCREASED_SPEED);
		ALCHEMIST_LEVELS[5] = new CompanionBatClassLevel(750, CompanionBatAbility.EMERGENCY_POTION);
		ALCHEMIST_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.EFFECT_POTION);
		ALCHEMIST_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.INCREASED_SPEED);
		ALCHEMIST_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.EFFECT_POTION);
		ALCHEMIST_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.EMERGENCY_POTION, true);

		CLASS_LEVELS.put(CompanionBatClass.ALCHEMIST, ALCHEMIST_LEVELS);

		CompanionBatClassLevel[] DUELIST_LEVELS = new CompanionBatClassLevel[10];
		DUELIST_LEVELS[0] = new CompanionBatClassLevel(0);
		DUELIST_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.COMBO_ATTACK);
		DUELIST_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.BLOCK_ATTACK);
		DUELIST_LEVELS[3] = new CompanionBatClassLevel(300, CompanionBatAbility.COUNTER_ATTACK);
		DUELIST_LEVELS[4] = new CompanionBatClassLevel(500, CompanionBatAbility.COMBO_ATTACK);
		DUELIST_LEVELS[5] = new CompanionBatClassLevel(750, CompanionBatAbility.COUNTER_ATTACK);
		DUELIST_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.BLOCK_ATTACK);
		DUELIST_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.COMBO_ATTACK);
		DUELIST_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.COUNTER_ATTACK);
		DUELIST_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.BLOCK_ATTACK, true);

		CLASS_LEVELS.put(CompanionBatClass.DUELIST, DUELIST_LEVELS);

		CompanionBatClassLevel[] NINJA_LEVELS = new CompanionBatClassLevel[10];
		NINJA_LEVELS[0] = new CompanionBatClassLevel(0);
		NINJA_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.INCREASED_ATTACK);
		NINJA_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.TELEPORT);
		NINJA_LEVELS[3] = new CompanionBatClassLevel(300, CompanionBatAbility.SNEAK_ATTACK);
		NINJA_LEVELS[4] = new CompanionBatClassLevel(500, CompanionBatAbility.INCREASED_ATTACK);
		NINJA_LEVELS[5] = new CompanionBatClassLevel(750, CompanionBatAbility.TELEPORT);
		NINJA_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.SNEAK_ATTACK);
		NINJA_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.INCREASED_ATTACK);
		NINJA_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.TELEPORT);
		NINJA_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.SNEAK_ATTACK, true);

		CLASS_LEVELS.put(CompanionBatClass.NINJA, NINJA_LEVELS);

		CompanionBatClassLevel[] MUMMY_LEVELS = new CompanionBatClassLevel[10];
		MUMMY_LEVELS[0] = new CompanionBatClassLevel(0);
		MUMMY_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.SLOWNESS);
		MUMMY_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.WEAKNESS);
		MUMMY_LEVELS[3] = new CompanionBatClassLevel(300);
		MUMMY_LEVELS[4] = new CompanionBatClassLevel(500, CompanionBatAbility.SLOWNESS);
		MUMMY_LEVELS[5] = new CompanionBatClassLevel(750, CompanionBatAbility.WITHER);
		MUMMY_LEVELS[6] = new CompanionBatClassLevel(1050);
		MUMMY_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.SLOWNESS);
		MUMMY_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.WITHER);
		MUMMY_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.SLOWNESS, true);

		CLASS_LEVELS.put(CompanionBatClass.MUMMY, MUMMY_LEVELS);

		CompanionBatClassLevel[] DESTROYER_LEVELS = new CompanionBatClassLevel[10];
		DESTROYER_LEVELS[0] = new CompanionBatClassLevel(0);
		DESTROYER_LEVELS[1] = new CompanionBatClassLevel(50);
		DESTROYER_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.DYNAMITE);
		DESTROYER_LEVELS[3] = new CompanionBatClassLevel(300, CompanionBatAbility.INCREASED_ARMOR);
		DESTROYER_LEVELS[4] = new CompanionBatClassLevel(500);
		DESTROYER_LEVELS[5] = new CompanionBatClassLevel(750, CompanionBatAbility.DYNAMITE);
		DESTROYER_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.INCREASED_ARMOR);
		DESTROYER_LEVELS[7] = new CompanionBatClassLevel(1400);
		DESTROYER_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.DYNAMITE);
		DESTROYER_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.INCREASED_ARMOR, true);

		CLASS_LEVELS.put(CompanionBatClass.DESTROYER, DESTROYER_LEVELS);
	}
}
