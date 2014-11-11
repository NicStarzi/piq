package edu.udo.piq.components;

public interface PTableModelObs {
	
	public void cellChanged(PTableModel model, Object cell, int columnIndex, int rowIndex);
	
	public void rowAdded(PTableModel model, int rowIndex);
	
	public void rowRemoved(PTableModel model, int rowIndex);
	
	public void columnAdded(PTableModel model, int columnIndex);
	
	public void columnRemoved(PTableModel model, int columnIndex);
	
}