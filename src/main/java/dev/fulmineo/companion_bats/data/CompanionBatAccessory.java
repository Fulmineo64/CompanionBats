package dev.fulmineo.companion_bats.data;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import draylar.staticcontent.api.ContentData;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CompanionBatAccessory implements ContentData {
	public String name;
	public CompanionBatAbility ability;

	@Override
	public void register(Identifier fileID) {
		CompanionBatAccessoryItem item = new CompanionBatAccessoryItem(this.name, this.ability, new FabricItemSettings().maxCount(1));
		Registry.register(Registries.ITEM, new Identifier(CompanionBats.MOD_ID, this.name), item);
		ItemGroupEvents.modifyEntriesEvent(CompanionBats.ITEM_GROUP).register(content -> {
			content.add(item);
		});
	}
}

