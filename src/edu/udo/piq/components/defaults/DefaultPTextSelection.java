package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PTextSelection;
import edu.udo.piq.tools.AbstractPTextSelection;

public class DefaultPTextSelection extends AbstractPTextSelection implements PTextSelection {
	
	private int from = -1;
	private int to = -1;
	
	public void clearSelection() {
		int oldFrom = from;
		int oldTo = to;
		from = -1;
		to = -1;
		for (int i = oldFrom; i < oldTo; i++) {
			fireSelectionRemovedEvent(i);
		}
	}
	
	public boolean isSelected(int index) {
		return index >= from && index <= to;
	}
	
	public void setSelection(int index1, int index2) {
		int oldFrom = from;
		int oldTo = to;
		if (index1 <= index2) {
			from = index1;
			to = index2;
		} else {
			from = index2;
			to = index1;
		}
		if (from == -1) {
			to = -1;
		}
		boolean intersect = !((to < oldFrom) || (oldTo < from));
		if (intersect) {
			int minFrom = Math.min(oldFrom, from);
			int maxTo = Math.max(oldTo, to);
			for (int i = minFrom; i < maxTo; i++) {
				if (i < from || i >= to) {
					fireSelectionRemovedEvent(i);
				} else if (i < oldFrom || i >= oldTo) {
					fireSelectionAddedEvent(i);
				}
			}
		} else {
			for (int i = oldFrom; i < oldTo; i++) {
				fireSelectionRemovedEvent(i);
			}
			for (int i = from; i < to; i++) {
				fireSelectionAddedEvent(i);
			}
		}
		fireSelectionChangedEvent();
	}
	
	public int getFrom() {
		return from;
	}
	
	public int getTo() {
		return to;
	}
	
}