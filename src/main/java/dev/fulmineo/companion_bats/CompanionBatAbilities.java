package dev.fulmineo.companion_bats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dev.fulmineo.companion_bats.data.CompanionBatClass;
import dev.fulmineo.companion_bats.data.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.data.EntityData;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

public class CompanionBatAbilities {
	private Map<CompanionBatAbility, Integer> abilities = new HashMap<>();
	public Map<StatusEffect, Pair<Integer, Integer>> onHitEffects = new HashMap<>();

	private void addAbility(CompanionBatAbility ability, int levelIncrease){
		if (ability != null){
			Integer current = this.abilities.get(ability);
			this.abilities.put(ability, (current != null ? current : 0) + levelIncrease);
		}
	}

	private void addOnHitEffect(StatusEffect effect, int levelIncrease, Integer duration) {
		if (effect != null) {
			Pair<Integer, Integer> current = this.onHitEffects.get(effect);
			if (current == null) current = new Pair<Integer,Integer>(0, 0);
			current.setLeft(current.getLeft() + levelIncrease);
			current.setRight(duration);
			this.onHitEffects.put(effect, current);
		}
	}

	public void addFromAccessory(CompanionBatAccessoryItem accessoryItem){
		if (accessoryItem != null){
			this.addAbility(accessoryItem.getAbility(), accessoryItem.getAbilityLevel());
		}
	}

	public void addFromClassLevel(CompanionBatClassLevel classLevel){
		if (classLevel != null && classLevel.ability != null){
			if (classLevel.abilityType == null || classLevel.abilityType.equals("ability")) {
				this.addAbility(CompanionBatAbility.valueOf(classLevel.ability), classLevel.getAbilityIncrement());
			} else if (classLevel.abilityType.equals("onHitEffect")) {
				StatusEffect effect = Registry.STATUS_EFFECT.get(new Identifier(classLevel.ability));
				this.addOnHitEffect(effect, classLevel.getAbilityIncrement(), classLevel.duration);
			}
		}
	}

	public void setFromNbt(Map<String, CompanionBatClass> classes, EntityData entityData) {
		String currentClass = "";
		ItemStack armorStack = ItemStack.fromNbt(entityData.getArmor());
		if (armorStack.getItem() instanceof CompanionBatArmorItem) {
			CompanionBatArmorItem armor = (CompanionBatArmorItem) armorStack.getItem();
			currentClass = armor.getClassName();
		}
		for (Entry<String, CompanionBatClass> entry: classes.entrySet()) {
			float classExp = entityData.getClassExp(entry.getKey());
			for (CompanionBatClassLevel level : entry.getValue().levels) {
				if (level.totalExp > classExp) {
					break;
				}
				if (level.permanent || currentClass.equals(entry.getKey())) {
					this.addFromClassLevel(level);
				}
			}
		}
		ItemStack accessoryStack = ItemStack.fromNbt(entityData.getAccessory());
		if (accessoryStack.getItem() instanceof CompanionBatAccessoryItem) {
			this.addFromAccessory((CompanionBatAccessoryItem) accessoryStack.getItem());
		}
	}

	public Integer get(CompanionBatAbility ability) {
		return this.abilities.get(ability);
	}

	public int getValue(CompanionBatAbility ability) {
		Integer level = this.get(ability);
		if (level != null) {
			switch (ability) {
				case BLOCK_ATTACK: {
					return level * CompanionBats.CONFIG.blockAttackMultiplier;
				}
				case BURN: {
					return level * CompanionBats.CONFIG.burnMultiplier;
				}
				case INCREASED_ARMOR: {
					return level * CompanionBats.CONFIG.increasedArmorMultiplier;
				}
				case INCREASED_ATTACK: {
					return level * CompanionBats.CONFIG.increasedAttackMultiplier;
				}
				case INCREASED_SPEED: {
					return level * CompanionBats.CONFIG.increasedSpeedMultiplier;
				}
				case LIFESTEAL: {
					return level * CompanionBats.CONFIG.lifestealMultiplier;
				}
				case TELEPORT: {
					return level * CompanionBats.CONFIG.teleportMultiplier;
				}
				default: {
					return level;
				}
			}
		}
		return 0;
	}

	public boolean hasAbility(CompanionBatAbility ability) {
		Integer abilityLevel = this.abilities.get(ability);
		return abilityLevel != null && abilityLevel > 0;
	}

	public boolean hasOnHitEffect(StatusEffect effect) {
		Pair<Integer, Integer> onHitEffectLevel = this.onHitEffects.get(effect);
		return onHitEffectLevel != null && onHitEffectLevel.getLeft() > 0;
	}

	public boolean has(String abilityType, String ability) {
		if (abilityType == null || abilityType == "ability") {
			return this.hasAbility(CompanionBatAbility.valueOf(ability));
		} else if (abilityType == "onHitEffect") {
			return this.hasOnHitEffect(Registry.STATUS_EFFECT.get(new Identifier(ability)));
		} else {
			return false;
		}
	}

	public List<Pair<MutableText, Integer>> toTranslatedList() {
		List<Pair<MutableText, Integer>> list = new ArrayList<>();
		for (Entry<CompanionBatAbility, Integer> entry: this.abilities.entrySet()) {
			list.add(new Pair<MutableText, Integer>(entry.getKey().toTranslatedText(), entry.getValue()));
		}
		for (Entry<StatusEffect, Pair<Integer, Integer>> entry: this.onHitEffects.entrySet()) {
			list.add(new Pair<MutableText, Integer>(new TranslatableText("ability.companion_bats.on_hit").append(" ").append(new TranslatableText(entry.getKey().getTranslationKey())), entry.getValue().getLeft()));
		}
		return list;
	}

	public void applyOnHitEffects(LivingEntity target) {
		for (Entry<StatusEffect, Pair<Integer, Integer>> entry: this.onHitEffects.entrySet()) {
			Pair<Integer, Integer> levelAndDuration = entry.getValue();
			target.addStatusEffect(new StatusEffectInstance(entry.getKey(), levelAndDuration.getRight(), levelAndDuration.getLeft() - 1));
		}
	}
}
