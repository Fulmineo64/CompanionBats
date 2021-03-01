package dev.fulmineo.companion_bats.item;

import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class CompanionBatAccessoryItem extends Item {
	private CompanionBatAbility ability;
	private Integer abilityLevel;
	private String entityTexture;

	public CompanionBatAccessoryItem(String identifier, CompanionBatAbility ability, Integer abilityLevel, Settings settings) {
        super(settings);
		this.ability = ability;
		this.abilityLevel = abilityLevel;
		this.entityTexture = "textures/entity/bat/accessory/" + identifier + ".png";
    }

	@Environment(EnvType.CLIENT)
    public Identifier getEntityTexture() {
       return new Identifier(CompanionBats.MOD_ID, this.entityTexture);
	}

	public CompanionBatAbility getAbility() {
		return this.ability;
	}

	public Integer getAbilityLevel() {
		return this.abilityLevel;
	}
}
