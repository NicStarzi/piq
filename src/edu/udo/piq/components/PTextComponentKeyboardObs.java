package edu.udo.piq.components;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.util.PInputUtil;
import edu.udo.piq.PKeyboardObs;

public abstract class PTextComponentKeyboardObs implements PKeyboardObs {
	
	public abstract void textTyped(PKeyboard keyboard, String typedString);
	
	public abstract void controlInput(PKeyboard keyboard, Key key);
	
	public abstract boolean skipInput(PKeyboard keyboard, Key key);
	
	public final void keyPressed(PKeyboard keyboard, Key key) {
		if (skipInput(keyboard, key)) {
			return;
		}
		String typedString = PInputUtil.getTypedString(keyboard, key);
		if (typedString != null) {
			textTyped(keyboard, typedString);
		} else {
			controlInput(keyboard, key);
		}
	}
	
	public final void keyTriggered(PKeyboard keyboard, Key key) {
	}
	
	public final void keyReleased(PKeyboard keyboard, Key key) {
	}
}