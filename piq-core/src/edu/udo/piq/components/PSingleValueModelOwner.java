package edu.udo.piq.components;

public interface PSingleValueModelOwner<ELEM_T, MODEL_T extends PSingleValueModel<ELEM_T>> {
	
	public void setModel(MODEL_T model);
	
	public MODEL_T getModel();
	
	public default void setModelValue(Object value) {
		if (getModel() == null) {
			return;
		}
		getModel().setValue(value);
	}
	
	public default ELEM_T getModelValue() {
		return getModel().getValue();
	}
	
}