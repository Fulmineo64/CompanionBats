package dev.fulmineo.companion_bats.item;

import java.util.List;

import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(new TranslatableComponent("item.companion_bats.accessory").withStyle(ChatFormatting.GOLD));
		tooltip.add(new TranslatableComponent("item.companion_bats.accessory.ability_when_equipped").withStyle(ChatFormatting.AQUA));
		tooltip.add(this.ability.toTranslatedText().withStyle(ChatFormatting.GRAY));
	}

	public CompanionBatAbility getAbility() {
		return this.ability;
	}

	public Integer getAbilityLevel() {
		return this.abilityLevel;
	}
}
