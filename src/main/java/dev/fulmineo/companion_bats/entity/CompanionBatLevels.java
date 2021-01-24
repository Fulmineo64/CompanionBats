package dev.fulmineo.companion_bats.entity;

import java.util.HashMap;
import java.util.Map;

import dev.fulmineo.companion_bats.item.CompanionBatAbility;
import dev.fulmineo.companion_bats.item.CompanionBatClass;

public class CompanionBatLevels {
	public static final CompanionBatLevel[] LEVELS;

	public static final Map<CompanionBatClass, CompanionBatClassLevel[]> CLASS_LEVELS = new HashMap<>();
	public static final Map<CompanionBatClass, CompanionBatClassLevel> GLOBAL_CLASS_LEVELS = new HashMap<>();

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
		public int abilityLevel;
		CompanionBatClassLevel(int totalExpNeeded, CompanionBatAbility ability, int abilityLevel){
			this.totalExpNeeded = totalExpNeeded;
			this.ability = ability;
			this.abilityLevel = abilityLevel;
		}
	}

	public static float getLevelHealth(int level){
		return LEVELS[level].healthBonus;
	}

	public static float getLevelAttack(int level){
		return LEVELS[level].attackBonus;
	}

	public static float getLevelSpeed(int level){
		return LEVELS[level].speedBonus;
	}

	public static int getLevelByExp(int exp) {
        for (int i=CompanionBatLevels.LEVELS.length-1; i>=0; i--) {
            if (CompanionBatLevels.LEVELS[i].totalExpNeeded <= exp){
                return i;
            }
        }
        return CompanionBatLevels.LEVELS.length-1;
	}

	public static int getClassLevelByExp(CompanionBatClass batClass, int exp) {
		CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(batClass);
		for (int i=classLevels.length-1; i>=0; i--) {
            if (classLevels[i].totalExpNeeded <= exp){
                return i;
            }
		}
        return classLevels.length-1;
	}

	static {
		LEVELS = new CompanionBatLevel[20];
        LEVELS[0] = new CompanionBatLevel(0    , 0F , 0F, 0F);
        LEVELS[1] = new CompanionBatLevel(50   , 1F , 0F, 0F);
        LEVELS[2] = new CompanionBatLevel(150  , 2F , 0F, 0F);
        LEVELS[3] = new CompanionBatLevel(300  , 3F , 0F, 0F);
        LEVELS[4] = new CompanionBatLevel(500  , 3F , 1F, 0F);
        LEVELS[5] = new CompanionBatLevel(750  , 4F , 1F, 0F);
        LEVELS[6] = new CompanionBatLevel(1050 , 4F , 1F, 0.03F);
		LEVELS[7] = new CompanionBatLevel(1400 , 5F , 1F, 0.03F);
        LEVELS[8] = new CompanionBatLevel(1800 , 6F , 1F, 0.03F);
		LEVELS[9] = new CompanionBatLevel(2250 , 6F , 2F, 0.03F);
		LEVELS[10] = new CompanionBatLevel(2750, 7F , 2F, 0.03F);
        LEVELS[11] = new CompanionBatLevel(3300, 7F , 2F, 0.06F);
        LEVELS[12] = new CompanionBatLevel(3900, 8F , 2F, 0.06F);
        LEVELS[13] = new CompanionBatLevel(4550, 9F , 2F, 0.06F);
        LEVELS[14] = new CompanionBatLevel(5250, 9F , 3F, 0.06F);
        LEVELS[15] = new CompanionBatLevel(6000, 10F, 3F, 0.06F);
        LEVELS[16] = new CompanionBatLevel(6800, 10F, 3F, 0.10F);
        LEVELS[17] = new CompanionBatLevel(7650, 11F, 3F, 0.10F);
		LEVELS[18] = new CompanionBatLevel(8550, 12F, 3F, 0.10F);
		LEVELS[19] = new CompanionBatLevel(9550, 12F, 4F, 0.10F);

		CompanionBatClassLevel[] INFERNO_LEVELS = new CompanionBatClassLevel[10];
		INFERNO_LEVELS[0] = new CompanionBatClassLevel(0   , null, 0);
        INFERNO_LEVELS[1] = new CompanionBatClassLevel(50  , CompanionBatAbility.INCREASED_DAMAGE, 1);
        INFERNO_LEVELS[2] = new CompanionBatClassLevel(150 , CompanionBatAbility.BURN, 1);
        INFERNO_LEVELS[3] = new CompanionBatClassLevel(300 , CompanionBatAbility.INCREASED_DAMAGE, 2);
        INFERNO_LEVELS[4] = new CompanionBatClassLevel(500 , CompanionBatAbility.BURN, 2);
        INFERNO_LEVELS[5] = new CompanionBatClassLevel(750 , CompanionBatAbility.INCREASED_DAMAGE, 3);
        INFERNO_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.BURN, 3);
		INFERNO_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.INCREASED_DAMAGE, 4);
        INFERNO_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.BURN, 4);
		INFERNO_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.FIRE_RESISTANCE, 1);

		CLASS_LEVELS.put(CompanionBatClass.INFERNO, INFERNO_LEVELS);
		GLOBAL_CLASS_LEVELS.put(CompanionBatClass.INFERNO, new CompanionBatClassLevel(2250, CompanionBatAbility.INCREASED_DAMAGE, 1));

		CompanionBatClassLevel[] VAMPIRE_LEVELS = new CompanionBatClassLevel[10];
		VAMPIRE_LEVELS[0] = new CompanionBatClassLevel(0   , null, 0);
        VAMPIRE_LEVELS[1] = new CompanionBatClassLevel(50  , null, 0);
        VAMPIRE_LEVELS[2] = new CompanionBatClassLevel(150 , CompanionBatAbility.INCREASED_ARMOR, 1);
        VAMPIRE_LEVELS[3] = new CompanionBatClassLevel(300 , CompanionBatAbility.LIFESTEAL, 1);
        VAMPIRE_LEVELS[4] = new CompanionBatClassLevel(500 , null, 0);
        VAMPIRE_LEVELS[5] = new CompanionBatClassLevel(750 , CompanionBatAbility.INCREASED_ARMOR, 2);
        VAMPIRE_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.LIFESTEAL, 2);
		VAMPIRE_LEVELS[7] = new CompanionBatClassLevel(1400, null, 0);
        VAMPIRE_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.INCREASED_ARMOR, 3);
		VAMPIRE_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.LIFESTEAL, 3);

		CLASS_LEVELS.put(CompanionBatClass.VAMPIRE, VAMPIRE_LEVELS);
		GLOBAL_CLASS_LEVELS.put(CompanionBatClass.VAMPIRE, new CompanionBatClassLevel(2250, CompanionBatAbility.LIFESTEAL, 1));

		CompanionBatClassLevel[] FORAGER_LEVELS = new CompanionBatClassLevel[10];
		FORAGER_LEVELS[0] = new CompanionBatClassLevel(0   , CompanionBatAbility.CANNOT_ATTACK, 1);
        FORAGER_LEVELS[1] = new CompanionBatClassLevel(50  , CompanionBatAbility.INCREASED_SPEED, 1);
        FORAGER_LEVELS[2] = new CompanionBatClassLevel(150 , null, 0);
        FORAGER_LEVELS[3] = new CompanionBatClassLevel(300 , CompanionBatAbility.INCREASED_SPEED, 2);
        FORAGER_LEVELS[4] = new CompanionBatClassLevel(500 , null, 0);
        FORAGER_LEVELS[5] = new CompanionBatClassLevel(750 , CompanionBatAbility.INCREASED_SPEED, 3);
        FORAGER_LEVELS[6] = new CompanionBatClassLevel(1050, null, 0);
		FORAGER_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.INCREASED_SPEED, 4);
        FORAGER_LEVELS[8] = new CompanionBatClassLevel(1800, null, 0);
		FORAGER_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.INCREASED_SPEED, 5);

		CLASS_LEVELS.put(CompanionBatClass.FORAGER, FORAGER_LEVELS);
		GLOBAL_CLASS_LEVELS.put(CompanionBatClass.FORAGER, new CompanionBatClassLevel(2250, CompanionBatAbility.INCREASED_SPEED, 1));

		CompanionBatClassLevel[] KNIGHT_LEVELS = new CompanionBatClassLevel[10];
		KNIGHT_LEVELS[0] = new CompanionBatClassLevel(0   , null, 0);
        KNIGHT_LEVELS[1] = new CompanionBatClassLevel(50  , CompanionBatAbility.INCREASED_ARMOR, 1);
        KNIGHT_LEVELS[2] = new CompanionBatClassLevel(150 , CompanionBatAbility.BLOCK_ATTACK, 1);
        KNIGHT_LEVELS[3] = new CompanionBatClassLevel(300 , null, 0);
        KNIGHT_LEVELS[4] = new CompanionBatClassLevel(500 , CompanionBatAbility.INCREASED_ARMOR, 2);
        KNIGHT_LEVELS[5] = new CompanionBatClassLevel(750 , CompanionBatAbility.BLOCK_ATTACK, 2);
        KNIGHT_LEVELS[6] = new CompanionBatClassLevel(1050, null, 0);
		KNIGHT_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.INCREASED_ARMOR, 3);
        KNIGHT_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.BLOCK_ATTACK, 3);
		KNIGHT_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.INCREASED_ARMOR, 4);

		CLASS_LEVELS.put(CompanionBatClass.KNIGHT, KNIGHT_LEVELS);
		GLOBAL_CLASS_LEVELS.put(CompanionBatClass.KNIGHT, new CompanionBatClassLevel(2250, CompanionBatAbility.INCREASED_ARMOR, 1));

		CompanionBatClassLevel[] ALCHEMIST_LEVELS = new CompanionBatClassLevel[10];
		ALCHEMIST_LEVELS[0] = new CompanionBatClassLevel(0   , CompanionBatAbility.CANNOT_ATTACK, 1);
        ALCHEMIST_LEVELS[1] = new CompanionBatClassLevel(50  , CompanionBatAbility.EFFECT_POTION, 1);
        ALCHEMIST_LEVELS[2] = new CompanionBatClassLevel(150 , null, 0);
        ALCHEMIST_LEVELS[3] = new CompanionBatClassLevel(300 , CompanionBatAbility.EFFECT_POTION, 2);
        ALCHEMIST_LEVELS[4] = new CompanionBatClassLevel(500 , null, 0);
        ALCHEMIST_LEVELS[5] = new CompanionBatClassLevel(750 , CompanionBatAbility.EMERGENCY_POTION, 1);
        ALCHEMIST_LEVELS[6] = new CompanionBatClassLevel(1050, null, 0);
		ALCHEMIST_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.EFFECT_POTION, 3);
        ALCHEMIST_LEVELS[8] = new CompanionBatClassLevel(1800, null, 0);
		ALCHEMIST_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.EFFECT_POTION, 4);

		CLASS_LEVELS.put(CompanionBatClass.ALCHEMIST, ALCHEMIST_LEVELS);
		GLOBAL_CLASS_LEVELS.put(CompanionBatClass.ALCHEMIST, new CompanionBatClassLevel(2250, CompanionBatAbility.EMERGENCY_POTION, 1));

		CompanionBatClassLevel[] DUELIST_LEVELS = new CompanionBatClassLevel[10];
		DUELIST_LEVELS[0] = new CompanionBatClassLevel(0   , null, 0);
        DUELIST_LEVELS[1] = new CompanionBatClassLevel(50  , CompanionBatAbility.COMBO_ATTACK, 1);
        DUELIST_LEVELS[2] = new CompanionBatClassLevel(150 , CompanionBatAbility.BLOCK_ATTACK, 1);
        DUELIST_LEVELS[3] = new CompanionBatClassLevel(300 , CompanionBatAbility.COUNTER_ATTACK, 1);
        DUELIST_LEVELS[4] = new CompanionBatClassLevel(500 , CompanionBatAbility.COMBO_ATTACK, 2);
        DUELIST_LEVELS[5] = new CompanionBatClassLevel(750 , CompanionBatAbility.BLOCK_ATTACK, 2);
        DUELIST_LEVELS[6] = new CompanionBatClassLevel(1050, CompanionBatAbility.COUNTER_ATTACK, 2);
		DUELIST_LEVELS[7] = new CompanionBatClassLevel(1400, CompanionBatAbility.COMBO_ATTACK, 3);
        DUELIST_LEVELS[8] = new CompanionBatClassLevel(1800, CompanionBatAbility.BLOCK_ATTACK, 3);
		DUELIST_LEVELS[9] = new CompanionBatClassLevel(2250, CompanionBatAbility.COUNTER_ATTACK, 3);

		CLASS_LEVELS.put(CompanionBatClass.DUELIST, DUELIST_LEVELS);
		GLOBAL_CLASS_LEVELS.put(CompanionBatClass.DUELIST, new CompanionBatClassLevel(2250, CompanionBatAbility.BLOCK_ATTACK, 1));
	}
}
