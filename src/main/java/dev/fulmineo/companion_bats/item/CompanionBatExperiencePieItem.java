package dev.fulmineo.companion_bats.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class CompanionBatExperiencePieItem extends Item {
    public CompanionBatExperiencePieItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new TranslatableText("item.companion_bats.experience_pie.when_eaten").formatted(Formatting.AQUA));
		tooltip.add(new TranslatableText("item.companion_bats.experience_pie.exp", CompanionBats.CONFIG.experiencePieGain).formatted(Formatting.GRAY));
	}
}
