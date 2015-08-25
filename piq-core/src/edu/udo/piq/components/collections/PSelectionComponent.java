package edu.udo.piq.components.collections;

import java.util.List;

public interface PSelectionComponent {
	
	public PSelection getSelection();
	
	public PModel getModel();
	
	public PModelIndex getIndexAt(int x, int y);
	
	public List<Object> getAllSelectedContent();
	
	public void addObs(PModelObs obs);
	
	public void removeObs(PModelObs obs);
	
	public void addObs(PSelectionObs obs);
	
	public void removeObs(PSelectionObs obs);
	
}