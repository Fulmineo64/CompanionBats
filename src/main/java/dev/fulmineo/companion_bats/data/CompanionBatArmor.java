package dev.fulmineo.companion_bats.data;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.init.CompanionBatLootTableInit;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import draylar.staticcontent.api.ContentData;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CompanionBatArmor implements ContentData {
	public String name;
	public String className;
	public String chest;
	public int rolls = 1;
	public float chance = 1;

	@Override
	public void register(Identifier fileID) {
		CompanionBatArmorItem item = new CompanionBatArmorItem(this.name, this.className, new FabricItemSettings().maxCount(1));
		Registry.register(Registries.ITEM, new Identifier(CompanionBats.MOD_ID, this.name), item);
		if (this.chest != null) {
			CompanionBatLootTableInit.toRegister.put(new Identifier(this.chest), LootPool.builder().rolls(BinomialLootNumberProvider.create(this.rolls, this.chance)).with(ItemEntry.builder(item)));
		}
		ItemGroupEvents.modifyEntriesEvent(CompanionBats.ITEM_GROUP).register(content -> {
			content.add(item);
		});
	}
}

