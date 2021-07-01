package dev.fulmineo.companion_bats.init;

import com.google.gson.JsonObject;
import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.List;

public class CompanionBatLootTableInit extends LootModifier {
	private static final ResourceLocation BASTION_TREASURE_ID = new ResourceLocation("minecraft", "chests/bastion_treasure");
	private static final ResourceLocation WOODLAND_MANSION_ID = new ResourceLocation("minecraft", "chests/woodland_mansion");
	private static final ResourceLocation BURIED_TREASURE_ID = new ResourceLocation("minecraft", "chests/buried_treasure");
	private static final ResourceLocation VILLAGE_WEAPONSMITH_ID = new ResourceLocation("minecraft", "chests/village/village_weaponsmith");
	private static final ResourceLocation PILLAGER_OUTPOST_ID = new ResourceLocation("minecraft", "chests/pillager_outpost");
	private static final ResourceLocation END_CITY_TREASURE_ID = new ResourceLocation("minecraft", "chests/end_city_treasure");
	private static final ResourceLocation DESERT_PYRAMID_ID = new ResourceLocation("minecraft", "chests/desert_pyramid");
	private static final ResourceLocation JUNGLE_TEMPLE_ID = new ResourceLocation("minecraft", "chests/jungle_temple");

	private static final Identifier SWAMP_HUT_ID = new Identifier(CompanionBats.MOD_ID, "chests/swamp_hut");

	private static final ResourceLocation SIMPLE_DUNGEON_ID = new ResourceLocation("minecraft", "chests/simple_dungeon");
	private static final ResourceLocation ABANDONED_MINESHAFT_ID = new ResourceLocation("minecraft", "chests/abandoned_mineshaft");
	private static final ResourceLocation SHIPWRECK_TREASURE_ID = new ResourceLocation("minecraft", "chests/shipwreck_treasure");
	private static final ResourceLocation STRONGHOLD_CORRIDOR_ID = new ResourceLocation("minecraft", "chests/stronghold_corridor");

	public CompanionBatLootTableInit(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Nonnull
	@Override
	public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		ResourceLocation id = context.getQueriedLootTableId();
		// Armors
		if (BASTION_TREASURE_ID.equals(id)) {
			generatedLoot.add(new ItemStack(CompanionBats.INFERNO_SUIT.get()));
		} else if (WOODLAND_MANSION_ID.equals(id)) {
			generatedLoot.add(new ItemStack(CompanionBats.VAMPIRIC_ATTIRE.get()));
		} else if (BURIED_TREASURE_ID.equals(id)) {
			generatedLoot.add(new ItemStack(CompanionBats.LOOTER_JACKET.get()));
		} else if (VILLAGE_WEAPONSMITH_ID.equals(id)) {
			generatedLoot.add(new ItemStack(CompanionBats.KNIGHT_PLATE.get()));
		} else if (SWAMP_HUT_ID.equals(id)) {
			generatedLoot.add(new ItemStack(CompanionBats.ALCHEMIST_ROBE.get()));
		} else if (PILLAGER_OUTPOST_ID.equals(id)) {
			generatedLoot.add(new ItemStack(CompanionBats.DUELIST_COSTUME.get()));
		} else if (END_CITY_TREASURE_ID.equals(id)) {
			if (context.getLevel().random.nextFloat() < 0.35F){
				generatedLoot.add(new ItemStack(CompanionBats.NINJA_GARB.get()));
			}
		} else if (DESERT_PYRAMID_ID.equals(id)) {
			if (context.getLevel().random.nextFloat() < 0.25F){
				generatedLoot.add(new ItemStack(CompanionBats.MUMMY_BANDAGES.get()));
			}
		} else if (JUNGLE_TEMPLE_ID.equals(id)) {
			generatedLoot.add(new ItemStack(CompanionBats.DESTROYER_GEAR.get()));
		}
		// Pie o' Enchanting
		if (WOODLAND_MANSION_ID.equals(id) || PILLAGER_OUTPOST_ID.equals(id) || SIMPLE_DUNGEON_ID.equals(id) || ABANDONED_MINESHAFT_ID.equals(id) || SHIPWRECK_TREASURE_ID.equals(id) || STRONGHOLD_CORRIDOR_ID.equals(id)) {
			for (int i=0; i < (WOODLAND_MANSION_ID.equals(id) ? 4 : 2); i++) {
				if (context.getLevel().random.nextFloat() < 0.35F) {
					generatedLoot.add(new ItemStack(CompanionBats.EXPERIENCE_PIE.get()));
				}
			}
		}
		return generatedLoot;
	}

	private static class Serializer extends GlobalLootModifierSerializer<CompanionBatLootTableInit> {
		@Override
		public CompanionBatLootTableInit read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
			return new CompanionBatLootTableInit(conditionsIn);
		}

		@Override
		public JsonObject write(CompanionBatLootTableInit instance) {
			return this.makeConditions(instance.conditions);
		}
	}

	@Mod.EventBusSubscriber(modid = CompanionBats.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class EventHandler {
		@SubscribeEvent
		public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
			event.getRegistry().register(new CompanionBatLootTableInit.Serializer().setRegistryName(new ResourceLocation(CompanionBats.MOD_ID, "loot_modifier")));
		}
	}
}
