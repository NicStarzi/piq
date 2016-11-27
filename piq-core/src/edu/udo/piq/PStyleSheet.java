package edu.udo.piq;

public interface PStyleSheet {
	
	public void setRoot(PRoot root);
	
	public PRoot getRoot();
	
	public PStyleComponent getStyleFor(PComponent component);
	
}