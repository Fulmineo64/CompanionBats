package dev.fulmineo.companion_bats;

import net.minecraft.text.TranslatableText;

public enum CompanionBatAbility {
	// Inferno

	INCREASED_ATTACK, BURN,

	// Vampire

	LIFESTEAL,

	// Looter

	INCREASED_SPEED, LOOTING,

	// Knight

	BLOCK_ATTACK, INCREASED_ARMOR,

	// Alchemist

	EFFECT_POTION, EMERGENCY_POTION,

	// Duelist

	COMBO_ATTACK, COUNTER_ATTACK,

	// Ninja

	SNEAK_ATTACK, TELEPORT,

	// Mummy

	// Destroyer

	DYNAMITE,

	// Merling

	SWIM, TRIDENT,

	// Accessory

	CANNOT_ATTACK, ATTACK_EVERYONE, ATTACK_HOSTILES, ATTACK_PASSIVE, NATURAL_REGENERATION, MAGIC_PROTECTION;

	public TranslatableText toTranslatedText() {
		return new TranslatableText("ability.companion_bats."+this.toString());
	}
}
