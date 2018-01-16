package edu.udo.piq.style;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;
import edu.udo.piq.TemplateMethod;

public interface PStyleSheet {
	
	@TemplateMethod
	public void onAddedToRoot(PRoot root);
	
	@TemplateMethod
	public void onRemovedFromRoot(PRoot root);
	
	public PStyleComponent getStyleFor(PComponent component);
	
}