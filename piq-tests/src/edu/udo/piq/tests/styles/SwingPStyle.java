package edu.udo.piq.tests.styles;

import edu.udo.piq.PRoot;
import edu.udo.piq.style.PStyle;

public interface SwingPStyle extends PStyle {
	
	public default void onAddedToRoot(PRoot root) {}
	
	public default void onRemovedFromRoot(PRoot root) {}
	
}