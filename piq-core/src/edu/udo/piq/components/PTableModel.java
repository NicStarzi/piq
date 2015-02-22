package edu.udo.piq.components;

import edu.udo.piq.components.util.PModelHistory;

public interface PTableModel {
	
	public int getColumnCount();
	
	public int getColumnIndexOf(Object cell);
	
	public int getRowCount();
	
	public int getRowIndexOf(Object cell);
	
	public Object getCell(int columnIndex, int rowIndex);
	
	public default PTablePosition getPositionFor(Object cell) {
		return new PTablePosition(this, cell);
	}
	
	public default PTablePosition getPositionAt(int columnIndex, int rowIndex) {
		return new PTablePosition(this, columnIndex, rowIndex);
	}
	
	public boolean canAdd(Object cell, int columnIndex, int rowIndex);
	
	public void add(Object cell, int columnIndex, int rowIndex);
	
	public boolean canRemove(int columnIndex, int rowIndex);
	
	public void remove(int columnIndex, int rowIndex);
	
	public PModelHistory getHistory();
	
	public void addObs(PTableModelObs obs);
	
	public void removeObs(PTableModelObs obs);
	
}