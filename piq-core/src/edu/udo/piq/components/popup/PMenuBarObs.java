package edu.udo.piq.components.popup;

public interface PMenuBarObs {
	
	public default void onArmedChanged(PMenuBar menuBar) {}
	
	public default void onItemClicked(PMenuBar menuBar, PMenuBarItem item) {}
	
	public default void onMenuShown(PMenuBar menuBar, PMenuBarItem item, PMenuBody body) {}
	
	public default void onMenuHidden(PMenuBar menuBar, PMenuBarItem item, PMenuBody body) {}
	
}