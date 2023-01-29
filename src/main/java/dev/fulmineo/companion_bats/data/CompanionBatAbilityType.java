package dev.fulmineo.companion_bats.data;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public enum CompanionBatAbilityType {
	// Inferno

	INCREASED_ATTACK, BURN, FLAME_EATER,

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

	CANNOT_ATTACK, ATTACK_EVERYONE, ATTACK_HOSTILES, ATTACK_PASSIVE, NATURAL_REGENERATION, MAGIC_PROTECTION, ATTRACT_FLAMES;

	public MutableText toTranslatedText() {
		return Text.translatable("ability.companion_bats."+this.toString());
	}
}
