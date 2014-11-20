package edu.udo.piq.components;

public interface PTableSelection extends PSelection<PTableCell> {
	
	public static final SelectionMode DEFAULT_SELECTION_MODE = SelectionMode.SINGLE_ROW;
	
	public void setSelectionMode(SelectionMode selectionMode);
	
	public SelectionMode getSelectionMode();
	
	public void addObs(PTableSelectionObs obs);
	
	public void removeObs(PTableSelectionObs obs);
	
	public static enum SelectionMode {
		SINGLE_CELL,
		MULTIPLE_CELL,
		SINGLE_ROW,
		MULTIPLE_ROW,
		SINGLE_COLUMN,
		MULTIPLE_COLUMN,
		;
	}
	
}