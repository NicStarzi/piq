package edu.udo.piq.tools;

import edu.udo.piq.components.AbstractPSingleValueModel;
import edu.udo.piq.components.PCheckBoxModel;

public abstract class AbstractPCheckBoxModel extends AbstractPSingleValueModel<Boolean> implements PCheckBoxModel {
	
	@Override
	protected void setValueInternal(Object newValue) {
		if (Boolean.TRUE.equals(newValue)) {
			if (!isChecked()) {
				toggleChecked();
			}
		} else if (Boolean.FALSE.equals(newValue)) {
			if (isChecked()) {
				toggleChecked();
			}
		}
	}
	
	@Override
	public Boolean getValue() {
		return Boolean.valueOf(isChecked());
	}
	
}