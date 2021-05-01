package dev.fulmineo.companion_bats.nbt;

import dev.fulmineo.companion_bats.CompanionBatClass;
import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;

public class EntityData {
	private CompoundTag tag;

	public EntityData(ItemStack itemStack){
		this.tag = itemStack.getSubTag("EntityTag");
		if (this.tag == null){
			this.tag = new CompoundTag();
		}
	}

	public EntityData(CompoundTag tag){
		this.tag = tag;
	}

	public static EntityData fromRegularBatEntity(LivingEntity entity){
		CompoundTag entityTag = new CompoundTag();
		entity.saveSelfToTag(entityTag);
		entityTag.putString("id", CompanionBats.MOD_ID+":bat");
		EntityData entityData = new EntityData(entityTag);
		entityData.init();
		entityData.clearAttributes();
		return entityData;
	}

	public static EntityData fromCompanionBatEntity(CompanionBatEntity entity){
		CompoundTag entityTag = new CompoundTag();
		entity.saveSelfToTag(entityTag);
		EntityData entityData = new EntityData(entityTag);
		entityData.clearAttributes();
		return entityData;
	}

	public static CompoundTag getFromStack(ItemStack itemStack){
		return itemStack.getSubTag("EntityTag");
	}

	public static void toStack(ItemStack itemStack, CompoundTag entityTag){
		itemStack.putSubTag("EntityTag", entityTag);
	}

	public void init(){
		ListTag armorDropChances = new ListTag();
		for(int i = 0; i < 4; ++i) {
		   armorDropChances.add(FloatTag.of(0.0F));
		}
		tag.put("ArmorDropChances", armorDropChances);

		ListTag handDropChances = new ListTag();
		handDropChances.add(FloatTag.of(0.0F));
		handDropChances.add(FloatTag.of(0.0F));
		tag.put("HandDropChances", handDropChances);
	}

	public void toStack(ItemStack itemStack){
		toStack(itemStack, this.tag);
	}

	public float getHealth(){
		return this.tag.getFloat("Health");
	}

	public void putHealth(float health){
		this.tag.putFloat("Health", health);
	}

	public int getExp(){
		return this.tag.getInt("Exp");
	}

	public void putExp(int exp){
		this.tag.putInt("Exp", exp);
	}

	public int getClassExp(CompanionBatClass batClass){
		return this.tag.getInt(getClassExpName(batClass));
	}

	public void putClassExp(CompanionBatClass batClass, int classExp){
		this.tag.putInt(getClassExpName(batClass), classExp);
	}

	public ListTag getArmorItems(){
		if (!this.tag.contains("ArmorItems")) {
			ListTag armorItems = new ListTag();
			armorItems.add(new CompoundTag());
			armorItems.add(new CompoundTag());
			armorItems.add(new CompoundTag());
			armorItems.add(new CompoundTag());
			this.tag.put("ArmorItems", armorItems);
		}
		return this.tag.getList("ArmorItems", 10);
	}

	public void putArmorItem(int index, CompoundTag armorItemTag){
		ListTag armorItems = this.getArmorItems();
		armorItems.set(index, armorItemTag);
	}

	public CompoundTag getAccessory(){
		return this.getArmorItems().getCompound(EquipmentSlot.HEAD.getEntitySlotId());
	}

	public CompoundTag getArmor(){
		return this.getArmorItems().getCompound(EquipmentSlot.CHEST.getEntitySlotId());
	}

	public CompoundTag getBundle(){
		return this.getArmorItems().getCompound(EquipmentSlot.FEET.getEntitySlotId());
	}

	public Byte getGuardMode(){
		return this.tag.getByte("GuardMode");
	}

	public void putAccessory(CompoundTag accessoryTag){
		this.putArmorItem(EquipmentSlot.HEAD.getEntitySlotId(), accessoryTag);
	}

	public void putArmor(CompoundTag armorTag){
		this.putArmorItem(EquipmentSlot.CHEST.getEntitySlotId(), armorTag);
	}

	public void putBundle(CompoundTag bundleTag){
		this.putArmorItem(EquipmentSlot.FEET.getEntitySlotId(), bundleTag);
	}

	public void putGuardMode(Byte mode){
		this.tag.putByte("GuardMode", mode);
	}

	public void putOwner(PlayerEntity player){
		this.tag.putUuid("Owner", player.getUuid());
	}

	public void clearAttributes(){
		// Vanilla Position attributes
		this.tag.remove("Pos");
		this.tag.remove("Motion");
		this.tag.remove("Rotation");
		// Vanilla Stat attibrutes
		this.tag.remove("Attributes");
		// Vanilla Negative effects
		this.tag.remove("Fire");
		// Probably from some mod
		this.tag.remove("BoundingBox");
	}

	public boolean hasPotionTicks(){
		return this.tag.contains("emergencyPotionTicks");
	}

	public int getEffectPotionTicks(){
		return this.tag.getInt("effectPotionTicks");
	}

	public int getEmergencyPotionTicks(){
		return this.tag.getInt("emergencyPotionTicks");
	}

	public void putEffectPotionTicks(int effectPotionTicks){
		this.tag.putInt("effectPotionTicks", effectPotionTicks);
	}

	public void putEmergencyPotionTicks(int emergencyPotionTicks){
		this.tag.putInt("emergencyPotionTicks", emergencyPotionTicks);
	}

	public static String getCapitalizedClassName(CompanionBatClass batClass){
		String lower = batClass.toString().toLowerCase();
		return lower.substring(0, 1).toUpperCase() + lower.substring(1);
	}

	public static String getClassExpName(CompanionBatClass batClass){
		return getCapitalizedClassName(batClass) + "Exp";
	}

	public static void createIfMissing(ItemStack batItemStack){
		CompoundTag tag = batItemStack.getOrCreateTag();
		if (!tag.contains("EntityTag")) {
			EntityData entityData = new EntityData(batItemStack);
			entityData.init();
			if (tag.contains("entityData")){
				CompoundTag oldEntityData = tag.getCompound("entityData");
				entityData.putHealth(oldEntityData.getFloat("health"));
				entityData.putAccessory(oldEntityData.getCompound("accessory"));
				entityData.putArmor(oldEntityData.getCompound("armor"));
				entityData.putBundle(oldEntityData.getCompound("bundle"));
				entityData.putExp(oldEntityData.getInt("exp"));
				for (CompanionBatClass batClass: CompanionBatClass.values()){
					entityData.putClassExp(batClass, oldEntityData.getInt(batClass.toString().toLowerCase() + "_exp"));
				}
			} else {
				entityData.putHealth(CompanionBatEntity.BASE_HEALTH);
			}
			entityData.toStack(batItemStack);
		}
	}
}
