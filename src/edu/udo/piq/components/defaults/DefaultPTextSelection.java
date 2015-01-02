package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PTextSelection;
import edu.udo.piq.tools.AbstractPTextSelection;

public class DefaultPTextSelection extends AbstractPTextSelection implements PTextSelection {
	
	private boolean firstIsFrom = true;
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
	
	public void setSelection(int first, int second) {
		int oldFrom = from;
		int oldTo = to;
		if (first <= second) {
			firstIsFrom = true;
			from = first;
			to = second;
		} else {
			firstIsFrom = false;
			from = second;
			to = first;
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
	
	public int getFirst() {
		if (firstIsFrom) {
			return getFrom();
		}
		return getTo();
	}
	
	public int getSecond() {
		if (firstIsFrom) {
			return getTo();
		}
		return getFrom();
	}
	
}