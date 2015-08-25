package edu.udo.piq.components.containers;

import edu.udo.piq.PComponent;

public interface PTabFactory {
	
	public PTabComponent getTabComponentFor(PComponent preview, int index);
	
}