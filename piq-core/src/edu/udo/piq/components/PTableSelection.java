package edu.udo.piq.components;

public interface PTableSelection extends PSelection<PTablePosition> {
	
	public void setModel(PTableModel model);
	
	public PTableModel getModel();
	
	public void addObs(PTableSelectionObs obs);
	
	public void removeObs(PTableSelectionObs obs);
	
}