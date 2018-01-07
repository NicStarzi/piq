package edu.udo.piq.style;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;

public interface PStyleSheet {
	
	public void onAddedToRoot(PRoot root);
	
	public void onRemovedFromRoot(PRoot root);
	
//	public void setRoot(PRoot root);
	
//	public PRoot getRoot();
	
	public PStyleComponent getStyleFor(PComponent component);
	
}