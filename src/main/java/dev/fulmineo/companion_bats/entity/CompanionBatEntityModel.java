package dev.fulmineo.companion_bats.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CompanionBatEntityModel extends SinglePartEntityModel<CompanionBatEntity> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart rightWingTip;
	private final ModelPart leftWingTip;

	public CompanionBatEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.rightWing = this.body.getChild("right_wing");
		this.rightWingTip = this.rightWing.getChild("right_wing_tip");
		this.leftWing = this.body.getChild("left_wing");
		this.leftWingTip = this.leftWing.getChild("left_wing_tip");
	}

	public static ModelData getModelData(Dilation dilation){
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.NONE);
		modelPartData2.addChild("right_ear", ModelPartBuilder.create().uv(24, 0).cuboid(-4.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F), ModelTransform.NONE);
		modelPartData2.addChild("left_ear", ModelPartBuilder.create().uv(24, 0).mirrored().cuboid(1.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F), ModelTransform.NONE);
		ModelPartData modelPartData3 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 16).cuboid(-3.0F, 4.0F, -3.0F, 6.0F, 12.0F, 6.0F, dilation).uv(0, 34).cuboid(-5.0F, 16.0F, 0.0F, 10.0F, 6.0F, 1.0F), ModelTransform.NONE);
		ModelPartData modelPartData4 = modelPartData3.addChild("right_wing", ModelPartBuilder.create().uv(42, 0).cuboid(-12.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F), ModelTransform.NONE);
		modelPartData4.addChild("right_wing_tip", ModelPartBuilder.create().uv(24, 16).cuboid(-8.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F), ModelTransform.pivot(-12.0F, 1.0F, 1.5F));
		ModelPartData modelPartData5 = modelPartData3.addChild("left_wing", ModelPartBuilder.create().uv(42, 0).mirrored().cuboid(2.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F), ModelTransform.NONE);
		modelPartData5.addChild("left_wing_tip", ModelPartBuilder.create().uv(24, 16).mirrored().cuboid(0.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F), ModelTransform.pivot(12.0F, 1.0F, 1.5F));
		return modelData;
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		return TexturedModelData.of(getModelData(dilation), 64, 64);
	}

	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(CompanionBatEntity batEntity, float f, float g, float h, float i, float j) {
		if (batEntity.isRoosting()) {
			this.head.pitch = j * 0.017453292F;
			this.head.yaw = 3.1415927F - i * 0.017453292F;
			this.head.roll = 3.1415927F;
			this.head.setPivot(0.0F, -2.0F, 0.0F);
			this.rightWing.setPivot(-3.0F, 0.0F, 3.0F);
			this.leftWing.setPivot(3.0F, 0.0F, 3.0F);
			this.body.pitch = 3.1415927F;
			this.rightWing.pitch = -0.15707964F;
			this.rightWing.yaw = -1.2566371F;
			this.rightWingTip.yaw = -1.7278761F;
			this.leftWing.pitch = this.rightWing.pitch;
			this.leftWing.yaw = -this.rightWing.yaw;
			this.leftWingTip.yaw = -this.rightWingTip.yaw;
		} else {
			this.head.pitch = j * 0.017453292F;
			this.head.yaw = i * 0.017453292F;
			this.head.roll = 0.0F;
			this.head.setPivot(0.0F, 0.0F, 0.0F);
			this.rightWing.setPivot(0.0F, 0.0F, 0.0F);
			this.leftWing.setPivot(0.0F, 0.0F, 0.0F);
			this.body.pitch = 0.7853982F + MathHelper.cos(h * 0.1F) * 0.15F;
			this.body.yaw = 0.0F;
			this.rightWing.yaw = MathHelper.cos(h * 1.3F) * 3.1415927F * 0.25F;
			this.leftWing.yaw = -this.rightWing.yaw;
			this.rightWingTip.yaw = this.rightWing.yaw * 0.5F;
			this.leftWingTip.yaw = -this.rightWing.yaw * 0.5F;
		}
	}
}
