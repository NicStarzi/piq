package edu.udo.piq;

public interface PDnDTransfer {
	
	public PComponent getSource();
	
	public int getDragStartX();
	
	public int getDragStartY();
	
	public Object getElement();
	
	public PComponent getVisibleRepresentation();
	
}