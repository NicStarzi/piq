package edu.udo.piq.components;

public interface PTextSelection extends PSelection<Integer> {
	
	public void setModel(PTextModel model);
	
	public PTextModel getModel();
	
	public void addObs(PTextSelectionObs obs);
	
	public void removeObs(PTextSelectionObs obs);
	
}