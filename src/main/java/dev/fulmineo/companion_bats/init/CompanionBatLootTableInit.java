package dev.fulmineo.companion_bats.init;

import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

public class CompanionBatLootTableInit {
	private static final Identifier BASTION_TREASURE_ID = new Identifier("minecraft", "chests/bastion_treasure");
	private static final Identifier WOODLAND_MANSION_ID = new Identifier("minecraft", "chests/woodland_mansion");
	private static final Identifier BURIED_TREASURE_ID = new Identifier("minecraft", "chests/buried_treasure");
	private static final Identifier VILLAGE_WEAPONSMITH_ID = new Identifier("minecraft", "chests/village/village_weaponsmith");
	private static final Identifier PILLAGER_OUTPOST_ID = new Identifier("minecraft", "chests/pillager_outpost");
	private static final Identifier END_CITY_TREASURE_ID = new Identifier("minecraft", "chests/end_city_treasure");
	private static final Identifier DESERT_PYRAMID_ID = new Identifier("minecraft", "chests/desert_pyramid");
	private static final Identifier JUNGLE_TEMPLE_ID = new Identifier("minecraft", "chests/jungle_temple");

	private static final Identifier SWAMP_HUT_ID = new Identifier(CompanionBats.MOD_ID, "chests/swamp_hut");

	private static final Identifier SIMPLE_DUNGEON_ID = new Identifier("minecraft", "chests/simple_dungeon");
	private static final Identifier ABANDONED_MINESHAFT_ID = new Identifier("minecraft", "chests/abandoned_mineshaft");
	private static final Identifier SHIPWRECK_TREASURE_ID = new Identifier("minecraft", "chests/shipwreck_treasure");
	private static final Identifier STRONGHOLD_CORRIDOR_ID = new Identifier("minecraft", "chests/stronghold_corridor");

	public static void init(){
		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
			// Armors
			if (BASTION_TREASURE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).with(ItemEntry.builder(CompanionBats.INFERNO_SUIT));
				supplier.pool(poolBuilder);
			} else if (WOODLAND_MANSION_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).with(ItemEntry.builder(CompanionBats.VAMPIRIC_ATTIRE));
				supplier.pool(poolBuilder);
			} else if (BURIED_TREASURE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).with(ItemEntry.builder(CompanionBats.LOOTER_JACKET));
				supplier.pool(poolBuilder);
			} else if (VILLAGE_WEAPONSMITH_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).with(ItemEntry.builder(CompanionBats.KNIGHT_PLATE));
				supplier.pool(poolBuilder);
			} else if (SWAMP_HUT_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).with(ItemEntry.builder(CompanionBats.ALCHEMIST_ROBE));
				supplier.pool(poolBuilder);
			} else if (PILLAGER_OUTPOST_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).with(ItemEntry.builder(CompanionBats.DUELIST_COSTUME));
				supplier.pool(poolBuilder);
			} else if (END_CITY_TREASURE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).with(ItemEntry.builder(CompanionBats.NINJA_GARB));
				supplier.pool(poolBuilder);
			} else if (DESERT_PYRAMID_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(BinomialLootNumberProvider.create(1, 0.25F)).with(ItemEntry.builder(CompanionBats.MUMMY_BANDAGES));
				supplier.pool(poolBuilder);
			} else if (JUNGLE_TEMPLE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootNumberProvider.create(1)).with(ItemEntry.builder(CompanionBats.DESTROYER_GEAR));
				supplier.pool(poolBuilder);
			}
			// Pie o' Enchanting
			if (WOODLAND_MANSION_ID.equals(id) || PILLAGER_OUTPOST_ID.equals(id) || SIMPLE_DUNGEON_ID.equals(id) || ABANDONED_MINESHAFT_ID.equals(id) || SHIPWRECK_TREASURE_ID.equals(id) || STRONGHOLD_CORRIDOR_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(BinomialLootNumberProvider.create(WOODLAND_MANSION_ID.equals(id) ? 4 : 2, 0.35F)).with(ItemEntry.builder(CompanionBats.EXPERIENCE_PIE));
				supplier.pool(poolBuilder);
			}
		});
	}
}
