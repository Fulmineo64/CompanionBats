package dev.fulmineo.companion_bats.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.data.ClientDataManager;
import dev.fulmineo.companion_bats.data.CompanionBatClass;
import dev.fulmineo.companion_bats.data.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.data.CompanionBatCombatLevel;
import dev.fulmineo.companion_bats.data.EntityData;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.minecraft.client.render.GameRenderer;

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
	private String currentClass;
	private ItemStack armorStack;
	private PlayerInventory inventory;

   	public CompanionBatScreen(CompanionBatScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);

		this.inventory = inventory;
		ItemStack batItemStack = inventory.player.getStackInHand(handler.hand);
		EntityData.createIfMissing(batItemStack);
		EntityData entityData = new EntityData(batItemStack);

		this.setLevel(entityData);
		this.setClassLevel(entityData);
		this.setAttributes(entityData);

		this.passEvents = false;
   	}

	private void setLevel(EntityData entityData){
		this.level = CompanionBatCombatLevel.getLevelByExp(ClientDataManager.combatLevels, entityData.getExp());

		if (this.level+1 < ClientDataManager.combatLevels.length){
			this.currentLevelExp = entityData.getExp() - ClientDataManager.combatLevels[this.level].totalExp;
			this.nextLevelExp = ClientDataManager.combatLevels[this.level+1].totalExp - ClientDataManager.combatLevels[this.level].totalExp;
		} else {
			this.currentLevelExp = entityData.getExp();
			this.nextLevelExp = ClientDataManager.combatLevels[this.level].totalExp;
			this.maxExpReached = this.currentLevelExp >= this.nextLevelExp;
		}
	}

	private void setClassLevel(EntityData entityData){
		this.armorStack = ItemStack.fromNbt((NbtCompound)entityData.getArmor());
		if (this.armorStack.getItem() instanceof CompanionBatArmorItem){
			this.currentClass = ((CompanionBatArmorItem)this.armorStack.getItem()).getClassName();
			if (this.currentClass != null){
				int classExp = entityData.getClassExp(this.currentClass);
				CompanionBatClassLevel[] classLevels = ClientDataManager.classes.get(this.currentClass).levels;
				this.classLevel = CompanionBatClassLevel.getClassLevelByExp(classLevels, classExp);
				if (this.classLevel+1 < classLevels.length){
					this.currentClassLevelExp = classExp - classLevels[this.classLevel].totalExp;
					this.nextClassLevelExp = classLevels[this.classLevel+1].totalExp - classLevels[this.classLevel].totalExp;
					this.maxClassExpReached = false;
				} else {
					this.currentClassLevelExp = classExp;
					this.nextClassLevelExp = classLevels[this.classLevel].totalExp;
					this.maxClassExpReached = this.currentClassLevelExp >= this.nextClassLevelExp;
				}
			}
		} else {
			this.currentClass = null;
		}
	}

	private void setAttributes(EntityData entityData){
		this.currentHealth = Math.round(entityData.getHealth() * 10F) / 10F;
		this.maxHealth = CompanionBatCombatLevel.getLevelHealth(ClientDataManager.baseHealth, ClientDataManager.combatLevels, this.level);
		this.attack = CompanionBatCombatLevel.getLevelAttack(ClientDataManager.baseAttack, ClientDataManager.combatLevels, this.level);
		this.speed = Math.round(CompanionBatCombatLevel.getLevelSpeed(ClientDataManager.baseSpeed, ClientDataManager.combatLevels, this.level) * 100F) / 100F;
	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;

		// Draws the background
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

		// Draws the additional slots
		this.drawTexture(matrices, i + 7, j + 35 - 18, 0, this.backgroundHeight, 18, 18);
		this.drawTexture(matrices, i + 7, j + 35, 18, this.backgroundHeight, 18, 18);
		this.drawTexture(matrices, i + 7, j + 35 + 18, 36, this.backgroundHeight, 18, 18);

		float x = i + 28;
		float y = j + 20;
		int strideY = 10;
		int strideX = 52;

		// Draws the text
		this.textRenderer.draw(matrices, Text.translatable("gui.companion_bats.bat.level"), x, y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, Text.translatable("").append(""+(this.level + 1)).append(" [").append(this.maxExpReached ? Text.translatable("gui.companion_bats.bat.max") : Text.translatable("").append(this.currentLevelExp+" / "+this.nextLevelExp)).append("]"), x + strideX, y, 0xFFFFFFFF);

		y += strideY;

		if (this.currentClass != null){
			CompanionBatClass cls = ClientDataManager.classes.get(this.currentClass);
			this.textRenderer.draw(matrices, Text.translatable("gui.companion_bats.bat.class"), x, y, 0xFFFFFFFF);
			this.textRenderer.draw(matrices, cls != null && cls.label != null ? Text.literal(cls.label) : Text.translatable("class." + this.currentClass.replace(":", ".")), x + strideX, y, 0xFFFFFFFF);

			y += strideY;

			this.textRenderer.draw(matrices, Text.translatable("gui.companion_bats.bat.class_level"), x, y, 0xFFFFFFFF);
			this.textRenderer.draw(matrices, Text.translatable("").append(""+(this.classLevel + 1)).append(" [").append(this.maxClassExpReached ? Text.translatable("gui.companion_bats.bat.max") : Text.translatable("").append(this.currentClassLevelExp+" / "+this.nextClassLevelExp)).append("]"), x + strideX, y, 0xFFFFFFFF);
		} else {
			y += strideY;
		}

		y += strideY * 2;

		strideX = 11;

		this.textRenderer.draw(matrices, "❤", x, y, 0xFFFFFFFF);
		int offset = this.textRenderer.draw(matrices, this.currentHealth+" / "+this.maxHealth, x + strideX, y, 0xFFFFFFFF) - (int)x;
		int speedOffset = 110 + (String.valueOf(this.speed).length() <= 3 ? 6 : 0);

		int digits = (String.valueOf(this.attack).length() - 2) * 3;
		offset += ((speedOffset - offset) / 2) - digits - 10;

		this.textRenderer.draw(matrices, "🗡", x + offset , y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, ""+this.attack, x + offset + strideX, y, 0xFFFFFFFF);

		this.textRenderer.draw(matrices, "➶", x + speedOffset, y, 0xFFFFFFFF);
		this.textRenderer.draw(matrices, ""+this.speed, x + speedOffset + strideX - 2, y, 0xFFFFFFFF);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		ItemStack batItemStack = this.inventory.player.getStackInHand(handler.hand);
		EntityData entityData = new EntityData(batItemStack);
		ItemStack armorStack = ItemStack.fromNbt(entityData.getArmor());
		if (!this.armorStack.getItem().equals(armorStack.getItem())) this.setClassLevel(entityData);
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}
 }
