package edu.udo.piq.components;

import edu.udo.piq.PComponent;

public interface PInteractiveComponent extends PComponent {
	
	public void setEnableModel(PEnableModel model);
	
	public PEnableModel getEnableModel();
	
	public default void setEnabled(boolean isEnabled) {
		PEnableModel model = getEnableModel();
		if (model != null) {
			model.setValue(isEnabled);
		}
	}
	
	public default boolean isEnabled() {
		PEnableModel model = getEnableModel();
		if (model == null) {
			return false;
		}
		return model.isEnabled();
	}
	
}