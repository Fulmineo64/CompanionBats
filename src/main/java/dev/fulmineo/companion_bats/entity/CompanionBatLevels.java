package dev.fulmineo.companion_bats.entity;

import java.util.HashMap;
import java.util.Map;

import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBatClass;

public class CompanionBatLevels {
	public static final CompanionBatCombatLevel[] LEVELS;

	public static final Map<CompanionBatClass, CompanionBatClassLevel[]> CLASS_LEVELS = new HashMap<>();

	public static class CompanionBatCombatLevel {
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
	}

	public static class CompanionBatClassLevel {
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
	}


	public static int getClassLevelByExp(CompanionBatClass batClass, int exp) {
		CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(batClass);
		for (int i = classLevels.length - 1; i >= 0; i--) {
			if (classLevels[i].totalExp <= exp) {
				return i;
			}
		}
		return classLevels.length - 1;
	}

	static {
		LEVELS = new CompanionBatCombatLevel[25];
		LEVELS[0] = new CompanionBatCombatLevel(0, 0F, 0F, 0F);
		LEVELS[1] = new CompanionBatCombatLevel(50, 1F, 0F, 0F); 			// Health
		LEVELS[2] = new CompanionBatCombatLevel(150, 2F, 0F, 0F); 		// Health
		LEVELS[3] = new CompanionBatCombatLevel(300, 2F, 0F, 0.03F); 		// Speed
		LEVELS[4] = new CompanionBatCombatLevel(500, 2F, 1F, 0.03F); 		// Attack
		LEVELS[5] = new CompanionBatCombatLevel(750, 3F, 1F, 0.03F); 		// Health
		LEVELS[6] = new CompanionBatCombatLevel(1050, 4F, 1F, 0.03F); 	// Health
		LEVELS[7] = new CompanionBatCombatLevel(1400, 4F, 1F, 0.06F); 	// Speed
		LEVELS[8] = new CompanionBatCombatLevel(1800, 4F, 2F, 0.06F); 	// Attack
		LEVELS[9] = new CompanionBatCombatLevel(2250, 5F, 2F, 0.06F);	 	// Health
		LEVELS[10] = new CompanionBatCombatLevel(2750, 6F, 2F, 0.06F); 	// Health
		LEVELS[11] = new CompanionBatCombatLevel(3300, 6F, 2F, 0.09F); 	// Speed
		LEVELS[12] = new CompanionBatCombatLevel(3900, 6F, 3F, 0.09F); 	// Attack
		LEVELS[13] = new CompanionBatCombatLevel(4550, 7F, 3F, 0.09F); 	// Health
		LEVELS[14] = new CompanionBatCombatLevel(5250, 8F, 3F, 0.09F); 	// Health
		LEVELS[15] = new CompanionBatCombatLevel(6000, 8F, 3F, 0.12F); 	// Speed
		LEVELS[16] = new CompanionBatCombatLevel(6800, 9F, 3F, 0.12F); 	// Health
		LEVELS[17] = new CompanionBatCombatLevel(7650, 10F, 3F, 0.12F); 	// Health
		LEVELS[18] = new CompanionBatCombatLevel(8550, 10F, 3F, 0.15F); 	// Speed
		LEVELS[19] = new CompanionBatCombatLevel(9550, 10F, 4F, 0.15F); 	// Attack
		LEVELS[20] = new CompanionBatCombatLevel(10600, 11F, 4F, 0.15F); 	// Health
		LEVELS[21] = new CompanionBatCombatLevel(11700, 12F, 4F, 0.15F); 	// Health
		LEVELS[22] = new CompanionBatCombatLevel(13200, 12F, 4F, 0.20F); 	// Speed x 1.6
		LEVELS[23] = new CompanionBatCombatLevel(14950, 12F, 5F, 0.20F); 	// Attack
		LEVELS[24] = new CompanionBatCombatLevel(16950, 14F, 5F, 0.20F); 	// Health x 2

		CompanionBatClassLevel[] INFERNO_LEVELS = new CompanionBatClassLevel[10];
		INFERNO_LEVELS[0] = new CompanionBatClassLevel(0);
		INFERNO_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.INCREASED_ATTACK);
		INFERNO_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.BURN);
		INFERNO_LEVELS[3] = new CompanionBatClassLevel(275, CompanionBatAbility.INCREASED_ATTACK);
		INFERNO_LEVELS[4] = new CompanionBatClassLevel(425, CompanionBatAbility.BURN);
		INFERNO_LEVELS[5] = new CompanionBatClassLevel(600, CompanionBatAbility.INCREASED_ATTACK);
		INFERNO_LEVELS[6] = new CompanionBatClassLevel(800, CompanionBatAbility.BURN);
		INFERNO_LEVELS[7] = new CompanionBatClassLevel(1025, CompanionBatAbility.INCREASED_ATTACK);
		INFERNO_LEVELS[8] = new CompanionBatClassLevel(1275, CompanionBatAbility.FIRE_RESISTANCE);
		INFERNO_LEVELS[9] = new CompanionBatClassLevel(1600, CompanionBatAbility.INCREASED_ATTACK, true);

		CLASS_LEVELS.put(CompanionBatClass.INFERNO, INFERNO_LEVELS);

		CompanionBatClassLevel[] VAMPIRE_LEVELS = new CompanionBatClassLevel[10];
		VAMPIRE_LEVELS[0] = new CompanionBatClassLevel(0);
		VAMPIRE_LEVELS[1] = new CompanionBatClassLevel(50);
		VAMPIRE_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.INCREASED_ARMOR);
		VAMPIRE_LEVELS[3] = new CompanionBatClassLevel(275, CompanionBatAbility.LIFESTEAL);
		VAMPIRE_LEVELS[4] = new CompanionBatClassLevel(425);
		VAMPIRE_LEVELS[5] = new CompanionBatClassLevel(600, CompanionBatAbility.INCREASED_ARMOR);
		VAMPIRE_LEVELS[6] = new CompanionBatClassLevel(800, CompanionBatAbility.LIFESTEAL);
		VAMPIRE_LEVELS[7] = new CompanionBatClassLevel(1025);
		VAMPIRE_LEVELS[8] = new CompanionBatClassLevel(1275, CompanionBatAbility.INCREASED_ARMOR);
		VAMPIRE_LEVELS[9] = new CompanionBatClassLevel(1600, CompanionBatAbility.LIFESTEAL, true);

		CLASS_LEVELS.put(CompanionBatClass.VAMPIRE, VAMPIRE_LEVELS);

		CompanionBatClassLevel[] LOOTER_LEVELS = new CompanionBatClassLevel[10];
		LOOTER_LEVELS[0] = new CompanionBatClassLevel(0);
		LOOTER_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.INCREASED_SPEED);
		LOOTER_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.LOOTING);
		LOOTER_LEVELS[3] = new CompanionBatClassLevel(275, CompanionBatAbility.INCREASED_SPEED);
		LOOTER_LEVELS[4] = new CompanionBatClassLevel(425, CompanionBatAbility.LOOTING);
		LOOTER_LEVELS[5] = new CompanionBatClassLevel(600, CompanionBatAbility.INCREASED_SPEED);
		LOOTER_LEVELS[6] = new CompanionBatClassLevel(800, CompanionBatAbility.LOOTING);
		LOOTER_LEVELS[7] = new CompanionBatClassLevel(1025, CompanionBatAbility.INCREASED_SPEED);
		LOOTER_LEVELS[8] = new CompanionBatClassLevel(1275, CompanionBatAbility.ADVENTURER_AURA);
		LOOTER_LEVELS[9] = new CompanionBatClassLevel(1600, CompanionBatAbility.LOOTING, true);

		CLASS_LEVELS.put(CompanionBatClass.LOOTER, LOOTER_LEVELS);

		CompanionBatClassLevel[] KNIGHT_LEVELS = new CompanionBatClassLevel[10];
		KNIGHT_LEVELS[0] = new CompanionBatClassLevel(0);
		KNIGHT_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.INCREASED_ARMOR);
		KNIGHT_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.BLOCK_ATTACK);
		KNIGHT_LEVELS[3] = new CompanionBatClassLevel(275, CompanionBatAbility.INCREASED_ARMOR);
		KNIGHT_LEVELS[4] = new CompanionBatClassLevel(425, CompanionBatAbility.BLOCK_ATTACK);
		KNIGHT_LEVELS[5] = new CompanionBatClassLevel(600, CompanionBatAbility.INCREASED_ARMOR);
		KNIGHT_LEVELS[6] = new CompanionBatClassLevel(800, CompanionBatAbility.BLOCK_ATTACK);
		KNIGHT_LEVELS[7] = new CompanionBatClassLevel(1025, CompanionBatAbility.INCREASED_ARMOR);
		KNIGHT_LEVELS[8] = new CompanionBatClassLevel(1275, CompanionBatAbility.BLOCK_ATTACK);
		KNIGHT_LEVELS[9] = new CompanionBatClassLevel(1600, CompanionBatAbility.INCREASED_ARMOR, true);

		CLASS_LEVELS.put(CompanionBatClass.KNIGHT, KNIGHT_LEVELS);

		CompanionBatClassLevel[] ALCHEMIST_LEVELS = new CompanionBatClassLevel[10];
		ALCHEMIST_LEVELS[0] = new CompanionBatClassLevel(0);
		ALCHEMIST_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.EFFECT_POTION);
		ALCHEMIST_LEVELS[2] = new CompanionBatClassLevel(150);
		ALCHEMIST_LEVELS[3] = new CompanionBatClassLevel(275, CompanionBatAbility.EFFECT_POTION);
		ALCHEMIST_LEVELS[4] = new CompanionBatClassLevel(425, CompanionBatAbility.INCREASED_SPEED);
		ALCHEMIST_LEVELS[5] = new CompanionBatClassLevel(600, CompanionBatAbility.EMERGENCY_POTION);
		ALCHEMIST_LEVELS[6] = new CompanionBatClassLevel(800, CompanionBatAbility.EFFECT_POTION);
		ALCHEMIST_LEVELS[7] = new CompanionBatClassLevel(1025, CompanionBatAbility.INCREASED_SPEED);
		ALCHEMIST_LEVELS[8] = new CompanionBatClassLevel(1275, CompanionBatAbility.EFFECT_POTION);
		ALCHEMIST_LEVELS[9] = new CompanionBatClassLevel(1600, CompanionBatAbility.EMERGENCY_POTION, true);

		CLASS_LEVELS.put(CompanionBatClass.ALCHEMIST, ALCHEMIST_LEVELS);

		CompanionBatClassLevel[] DUELIST_LEVELS = new CompanionBatClassLevel[10];
		DUELIST_LEVELS[0] = new CompanionBatClassLevel(0);
		DUELIST_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.COMBO_ATTACK);
		DUELIST_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.BLOCK_ATTACK);
		DUELIST_LEVELS[3] = new CompanionBatClassLevel(275, CompanionBatAbility.COUNTER_ATTACK);
		DUELIST_LEVELS[4] = new CompanionBatClassLevel(425, CompanionBatAbility.COMBO_ATTACK);
		DUELIST_LEVELS[5] = new CompanionBatClassLevel(600, CompanionBatAbility.COUNTER_ATTACK);
		DUELIST_LEVELS[6] = new CompanionBatClassLevel(800, CompanionBatAbility.BLOCK_ATTACK);
		DUELIST_LEVELS[7] = new CompanionBatClassLevel(1025, CompanionBatAbility.COMBO_ATTACK);
		DUELIST_LEVELS[8] = new CompanionBatClassLevel(1275, CompanionBatAbility.COUNTER_ATTACK);
		DUELIST_LEVELS[9] = new CompanionBatClassLevel(1600, CompanionBatAbility.BLOCK_ATTACK, true);

		CLASS_LEVELS.put(CompanionBatClass.DUELIST, DUELIST_LEVELS);

		CompanionBatClassLevel[] NINJA_LEVELS = new CompanionBatClassLevel[10];
		NINJA_LEVELS[0] = new CompanionBatClassLevel(0);
		NINJA_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.INCREASED_ATTACK);
		NINJA_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.TELEPORT);
		NINJA_LEVELS[3] = new CompanionBatClassLevel(275, CompanionBatAbility.SNEAK_ATTACK);
		NINJA_LEVELS[4] = new CompanionBatClassLevel(425, CompanionBatAbility.INCREASED_ATTACK);
		NINJA_LEVELS[5] = new CompanionBatClassLevel(600, CompanionBatAbility.TELEPORT);
		NINJA_LEVELS[6] = new CompanionBatClassLevel(800, CompanionBatAbility.SNEAK_ATTACK);
		NINJA_LEVELS[7] = new CompanionBatClassLevel(1025, CompanionBatAbility.INCREASED_ATTACK);
		NINJA_LEVELS[8] = new CompanionBatClassLevel(1275, CompanionBatAbility.TELEPORT);
		NINJA_LEVELS[9] = new CompanionBatClassLevel(1600, CompanionBatAbility.SNEAK_ATTACK, true);

		CLASS_LEVELS.put(CompanionBatClass.NINJA, NINJA_LEVELS);

		CompanionBatClassLevel[] MUMMY_LEVELS = new CompanionBatClassLevel[10];
		MUMMY_LEVELS[0] = new CompanionBatClassLevel(0);
		MUMMY_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.SLOWNESS);
		MUMMY_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.WEAKNESS);
		MUMMY_LEVELS[3] = new CompanionBatClassLevel(275);
		MUMMY_LEVELS[4] = new CompanionBatClassLevel(425, CompanionBatAbility.SLOWNESS);
		MUMMY_LEVELS[5] = new CompanionBatClassLevel(600, CompanionBatAbility.WITHER);
		MUMMY_LEVELS[6] = new CompanionBatClassLevel(800);
		MUMMY_LEVELS[7] = new CompanionBatClassLevel(1025, CompanionBatAbility.SLOWNESS);
		MUMMY_LEVELS[8] = new CompanionBatClassLevel(1275, CompanionBatAbility.WITHER);
		MUMMY_LEVELS[9] = new CompanionBatClassLevel(1600, CompanionBatAbility.SLOWNESS, true);

		CLASS_LEVELS.put(CompanionBatClass.MUMMY, MUMMY_LEVELS);

		CompanionBatClassLevel[] DESTROYER_LEVELS = new CompanionBatClassLevel[10];
		DESTROYER_LEVELS[0] = new CompanionBatClassLevel(0);
		DESTROYER_LEVELS[1] = new CompanionBatClassLevel(50);
		DESTROYER_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.DYNAMITE);
		DESTROYER_LEVELS[3] = new CompanionBatClassLevel(275, CompanionBatAbility.INCREASED_ARMOR);
		DESTROYER_LEVELS[4] = new CompanionBatClassLevel(425);
		DESTROYER_LEVELS[5] = new CompanionBatClassLevel(600, CompanionBatAbility.DYNAMITE);
		DESTROYER_LEVELS[6] = new CompanionBatClassLevel(800, CompanionBatAbility.INCREASED_ARMOR);
		DESTROYER_LEVELS[7] = new CompanionBatClassLevel(1025);
		DESTROYER_LEVELS[8] = new CompanionBatClassLevel(1275, CompanionBatAbility.DYNAMITE);
		DESTROYER_LEVELS[9] = new CompanionBatClassLevel(1600, CompanionBatAbility.INCREASED_ARMOR, true);

		CLASS_LEVELS.put(CompanionBatClass.DESTROYER, DESTROYER_LEVELS);

		CompanionBatClassLevel[] MERLING_LEVELS = new CompanionBatClassLevel[10];
		MERLING_LEVELS[0] = new CompanionBatClassLevel(0);
		MERLING_LEVELS[1] = new CompanionBatClassLevel(50, CompanionBatAbility.SWIM);
		MERLING_LEVELS[2] = new CompanionBatClassLevel(150, CompanionBatAbility.SLOWNESS);
		MERLING_LEVELS[3] = new CompanionBatClassLevel(275, CompanionBatAbility.INCREASED_ATTACK);
		MERLING_LEVELS[4] = new CompanionBatClassLevel(425, CompanionBatAbility.TRIDENT);
		MERLING_LEVELS[5] = new CompanionBatClassLevel(600, CompanionBatAbility.MERLING_AURA);
		MERLING_LEVELS[6] = new CompanionBatClassLevel(800, CompanionBatAbility.SLOWNESS);
		MERLING_LEVELS[7] = new CompanionBatClassLevel(1025, CompanionBatAbility.INCREASED_ATTACK);
		MERLING_LEVELS[8] = new CompanionBatClassLevel(1275, CompanionBatAbility.SLOWNESS);
		MERLING_LEVELS[9] = new CompanionBatClassLevel(1600, CompanionBatAbility.INCREASED_ATTACK, true);

		CLASS_LEVELS.put(CompanionBatClass.MERLING, MERLING_LEVELS);
	}
}
