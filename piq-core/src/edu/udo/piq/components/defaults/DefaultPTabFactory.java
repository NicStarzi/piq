package edu.udo.piq.components.defaults;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.PTabComponent;
import edu.udo.piq.components.PTabFactory;

public class DefaultPTabFactory implements PTabFactory {
	
	public PTabComponent getTabComponentFor(PComponent preview, int index) {
		DefaultPTabComponent tab = new DefaultPTabComponent();
		tab.setPreview(preview);
		tab.setIndex(index);
		return tab;
	}
	
}