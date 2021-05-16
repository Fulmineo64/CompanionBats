package dev.fulmineo.companion_bats.nbt;

import dev.fulmineo.companion_bats.CompanionBatClass;
import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;

public class EntityData {
	private CompoundNBT tag;

	public EntityData(ItemStack itemStack){
		this.tag = itemStack.getTagElement("EntityTag");
		if (this.tag == null){
			this.tag = new CompoundNBT();
		}
	}

	public EntityData(CompoundNBT tag){
		this.tag = tag;
	}

	public static EntityData fromRegularBatEntity(LivingEntity entity){
		CompoundNBT entityTag = new CompoundNBT();
		entity.save(entityTag);
		entityTag.putString("id", CompanionBats.MOD_ID+":bat");
		EntityData entityData = new EntityData(entityTag);
		entityData.init();
		entityData.clearAttributes();
		return entityData;
	}

	public static EntityData fromCompanionBatEntity(CompanionBatEntity entity){
		CompoundNBT entityTag = new CompoundNBT();
		entity.save(entityTag);
		EntityData entityData = new EntityData(entityTag);
		entityData.clearAttributes();
		return entityData;
	}

	public static CompoundNBT getFromStack(ItemStack itemStack){
		return itemStack.getTagElement("EntityTag");
	}

	public static void toStack(ItemStack itemStack, CompoundNBT entityTag){
		CompoundNBT tag = itemStack.getTag();
		tag.put("EntityTag", entityTag);
	}

	public void init(){
		ListNBT armorDropChances = new ListNBT();
		for(int i = 0; i < 4; ++i) {
		   armorDropChances.add(FloatNBT.valueOf(0.0F));
		}
		tag.put("ArmorDropChances", armorDropChances);

		ListNBT handDropChances = new ListNBT();
		handDropChances.add(FloatNBT.valueOf(0.0F));
		handDropChances.add(FloatNBT.valueOf(0.0F));
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

	public ListNBT getArmorItems(){
		if (!this.tag.contains("ArmorItems")) {
			ListNBT armorItems = new ListNBT();
			armorItems.add(new CompoundNBT());
			armorItems.add(new CompoundNBT());
			armorItems.add(new CompoundNBT());
			armorItems.add(new CompoundNBT());
			this.tag.put("ArmorItems", armorItems);
		}
		return this.tag.getList("ArmorItems", 10);
	}

	public void putArmorItem(int index, CompoundNBT armorItemTag){
		ListNBT armorItems = this.getArmorItems();
		armorItems.set(index, armorItemTag);
	}

	public CompoundNBT getAccessory(){
		return this.getArmorItems().getCompound(EquipmentSlotType.HEAD.getIndex());
	}

	public CompoundNBT getArmor(){
		return this.getArmorItems().getCompound(EquipmentSlotType.CHEST.getIndex());
	}

	public CompoundNBT getBundle(){
		return this.getArmorItems().getCompound(EquipmentSlotType.FEET.getIndex());
	}

	public Byte getGuardMode(){
		return this.tag.getByte("GuardMode");
	}

	public void putAccessory(CompoundNBT accessoryTag){
		this.putArmorItem(EquipmentSlotType.HEAD.getIndex(), accessoryTag);
	}

	public void putArmor(CompoundNBT armorTag){
		this.putArmorItem(EquipmentSlotType.CHEST.getIndex(), armorTag);
	}

	public void putBundle(CompoundNBT bundleTag){
		this.putArmorItem(EquipmentSlotType.FEET.getIndex(), bundleTag);
	}

	public void putGuardMode(Byte mode){
		this.tag.putByte("GuardMode", mode);
	}

	public void putOwner(PlayerEntity player){
		this.tag.putUUID("Owner", player.getUUID());
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
		CompoundNBT tag = batItemStack.getOrCreateTag();
		if (!tag.contains("EntityTag")) {
			EntityData entityData = new EntityData(batItemStack);
			entityData.init();
			if (tag.contains("entityData")){
				CompoundNBT oldEntityData = tag.getCompound("entityData");
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
