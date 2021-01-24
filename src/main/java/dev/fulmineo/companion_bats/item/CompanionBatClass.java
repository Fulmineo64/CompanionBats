package dev.fulmineo.companion_bats.item;

public enum CompanionBatClass {
	INFERNO,
	VAMPIRE,
	FORAGER,
	KNIGHT,
	ALCHEMIST,
	DUELIST;

	public String getExpTagName(){
		return this.toString().toLowerCase()+"_exp";
	}
}
