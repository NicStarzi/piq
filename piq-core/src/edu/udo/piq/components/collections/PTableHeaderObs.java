package edu.udo.piq.components.collections;

public interface PTableHeaderObs {
	
	public void onHeaderStatusChanged(PTableModel model);
	
	public void onHeaderElementChanged(PTableModel model, int index, Object oldElement);
	
}