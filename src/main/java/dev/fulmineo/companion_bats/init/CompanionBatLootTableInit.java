package dev.fulmineo.companion_bats.init;

import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

public class CompanionBatLootTableInit {
	private static final Identifier INFERNO_LOOT_TABLE_ID = new Identifier("minecraft", "chests/bastion_treasure");
	private static final Identifier VAMPIRE_LOOT_TABLE_ID = new Identifier("minecraft", "chests/woodland_mansion");
	private static final Identifier LOOTER_LOOT_TABLE_ID = new Identifier("minecraft", "chests/buried_treasure");
	private static final Identifier KNIGHT_LOOT_TABLE_ID = new Identifier("minecraft", "chests/village/village_weaponsmith");
	private static final Identifier ALCHEMIST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/stronghold_library");
	private static final Identifier DUELIST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/pillager_outpost");
	private static final Identifier NINJA_LOOT_TABLE_ID = new Identifier("minecraft", "chests/end_city_treasure");

	public static void init(){
		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
			if (INFERNO_LOOT_TABLE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(CompanionBats.INFERNO_SUIT));
				supplier.pool(poolBuilder);
			} else if (VAMPIRE_LOOT_TABLE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(CompanionBats.VAMPIRIC_ATTIRE));
				supplier.pool(poolBuilder);
			} else if (LOOTER_LOOT_TABLE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(CompanionBats.LOOTER_JACKET));
				supplier.pool(poolBuilder);
			} else if (KNIGHT_LOOT_TABLE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(CompanionBats.KNIGHT_PLATE));
				supplier.pool(poolBuilder);
			} else if (ALCHEMIST_LOOT_TABLE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(CompanionBats.ALCHEMIST_ROBE));
				supplier.pool(poolBuilder);
			} else if (DUELIST_LOOT_TABLE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(CompanionBats.DUELIST_COSTUME));
				supplier.pool(poolBuilder);
			} else if (NINJA_LOOT_TABLE_ID.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(CompanionBats.NINJA_GARB));
				supplier.pool(poolBuilder);
			}
		});
	}
}
