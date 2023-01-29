package dev.fulmineo.companion_bats.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.data.CompanionBatAbilities;
import dev.fulmineo.companion_bats.data.CompanionBatAbility;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

public class CompanionBatAccessoryItem extends Item {
	public CompanionBatAbility ability;
	public String identifier;

	public CompanionBatAccessoryItem(String identifier, CompanionBatAbility ability, Settings settings) {
        super(settings);
		this.ability = ability;
		this.identifier = identifier;
    }

	@Environment(EnvType.CLIENT)
    public Identifier getEntityTexture() {
       return new Identifier(CompanionBats.MOD_ID, "textures/entity/bat/accessory/" + this.identifier + ".png");
	}

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("item.companion_bats.accessory").formatted(Formatting.GOLD));
		CompanionBatAbilities abilities = new CompanionBatAbilities();
		abilities.addFromAccessory(this);
		List<Pair<MutableText, Integer>> list = abilities.toTranslatedList();
		if (list.size() > 0){
			tooltip.add(Text.translatable("item.companion_bats.accessory.ability_when_equipped").formatted(Formatting.AQUA));
			for (Pair<MutableText, Integer> entry: list) {
				tooltip.add(entry.getLeft().formatted(Formatting.GRAY).append(entry.getRight() > 1 ? " "+entry.getRight() : ""));
			}
		}
	}
}
