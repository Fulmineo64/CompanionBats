package dev.fulmineo.companion_bats.init;

import java.util.HashMap;
import java.util.Map;

import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

public class CompanionBatLootTableInit {
	public static Map<Identifier, LootPool.Builder> toRegister = new HashMap<>();

	private static final Identifier WOODLAND_MANSION_ID = new Identifier("minecraft", "chests/woodland_mansion");
	private static final Identifier PILLAGER_OUTPOST_ID = new Identifier("minecraft", "chests/pillager_outpost");
	private static final Identifier SIMPLE_DUNGEON_ID = new Identifier("minecraft", "chests/simple_dungeon");
	private static final Identifier ABANDONED_MINESHAFT_ID = new Identifier("minecraft", "chests/abandoned_mineshaft");
	private static final Identifier SHIPWRECK_TREASURE_ID = new Identifier("minecraft", "chests/shipwreck_treasure");
	private static final Identifier STRONGHOLD_CORRIDOR_ID = new Identifier("minecraft", "chests/stronghold_corridor");

	public static void init(){
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			// Armors
			LootPool.Builder poolBuilder = toRegister.get(id);
			if (source.isBuiltin() && poolBuilder != null) {
				tableBuilder.pool(poolBuilder);
			}

			// Pie o' Enchanting
			if (WOODLAND_MANSION_ID.equals(id) || PILLAGER_OUTPOST_ID.equals(id) || SIMPLE_DUNGEON_ID.equals(id) || ABANDONED_MINESHAFT_ID.equals(id) || SHIPWRECK_TREASURE_ID.equals(id) || STRONGHOLD_CORRIDOR_ID.equals(id)) {
				poolBuilder = LootPool.builder().rolls(BinomialLootNumberProvider.create(WOODLAND_MANSION_ID.equals(id) ? 4 : 2, 0.35F)).with(ItemEntry.builder(CompanionBats.EXPERIENCE_PIE));
				tableBuilder.pool(poolBuilder);
			}
		});
	}
}
