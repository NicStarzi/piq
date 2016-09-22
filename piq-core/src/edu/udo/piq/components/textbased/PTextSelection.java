package edu.udo.piq.components.textbased;

import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PSelection;

public interface PTextSelection extends PSelection {
	
	public PListIndex getLastSelected();
	
	public PListIndex getLowestSelectedIndex();
	
	public PListIndex getHighestSelectedIndex();
	
	public default void selectAll(PTextModel model) {
		addSelection(new PListIndex(0));
		addSelection(new PListIndex(model.getLength()));
	}
	
}