package dev.fulmineo.companion_bats;

import com.google.common.collect.ImmutableList;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.entity.CompanionBatEntityRenderer;
import dev.fulmineo.companion_bats.entity.DynamiteEntity;
import dev.fulmineo.companion_bats.feature.CaveHouseFeature;
import dev.fulmineo.companion_bats.feature.CaveHouseGenerator;
import dev.fulmineo.companion_bats.init.CompanionBatCommandInit;
import dev.fulmineo.companion_bats.item.*;
import dev.fulmineo.companion_bats.screen.CompanionBatScreen;
import dev.fulmineo.companion_bats.screen.CompanionBatScreenHandler;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

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

	// Structure

	public static final JigsawPiece CAVE_HOUSE_PIECE = CaveHouseGenerator.Piece::new;
	private static final StructureFeature<DefaultFeatureConfig> CAVE_HOUSE_STRUCTURE = new CaveHouseFeature(DefaultFeatureConfig.CODEC);
	private static final ConfiguredStructureFeature<?, ?> CAVE_HOUSE_CONFIGURED = CAVE_HOUSE_STRUCTURE.configure(DefaultFeatureConfig.DEFAULT);

	public static final Identifier CAVE_HOUSE_POOL = new Identifier("companion_bats","cave_house_pool");

    public CompanionBats() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);*/
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);*/
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);

		// Structure

		Registry.register(Registry.STRUCTURE_PIECE, new Identifier("companion_bats", "cave_house_piece"), CAVE_HOUSE_PIECE);
		FabricStructureBuilder.create(new Identifier("companion_bats", "cave_house"), CAVE_HOUSE_STRUCTURE)
			.step(GenerationStep.Feature.UNDERGROUND_STRUCTURES)
			.defaultConfig(48, 12, 478010)
			.register();

		RegistryKey<ConfiguredStructureFeature<?, ?>> myConfigured = RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier("companion_bats", "cave_house"));
		BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, myConfigured.getValue(), CAVE_HOUSE_CONFIGURED);
		BiomeModifications.addStructure(
			BiomeSelectors.foundInOverworld().and(BiomeSelectors.excludeByKey(BiomeKeys.DEEP_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.DEEP_WARM_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN))
		, myConfigured);

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

	@SubscribeEvent
	public void entityTravelToDimension(final EntityTravelToDimensionEvent event) {
		Entity eventEntity = event.getEntity();
		if (eventEntity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)eventEntity;
			PlayerInventory inventory = player.inventory;
			ImmutableList<NonNullList<ItemStack>> mainAndOffhand = ImmutableList.of(inventory.items, inventory.offhand);
			Iterator<NonNullList<ItemStack>> iterator = mainAndOffhand.iterator();
			while (iterator.hasNext()) {
				NonNullList<ItemStack> defaultedList = (NonNullList<ItemStack>) iterator.next();
				for (int i = 0; i < defaultedList.size(); ++i) {
					if (defaultedList.get(i).getItem() == CompanionBats.BAT_FLUTE_ITEM.get()) {
						CompanionBatEntity entity = (CompanionBatEntity) ((ServerWorld) player.level).getEntity(defaultedList.get(i).getTag().getUUID("EntityUUID"));
						if (entity != null) {
							defaultedList.set(i, entity.toItemStack());
							entity.remove();
						}
					}
				}
			}
		}
	}

	// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
   /* @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }*/

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
