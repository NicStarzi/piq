package edu.udo.piq.components;

public interface PListSelection extends PSelection<Integer> {
	
	public static final SelectionMode DEFAULT_SELECTION_MODE = SelectionMode.SINGLE_ROW;
	
//	public void addSelection(Integer index);
//	
//	public void removeSelection(Integer index);
//	
//	public void clearSelection();
//	
//	public Set<Integer> getSelection();
//	
//	public boolean isSelected(Integer index);
	
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