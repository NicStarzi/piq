package edu.udo.piq.components;

import java.util.List;

public interface PTabelSelection {
	
	public void addSelection(PTableCell cell);
	
	public void removeSelection(PTableCell cell);
	
	public void clearSelection();
	
	public List<PTableCell> getSelection();
	
}