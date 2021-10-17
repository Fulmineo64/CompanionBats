package dev.fulmineo.companion_bats.config;

import dev.fulmineo.companion_bats.CompanionBats;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = CompanionBats.MOD_ID)
public class CompanionBatsConfig implements ConfigData {

	public float baseHealth = 6.0F;
	public float baseAttack = 2.0f;
	public float baseSpeed = 0.35f;

	public int expGain = 1;
	public int experiencePieGain = 100;

	public int roostStartTicks = 200;
	public int regenTicks = 600;
	public int emergencyPotionTicks = 4800;
	public int effectPotionTicks = 1600;
	public int comboAttackResetTicks = 300;
	public int teleportTicks = 7;
	public int rangedAttackTicks = 100;
	public int statusEffectTicks = 320;

    public static CompanionBatsConfig getInstance() {
        return AutoConfig.getConfigHolder(CompanionBatsConfig.class).getConfig();
    }

}
