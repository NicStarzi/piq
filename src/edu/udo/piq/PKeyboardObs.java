package edu.udo.piq;

import edu.udo.piq.PKeyboard.Key;

public interface PKeyboardObs {
	
	public void keyPressed(PKeyboard keyboard, Key key);
	
	public void keyTriggered(PKeyboard keyboard, Key key);
	
	public void keyReleased(PKeyboard keyboard, Key key);
	
}