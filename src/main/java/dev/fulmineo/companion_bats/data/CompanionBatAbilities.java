package dev.fulmineo.companion_bats.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class CompanionBatAbilities {
	private Map<CompanionBatAbilityType, Integer> abilities = new HashMap<>();
	public Map<StatusEffect, Pair<Integer, Integer>> onHitEffects = new HashMap<>();
	public Map<StatusEffect, Integer> activeEffects = new HashMap<>();
	public Map<StatusEffect, Integer> auraEffects = new HashMap<>();

	private void addAbility(CompanionBatAbilityType ability, int levelIncrease){
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

	private void addActiveEffect(StatusEffect effect, int levelIncrease) {
		if (effect != null) {
			Integer current = this.activeEffects.get(effect);
			this.activeEffects.put(effect, (current != null ? current : 0) + levelIncrease);
		}
	}

	private void addAuraEffect(StatusEffect effect, int levelIncrease) {
		if (effect != null) {
			Integer current = this.auraEffects.get(effect);
			this.auraEffects.put(effect, (current != null ? current : 0) + levelIncrease);
		}
	}

	public void addFromAccessory(CompanionBatAccessoryItem accessory){
		if (accessory != null){
			this.add(accessory.ability, false);
		}
	}

	public void addFromClassLevel(CompanionBatClassLevel classLevel){
		if (classLevel != null){
			this.add(classLevel.ability, classLevel.permanent);
		}
	}

	public void add(CompanionBatAbility ability, boolean permanent) {
		if (ability != null) {
			if (ability.type == null || ability.type.equals("ability")) {
				this.addAbility(CompanionBatAbilityType.valueOf(ability.id), ability.getIncrement());
			} else if (ability.type.equals("onHitEffect")) {
				StatusEffect effect = Registries.STATUS_EFFECT.get(new Identifier(ability.id));
				this.addOnHitEffect(effect, ability.getIncrement(), ability.duration);
			} else if (ability.type.equals("activeEffect")) {
				StatusEffect effect = Registries.STATUS_EFFECT.get(new Identifier(ability.id));
				this.addActiveEffect(effect, ability.getIncrement());
			} else if (ability.type.equals("auraEffect")) {
				StatusEffect effect = Registries.STATUS_EFFECT.get(new Identifier(ability.id));
				this.addAuraEffect(effect, ability.getIncrement());
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

	public Integer get(CompanionBatAbilityType ability) {
		return this.abilities.get(ability);
	}

	public int getValue(CompanionBatAbilityType ability) {
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

	public boolean hasAbility(CompanionBatAbilityType ability) {
		Integer abilityLevel = this.abilities.get(ability);
		return abilityLevel != null && abilityLevel > 0;
	}

	public boolean hasOnHitEffect(StatusEffect effect) {
		Pair<Integer, Integer> onHitEffectLevel = this.onHitEffects.get(effect);
		return onHitEffectLevel != null && onHitEffectLevel.getLeft() > 0;
	}

	public boolean hasActiveEffect(StatusEffect effect) {
		Integer activeEffectLevel = this.activeEffects.get(effect);
		return activeEffectLevel != null && activeEffectLevel > 0;
	}

	public boolean hasAuraEffect(StatusEffect effect) {
		Integer auraEffectLevel = this.auraEffects.get(effect);
		return auraEffectLevel != null && auraEffectLevel > 0;
	}

	public boolean hasStatusEffect(StatusEffect effect) {
		return this.hasActiveEffect(effect) || this.hasAuraEffect(effect);
	}

	public boolean has(CompanionBatAbility ability) {
		if (ability.type == null || ability.type == "ability") {
			return this.hasAbility(CompanionBatAbilityType.valueOf(ability.id));
		} else if (ability.type == "onHitEffect") {
			return this.hasOnHitEffect(Registries.STATUS_EFFECT.get(new Identifier(ability.id)));
		} else if (ability.type == "activeEffect") {
			return this.hasActiveEffect(Registries.STATUS_EFFECT.get(new Identifier(ability.id)));
		} else if (ability.type == "auraEffect") {
			return this.hasAuraEffect(Registries.STATUS_EFFECT.get(new Identifier(ability.id)));
		} else {
			return false;
		}
	}

	public List<Pair<MutableText, Integer>> toTranslatedList() {
		List<Pair<MutableText, Integer>> list = new ArrayList<>();
		for (Entry<CompanionBatAbilityType, Integer> entry: this.abilities.entrySet()) {
			list.add(new Pair<MutableText, Integer>(entry.getKey().toTranslatedText(), entry.getValue()));
		}
		for (Entry<StatusEffect, Pair<Integer, Integer>> entry: this.onHitEffects.entrySet()) {
			list.add(new Pair<MutableText, Integer>(Text.translatable("ability.companion_bats.on_hit_effect", Text.translatable(entry.getKey().getTranslationKey())), entry.getValue().getLeft()));
		}
		for (Entry<StatusEffect, Integer> entry: this.activeEffects.entrySet()) {
			list.add(new Pair<MutableText, Integer>(Text.translatable("ability.companion_bats.active_effect", Text.translatable(entry.getKey().getTranslationKey())), entry.getValue()));
		}
		for (Entry<StatusEffect, Integer> entry: this.auraEffects.entrySet()) {
			list.add(new Pair<MutableText, Integer>(Text.translatable("ability.companion_bats.aura_effect", Text.translatable(entry.getKey().getTranslationKey())), entry.getValue()));
		}
		return list;
	}

	public void applyOnHitEffects(LivingEntity target) {
		for (Entry<StatusEffect, Pair<Integer, Integer>> entry: this.onHitEffects.entrySet()) {
			Pair<Integer, Integer> levelAndDuration = entry.getValue();
			target.addStatusEffect(new StatusEffectInstance(entry.getKey(), levelAndDuration.getRight(), levelAndDuration.getLeft() - 1));
		}
	}

	public void applyActiveEffects(CompanionBatEntity entity, int duration) {
		if (this.activeEffects.size() == 0) return;
		for (Entry<StatusEffect, Integer> entry: this.activeEffects.entrySet()) {
			entity.addStatusEffect(new StatusEffectInstance(entry.getKey(), duration, entry.getValue() - 1, false, false));
		}
	}

	public void applyAuraEffects(CompanionBatEntity entity, int duration) {
		if (this.auraEffects.size() == 0) return;
		LivingEntity owner = entity.getOwner();
		for (Entry<StatusEffect, Integer> entry: this.auraEffects.entrySet()) {
			entity.addStatusEffect(new StatusEffectInstance(entry.getKey(), duration, entry.getValue() - 1, false, false));
			if (owner != null) owner.addStatusEffect(new StatusEffectInstance(entry.getKey(), duration, entry.getValue() - 1, false, false));
		}
	}
}
