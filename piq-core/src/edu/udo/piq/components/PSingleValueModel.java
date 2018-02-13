package edu.udo.piq.components;

public interface PSingleValueModel<VALUE_TYPE> {
	
	public void setValue(Object value);
	
	public VALUE_TYPE getValue();
	
	public void addObs(PSingleValueModelObs<VALUE_TYPE> obs);
	
	public void removeObs(PSingleValueModelObs<VALUE_TYPE> obs);
	
}