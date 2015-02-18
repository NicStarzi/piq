package edu.udo.piq.components;

public interface PTableModelObs {
	
	public void rowAdded(PTableModel model, int rowIndex);
	
	public void rowRemoved(PTableModel model, int rowIndex);
	
	public void cellChanged(PTableModel model, Object cell, int columnIndex, int rowIndex);
	
}