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
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import dev.fulmineo.companion_bats.item.CompanionBatClass;
import dev.fulmineo.companion_bats.item.CompanionBatFluteItem;
import dev.fulmineo.companion_bats.item.CompanionBatItem;
import dev.fulmineo.companion_bats.screen.CompanionBatScreenHandler;

public class CompanionBats implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    // Identifiers

    public static final String MOD_ID = "companion_bats";
    public static final String MOD_NAME = "Companion bats";

    public static final Identifier BAT_ITEM_IDENTIFIER = new Identifier(MOD_ID, "bat_item");
    public static final Identifier BAT_FLUTE_IDENTIFIER = new Identifier(MOD_ID, "bat_flute");
    public static final ScreenHandlerType<CompanionBatScreenHandler> BAT_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(BAT_ITEM_IDENTIFIER, CompanionBatScreenHandler::new);

    // Entities

    public static final EntityType<CompanionBatEntity> COMPANION_BAT = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MOD_ID, "bat"),
        FabricEntityTypeBuilder.<CompanionBatEntity>create(SpawnGroup.CREATURE, CompanionBatEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    );

    // Items

    public static final ItemGroup GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID,"group"), () -> new ItemStack(Registry.ITEM.get(new Identifier(MOD_ID,"bat_item"))));
    public static final Item BAT_ITEM = new CompanionBatItem(new FabricItemSettings().maxDamage((int)CompanionBatEntity.getMaxLevelHealth()).group(GROUP));
    public static final Item BAT_FLUTE_ITEM = new CompanionBatFluteItem(new FabricItemSettings().maxCount(1));

	private static final Item INFERNO_SUIT = new CompanionBatArmorItem("inferno_suit", 10, CompanionBatClass.INFERNO, new FabricItemSettings().group(GROUP).maxCount(1));
    /*private static final Item BAT_ARMOR_GOLD = new CompanionBatArmorItem(ArmorMaterials.GOLD, new FabricItemSettings().group(GROUP));
    private static final Item BAT_ARMOR_DIAMOND = new CompanionBatArmorItem(ArmorMaterials.DIAMOND, new FabricItemSettings().group(GROUP));
	private static final Item BAT_ARMOR_NETHERITE = new CompanionBatArmorItem(ArmorMaterials.NETHERITE, new FabricItemSettings().group(GROUP));*/

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(COMPANION_BAT, CompanionBatEntity.createMobAttributes());
        Registry.register(Registry.ITEM, BAT_ITEM_IDENTIFIER, BAT_ITEM);
        Registry.register(Registry.ITEM, BAT_FLUTE_IDENTIFIER, BAT_FLUTE_ITEM);

        // Registry.register(Registry.ITEM, new Identifier(MOD_ID, "leather_bat_armor"), BAT_ARMOR_LEATHER);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "inferno_suit"), INFERNO_SUIT);
        /*Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gold_bat_armor"), BAT_ARMOR_GOLD);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "diamond_bat_armor"), BAT_ARMOR_DIAMOND);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "netherite_bat_armor"), BAT_ARMOR_NETHERITE);*/
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

    public static void info(String message){
        log(Level.INFO, message);
    }
}