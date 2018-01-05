package edu.udo.piq.components;

public interface PRadioButtonModel extends PSingleValueModel {
	
	public static final boolean DEFAULT_SELECTED_VALUE = false;
	public static final boolean DEFAULT_ENABLED_VALUE = true;
	
	@Override
	public default void setValue(Object obj) {
		setSelected(Boolean.TRUE == obj || Boolean.TRUE.equals(obj));
	}
	
	@Override
	public default Object getValue() {
		return Boolean.valueOf(isSelected());
	}
	
	public void setSelected(boolean value);
	
	public boolean isSelected();
	
	public void setEnabled(boolean trueIfEnabled);
	
	public boolean isEnabled();
	
}