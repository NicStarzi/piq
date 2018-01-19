package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PRadioButtonModel;
import edu.udo.piq.tools.AbstractPRadioButtonModel;

public class DefaultPRadioButtonModel extends AbstractPRadioButtonModel implements PRadioButtonModel {
	
	protected boolean selected = PRadioButtonModel.DEFAULT_SELECTED_VALUE;
	
	@Override
	public void setSelected(boolean value) {
		if (selected != value) {
			Object oldValue = getValue();
			selected = value;
			fireChangeEvent(oldValue);
		}
	}
	
	@Override
	public boolean isSelected() {
		return selected;
	}
	
}