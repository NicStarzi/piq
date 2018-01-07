package edu.udo.piq.dnd;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.collections.PModel;

public interface PDnDTransfer {
	
	public PComponent getSource();
	
	public int getDragStartX();
	
	public int getDragStartY();
	
	public PModel getData();
	
	public PDnDIndicator getIndicator();
	
}