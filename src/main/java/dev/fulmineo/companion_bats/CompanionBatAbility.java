package dev.fulmineo.companion_bats;

import net.minecraft.util.text.TranslationTextComponent;

public enum CompanionBatAbility {
	// Inferno

	INCREASED_ATTACK, FIRE_RESISTANCE, BURN,

	// Vampire

	LIFESTEAL,

	// Looter

	INCREASED_SPEED, LOOTING, ADVENTURER_AURA,

	// Knight

	BLOCK_ATTACK, INCREASED_ARMOR,

	// Alchemist

	EFFECT_POTION, EMERGENCY_POTION,

	// Duelist

	COMBO_ATTACK, COUNTER_ATTACK,

	// Ninja

	SNEAK_ATTACK, TELEPORT,

	// Mummy

	SLOWNESS, WEAKNESS, WITHER,

	// Destroyer

	DYNAMITE, DESTROYER_AURA,

	// Accessory

	CANNOT_ATTACK, ATTACK_EVERYONE, ATTACK_HOSTILES, ATTACK_PASSIVE, NATURAL_REGENERATION;

	public TranslationTextComponent toTranslatedText() {
		return new TranslationTextComponent("ability.companion_bats."+this.toString());
	}
}
