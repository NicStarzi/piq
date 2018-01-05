package edu.udo.piq.components;

public interface PSpinnerModel {
	
	public default boolean hasNext() {
		return getNext() != null;
	}
	
	public Object getNext();
	
	public default boolean hasPrevious() {
		return getPrevious() != null;
	}
	
	public Object getPrevious();
	
	public boolean canSetValue(Object obj);
	
	public void setValue(Object obj);
	
	public Object getValue();
	
	public void setEnabled(boolean value);
	
	public boolean isEnabled();
	
	public void addObs(PSpinnerModelObs obs);
	
	public void removeObs(PSpinnerModelObs obs);
	
}