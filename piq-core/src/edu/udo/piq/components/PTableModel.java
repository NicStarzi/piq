package edu.udo.piq.components;

public interface PTableModel {
	
	public int getColumnCount();
	
	public int getRowCount();
	
	public Object getCell(int columnIndex, int rowIndex);
	
	public void addObs(PTableModelObs obs);
	
	public void removeObs(PTableModelObs obs);
	
}