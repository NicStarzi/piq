package edu.udo.piq.components;

import edu.udo.piq.components.util.PModelHistory;

public interface PCheckBoxModel {
	
	public void setChecked(boolean value);
	
	public boolean isChecked();
	
	/**
	 * Returns an instance of of {@link PModelHistory} if this model 
	 * supports undo and redo operations or returns null if undo and 
	 * redo is not supported.<br>
	 * 
	 * @return an instance of {@link PModelHistory} or null
	 */
	public PModelHistory getHistory();
	
	public void addObs(PCheckBoxModelObs obs);
	
	public void removeObs(PCheckBoxModelObs obs);
	
}