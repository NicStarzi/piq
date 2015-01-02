package edu.udo.piq.components;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboardObs;

public abstract class PTextComponentKeyboardObs implements PKeyboardObs {
	
	public abstract void textTyped(PKeyboard keyboard, String typedString);
	
	public abstract void controlInput(PKeyboard keyboard, Key key);
	
	public abstract boolean skipInput(PKeyboard keyboard, Key key);
	
	public final void keyPressed(PKeyboard keyboard, Key key) {
		if (skipInput(keyboard, key)) {
			return;
		}
		boolean shift = keyboard.isCapsToggled();
		String typedString = keyToTypedString(key, shift);
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
	
	protected String keyToTypedString(Key key, boolean shift) {
		switch (key) {
		case ENTER:
			return "\n";
		case SPACE:
			return " ";
		case A:
			return shift ? "A" : "a";
		case B:
			return shift ? "B" : "b";
		case C:
			return shift ? "C" : "c";
		case D:
			return shift ? "D" : "d";
		case E:
			return shift ? "E" : "e";
		case F:
			return shift ? "F" : "f";
		case G:
			return shift ? "G" : "g";
		case H:
			return shift ? "H" : "h";
		case I:
			return shift ? "I" : "i";
		case J:
			return shift ? "J" : "j";
		case K:
			return shift ? "K" : "k";
		case L:
			return shift ? "L" : "l";
		case M:
			return shift ? "M" : "m";
		case N:
			return shift ? "N" : "n";
		case O:
			return shift ? "O" : "o";
		case P:
			return shift ? "P" : "p";
		case Q:
			return shift ? "Q" : "q";
		case R:
			return shift ? "R" : "r";
		case S:
			return shift ? "S" : "s";
		case T:
			return shift ? "T" : "t";
		case U:
			return shift ? "U" : "u";
		case V:
			return shift ? "V" : "v";
		case W:
			return shift ? "W" : "w";
		case X:
			return shift ? "X" : "x";
		case Y:
			return shift ? "Y" : "y";
		case Z:
			return shift ? "Z" : "z";
		default:
		}
		return null;
	}
}