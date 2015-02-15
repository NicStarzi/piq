package edu.udo.piq.components;

public interface PTreeSelection extends PSelection<Object> {
	
	public static final SelectionMode DEFAULT_SELECTION_MODE = SelectionMode.SINGLE_NODE;
	
	public void setSelectionMode(SelectionMode selectionMode);
	
	public SelectionMode getSelectionMode();
	
	public void addObs(PTreeSelectionObs obs);
	
	public void removeObs(PTreeSelectionObs obs);
	
	public static enum SelectionMode {
		SINGLE_NODE,
		CONTIGUOUS_NODES,
		ARBITRARY_NODES,
		;
	}
	
}