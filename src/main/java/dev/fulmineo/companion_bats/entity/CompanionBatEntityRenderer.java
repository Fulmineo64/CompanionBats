package dev.fulmineo.companion_bats.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import net.minecraft.client.renderer.entity.BatRenderer;

public class CompanionBatEntityRenderer extends MobRenderer<CompanionBatEntity, CompanionBatEntityModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/bat.png");

    public CompanionBatEntityRenderer(EntityRendererManager context) {
        super(context, new CompanionBatEntityModel(), 0.25F);
        this.addLayer(new CompanionBatArmorFeatureRenderer(this));
		this.addLayer(new CompanionBatAccessoryFeatureRenderer(this));
    }

    public ResourceLocation getTextureLocation(CompanionBatEntity batEntity) {
       return TEXTURE;
    }

    protected void scale(CompanionBatEntity batEntity, MatrixStack matrixStack, float f) {
       matrixStack.scale(0.35F, 0.35F, 0.35F);
    }

    protected void setupRotations(CompanionBatEntity batEntity, MatrixStack matrixStack, float f, float g, float h) {
        if (batEntity.isRoosting()) {
            matrixStack.translate(0.0D, -0.10000000149011612D, 0.0D);
        } else {
            matrixStack.translate(0.0D, (double)(MathHelper.cos(f * 0.3F) * 0.1F), 0.0D);
        }
        super.setupRotations(batEntity, matrixStack, f, g, h);
    }
}