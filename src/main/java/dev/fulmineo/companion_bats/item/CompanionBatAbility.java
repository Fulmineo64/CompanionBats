package dev.fulmineo.companion_bats.item;

public enum CompanionBatAbility {
	// Inferno

	INCREASED_DAMAGE,
	FIRE_RESISTANCE,
	BURN,

	// Vampire

	LIFESTEAL,

	// Forager

	CANNOT_ATTACK,
	INCREASED_SPEED,

	// Knight

	BLOCK_ATTACK,
	INCREASED_ARMOR,

	// Alchemist

	EFFECT_POTION,
	EMERGENCY_POTION,

	// Duelist

	COMBO_ATTACK,
	COUNTER_ATTACK;

	public int getValue(int level){
		switch (this) {
			case BLOCK_ATTACK:
			case INCREASED_DAMAGE:
			case INCREASED_SPEED:
			case LIFESTEAL: {
				return 10 * level;
			}
			default: {
				return level;
			}
		}
	}
}
