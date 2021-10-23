package dev.fulmineo.companion_bats.data;

import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import draylar.staticcontent.api.ContentData;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CompanionBatAccessoryData implements ContentData {
	public String name;
	public String ability;
	public int abilityLevel = 1;

	@Override
	public void register(Identifier fileID) {
		CompanionBatAbility ability = CompanionBatAbility.valueOf(this.ability);
		if (ability == null) {
			CompanionBats.info("The ability "+this.ability+ " required by the accessory "+ this.name + " was not found");
		} else {
			Registry.register(Registry.ITEM, new Identifier(CompanionBats.MOD_ID, this.name), new CompanionBatAccessoryItem(this.name, ability, this.abilityLevel, new FabricItemSettings().group(CompanionBats.GROUP).maxCount(1)));
		}
	}
}

