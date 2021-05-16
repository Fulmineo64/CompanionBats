package dev.fulmineo.companion_bats;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.entity.CompanionBatEntityRenderer;
import dev.fulmineo.companion_bats.entity.DynamiteEntity;
import dev.fulmineo.companion_bats.init.CompanionBatCommandInit;
import dev.fulmineo.companion_bats.item.*;
import dev.fulmineo.companion_bats.nbt.EntityData;
import dev.fulmineo.companion_bats.screen.CompanionBatScreen;
import dev.fulmineo.companion_bats.screen.CompanionBatScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("companion_bats")
public class CompanionBats
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "companion_bats";
    public static final ItemGroup GROUP = new ItemGroup(MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(CompanionBats.BAT_ITEM.get());
        }
    };

    // Containers (ScreenHandlers)

    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);
    public static final RegistryObject<ContainerType<CompanionBatScreenHandler>> BAT_SCREEN_HANDLER = CONTAINERS.register("bat", () -> new ContainerType<>(CompanionBatScreenHandler::new));

    // Entities

    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);

    public static final RegistryObject<EntityType<CompanionBatEntity>> COMPANION_BAT = ENTITIES.register("bat", () -> EntityType.Builder.of(CompanionBatEntity::new, EntityClassification.CREATURE).sized(0.75F, 0.75F).build(new ResourceLocation(MOD_ID, "bat").toString()));
    public static final RegistryObject<EntityType<DynamiteEntity>> DYNAMITE = ENTITIES.register("dynamite", () -> EntityType.Builder.<DynamiteEntity>of(DynamiteEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(new ResourceLocation(MOD_ID, "dynamite").toString()));

    // Items

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> BAT_ITEM = ITEMS.register("bat_item", () -> new CompanionBatItem(new Item.Properties().durability((int) CompanionBatEntity.getMaxLevelHealth()).tab(GROUP)));
    public static final RegistryObject<Item> BAT_FLUTE_ITEM = ITEMS.register("bat_flute", () -> new CompanionBatFluteItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BAT_POUCH_ITEM = ITEMS.register("bat_pouch", () -> new CompanionBatPouchItem(new Item.Properties().stacksTo(1).tab(GROUP)));

    /*public static final Item COMMAND_FLUTE_ATTACK = new CompanionBatCommandFluteAttackItem(new Item.Properties().stacksTo(1).tab(GROUP));
    public static final Item COMMAND_FLUTE_REST = new CompanionBatCommandFluteRestItem(new Item.Properties().stacksTo(1));
    public static final Item COMMAND_FLUTE_GUARD = new CompanionBatCommandFluteGuardItem(new Item.Properties().stacksTo(1));*/
    public static final RegistryObject<Item> SPIRIT_SHARD =  ITEMS.register("spirit_shard", () -> new Item(new Item.Properties().tab(GROUP)));
    public static final RegistryObject<Item> SPIRIT_CRYSTAL =  ITEMS.register("spirit_crystal", () -> new Item(new Item.Properties().tab(GROUP)));
    public static final RegistryObject<Item> EXPERIENCE_PIE = ITEMS.register("experience_pie", () -> new CompanionBatExperiencePieItem(new Item.Properties().food((new Food.Builder()).nutrition(10).saturationMod(0.5F).build()).rarity(Rarity.UNCOMMON).tab(GROUP)));

    // Accessories

    public static final RegistryObject<Item> BUNNY_EARS = ITEMS.register("bunny_ears", () -> new CompanionBatAccessoryItem("bunny_ears", CompanionBatAbility.CANNOT_ATTACK, 1, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> WITHER_MASK = ITEMS.register("wither_mask", () -> new CompanionBatAccessoryItem("wither_mask", CompanionBatAbility.ATTACK_EVERYONE, 1, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> DECORATIVE_FLOWER = ITEMS.register("decorative_flower", () -> new CompanionBatAccessoryItem("decorative_flower", CompanionBatAbility.ATTACK_HOSTILES, 1, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> WOLF_PELT = ITEMS.register("wolf_pelt", () -> new CompanionBatAccessoryItem("wolf_pelt", CompanionBatAbility.ATTACK_PASSIVE, 1, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> PLATED_BOOTS = ITEMS.register("plated_boots", () -> new CompanionBatAccessoryItem("plated_boots", CompanionBatAbility.INCREASED_ARMOR, 1, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> PUMPKIN_BANDANA = ITEMS.register("pumpkin_bandana", () -> new CompanionBatAccessoryItem("pumpkin_bandana", CompanionBatAbility.NATURAL_REGENERATION, 1, new Item.Properties().tab(GROUP).stacksTo(1)));

    // Armors

    public static final RegistryObject<Item> INFERNO_SUIT = ITEMS.register("inferno_suit", () -> new CompanionBatArmorItem("inferno_suit", CompanionBatClass.INFERNO, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> VAMPIRIC_ATTIRE = ITEMS.register("vampiric_attire", () -> new CompanionBatArmorItem("vampiric_attire", CompanionBatClass.VAMPIRE, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> LOOTER_JACKET = ITEMS.register("looter_jacket", () -> new CompanionBatArmorItem("looter_jacket", CompanionBatClass.LOOTER, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> KNIGHT_PLATE = ITEMS.register("knight_plate", () -> new CompanionBatArmorItem("knight_plate", CompanionBatClass.KNIGHT, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> ALCHEMIST_ROBE = ITEMS.register("alchemist_robe", () -> new CompanionBatArmorItem("alchemist_robe", CompanionBatClass.ALCHEMIST, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> DUELIST_COSTUME = ITEMS.register("duelist_costume", () -> new CompanionBatArmorItem("duelist_costume", CompanionBatClass.DUELIST, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> NINJA_GARB = ITEMS.register("ninja_garb", () -> new CompanionBatArmorItem("ninja_garb", CompanionBatClass.NINJA, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> MUMMY_BANDAGES = ITEMS.register("mummy_bandages", () -> new CompanionBatArmorItem("mummy_bandages", CompanionBatClass.MUMMY, new Item.Properties().tab(GROUP).stacksTo(1)));
    public static final RegistryObject<Item> DESTROYER_GEAR = ITEMS.register("destroyer_gear", () -> new CompanionBatArmorItem("destroyer_gear", CompanionBatClass.DESTROYER, new Item.Properties().tab(GROUP).stacksTo(1)));

    public CompanionBats() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);*/
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);*/
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);

        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event) {
        GlobalEntityTypeAttributes.put(COMPANION_BAT.get(),CompanionBatEntity.createMobAttributes().build());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(COMPANION_BAT.get(), CompanionBatEntityRenderer::new);
        ScreenManager.register(BAT_SCREEN_HANDLER.get(), CompanionBatScreen::new);
    }

    /*private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
         LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }*/

    @SubscribeEvent
    public void registerCommands(final RegisterCommandsEvent event) {
        CompanionBatCommandInit.init(event.getDispatcher());
    }

    /*@SubscribeEvent
    public void entityHurt(final LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Entity attacker = event.getSource().getEntity();
        if (attacker != null && attacker instanceof CompanionBatEntity){
            CompanionBatEntity batEntity = (CompanionBatEntity)attacker;
            LivingEntity player = batEntity.getOwner();
            entity.setLastHurtByPlayer((PlayerEntity) player);
        }
    }*/

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }

    /*@SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!(event.getEntity() instanceof PlayerEntity)) return;
        if (event.getTarget() instanceof BatEntity) {
            BatEntity bat = (BatEntity)event.getTarget();
            PlayerEntity player = ((PlayerEntity)event.getEntity());
            ItemStack itemStack = player.getItemInHand(event.getHand());
            if (!event.getTarget().level.isClientSide){
                if (CompanionBatEntity.IS_FOOD_ITEM.test(itemStack)) {
                    if (!player.abilities.invulnerable) {
                        itemStack.shrink(1);
                    }

                    ItemStack batItemStack = new ItemStack(CompanionBats.BAT_ITEM.get());
                    EntityData.fromRegularBatEntity(bat).toStack(batItemStack);

                    bat.remove();
                    if (!player.addItem(batItemStack)){
                        ItemEntity itemEntity = player.drop(batItemStack, false);
                        if (itemEntity != null) {
                            itemEntity.setDefaultPickUpDelay();
                            itemEntity.setOwner(player.getUUID());
                        }
                    }
                }
            }
        }
    }*/

    public static void info(String message){
        LOGGER.info(message);
    }
}
