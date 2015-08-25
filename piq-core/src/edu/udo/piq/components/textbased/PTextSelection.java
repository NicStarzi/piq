package edu.udo.piq.components.textbased;

import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PSelection;

public interface PTextSelection extends PSelection {
	
	public PListIndex getLastSelected();
	
	public PListIndex getLowestSelectedIndex();
	
	public PListIndex getHighestSelectedIndex();
	
}