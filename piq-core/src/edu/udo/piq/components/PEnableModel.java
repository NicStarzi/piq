package edu.udo.piq.components;

public interface PEnableModel extends PSingleValueModel {
	
	public static final boolean DEFAULT_ENABLED_VALUE = true;
	
	@Override
	public default Boolean getValue() {
		return Boolean.valueOf(isEnabled());
	}
	
	public boolean isEnabled();
	
}