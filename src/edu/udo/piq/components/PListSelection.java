package edu.udo.piq.components;

import java.util.Set;

public interface PListSelection {
	
	public static final SelectionMode DEFAULT_SELECTION_MODE = SelectionMode.SINGLE_ROW;
	
	public void addSelection(int index);
	
	public void removeSelection(int index);
	
	public void clearSelection();
	
	public Set<Integer> getSelection();
	
	public boolean isSelected(int index);
	
	public void setSelectionMode(SelectionMode selectionMode);
	
	public SelectionMode getSelectionMode();
	
	public void addObs(PListSelectionObs obs);
	
	public void removeObs(PListSelectionObs obs);
	
	public static enum SelectionMode {
		SINGLE_ROW,
		CONTIGUOUS_ROWS,
		ARBITRARY_ROWS,
		;
	}
	
}