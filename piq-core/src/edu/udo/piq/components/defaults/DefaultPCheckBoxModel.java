package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PCheckBoxModel;
import edu.udo.piq.tools.AbstractPCheckBoxModel;

public class DefaultPCheckBoxModel extends AbstractPCheckBoxModel implements PCheckBoxModel {
	
	protected boolean checked = PCheckBoxModel.DEFAULT_CHECKED_VALUE;
	protected boolean enabled = PCheckBoxModel.DEFAULT_ENABLED_VALUE;
	
	@Override
	public void toggleChecked() {
		Boolean oldValue = Boolean.valueOf(checked);
		checked = !isChecked();
		fireChangeEvent(oldValue);
	}
	
	@Override
	public boolean isChecked() {
		return checked;
	}
	
	@Override
	public void setEnabled(boolean trueIfEnabled) {
		if (enabled != trueIfEnabled) {
			Object oldValue = getValue();
			enabled = trueIfEnabled;
			fireChangeEvent(oldValue);
		}
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
}