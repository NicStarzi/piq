package edu.udo.piq.components;

public interface PSingleValueModelObs<VALUE_TYPE> {
	
	public void onValueChanged(PSingleValueModel<VALUE_TYPE> model, VALUE_TYPE oldValue, VALUE_TYPE newValue);
	
}