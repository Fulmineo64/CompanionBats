package dev.fulmineo.companion_bats;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.fulmineo.companion_bats.config.CompanionBatsConfig;
import dev.fulmineo.companion_bats.data.CompanionBatAccessory;
import dev.fulmineo.companion_bats.data.CompanionBatArmor;
import dev.fulmineo.companion_bats.data.ServerDataManager;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.entity.DynamiteEntity;
import dev.fulmineo.companion_bats.init.CompanionBatCommandInit;
import dev.fulmineo.companion_bats.init.CompanionBatLootTableInit;
import dev.fulmineo.companion_bats.init.ServerEventInit;
import dev.fulmineo.companion_bats.item.CompanionBatCommandFluteAttackItem;
import dev.fulmineo.companion_bats.item.CompanionBatCommandFluteGuardItem;
import dev.fulmineo.companion_bats.item.CompanionBatCommandFluteRestItem;
import dev.fulmineo.companion_bats.item.CompanionBatExperiencePieItem;
import dev.fulmineo.companion_bats.item.CompanionBatFluteItem;
import dev.fulmineo.companion_bats.item.CompanionBatItem;
import dev.fulmineo.companion_bats.network.ServerNetworkManager;
import dev.fulmineo.companion_bats.screen.CompanionBatScreenHandler;
import draylar.staticcontent.StaticContent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class CompanionBats implements ModInitializer {

	public static Logger LOGGER = LogManager.getLogger();
	public static CompanionBatsConfig CONFIG;

    // Identifiers

    public static final String MOD_ID = "companion_bats";
    public static final Identifier REQUEST_CLIENT_DATA_ID = new Identifier(MOD_ID, "request_client_data_packet");
    public static final Identifier TRANSFER_CLIENT_DATA_ID = new Identifier(MOD_ID, "transfer_client_data_packet");
    public static final ScreenHandlerType<CompanionBatScreenHandler> BAT_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MOD_ID, "bat_item"), CompanionBatScreenHandler::new);

    // Entities

    public static final EntityType<CompanionBatEntity> COMPANION_BAT = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MOD_ID, "bat"),
        FabricEntityTypeBuilder.<CompanionBatEntity>create(SpawnGroup.CREATURE, CompanionBatEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    );
    public static final EntityType<DynamiteEntity> DYNAMITE = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MOD_ID, "dynamite"),
		FabricEntityTypeBuilder.<DynamiteEntity>create(SpawnGroup.MISC, DynamiteEntity::new).dimensions(EntityDimensions.fixed(0.25F, 0.25F)).trackRangeChunks(4).trackedUpdateRate(10).build()
	);

    // Items

	public static final ItemGroup GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID,"group"), () -> new ItemStack(Registry.ITEM.get(new Identifier(MOD_ID,"bat_item"))));

    public static final Item BAT_ITEM = new CompanionBatItem(new FabricItemSettings().maxDamage(100).group(GROUP));
    public static final Item NETHERITE_BAT_ITEM = new CompanionBatItem(new FabricItemSettings().fireproof().maxDamage(100).rarity(Rarity.EPIC));
	public static final Item BAT_FLUTE_ITEM = new CompanionBatFluteItem(new FabricItemSettings().maxCount(1));
	public static final Item COMMAND_FLUTE_ATTACK = new CompanionBatCommandFluteAttackItem(new FabricItemSettings().maxCount(1).group(GROUP));
	public static final Item COMMAND_FLUTE_REST = new CompanionBatCommandFluteRestItem(new FabricItemSettings().maxCount(1));
	public static final Item COMMAND_FLUTE_GUARD = new CompanionBatCommandFluteGuardItem(new FabricItemSettings().maxCount(1));
    public static final Item SPIRIT_SHARD = new Item(new FabricItemSettings().group(GROUP));
    public static final Item SPIRIT_CRYSTAL = new Item(new FabricItemSettings().group(GROUP));
    public static final Item EXPERIENCE_PIE = new CompanionBatExperiencePieItem(new FabricItemSettings().food((new FoodComponent.Builder()).hunger(10).saturationModifier(0.5F).build()).rarity(Rarity.UNCOMMON).group(GROUP));

    @Override
    public void onInitialize() {
		AutoConfig.register(CompanionBatsConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(CompanionBatsConfig.class).getConfig();
        FabricDefaultAttributeRegistry.register(COMPANION_BAT, CompanionBatEntity.createMobAttributes());

		// Items

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "bat_item"), 		  	BAT_ITEM);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "netherite_bat_item"),  NETHERITE_BAT_ITEM);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "bat_flute"), 	 	  	BAT_FLUTE_ITEM);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "command_flute"), 		COMMAND_FLUTE_ATTACK);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "command_flute_rest"), 	COMMAND_FLUTE_REST);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "command_flute_guard"), COMMAND_FLUTE_GUARD);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "spirit_shard"), 	  	SPIRIT_SHARD);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "spirit_crystal"),    	SPIRIT_CRYSTAL);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "experience_pie"),  	EXPERIENCE_PIE);

		// Accessories

		StaticContent.load(new Identifier(CompanionBats.MOD_ID, "accessories"), CompanionBatAccessory.class);

		// Armors

		StaticContent.load(new Identifier(CompanionBats.MOD_ID, "armors"), CompanionBatArmor.class);

		// Networking

		ServerNetworkManager.registerClientReceiver();

		// Init

		CompanionBatLootTableInit.init();
		CompanionBatCommandInit.init();
		ServerDataManager.init();
		ServerEventInit.init();
    }

	public static void info(String message){
        LOGGER.log(Level.INFO, message);
    }
}
