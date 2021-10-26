package dev.fulmineo.companion_bats.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class CompanionBatClass {
	public String label;
	public String hint;
	public CompanionBatClassLevel[] levels;

	public CompanionBatClass(String label, String hint, CompanionBatClassLevel[] levels) {
		this.label = label;
		this.hint = hint;
		this.levels = levels;
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		if (this.label != null) nbt.putString("label", this.label);
		if (this.hint != null) nbt.putString("hint", this.label);
		NbtList levels = new NbtList();
		for (CompanionBatClassLevel cl: this.levels) {
			levels.add(cl.writeNbt(new NbtCompound()));
		}
		nbt.put("levels", levels);
		return nbt;
	}

	public static CompanionBatClass fromNbt(NbtCompound nbt) {
		NbtList list = nbt.getList("levels", NbtElement.COMPOUND_TYPE);
		CompanionBatClassLevel[] levels = new CompanionBatClassLevel[list.size()];
		for (int i = 0; i < list.size(); i++) {
			levels[i] = CompanionBatClassLevel.fromNbt((NbtCompound)list.get(i));
		}
		return new CompanionBatClass(nbt.contains("label") ? nbt.getString("label") : null, nbt.contains("hint") ? nbt.getString("hint") : null, levels);
	}
}
