package dev.fulmineo.companion_bats.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CompanionBatAccessoryItem extends Item {
	private CompanionBatAbility ability;
	private Integer abilityLevel;
	private String identifier;

	public CompanionBatAccessoryItem(String identifier, CompanionBatAbility ability, Integer abilityLevel, Settings settings) {
        super(settings);
		this.ability = ability;
		this.abilityLevel = abilityLevel;
		this.identifier = identifier;
    }

	@Environment(EnvType.CLIENT)
    public Identifier getEntityTexture() {
       return new Identifier(CompanionBats.MOD_ID, "textures/entity/bat/accessory/" + this.identifier + ".png");
	}

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new TranslatableText("item.companion_bats.accessory").formatted(Formatting.GOLD));
		tooltip.add(new TranslatableText("item.companion_bats.accessory.ability_when_equipped").formatted(Formatting.AQUA));
		tooltip.add(this.ability.toTranslatedText().formatted(Formatting.GRAY));
	}

	public CompanionBatAbility getAbility() {
		return this.ability;
	}

	public Integer getAbilityLevel() {
		return this.abilityLevel;
	}
}
