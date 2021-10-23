package dev.fulmineo.companion_bats.data;

import dev.fulmineo.companion_bats.CompanionBatClass;
import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.init.CompanionBatLootTableInit;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import draylar.staticcontent.api.ContentData;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CompanionBatArmorData implements ContentData {
	public String name;
	public String batClass;
	public String chest;
	public int rolls = 1;
	public float chance = 1;

	@Override
	public void register(Identifier fileID) {
		CompanionBatClass batClass = CompanionBatClass.valueOf(this.batClass);
		if (batClass == null) {
			CompanionBats.info("The class "+this.batClass+ " required by the armor "+ this.name + " was not found");
		} else {
			CompanionBatArmorItem item = new CompanionBatArmorItem(this.name, batClass, new FabricItemSettings().group(CompanionBats.GROUP).maxCount(1));
			Registry.register(Registry.ITEM, new Identifier(CompanionBats.MOD_ID, this.name), item);
			if (this.chest != null) {
				CompanionBatLootTableInit.toRegister.put(new Identifier(this.chest), FabricLootPoolBuilder.builder().rolls(BinomialLootNumberProvider.create(this.rolls, this.chance)).with(ItemEntry.builder(item)));
			}
		}
	}
}

