package dev.fulmineo.companion_bats.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class CompanionBatAbility {
	public String name;
	public String type;
	public String id;
	public int increment;
	public int duration;
	public CompanionBatAbilityCondition[] conditions;

	public int getIncrement() {
		return this.increment == 0 ? 1 : this.increment;
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		if (this.name != null) nbt.putString("name", this.name);
		if (this.type != null) nbt.putString("type", this.type);
		if (this.id != null) nbt.putString("id", this.id);
		nbt.putInt("increment", this.increment);
		nbt.putInt("duration", this.duration);
		if (this.conditions != null) {
			NbtList conditions = new NbtList();
			for (CompanionBatAbilityCondition condition: this.conditions) {
				conditions.add(condition.writeNbt(new NbtCompound()));
			}
			nbt.put("conditions", conditions);
		}
		return nbt;
	}

	public static CompanionBatAbility fromNbt(NbtCompound nbt) {
		CompanionBatAbility ability = new CompanionBatAbility();
		if (nbt.contains("name")) ability.name = nbt.getString("name");
		if (nbt.contains("type")) ability.type = nbt.getString("type");
		if (nbt.contains("id")) ability.id = nbt.getString("id");
		ability.increment = nbt.getInt("increment");
		ability.duration = nbt.getInt("duration");
		return ability;
	}
}
