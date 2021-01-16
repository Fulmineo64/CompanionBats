package dev.fulmineo.companion_bats.item;

import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class CompanionBatArmorItem extends Item {
    private ArmorMaterial material;
	private String entityTexture;
    public CompanionBatArmorItem(ArmorMaterial material, Item.Settings settings) {
		super(settings);
		this.material = material;

		this.entityTexture = "textures/entity/bat/armor/bat_armor_" + material.getName() + ".png";
		CompanionBats.info(this.entityTexture);
    }

    @Environment(EnvType.CLIENT)
    public Identifier getEntityTexture() {
       return new Identifier(CompanionBats.MOD_ID, this.entityTexture);
	}

	public int getProtectionAmount(){
		return this.material.getProtectionAmount(EquipmentSlot.CHEST);
	}
}
