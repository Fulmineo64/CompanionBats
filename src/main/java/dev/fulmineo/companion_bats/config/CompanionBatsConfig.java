package dev.fulmineo.companion_bats.config;

import dev.fulmineo.companion_bats.CompanionBats;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = CompanionBats.MOD_ID)
public class CompanionBatsConfig implements ConfigData {

	// Bat base stats

	public float baseHealth = 6.0F;
	public float baseAttack = 2.0f;
	public float baseSpeed = 0.35f;

	// Armor base stats

	public int armorProtectionAmount = 10;

	// Experience values

	public int expGain = 1;
	public int experiencePieGain = 100;

	// Cooldowns

	public int roostStartTicks = 200;
	public int regenTicks = 600;
	public int emergencyPotionTicks = 4800;
	public int effectPotionTicks = 1600;
	public int comboAttackResetTicks = 300;
	public int teleportTicks = 7;
	public int statusEffectTicks = 320;
	public int dynamiteTicks = 100;
	public int tridentTicks = 60;

	// Ability level multipliers

	public int blockAttackMultiplier = 10;
	public int burnMultiplier = 3;
	public int increasedArmorMultiplier = 2;
	public int increasedAttackMultiplier = 10;
	public int increasedSpeedMultiplier = 15;
	public int lifestealMultiplier = 10;
	public int teleportMultiplier = 25;

    public static CompanionBatsConfig getInstance() {
        return AutoConfig.getConfigHolder(CompanionBatsConfig.class).getConfig();
    }

}
