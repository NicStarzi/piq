package edu.udo.piq;

public interface PStyleSheet {
	
	public PStyleComponent getStyleFor(PComponent component);
	
	public PStyleLayout getStyleFor(PReadOnlyLayout layout);
	
	public default void registerStyles(PRoot root) {
	}
	
}