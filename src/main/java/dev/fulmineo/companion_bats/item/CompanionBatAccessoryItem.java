package dev.fulmineo.companion_bats.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CompanionBatAccessoryItem extends Item {
	private CompanionBatAbility ability;
	private Integer abilityLevel;
	private String identifier;

	public CompanionBatAccessoryItem(String identifier, CompanionBatAbility ability, Integer abilityLevel, Properties settings) {
        super(settings);
		this.ability = ability;
		this.abilityLevel = abilityLevel;
		this.identifier = identifier;
    }

	@OnlyIn(Dist.CLIENT)
    public ResourceLocation getEntityTexture() {
       return new ResourceLocation(CompanionBats.MOD_ID, "textures/entity/bat/accessory/" + this.identifier + ".png");
	}

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent("item.companion_bats.accessory").withStyle(TextFormatting.GOLD));
		tooltip.add(new TranslationTextComponent("item.companion_bats.accessory.ability_when_equipped").withStyle(TextFormatting.AQUA));
		tooltip.add(this.ability.toTranslatedText().withStyle(TextFormatting.GRAY));
	}

	public CompanionBatAbility getAbility() {
		return this.ability;
	}

	public Integer getAbilityLevel() {
		return this.abilityLevel;
	}
}
