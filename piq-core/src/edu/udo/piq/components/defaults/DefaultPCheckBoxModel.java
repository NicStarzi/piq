package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PCheckBoxModel;
import edu.udo.piq.tools.AbstractPCheckBoxModel;

public class DefaultPCheckBoxModel extends AbstractPCheckBoxModel implements PCheckBoxModel {
	
	protected boolean checked;
	
	public void toggleChecked() {
		checked = !isChecked();
		fireChangeEvent();
	}
	
	public boolean isChecked() {
		return checked;
	}
	
}