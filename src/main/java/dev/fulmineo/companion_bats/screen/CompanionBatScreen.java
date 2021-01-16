package dev.fulmineo.companion_bats.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.item.CompanionBatItem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CompanionBatScreen extends HandledScreen<CompanionBatScreenHandler> {
	private static final Identifier TEXTURE = new Identifier(CompanionBats.MOD_ID, "textures/gui/container/bat.png");
	private int level;
	private int currentLevelExp;
	private int nextLevelExp;
	private boolean maxExpReached;
	private float currentHealth;
	private float maxHealth;
	private float attack;
	private float speed;

   	public CompanionBatScreen(CompanionBatScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);

		ItemStack stack = inventory.player.getStackInHand(handler.hand);
		CompoundTag entityData = CompanionBatItem.getOrCreateEntityData(stack);

		this.level = CompanionBatEntity.getLevelByExp(entityData.getInt("exp"));

		if (this.level+1 < CompanionBatEntity.LEVELS.length){
			this.currentLevelExp = entityData.getInt("exp") - CompanionBatEntity.LEVELS[this.level].totalExpNeeded;
			this.nextLevelExp = CompanionBatEntity.LEVELS[this.level+1].totalExpNeeded - CompanionBatEntity.LEVELS[this.level].totalExpNeeded;
		} else {
			this.currentLevelExp = entityData.getInt("exp");
			this.nextLevelExp = CompanionBatEntity.LEVELS[this.level].totalExpNeeded;
			this.maxExpReached = this.currentLevelExp >= this.nextLevelExp;
		}

		this.currentHealth = Math.round(entityData.getFloat("health") * 10F) / 10F;
		this.maxHealth = CompanionBatEntity.LEVELS[this.level].health;

		this.attack = CompanionBatEntity.LEVELS[this.level].attack;
		this.speed = CompanionBatEntity.LEVELS[this.level].speed;

		this.passEvents = false;
   	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;

		// Draws the background
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

		// Draws the additional slots
		this.drawTexture(matrices, i + 7, j + 35 - 18, 18, this.backgroundHeight, 18, 18);
		this.drawTexture(matrices, i + 7, j + 35, 0, this.backgroundHeight, 18, 18);
		this.drawTexture(matrices, i + 7, j + 35 + 18, 0, this.backgroundHeight, 18, 18);

		float x = i + 28;
		float y = j + 20;
		int strideY = 10;
		int strideX = 38;

		// Draws the text
		this.textRenderer.draw(matrices, new TranslatableText("gui.companion_bats.bat.level"), x, y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, ""+(this.level + 1)+(this.maxExpReached ? " [MAX]": ""), x + strideX, y, 0xFFFFFFFF);

		y += strideY;

		this.textRenderer.draw(matrices, new TranslatableText("gui.companion_bats.bat.exp"), x, y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, this.currentLevelExp+" / "+this.nextLevelExp, x + strideX, y, 0xFFFFFFFF);

		y += strideY;

		this.textRenderer.draw(matrices, new TranslatableText("gui.companion_bats.bat.health"), x, y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, this.currentHealth+" / "+this.maxHealth, x + strideX, y, 0xFFFFFFFF);

		y += strideY;

		this.textRenderer.draw(matrices, new TranslatableText("gui.companion_bats.bat.attack"), x, y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, ""+this.attack, x + strideX, y, 0xFFFFFFFF);

		y += strideY;

		this.textRenderer.draw(matrices, new TranslatableText("gui.companion_bats.bat.speed"), x, y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, ""+this.speed, x + strideX, y, 0xFFFFFFFF);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}
 }
