package edu.udo.piq.components;

public interface PRadioButtonModel extends PSingleValueModel<Boolean> {
	
	public static final boolean DEFAULT_SELECTED_VALUE = false;
	
	@Override
	public default void setValue(Object obj) {
		setSelected(Boolean.TRUE == obj || Boolean.TRUE.equals(obj));
	}
	
	@Override
	public default Boolean getValue() {
		return Boolean.valueOf(isSelected());
	}
	
	public void setSelected(boolean value);
	
	public boolean isSelected();
	
}