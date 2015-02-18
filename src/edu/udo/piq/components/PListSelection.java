package edu.udo.piq.components;

import java.util.ArrayList;
import java.util.List;

public interface PListSelection extends PSelection<Object> {
	
//	public static final SelectionMode DEFAULT_SELECTION_MODE = SelectionMode.SINGLE_ROW;
	
	public default List<PListPosition> getSelectedPositions() {
		List<Object> selected = getSelection();
		List<PListPosition> list = new ArrayList<>(selected.size());
		for (Object element : selected) {
			list.add(new PListPosition(getModel(), element));
		}
		return list;
	}
	
	public void setModel(PListModel model);
	
	public PListModel getModel();
	
//	public void setSelectionMode(SelectionMode selectionMode);
//	
//	public SelectionMode getSelectionMode();
	
	public void addObs(PListSelectionObs obs);
	
	public void removeObs(PListSelectionObs obs);
	
//	public static enum SelectionMode {
//		SINGLE_ROW,
//		CONTIGUOUS_ROWS,
//		ARBITRARY_ROWS,
//		;
//	}
	
}