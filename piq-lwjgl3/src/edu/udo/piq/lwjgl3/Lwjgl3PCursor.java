package edu.udo.piq.lwjgl3;

import edu.udo.piq.PCursor;

public class Lwjgl3PCursor implements PCursor {
	
	private final PCursorType type;
	
	public Lwjgl3PCursor(PCursorType cursorType) {
		type = cursorType;
	}
	
	public PCursorType getCursorType() {
		return type;
	}
	
}