package edu.udo.piq.components.textbased;

import edu.udo.piq.components.PSingleValueModel;

public interface PTextModel extends PSingleValueModel<Object> {
	
	public String getText();
	
	public default int getLength() {
		return getText().length();
	}
	
}