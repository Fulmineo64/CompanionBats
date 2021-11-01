package dev.fulmineo.companion_bats.data;

import net.minecraft.nbt.NbtCompound;

public class CompanionBatAbilityCondition {
	public String type;

	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putString("type", this.type);
		return nbt;
	}

	public static CompanionBatAbilityCondition fromNbt(NbtCompound nbt) {
		CompanionBatAbilityCondition condition = new CompanionBatAbilityCondition();
		condition.type = nbt.getString("type");
		return condition;
	}
}
