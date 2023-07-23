package dev.fulmineo.companion_bats.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

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

	public void migrate() {
		// Migration code from 1.17.x to 1.18.0
		// TODO: Remove me after a while
		if (this.tag.getCompound("ClassExp").getSize() == 0) {
			NbtCompound classExp = new NbtCompound();
			for (String key: ServerDataManager.classes.keySet()) {
				String oldKey = new Identifier(key).getPath();
				oldKey = oldKey.substring(0, 1).toUpperCase() + oldKey.substring(1) + "Exp";
				classExp.putInt(key, this.tag.getInt(oldKey));
			}
			this.tag.put("ClassExp", classExp);
		}
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
		tag.put("ClassExp", new NbtCompound());
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

	public int getClassExp(String className){
		return this.tag.getCompound("ClassExp").getInt(className);
	}

	public Map<String, Integer> getClassesExp() {
		Map<String, Integer> classesExp = new HashMap<>();
		NbtCompound nbt = this.tag.getCompound("ClassExp");
		for (String key: nbt.getKeys()) {
			classesExp.put(key, nbt.getInt(key));
		}
		return classesExp;
	}

	public void putClassExp(String className, int classExp){
		this.tag.getCompound("ClassExp").putInt(className, classExp);
	}

	public void putClassesExp(Map<String, Integer> classesExp) {
		this.tag.put("ClassExp", new NbtCompound());
		for (Entry<String, Integer> entry : classesExp.entrySet()) {
			if (entry.getValue() > 0) {
				this.putClassExp(entry.getKey(), entry.getValue());
			}
		}
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

	public static void createIfMissing(ItemStack batItemStack){
		NbtCompound tag = batItemStack.getOrCreateNbt();
		if (!tag.contains("EntityTag")) {
			EntityData entityData = new EntityData(batItemStack);
			entityData.init();
			entityData.putHealth(CompanionBats.CONFIG.baseHealth);
			entityData.toStack(batItemStack);
		}
	}
}
