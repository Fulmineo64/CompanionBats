package dev.fulmineo.companion_bats.item;

import java.util.List;

import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CompanionBatGemItem extends Item {
	private String identifier;
	private List<CompanionBatAbility> abilities;
	public CompanionBatGemItem(String identifier, List<CompanionBatAbility> abilities, Settings settings) {
        super(settings);
		this.identifier = identifier;
		this.abilities = abilities;
	}

	public boolean hasAbility(CompanionBatAbility ability){
		return this.abilities.contains(ability);
	}

	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
		if (this.identifier.length() > 0) tooltip.add(new TranslatableText("item.companion_bats."+this.identifier+".tooltip"));
	}

	@Environment(EnvType.CLIENT)
    public Identifier getEntityTexture() {
       return new Identifier(CompanionBats.MOD_ID, "textures/entity/bat/armor/"+this.identifier+".png");
	}
}
