package edu.udo.piq;

import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;

public interface PKeyboardObs {
	
	/**
	 * This method is called (potentially repeatedly) while 
	 * a key is being pressed.<br>
	 * @param keyboard		the keyboard which had its key pressed. This is never null
	 * @param key			the key that was pressed. This is never null
	 */
	public default void onKeyPressed(PKeyboard keyboard, Key key) {}
	
	/**
	 * This method is called when a key, which was not pressed 
	 * before, has just been pressed. Between two calls of this 
	 * method for the same key there must have been a call to 
	 * {@link #onKeyReleased(PKeyboard, Key)} for the key as well.<br>
	 * @param keyboard		the keyboard which had its key triggered. This is never null
	 * @param key			the key that was triggered. This is never null
	 */
	public default void onKeyTriggered(PKeyboard keyboard, Key key) {}
	
	public default void onKeyReleased(PKeyboard keyboard, Key key) {}
	
	public default void onModifierToggled(PKeyboard keyboard, Modifier modifier) {}
	
	/**
	 * This method is called when a text was typed on the 
	 * keyboard.<br>
	 * @param keyboard
	 * @param string
	 */
	public default void onStringTyped(PKeyboard keyboard, String string) {}
	
}