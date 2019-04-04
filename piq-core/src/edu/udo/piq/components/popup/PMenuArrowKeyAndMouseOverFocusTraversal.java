package edu.udo.piq.components.popup;

import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusTraversal;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.tools.AbstractPFocusTraversal;
import edu.udo.piq.util.ThrowException;

public class PMenuArrowKeyAndMouseOverFocusTraversal extends AbstractPFocusTraversal implements PFocusTraversal {
	
	protected PMenuBody container;
	
	public PMenuArrowKeyAndMouseOverFocusTraversal(PMenuBody container) {
		ThrowException.ifNull(container, "container == null");
		this.container = container;
	}
	
	@Override
	protected void onKeyTriggered(PKeyboard keyboard, ActualKey key) {
		switch (key) {
		case UP:
			if (keyboard.isModifierToggled(Modifier.CTRL)) {
				tryToGiveFocus(getFocusCandidateByIndex(0));
			} else {
				tryToGiveFocus(getFocusCandidateUp());
			}
			break;
		case DOWN:
			if (keyboard.isModifierToggled(Modifier.CTRL)) {
				tryToGiveFocus(getFocusCandidateByIndex(getFocusComponentCount() - 1));
			} else {
				tryToGiveFocus(getFocusCandidateDown());
			}
			break;
		default:
		}
	}
	
	protected void tryToGiveFocus(PComponent comp) {
		if (comp != null) {
			comp.tryToTakeFocus();
		}
	}
	
	protected PComponent getFocusCandidateUp() {
		return getFocusCandidateByOffset(-1);
	}
	
	protected PComponent getFocusCandidateDown() {
		return getFocusCandidateByOffset(+1);
	}
	
	protected PComponent getFocusCandidateLeft() {
		return getFocusCandidateByOffset(-1);
	}
	
	protected PComponent getFocusCandidateRight() {
		return getFocusCandidateByOffset(+1);
	}
	
	protected PComponent getFocusCandidateByOffset(int offset) {
		if (offset != +1 && offset != -1) {
			throw new IllegalArgumentException("offset == "+offset+"; must be either +1 or -1");
		}
		PComponent focusOwner = curRoot.getFocusOwner();
		int size = getFocusComponentCount();
		int curIdx = getIndexOfFocusCandidate(focusOwner);
		int iterCount = 0;
		PComponent curComp = null;
		while (iterCount < size && (curComp == null || !curComp.isFocusable())) {
			int newIdx = (curIdx + offset) % size;
			if (newIdx < 0) {
				newIdx += size;
			}
			curIdx = newIdx;
			curComp = getFocusCandidateByIndex(curIdx);
			iterCount++;
		}
		return curComp;
	}
	
	protected int getIndexOfFocusCandidate(PComponent component) {
		if (!container.isAncestorOf(component)) {
			return -1;
		}
		PComponent menuItem = component.getAncestorsAndSelf().getNextMatching(
			anc -> anc.getParent() == container
		);
		return container.getMenuItemIndex(menuItem);
	}
	
	protected PComponent getFocusCandidateByIndex(int index) {
		return container.getMenuItemAtIndex(index);
	}
	
	protected int getFocusComponentCount() {
		return container.getMenuItemCount();
	}
	
}