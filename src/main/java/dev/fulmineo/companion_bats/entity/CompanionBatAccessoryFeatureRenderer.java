package dev.fulmineo.companion_bats.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;

public class CompanionBatAccessoryFeatureRenderer extends LayerRenderer<CompanionBatEntity, CompanionBatEntityModel> {
    private final CompanionBatEntityModel model;

    public CompanionBatAccessoryFeatureRenderer(IEntityRenderer<CompanionBatEntity, CompanionBatEntityModel> featureRendererContext) {
		super(featureRendererContext);
		this.model = new CompanionBatEntityModel();
	 }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider, int i, CompanionBatEntity entity, float f, float g, float h, float j, float k, float l) {
        ItemStack accessoryStack = entity.getAccessory();
        if (accessoryStack.getItem() instanceof CompanionBatAccessoryItem) {
            CompanionBatAccessoryItem accessoryItem = (CompanionBatAccessoryItem)accessoryStack.getItem();
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(entity, f, g, h);
            this.model.setupAnim(entity, f, g, j, k, l);
            IVertexBuilder vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityCutoutNoCull(accessoryItem.getEntityTexture()));
            this.model.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		}
    }
}