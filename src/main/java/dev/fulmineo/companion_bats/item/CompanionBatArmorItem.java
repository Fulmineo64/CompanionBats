package dev.fulmineo.companion_bats.item;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import dev.fulmineo.companion_bats.CompanionBatAbilities;
import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBatClass;
import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels.CompanionBatClassLevel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CompanionBatArmorItem extends Item {
	private CompanionBatClass batClass;
	private String identifier;

	private String entityTexture;
    public CompanionBatArmorItem(String identifier, CompanionBatClass batClass, Properties settings) {
		super(settings);
		this.batClass = batClass;
		this.identifier = identifier;
		this.entityTexture = "textures/entity/bat/armor/" + identifier + ".png";
	}

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		CompanionBatAbilities abilities = new CompanionBatAbilities();
		CompanionBatAbilities permanentAbilities = new CompanionBatAbilities();
		for (CompanionBatClassLevel level : CompanionBatLevels.CLASS_LEVELS.get(batClass)){
			abilities.addFromClassLevel(level);
			if (level.permanent){
				permanentAbilities.addFromClassLevel(level);
			}
		}

		tooltip.add(new TranslationTextComponent("item.companion_bats.armor").withStyle(TextFormatting.GOLD));

		Set<Entry<CompanionBatAbility, Integer>> entrySet = abilities.entrySet();
		if (entrySet.size() > 0){
			tooltip.add(new TranslationTextComponent("item.companion_bats.armor.abilities").withStyle(TextFormatting.AQUA));
			for (Map.Entry<CompanionBatAbility, Integer> entry : entrySet) {
				tooltip.add(entry.getKey().toTranslatedText().withStyle(TextFormatting.GRAY));
			}
		}

		entrySet = permanentAbilities.entrySet();
		if (entrySet.size() > 0){
			tooltip.add(new TranslationTextComponent("item.companion_bats.armor.permanent_ability").withStyle(TextFormatting.AQUA));
			for (Map.Entry<CompanionBatAbility, Integer> entry : entrySet) {
				tooltip.add(entry.getKey().toTranslatedText().withStyle(TextFormatting.GRAY));
			}
		}

		tooltip.add(new TranslationTextComponent("item.companion_bats."+this.identifier+".tooltip").withStyle(TextFormatting.DARK_GRAY).withStyle(TextFormatting.ITALIC));
	}

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getEntityTexture() {
       return new ResourceLocation(CompanionBats.MOD_ID, this.entityTexture);
	}

	public CompanionBatClass getBatClass() {
		return this.batClass;
	}

	public int getProtectionAmount(){
		return 10;
	}
}
