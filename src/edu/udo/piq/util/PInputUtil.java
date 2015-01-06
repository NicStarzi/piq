package edu.udo.piq.util;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;

public class PInputUtil {
	
	private PInputUtil() {}
	
	public boolean isCopy(PKeyboard keyboard) {
		return keyboard.isCtrlToggled() && keyboard.isTriggered(Key.C);
	}
	
	public boolean isCut(PKeyboard keyboard) {
		return keyboard.isCtrlToggled() && keyboard.isTriggered(Key.X);
	}
	
	public boolean isPaste(PKeyboard keyboard) {
		return keyboard.isCtrlToggled() && keyboard.isTriggered(Key.P);
	}
	
	public boolean isUndo(PKeyboard keyboard) {
		return keyboard.isCtrlToggled() && keyboard.isTriggered(Key.Z);
	}
	
	public boolean isRedo(PKeyboard keyboard) {
		return keyboard.isCtrlToggled() && keyboard.isTriggered(Key.Y);
	}
	
	public static String getTypedString(PKeyboard keyboard, Key key) {
		boolean caps = keyboard.isCapsToggled();
		switch (key) {
		case ENTER:
			return "\n";
		case SPACE:
			return " ";
		case NUM_0:
			return "0";
		case NUM_1:
			return "1";
		case NUM_2:
			return "2";
		case NUM_3:
			return "3";
		case NUM_4:
			return "4";
		case NUM_5:
			return "5";
		case NUM_6:
			return "6";
		case NUM_7:
			return "7";
		case NUM_8:
			return "8";
		case NUM_9:
			return "9";
		case A:
			return caps ? "A" : "a";
		case B:
			return caps ? "B" : "b";
		case C:
			return caps ? "C" : "c";
		case D:
			return caps ? "D" : "d";
		case E:
			return caps ? "E" : "e";
		case F:
			return caps ? "F" : "f";
		case G:
			return caps ? "G" : "g";
		case H:
			return caps ? "H" : "h";
		case I:
			return caps ? "I" : "i";
		case J:
			return caps ? "J" : "j";
		case K:
			return caps ? "K" : "k";
		case L:
			return caps ? "L" : "l";
		case M:
			return caps ? "M" : "m";
		case N:
			return caps ? "N" : "n";
		case O:
			return caps ? "O" : "o";
		case P:
			return caps ? "P" : "p";
		case Q:
			return caps ? "Q" : "q";
		case R:
			return caps ? "R" : "r";
		case S:
			return caps ? "S" : "s";
		case T:
			return caps ? "T" : "t";
		case U:
			return caps ? "U" : "u";
		case V:
			return caps ? "V" : "v";
		case W:
			return caps ? "W" : "w";
		case X:
			return caps ? "X" : "x";
		case Y:
			return caps ? "Y" : "y";
		case Z:
			return caps ? "Z" : "z";
		default:
		}
		return null;
	}
	
}