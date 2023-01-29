package dev.fulmineo.companion_bats.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.data.ClientDataManager;
import dev.fulmineo.companion_bats.data.CompanionBatAbilities;
import dev.fulmineo.companion_bats.data.CompanionBatClass;
import dev.fulmineo.companion_bats.data.CompanionBatClassLevel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
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

		tooltip.add(Text.translatable("item.companion_bats.armor").formatted(Formatting.GOLD));

		List<Pair<MutableText, Integer>> list = abilities.toTranslatedList();
		if (list.size() > 0){
			tooltip.add(Text.translatable("item.companion_bats.armor.abilities").formatted(Formatting.AQUA));
			for (Pair<MutableText, Integer> entry: list) {
				tooltip.add(entry.getLeft().formatted(Formatting.GRAY));
			}
		}

		list = permanentAbilities.toTranslatedList();
		if (list.size() > 0){
			tooltip.add(Text.translatable("item.companion_bats.armor.permanent_ability").formatted(Formatting.AQUA));
			for (Pair<MutableText, Integer> entry: list) {
				tooltip.add(entry.getLeft().formatted(Formatting.GRAY));
			}
		}

		tooltip.add((cls.hint != null ? Text.literal(cls.hint) : Text.translatable("item.companion_bats."+this.identifier+".hint")).formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC));
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
