package edu.udo.piq;

import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;

public interface PKeyboardObs {
	
	public default void keyPressed(PKeyboard keyboard, Key key) {}
	
	public default void keyTriggered(PKeyboard keyboard, Key key) {}
	
	public default void keyReleased(PKeyboard keyboard, Key key) {}
	
	public default void modifierToggled(PKeyboard keyboard, Modifier modifier) {}
	
	public default void stringTyped(PKeyboard keyboard, String string) {}
	
}