package dev.fulmineo.companion_bats.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import dev.fulmineo.companion_bats.item.CompanionBatClass;
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
	private int classLevel;
	private int currentClassLevelExp;
	private int nextClassLevelExp;
	private boolean maxExpReached;
	private boolean maxClassExpReached;
	private float currentHealth;
	private float maxHealth;
	private float attack;
	private float speed;
	private CompanionBatClass currentClass;
	private ItemStack armorStack;
	private PlayerInventory inventory;

   	public CompanionBatScreen(CompanionBatScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);

		this.inventory = inventory;
		ItemStack batItemStack = inventory.player.getStackInHand(handler.hand);
		CompoundTag entityData = CompanionBatItem.getOrCreateEntityData(batItemStack);

		this.setLevel(entityData);
		this.setClassLevel(entityData);
		this.setAttributes(entityData);

		this.passEvents = false;
   	}

	private void setLevel(CompoundTag entityData){
		this.level = CompanionBatLevels.getLevelByExp(entityData.getInt("exp"));

		if (this.level+1 < CompanionBatLevels.LEVELS.length){
			this.currentLevelExp = entityData.getInt("exp") - CompanionBatLevels.LEVELS[this.level].totalExpNeeded;
			this.nextLevelExp = CompanionBatLevels.LEVELS[this.level+1].totalExpNeeded - CompanionBatLevels.LEVELS[this.level].totalExpNeeded;
		} else {
			this.currentLevelExp = entityData.getInt("exp");
			this.nextLevelExp = CompanionBatLevels.LEVELS[this.level].totalExpNeeded;
			this.maxExpReached = this.currentLevelExp == this.nextLevelExp;
		}
	}

	private void setClassLevel(CompoundTag entityData){
		this.armorStack = ItemStack.fromTag(entityData.getCompound("armor"));
		if (this.armorStack.getItem() instanceof CompanionBatArmorItem){
			this.currentClass = ((CompanionBatArmorItem)this.armorStack.getItem()).getBatClass();
			if (this.currentClass != null){
				int classExp = entityData.getInt(this.currentClass.getExpTagName());
				this.classLevel = CompanionBatLevels.getClassLevelByExp(this.currentClass, classExp);
				this.maxClassExpReached = false;
				CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(this.currentClass);
				if (this.classLevel+1 < classLevels.length){
					this.currentClassLevelExp = classExp - classLevels[this.classLevel].totalExpNeeded;
					this.nextClassLevelExp = classLevels[this.classLevel+1].totalExpNeeded - classLevels[this.classLevel].totalExpNeeded;
				} else {
					this.currentClassLevelExp = classExp;
					this.nextClassLevelExp = classLevels[this.classLevel].totalExpNeeded;
					this.maxClassExpReached = this.currentClassLevelExp == this.nextClassLevelExp;
				}
			}
		} else {
			this.currentClass = null;
		}
	}

	private void setAttributes(CompoundTag entityData){
		this.currentHealth = Math.round(entityData.getFloat("health") * 10F) / 10F;
		this.maxHealth = CompanionBatEntity.getLevelHealth(this.level);
		this.attack = CompanionBatEntity.getLevelAttack(this.level);
		this.speed = CompanionBatEntity.getLevelSpeed(this.level);
	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;

		// Draws the background
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

		// Draws the additional slots
		this.drawTexture(matrices, i + 7, j + 35 - 18, 0, this.backgroundHeight, 18, 18);
		this.drawTexture(matrices, i + 7, j + 35, 18, this.backgroundHeight, 18, 18);

		float x = i + 28;
		float y = j + 20;
		int strideY = 10;
		int strideX = 62;

		// Draws the text
		this.textRenderer.draw(matrices, new TranslatableText("gui.companion_bats.bat.level"), x, y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, new TranslatableText("").append(""+(this.level + 1)).append(" [").append(this.maxExpReached ? new TranslatableText("gui.companion_bats.bat.max") : new TranslatableText("").append(this.currentLevelExp+" / "+this.nextLevelExp)).append("]"), x + strideX, y, 0xFFFFFFFF);

		y += strideY;

		if (this.currentClass != null){
			this.textRenderer.draw(matrices, new TranslatableText("gui.companion_bats.bat.class"), x, y, 0xFFFFFFFF);
			this.textRenderer.draw(matrices, this.currentClass.toString(), x + strideX, y, 0xFFFFFFFF);

			y += strideY;

			this.textRenderer.draw(matrices, new TranslatableText("gui.companion_bats.bat.class_level"), x, y, 0xFFFFFFFF);
			this.textRenderer.draw(matrices, new TranslatableText("").append(""+(this.classLevel + 1)).append(" [").append(this.maxClassExpReached ? new TranslatableText("gui.companion_bats.bat.max") : new TranslatableText("").append(this.currentClassLevelExp+" / "+this.nextClassLevelExp)).append("]"), x + strideX, y, 0xFFFFFFFF);
		} else {
			y += strideY;
		}

		y += strideY * 2;

		strideX = 12;

		this.textRenderer.draw(matrices, "❤", x, y, 0xFFFFFFFF);
		int offset = this.textRenderer.draw(matrices, this.currentHealth+" / "+this.maxHealth, x + strideX, y, 0xFFFFFFFF) - (int)x;
		int digits = (String.valueOf(this.attack).length() - 2) * 3;
		offset += ((112 - offset) / 2) - digits - 10;

		this.textRenderer.draw(matrices, "🗡", x + offset , y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, ""+this.attack, x + offset + strideX, y, 0xFFFFFFFF);

		offset = 112;

		this.textRenderer.draw(matrices, "⬈", x + offset, y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, ""+this.speed, x + offset + strideX, y, 0xFFFFFFFF);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		ItemStack batItemStack = this.inventory.player.getStackInHand(handler.hand);
		CompoundTag entityData = CompanionBatItem.getOrCreateEntityData(batItemStack);
		ItemStack armorStack = ItemStack.fromTag(entityData.getCompound("armor"));
		if (!this.armorStack.getItem().equals(armorStack.getItem())) this.setClassLevel(entityData);
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}
 }
