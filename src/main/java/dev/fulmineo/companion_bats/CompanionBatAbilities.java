package dev.fulmineo.companion_bats;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import dev.fulmineo.companion_bats.entity.CompanionBatLevels;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import dev.fulmineo.companion_bats.nbt.EntityData;
import net.minecraft.item.ItemStack;

public class CompanionBatAbilities {
	public static final int BLOCK_ATTACK_MULTIPLIER = 10;
	public static final int BURN_MULTIPLIER = 3;
	public static final int INCREASED_ARMOR_MULTIPLIER = 2;
	public static final int INCREASED_ATTACK_MULTIPLIER = 10;
	public static final int INCREASED_SPEED_MULTIPLIER = 15;
	public static final int LIFESTEAL_MULTIPLIER = 10;
	public static final int TELEPORT_MULTIPLIER = 25;

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
			this.add(classLevel.ability, classLevel.abilityLevelIncrease);
		}
	}

	public void setFromNbt(EntityData entityData) {
		CompanionBatClass currentClass = null;
		ItemStack armorStack = ItemStack.of(entityData.getArmor());
		if (armorStack.getItem() instanceof CompanionBatArmorItem) {
			CompanionBatArmorItem armor = (CompanionBatArmorItem) armorStack.getItem();
			currentClass = armor.getBatClass();
		}
		for (CompanionBatClass batClass : CompanionBatClass.values()) {
			float classExp = entityData.getClassExp(batClass);
			for (CompanionBatClassLevel level : CompanionBatLevels.CLASS_LEVELS.get(batClass)) {
				if (level.totalExpNeeded > classExp) {
					break;
				}
				if (currentClass == batClass || level.permanent) {
					this.addFromClassLevel(level);
				}
			}
		}
		ItemStack accessoryStack = ItemStack.of(entityData.getAccessory());
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
					return level * BLOCK_ATTACK_MULTIPLIER;
				}
				case BURN: {
					return level * BURN_MULTIPLIER;
				}
				case INCREASED_ARMOR: {
					return level * INCREASED_ARMOR_MULTIPLIER;
				}
				case INCREASED_ATTACK: {
					return level * INCREASED_ATTACK_MULTIPLIER;
				}
				case INCREASED_SPEED: {
					return level * INCREASED_SPEED_MULTIPLIER;
				}
				case LIFESTEAL: {
					return level * LIFESTEAL_MULTIPLIER;
				}
				case TELEPORT: {
					return level * TELEPORT_MULTIPLIER;
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
