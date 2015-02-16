package edu.udo.piq.components;

import java.util.ArrayList;
import java.util.List;

public interface PTreeSelection extends PSelection<Object> {
	
	public static final SelectionMode DEFAULT_SELECTION_MODE = SelectionMode.SINGLE_NODE;
	
	public default List<PTreePosition> getSelectedPositions() {
		List<PTreePosition> list = new ArrayList<>();
		for (Object node : getSelection()) {
			list.add(new PTreePosition(getModel(), node));
		}
		return list;
	}
	
	public void setModel(PTreeModel model);
	
	public PTreeModel getModel();
	
	public void setSelectionMode(SelectionMode selectionMode);
	
	public SelectionMode getSelectionMode();
	
	public void addObs(PTreeSelectionObs obs);
	
	public void removeObs(PTreeSelectionObs obs);
	
	public static enum SelectionMode {
		SINGLE_NODE,
//		CONTIGUOUS_NODES,
		ARBITRARY_NODES,
		;
	}
	
}