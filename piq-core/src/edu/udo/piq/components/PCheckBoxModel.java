package edu.udo.piq.components;

public interface PCheckBoxModel extends PSingleValueModel {
	
	@Override
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
	
	@Override
	public default Object getValue() {
		return Boolean.valueOf(isChecked());
	}
	
	public void toggleChecked();
	
	public boolean isChecked();
	
}