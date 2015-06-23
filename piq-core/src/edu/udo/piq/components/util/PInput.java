package edu.udo.piq.components.util;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;

public interface PInput {
	
	public default Object getDefaultIdentifier() {
		return getClass().getName();
	}
	
	public default KeyInputType getKeyInputType() {
		return KeyInputType.TRIGGER;
	}
	
	public Key getInputKey();
	
	public boolean canBeUsed(PKeyboard keyboard);
	
	public static enum KeyInputType {
		PRESS, 
		RELEASE, 
		TRIGGER, 
		;
	}
	
}