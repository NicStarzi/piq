package edu.udo.piq.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusTraversal;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.VirtualKey;
import edu.udo.piq.components.textbased.PTextComponent;

public class IndexedTabbingPFocusTraversal extends AbstractPFocusTraversal implements PFocusTraversal {
	
	protected final List<PComponent> components;
	
	public IndexedTabbingPFocusTraversal(PComponent ... components) {
		this.components = Collections.unmodifiableList(
				new ArrayList<>(Arrays.asList(components)));
	}
	
	@Override
	protected void onKeyTriggered(PKeyboard keyboard, Key key) {
		PComponent focusOwner = curRoot.getFocusOwner();
		if (focusOwner == null) {
			return;
		}
		if (focusOwner instanceof PTextComponent) {
			if (keyboard.isTriggered(VirtualKey.FOCUS_UP)) {
				moveFocus(focusOwner, +1);
			} else if (keyboard.isTriggered(VirtualKey.FOCUS_DOWN)) {
				moveFocus(focusOwner, -1);
			}
		} else {
			if (keyboard.isTriggered(VirtualKey.FOCUS_NEXT)) {
				moveFocus(focusOwner, +1);
			} else if (keyboard.isTriggered(VirtualKey.FOCUS_PREV)) {
				moveFocus(focusOwner, -1);
			}
		}
	}
	
	protected void moveFocus(PComponent focusOwner, int offset) {
		int index = components.indexOf(focusOwner);
		if (index == -1) {
			for (int i = 0; i < components.size(); i++) {
				if (components.get(i).isAncestorOf(focusOwner)) {
					index = i;
					break;
				}
			}
		}
		if (index == -1) {
			index = 0;
		} else {
			index = (index + offset) % components.size();
			if (index < 0) {
				index += components.size();
			}
		}
		PComponent comp = components.get(index);
		if (!comp.isFocusable()) {
			for (PComponent desc : comp.getDescendants()) {
				if (desc.isFocusable()) {
					comp = desc;
					break;
				}
			}
		}
		comp.tryToTakeFocus();
	}
	
}