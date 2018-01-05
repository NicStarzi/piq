package edu.udo.piq.tools;

import edu.udo.piq.components.AbstractPSingleValueModel;
import edu.udo.piq.components.PRadioButtonModel;

public abstract class AbstractPRadioButtonModel extends AbstractPSingleValueModel implements PRadioButtonModel {
	
	@Override
	protected void setValueInternal(Object newValue) {
		setSelected(Boolean.TRUE == newValue || Boolean.TRUE.equals(newValue));
	}
	
	@Override
	public Object getValue() {
		return Boolean.valueOf(isSelected());
	}
	
}