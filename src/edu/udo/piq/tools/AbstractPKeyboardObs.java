package edu.udo.piq.tools;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboardObs;

public abstract class AbstractPKeyboardObs implements PKeyboardObs {
	public void keyPressed(PKeyboard keyboard, Key key) {
	}
	public void keyTriggered(PKeyboard keyboard, Key key) {
	}
	public void keyReleased(PKeyboard keyboard, Key key) {
	}
}