package dev.fulmineo.companion_bats.item;

import java.util.List;

import net.minecraft.item.Item;

public class CompanionBatGemItem extends Item {
	private List<CompanionBatAbility> abilities;
	public CompanionBatGemItem(List<CompanionBatAbility> abilities, Settings settings) {
        super(settings);
		this.abilities = abilities;
	}

	public boolean hasAbility(CompanionBatAbility ability){
		return this.abilities.contains(ability);
	}
}
