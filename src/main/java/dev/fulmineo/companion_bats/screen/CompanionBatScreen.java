package dev.fulmineo.companion_bats.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import dev.fulmineo.companion_bats.CompanionBatClass;
import dev.fulmineo.companion_bats.nbt.EntityData;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Component;
import net.minecraft.util.text.TranslationTextComponent;

public class CompanionBatScreen extends ContainerScreen<CompanionBatScreenHandler> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CompanionBats.MOD_ID, "textures/gui/container/bat.png");
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

   	public CompanionBatScreen(CompanionBatScreenHandler handler, PlayerInventory inventory, Component title) {
		super(handler, inventory, title);

		this.inventory = inventory;
		ItemStack batItemStack = inventory.player.getItemInHand(handler.hand);
		EntityData.createIfMissing(batItemStack);
		EntityData entityData = new EntityData(batItemStack);

		this.setLevel(entityData);
		this.setClassLevel(entityData);
		this.setAttributes(entityData);

		this.passEvents = false;
   	}

	private void setLevel(EntityData entityData){
		this.level = CompanionBatLevels.getLevelByExp(entityData.getExp());

		if (this.level+1 < CompanionBatLevels.LEVELS.length){
			this.currentLevelExp = entityData.getExp() - CompanionBatLevels.LEVELS[this.level].totalExpNeeded;
			this.nextLevelExp = CompanionBatLevels.LEVELS[this.level+1].totalExpNeeded - CompanionBatLevels.LEVELS[this.level].totalExpNeeded;
		} else {
			this.currentLevelExp = entityData.getExp();
			this.nextLevelExp = CompanionBatLevels.LEVELS[this.level].totalExpNeeded;
			this.maxExpReached = this.currentLevelExp >= this.nextLevelExp;
		}
	}

	private void setClassLevel(EntityData entityData){
		this.armorStack = ItemStack.of((CompoundTag)entityData.getArmor());
		if (this.armorStack.getItem() instanceof CompanionBatArmorItem){
			this.currentClass = ((CompanionBatArmorItem)this.armorStack.getItem()).getBatClass();
			if (this.currentClass != null){
				int classExp = entityData.getClassExp(this.currentClass);
				this.classLevel = CompanionBatLevels.getClassLevelByExp(this.currentClass, classExp);
				CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(this.currentClass);
				if (this.classLevel+1 < classLevels.length){
					this.currentClassLevelExp = classExp - classLevels[this.classLevel].totalExpNeeded;
					this.nextClassLevelExp = classLevels[this.classLevel+1].totalExpNeeded - classLevels[this.classLevel].totalExpNeeded;
					this.maxClassExpReached = false;
				} else {
					this.currentClassLevelExp = classExp;
					this.nextClassLevelExp = classLevels[this.classLevel].totalExpNeeded;
					this.maxClassExpReached = this.currentClassLevelExp >= this.nextClassLevelExp;
				}
			}
		} else {
			this.currentClass = null;
		}
	}

	private void setAttributes(EntityData entityData){
		this.currentHealth = Math.round(entityData.getHealth() * 10F) / 10F;
		this.maxHealth = CompanionBatEntity.getLevelHealth(this.level);
		this.attack = CompanionBatEntity.getLevelAttack(this.level);
		this.speed = Math.round(CompanionBatEntity.getLevelSpeed(this.level) * 100F) / 100F;
	}

	protected void renderBg(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(TEXTURE);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;

		// Draws the background
		this.blit(matrices, i, j, 0, 0, this.imageWidth, this.imageHeight);

		// Draws the additional slots
		this.blit(matrices, i + 7, j + 35 - 18, 0, this.imageHeight, 18, 18);
		this.blit(matrices, i + 7, j + 35, 18, this.imageHeight, 18, 18);
		this.blit(matrices, i + 7, j + 35 + 18, 36, this.imageHeight, 18, 18);

		float x = i + 28;
		float y = j + 20;
		int strideY = 10;
		int strideX = 59;

		// Draws the text
		this.font.draw(matrices, new TranslationTextComponent("gui.companion_bats.bat.level"), x, y, 0xFFFFFFFF);
		this.font.draw(matrices, new TranslationTextComponent("").append(""+(this.level + 1)).append(" [").append(this.maxExpReached ? new TranslationTextComponent("gui.companion_bats.bat.max") : new TranslationTextComponent("").append(this.currentLevelExp+" / "+this.nextLevelExp)).append("]"), x + strideX, y, 0xFFFFFFFF);

		y += strideY;

		if (this.currentClass != null){
			this.font.draw(matrices, new TranslationTextComponent("gui.companion_bats.bat.class"), x, y, 0xFFFFFFFF);
			this.font.draw(matrices, new TranslationTextComponent("gui.companion_bats.bat.class." + this.currentClass.toString()), x + strideX, y, 0xFFFFFFFF);

			y += strideY;

			this.font.draw(matrices, new TranslationTextComponent("gui.companion_bats.bat.class_level"), x, y, 0xFFFFFFFF);
			this.font.draw(matrices, new TranslationTextComponent("").append(""+(this.classLevel + 1)).append(" [").append(this.maxClassExpReached ? new TranslationTextComponent("gui.companion_bats.bat.max") : new TranslationTextComponent("").append(this.currentClassLevelExp+" / "+this.nextClassLevelExp)).append("]"), x + strideX, y, 0xFFFFFFFF);
		} else {
			y += strideY;
		}

		y += strideY * 2;

		strideX = 11;

		this.font.draw(matrices, "\u2764", x, y, 0xFFFFFFFF);
		int offset = this.font.draw(matrices, this.currentHealth+" / "+this.maxHealth, x + strideX, y, 0xFFFFFFFF) - (int)x;
		int speedOffset = 110 + (String.valueOf(this.speed).length() <= 3 ? 6 : 0);

		int digits = (String.valueOf(this.attack).length() - 2) * 3;
		offset += ((speedOffset - offset) / 2) - digits - 10;

		this.font.draw(matrices, ""/*"🗡"*/, x + offset , y, 0xFFFFFFFF);
		this.font.draw(matrices, ""+this.attack, x + offset + strideX, y, 0xFFFFFFFF);

		this.font.draw(matrices, ""/*"⬈"*/, x + speedOffset, y, 0xFFFFFFFF);
		this.font.draw(matrices, ""+this.speed, x + speedOffset + strideX - 2, y, 0xFFFFFFFF);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		ItemStack batItemStack = this.inventory.player.getItemInHand(menu.hand);
		EntityData entityData = new EntityData(batItemStack);
		ItemStack armorStack = ItemStack.of(entityData.getArmor());
		if (!this.armorStack.getItem().equals(armorStack.getItem())) this.setClassLevel(entityData);
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.renderTooltip(matrices, mouseX, mouseY);
	}
 }
