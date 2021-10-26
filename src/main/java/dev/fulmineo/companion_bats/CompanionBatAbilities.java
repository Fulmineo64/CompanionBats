package dev.fulmineo.companion_bats;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import dev.fulmineo.companion_bats.data.CompanionBatClass;
import dev.fulmineo.companion_bats.data.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.data.EntityData;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import net.minecraft.item.ItemStack;

public class CompanionBatAbilities {
	private Map<CompanionBatAbility, Integer> levels = new HashMap<>();

	public void add(CompanionBatAbility ability, Integer levelIncrease){
		if (ability != null && levelIncrease != null){
			Integer current = this.levels.get(ability);
			this.levels.put(ability, (current != null ? current : 0) + levelIncrease);
		}
	}

	public void addFromAccessory(CompanionBatAccessoryItem accessoryItem){
		if (accessoryItem != null){
			this.add(accessoryItem.getAbility(), accessoryItem.getAbilityLevel());
		}
	}

	public void addFromClassLevel(CompanionBatClassLevel classLevel){
		if (classLevel != null){
			this.add(classLevel.ability, classLevel.getAbilityLevelIncrease());
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
		return this.levels.get(ability);
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
				case SLOWNESS:
				case WEAKNESS:
				case WITHER: {
					return level -1;
				}
				default: {
					return level;
				}
			}
		}
		return 0;
	}

	public boolean has(CompanionBatAbility ability) {
		Integer abilityLevel = this.levels.get(ability);
		return abilityLevel != null && abilityLevel > 0;
	}

	public Set<Entry<CompanionBatAbility, Integer>> entrySet() {
		return this.levels.entrySet();
	}
}
