package edu.udo.piq.components;

public interface PListSelection extends PSelection<Object> {
	
	public static final SelectionMode DEFAULT_SELECTION_MODE = SelectionMode.SINGLE_ROW;
	
	public void setModel(PListModel model);
	
	public PListModel getModel();
	
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