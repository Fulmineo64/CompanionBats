package dev.fulmineo.companion_bats;

public enum CompanionBatClass {
	INFERNO,
	VAMPIRE,
	LOOTER,
	KNIGHT,
	ALCHEMIST,
	DUELIST,
	NINJA,
	MUMMY;

	public String getExpTagName(){
		return this.toString().toLowerCase()+"_exp";
	}
}
