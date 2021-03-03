package dev.fulmineo.companion_bats.entity;

import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class CompanionBatAccessoryFeatureRenderer extends FeatureRenderer<CompanionBatEntity, CompanionBatEntityModel> {
    private final CompanionBatEntityModel model;

    public CompanionBatAccessoryFeatureRenderer(FeatureRendererContext<CompanionBatEntity, CompanionBatEntityModel> featureRendererContext) {
		super(featureRendererContext);
		this.model = new CompanionBatEntityModel();
	 }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CompanionBatEntity entity, float f, float g, float h, float j, float k, float l) {
        ItemStack accessoryStack = entity.getAccessory();
        if (accessoryStack.getItem() instanceof CompanionBatAccessoryItem) {
            CompanionBatAccessoryItem accessoryItem = (CompanionBatAccessoryItem)accessoryStack.getItem();
            this.getContextModel().copyStateTo(this.model);
            this.model.animateModel(entity, f, g, h);
            this.model.setAngles(entity, f, g, j, k, l);
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(accessoryItem.getEntityTexture()));
            this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		}
    }
}