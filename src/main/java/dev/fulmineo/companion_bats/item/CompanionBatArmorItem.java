package dev.fulmineo.companion_bats.item;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBatAbilities;
import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.data.ClientDataManager;
import dev.fulmineo.companion_bats.data.CompanionBatClass;
import dev.fulmineo.companion_bats.data.CompanionBatClassLevel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CompanionBatArmorItem extends Item {
	private String className;
	private String identifier;

	private String entityTexture;
    public CompanionBatArmorItem(String identifier, String className, Item.Settings settings) {
		super(settings);
		this.className = className;
		this.identifier = identifier;
		this.entityTexture = "textures/entity/bat/armor/" + identifier + ".png";
	}

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		CompanionBatClass cls = ClientDataManager.classes.get(this.className);
		if (cls == null) return;
		CompanionBatAbilities abilities = new CompanionBatAbilities();
		CompanionBatAbilities permanentAbilities = new CompanionBatAbilities();
		for (CompanionBatClassLevel level : cls.levels){
			abilities.addFromClassLevel(level);
			if (level.permanent){
				permanentAbilities.addFromClassLevel(level);
			}
		}

		tooltip.add(new TranslatableText("item.companion_bats.armor").formatted(Formatting.GOLD));

		Set<Entry<CompanionBatAbility, Integer>> entrySet = abilities.entrySet();
		if (entrySet.size() > 0){
			tooltip.add(new TranslatableText("item.companion_bats.armor.abilities").formatted(Formatting.AQUA));
			for (Map.Entry<CompanionBatAbility, Integer> entry : entrySet) {
				tooltip.add(entry.getKey().toTranslatedText().formatted(Formatting.GRAY));
			}
		}

		entrySet = permanentAbilities.entrySet();
		if (entrySet.size() > 0){
			tooltip.add(new TranslatableText("item.companion_bats.armor.permanent_ability").formatted(Formatting.AQUA));
			for (Map.Entry<CompanionBatAbility, Integer> entry : entrySet) {
				tooltip.add(entry.getKey().toTranslatedText().formatted(Formatting.GRAY));
			}
		}

		tooltip.add((cls.hint != null ? new LiteralText(cls.hint) : new TranslatableText("item.companion_bats."+this.identifier+".hint")).formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC));
	}

    @Environment(EnvType.CLIENT)
    public Identifier getEntityTexture() {
       return new Identifier(CompanionBats.MOD_ID, this.entityTexture);
	}

	public String getClassName() {
		return this.className;
	}

	public int getProtectionAmount(){
		return CompanionBats.CONFIG.armorProtectionAmount;
	}
}
