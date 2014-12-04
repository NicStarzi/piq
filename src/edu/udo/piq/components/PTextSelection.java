package edu.udo.piq.components;

public interface PTextSelection {
	
	public void setModel(PTextModel model);
	
	public PTextModel getModel();
	
	public void addObs(PTextSelectionObs obs);
	
	public void removeObs(PTextSelectionObs obs);
	
	public void setSelection(int index1, int index2);
	
	public void clearSelection();
	
	public int getFrom();
	
	public int getTo();
	
	public boolean isSelected(int index);
	
}