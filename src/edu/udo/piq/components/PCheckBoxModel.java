package edu.udo.piq.components;

import edu.udo.piq.components.util.PModelHistory;

public interface PCheckBoxModel {
	
	public void setChecked(boolean value);
	
	public boolean isChecked();
	
	public PModelHistory getHistory();
	
	public void addObs(PCheckBoxModelObs obs);
	
	public void removeObs(PCheckBoxModelObs obs);
	
}