package dev.fulmineo.companion_bats;

import dev.fulmineo.companion_bats.entity.CompanionBatEntityRenderer;
import dev.fulmineo.companion_bats.screen.CompanionBatScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CompanionBatsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(CompanionBats.BAT_SCREEN_HANDLER, CompanionBatScreen::new);
        EntityRendererRegistry.INSTANCE.register(CompanionBats.COMPANION_BAT, CompanionBatEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(CompanionBats.DYNAMITE, FlyingItemEntityRenderer::new);
		FabricModelPredicateProviderRegistry.register(CompanionBats.COMMAND_FLUTE, new Identifier("mode"), (itemStack, clientWorld, livingEntity, seed) -> {
			NbtCompound tag = itemStack.getTag();
			CompanionBats.info(""+ tag.getByte("mode"));
			return (float)tag.getByte("mode");
		});
    }
}