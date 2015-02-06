package edu.udo.piq.components;

public interface PDropDownSelection {
	
	public void setSelection(Object object);
	
	public void clearSelection();
	
	public boolean isSelected(Object object);
	
	public Object getSelection();
	
	public void addObs(PDropDownSelectionObs obs);
	
	public void removeObs(PDropDownSelectionObs obs);
	
}