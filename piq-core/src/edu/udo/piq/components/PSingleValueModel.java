package edu.udo.piq.components;

public interface PSingleValueModel {
	
	public void setValue(Object value);
	
	public Object getValue();
	
	public void addObs(PSingleValueModelObs obs);
	
	public void removeObs(PSingleValueModelObs obs);
	
}