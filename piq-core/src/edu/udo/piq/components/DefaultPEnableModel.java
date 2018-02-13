package edu.udo.piq.components;

public class DefaultPEnableModel extends AbstractPSingleValueModel<Boolean> implements PEnableModel {
	
	protected boolean enabled = DEFAULT_ENABLED_VALUE;
	
	@Override
	public Boolean getValue() {
		return Boolean.valueOf(isEnabled());
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	protected void setValueInternal(Object newValue) {
		enabled = Boolean.TRUE.equals(newValue);
	}
	
}