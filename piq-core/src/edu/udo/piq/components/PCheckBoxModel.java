package edu.udo.piq.components;

import edu.udo.piq.components.util.PModelHistory;

public interface PCheckBoxModel {
	
	public default void setValue(Object obj) {
		if (Boolean.TRUE.equals(obj)) {
			if (!isChecked()) {
				toggleChecked();
			}
		} else if (Boolean.FALSE.equals(obj)) {
			if (isChecked()) {
				toggleChecked();
			}
		}
	}
	
	public default Object getValue() {
		return Boolean.valueOf(isChecked());
	}
	
	public void toggleChecked();
	
	public boolean isChecked();
	
	/**
	 * Returns an instance of of {@link PModelHistory} if this model 
	 * supports undo and redo operations or returns null if undo and 
	 * redo is not supported.<br>
	 * 
	 * @return an instance of {@link PModelHistory} or null
	 */
	public default PModelHistory getHistory() {
		return null;
	}
	
	public void addObs(PCheckBoxModelObs obs);
	
	public void removeObs(PCheckBoxModelObs obs);
	
}