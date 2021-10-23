package dev.fulmineo.companion_bats.init;

import java.util.HashMap;
import java.util.Map;

import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

public class CompanionBatLootTableInit {
	public static Map<Identifier, FabricLootPoolBuilder> toRegister = new HashMap<>();

	private static final Identifier WOODLAND_MANSION_ID = new Identifier("minecraft", "chests/woodland_mansion");
	private static final Identifier PILLAGER_OUTPOST_ID = new Identifier("minecraft", "chests/pillager_outpost");
	private static final Identifier SIMPLE_DUNGEON_ID = new Identifier("minecraft", "chests/simple_dungeon");
	private static final Identifier ABANDONED_MINESHAFT_ID = new Identifier("minecraft", "chests/abandoned_mineshaft");
	private static final Identifier SHIPWRECK_TREASURE_ID = new Identifier("minecraft", "chests/shipwreck_treasure");
	private static final Identifier STRONGHOLD_CORRIDOR_ID = new Identifier("minecraft", "chests/stronghold_corridor");

	public static void init(){
		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
			// Armors
			FabricLootPoolBuilder pool = toRegister.get(id);
			if (pool != null) {
				supplier.pool(pool);
				toRegister.remove(id);
			}

			// Pie o' Enchanting
			if (WOODLAND_MANSION_ID.equals(id) || PILLAGER_OUTPOST_ID.equals(id) || SIMPLE_DUNGEON_ID.equals(id) || ABANDONED_MINESHAFT_ID.equals(id) || SHIPWRECK_TREASURE_ID.equals(id) || STRONGHOLD_CORRIDOR_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(BinomialLootNumberProvider.create(WOODLAND_MANSION_ID.equals(id) ? 4 : 2, 0.35F)).with(ItemEntry.builder(CompanionBats.EXPERIENCE_PIE));
				supplier.pool(poolBuilder);
			}
		});
	}
}
