package dev.fulmineo.companion_bats;

import dev.fulmineo.companion_bats.entity.CompanionBatEntityRenderer;
import dev.fulmineo.companion_bats.screen.CompanionBatScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class CompanionBatsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(CompanionBats.BAT_SCREEN_HANDLER, CompanionBatScreen::new);
        EntityRendererRegistry.INSTANCE.register(CompanionBats.COMPANION_BAT, (dispatcher, factory) -> new CompanionBatEntityRenderer(dispatcher));
    }
}