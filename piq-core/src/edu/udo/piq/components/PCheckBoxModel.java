package edu.udo.piq.components;

public interface PCheckBoxModel extends PSingleValueModel {
	
	public static final boolean DEFAULT_CHECKED_VALUE = false;
	public static final boolean DEFAULT_ENABLED_VALUE = true;
	
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