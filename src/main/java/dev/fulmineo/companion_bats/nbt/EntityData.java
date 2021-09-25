package dev.fulmineo.companion_bats.nbt;

import dev.fulmineo.companion_bats.CompanionBatClass;
import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;

public class EntityData {
	private NbtCompound tag;

	public EntityData(ItemStack itemStack){
		this.tag = itemStack.getSubNbt("EntityTag");
		if (this.tag == null){
			this.tag = new NbtCompound();
		}
	}

	public EntityData(NbtCompound tag){
		this.tag = tag;
	}

	public static EntityData fromRegularBatEntity(LivingEntity entity){
		NbtCompound entityTag = new NbtCompound();
		entity.saveSelfNbt(entityTag);
		entityTag.putString("id", CompanionBats.MOD_ID+":bat");
		EntityData entityData = new EntityData(entityTag);
		entityData.init();
		entityData.clearAttributes();
		return entityData;
	}

	public static EntityData fromCompanionBatEntity(CompanionBatEntity entity){
		NbtCompound entityTag = new NbtCompound();
		entity.saveSelfNbt(entityTag);
		EntityData entityData = new EntityData(entityTag);
		entityData.clearAttributes();
		return entityData;
	}

	public static NbtCompound getFromStack(ItemStack itemStack){
		return itemStack.getSubNbt("EntityTag");
	}

	public static void toStack(ItemStack itemStack, NbtCompound entityTag){
		itemStack.setSubNbt("EntityTag", entityTag);
	}

	public void init(){
		NbtList armorDropChances = new NbtList();
		for(int i = 0; i < 4; ++i) {
		   armorDropChances.add(NbtFloat.of(0.0F));
		}
		tag.put("ArmorDropChances", armorDropChances);

		NbtList handDropChances = new NbtList();
		handDropChances.add(NbtFloat.of(0.0F));
		handDropChances.add(NbtFloat.of(0.0F));
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

	public NbtList getArmorItems(){
		if (!this.tag.contains("ArmorItems")) {
			NbtList armorItems = new NbtList();
			armorItems.add(new NbtCompound());
			armorItems.add(new NbtCompound());
			armorItems.add(new NbtCompound());
			armorItems.add(new NbtCompound());
			this.tag.put("ArmorItems", armorItems);
		}
		return this.tag.getList("ArmorItems", 10);
	}

	public void putArmorItem(int index, NbtCompound armorItemTag){
		NbtList armorItems = this.getArmorItems();
		armorItems.set(index, armorItemTag);
	}

	public NbtCompound getAccessory(){
		return this.getArmorItems().getCompound(EquipmentSlot.HEAD.getEntitySlotId());
	}

	public NbtCompound getArmor(){
		return this.getArmorItems().getCompound(EquipmentSlot.CHEST.getEntitySlotId());
	}

	public NbtCompound getBundle(){
		return this.getArmorItems().getCompound(EquipmentSlot.FEET.getEntitySlotId());
	}

	public Byte getGuardMode(){
		return this.tag.getByte("GuardMode");
	}

	public void putAccessory(NbtCompound accessoryTag){
		this.putArmorItem(EquipmentSlot.HEAD.getEntitySlotId(), accessoryTag);
	}

	public void putArmor(NbtCompound armorTag){
		this.putArmorItem(EquipmentSlot.CHEST.getEntitySlotId(), armorTag);
	}

	public void putBundle(NbtCompound bundleTag){
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
		// Vanilla Stat attributes
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
		NbtCompound tag = batItemStack.getOrCreateNbt();
		if (!tag.contains("EntityTag")) {
			EntityData entityData = new EntityData(batItemStack);
			entityData.init();
			if (tag.contains("entityData")){
				NbtCompound oldEntityData = tag.getCompound("entityData");
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
