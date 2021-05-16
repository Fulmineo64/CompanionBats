package dev.fulmineo.companion_bats.item;

import java.util.List;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CompanionBatExperiencePieItem extends Item {
    public CompanionBatExperiencePieItem(Properties settings) {
		super(settings);
	}
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent("item.companion_bats.experience_pie.when_eaten").withStyle(TextFormatting.AQUA));
		tooltip.add(new TranslationTextComponent("item.companion_bats.experience_pie.exp", CompanionBatEntity.EXPERIENCE_PIE_GAIN).withStyle(TextFormatting.GRAY));
	}
}
