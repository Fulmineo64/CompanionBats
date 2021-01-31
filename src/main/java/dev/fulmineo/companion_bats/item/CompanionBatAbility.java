package dev.fulmineo.companion_bats.item;

public enum CompanionBatAbility {
	// Inferno

	INCREASED_ATTACK,
	FIRE_RESISTANCE,
	BURN,

	// Vampire

	LIFESTEAL,

	// Looter

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
	COUNTER_ATTACK,

	// Ninja

	SNEAK_ATTACK,
	TELEPORT;

	public int getValue(int level){
		switch (this) {
			case BLOCK_ATTACK:
			case INCREASED_ATTACK:
			case INCREASED_SPEED:
			case LIFESTEAL: {
				return 10 * level;
			}
			case TELEPORT: {
				int val = 5 * level;
				return val * val;
			}
			default: {
				return level;
			}
		}
	}
}
