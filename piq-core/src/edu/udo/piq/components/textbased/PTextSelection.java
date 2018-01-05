package edu.udo.piq.components.textbased;

import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PSelection;

public interface PTextSelection extends PSelection {
	
	@Override
	public PListIndex getLastSelected();
	
	public PListIndex getLowestSelectedIndex();
	
	public PListIndex getHighestSelectedIndex();
	
	public default void selectAll(PTextModel model) {
		setSelectionToRange(0, model.getLength());
	}
	
	public default void setSelectionToRange(int idx1, int idx2) {
		setSelectionToRange(new PListIndex(idx1), new PListIndex(idx2));
	}
	
	public default void setSelectionToRange(PListIndex idx1, PListIndex idx2) {
		clearSelection();
		addSelection(idx1);
		addSelection(idx2);
	}
	
}