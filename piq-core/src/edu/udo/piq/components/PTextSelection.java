package edu.udo.piq.components;

public interface PTextSelection {
	
	public static final int INDEX_NO_SELECTION = -1;
	
	public void setModel(PTextModel model);
	
	public PTextModel getModel();
	
	public void addObs(PTextSelectionObs obs);
	
	public void removeObs(PTextSelectionObs obs);
	
	public void setSelection(int first, int second);
	
	public void clearSelection();
	
	public int getFrom();
	
	public int getTo();
	
	public int getFirst();
	
	public int getSecond();
	
	public boolean isSelected(int index);
	
}