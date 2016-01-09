package edu.udo.piq.components.textbased;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.defaults.DefaultPTextSelection;

public abstract class PEditableTextSupport {
	
	private static final int PAGE_UP_OR_DOWN_ROW_COUNT = 50;
	
	private final PTextComponent owner;
	
	public PEditableTextSupport(PTextComponent component) {
		owner = component;
	}
	
	public PTextSelection getSelection() {
		return owner.getSelection();
	}
	
	public PTextModel getModel() {
		return owner.getModel();
	}
	
	protected boolean skipInput(PKeyboard keyboard, Key key) {
		return !owner.hasFocus()
				|| !owner.isEditable()
				|| owner.getSelection() == null 
				|| owner.getModel() == null;
	}
	
	protected void onStringTyped(PKeyboard keyboard, String typedString) {
		if (skipInput(keyboard, null)) {
			return;
		}
		PTextSelection selection = getSelection();
		int from = selection.getLowestSelectedIndex().getIndexValue();
		int to = selection.getHighestSelectedIndex().getIndexValue();
		
		int newFrom = from + typedString.length();
		
		PTextModel model = getModel();
		String oldText = model.getText();
		String newText = oldText.substring(0, from) + typedString + oldText.substring(to);
		selection.clearSelection();
		selection.addSelection(new PListIndex(newFrom));
		model.setValue(newText);
	}
	
	protected void onKeyPressed(PKeyboard keyboard, Key key) {
		if (skipInput(keyboard, key)) {
			return;
		}
		PTextSelection selection = getSelection();
		int from = selection.getLowestSelectedIndex().getIndexValue();
		int to = selection.getHighestSelectedIndex().getIndexValue();
		int second = selection.getLastSelected().getIndexValue();
		int first;
		if (second == from) {
			first = to;
		} else {
			first = from;
		}
		int newFirst = first;
		int newSecond = second;
		
		PTextModel model = getModel();
		String oldText = model.getText();
		String newText = oldText;
		
		boolean shift = keyboard.isPressed(Key.SHIFT);
		
		switch (key) {
		case BACKSPACE:
			if (from != to) {
				newText = oldText.substring(0, from) + oldText.substring(to);
				newFirst = from;
				newSecond = from;
			} else if (from > 0) {
				newText = oldText.substring(0, from - 1) + oldText.substring(to);
				newFirst = from - 1;
				newSecond = newFirst;
			}
			break;
		case DEL:
			if (from != to) {
				newText = oldText.substring(0, from) + oldText.substring(to);
				newFirst = from;
				newSecond = from;
			} else if (from < oldText.length()) {
				newText = oldText.substring(0, from) + oldText.substring(to + 1);
				newFirst = from;
				newSecond = from;
			}
			break;
		case UP:
			int rowFirstUp = posTable.getRowOf(newFirst) - 1;
			int colFirstUp = posTable.getColumnOf(newFirst);
			newFirst = posTable.getIndex(rowFirstUp, colFirstUp);
			if (!shift) {
				newSecond = newFirst;
			}
			break;
		case DOWN:
			int rowFirstDwn = posTable.getRowOf(newFirst) + 1;
			int colFirstDwn = posTable.getColumnOf(newFirst);
			newFirst = posTable.getIndex(rowFirstDwn, colFirstDwn);
			if (!shift) {
				newSecond = newFirst;
			}
			break;
		case HOME:
			newFirst = 0;
			if (!shift) {
				newSecond = newFirst;
			}
			break;
		case END:
			newFirst = newText.length();
			if (!shift) {
				newSecond = newFirst;
			}
			break;
		case LEFT:
			if (!shift && from != to) {
				newFirst = from;
				newSecond = from;
			} else {
				if (newFirst > 0) {
					newFirst = newFirst - 1;
				}
				if (!shift) {
					newSecond = newFirst;
				}
			}
			break;
		case RIGHT:
			if (!shift && from != to) {
				newFirst = to;
				newSecond = to;
			} else {
				if (newFirst < newText.length()) {
					newFirst = newFirst + 1;
				}
				if (!shift) {
					newSecond = newFirst;
				}
			}
			break;
		case PAGE_DOWN:
			break;
		case PAGE_UP:
			break;
			//$CASES-OMITTED$
		default:
			break;
		}
		
		// We dont have to use equals here since Strings are immutable 
		// and oldText = newText at the beginning of the method.
		if (oldText != newText) {
			model.setValue(newText);
		}
		if (newFirst != first || newSecond != second) {
			selection.clearSelection();
			selection.addSelection(new PListIndex(newFirst));
			selection.addSelection(new PListIndex(newSecond));
		}
	}
	
	private final PKeyboardObs keyObs = new PKeyboardObs() {
		public void onStringTyped(PKeyboard keyboard, String typedString) {
			onStringTyped(keyboard, typedString);
		}
		public void onKeyPressed(PKeyboard keyboard, Key key) {
			onKeyPressed(keyboard, key);
		}
	};
	
}