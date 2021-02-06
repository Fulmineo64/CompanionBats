package dev.fulmineo.companion_bats.item;

import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class CompanionBatArmorItem extends Item {
	private CompanionBatClass batClass;

	private String entityTexture;
    public CompanionBatArmorItem(String identifier, CompanionBatClass batClass, Item.Settings settings) {
		super(settings);
		this.batClass = batClass;
		this.entityTexture = "textures/entity/bat/armor/" + identifier + ".png";
	}

    @Environment(EnvType.CLIENT)
    public Identifier getEntityTexture() {
       return new Identifier(CompanionBats.MOD_ID, this.entityTexture);
	}

	public CompanionBatClass getBatClass() {
		return this.batClass;
	}

	public int getProtectionAmount(){
		return 10;
	}
}
