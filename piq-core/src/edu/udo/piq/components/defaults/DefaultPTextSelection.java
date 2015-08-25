package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.textbased.PTextSelection;
import edu.udo.piq.tools.AbstractPSelection;
import edu.udo.piq.util.ThrowException;

public class DefaultPTextSelection extends AbstractPSelection implements PTextSelection {
	
	private PListIndex from = null;
	private PListIndex to = null;
	
	public PListIndex getLastSelected() {
		return (PListIndex) super.getLastSelected();
	}
	
	public PListIndex getLowestSelectedIndex() {
		if (!hasSelection()) {
			return null;
		}
		if (from.getIndexValue() < to.getIndexValue()) {
			return from;
		}
		return to;
	}
	
	public PListIndex getHighestSelectedIndex() {
		if (!hasSelection()) {
			return null;
		}
		if (from.getIndexValue() < to.getIndexValue()) {
			return to;
		}
		return from;
	}
	
	public void addSelection(PModelIndex index) {
		PListIndex listIndex = ThrowException.ifTypeCastFails(index, PListIndex.class, "Bad index type");
		
		if (hasSelection()) {
			PListIndex oldFrom = from;
			PListIndex oldTo = to;
			
			to = listIndex;
			
			fireEvents(oldFrom.getIndexValue(), oldTo.getIndexValue());
		} else {
			from = to = listIndex;
			fireSelectionAdded(listIndex);
		}
		setLastSelected(to);
	}
	
	public void removeSelection(PModelIndex index) {
		PListIndex listIndex = ThrowException.ifTypeCastFails(index, PListIndex.class, "Illegal index type");
		if (!hasSelection()) {
			return;
		}
		int indexCheck = listIndex.getIndexValue();
		int indexFrom = from.getIndexValue();
		int indexTo = to.getIndexValue();
		if (!isSelected(indexCheck, indexFrom, indexTo)) {
			return;
		}
		if (indexFrom == indexTo) {
			clearSelection();
			return;
		}
		
		int offset;
		if (indexFrom < indexTo) {
			offset = -1;
		} else {
			offset = 1;
		}
		to = new PListIndex(indexTo + offset);
		setLastSelected(to);
	}
	
	public void clearSelection() {
		if (!hasSelection()) {
			return;
		}
		int oldFrom = from.getIndexValue();
		int oldTo = to.getIndexValue();
		for (int i = oldFrom; i < oldTo; i++) {
			fireSelectionRemoved(new PListIndex(i));
		}
		from = to = null;
		setLastSelected(null);
	}
	
	public List<PModelIndex> getAllSelected() {
		if (!hasSelection()) {
			return Collections.emptyList();
		}
		int indexFrom = from.getIndexValue();
		int indexTo = to.getIndexValue();
		int size = indexTo - indexFrom + 1;
		List<PModelIndex> result = new ArrayList<>(size);
		for (int i = indexFrom; i <= indexTo; i++) {
			result.add(new PListIndex(i));
		}
		return result;
	}
	
	public boolean isSelected(PModelIndex index) {
		PListIndex listIndex = ThrowException.ifTypeCastFails(index, PListIndex.class, "Illegal index type");
		if (!hasSelection()) {
			return false;
		}
		int indexCheck = listIndex.getIndexValue();
		int indexFrom = from.getIndexValue();
		int indexTo = to.getIndexValue();
		return isSelected(indexCheck, indexFrom, indexTo);
	}
	
	public boolean hasSelection() {
		return from != null;
	}
	
	protected boolean isSelected(int val, int from, int to) {
		return val >= from && val <= to;
	}
	
	public static class Test$ {
		
	}
	
	protected void fireEvents(int oldFrom, int oldTo) {
		int newFrom = from.getIndexValue();
		int newTo = to.getIndexValue();
		int minNew;
		int maxNew;
		if (newFrom < newTo) {
			minNew = newFrom;
			maxNew = newTo;
		} else {
			minNew = newTo;
			maxNew = newFrom;
		}
		
		int minOld;
		int maxOld;
		if (oldFrom < oldTo) {
			minOld = oldFrom;
			maxOld = oldTo;
		} else {
			minOld = oldTo;
			maxOld = oldFrom;
		}
		
		for (int i = minOld; i < minNew && i <= maxOld; i++) {
			fireSelectionRemoved(new PListIndex(i));
		}
		for (int i = minNew; i < minOld && i <= maxNew; i++) {
			fireSelectionAdded(new PListIndex(i));
		}
		for (int i = maxNew; i >= minNew && maxNew > maxOld; i--) {
			fireSelectionAdded(new PListIndex(i));
		}
		for (int i = maxNew; i <= maxOld; i++) {
			fireSelectionRemoved(new PListIndex(i));
		}
	}
	
}