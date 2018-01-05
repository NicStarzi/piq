package edu.udo.piq.actions;

public class PActionKey {
	
	private final String name;
	
	public PActionKey(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}