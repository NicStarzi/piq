package edu.udo.piq.components;

import edu.udo.piq.TemplateMethod;
import edu.udo.piq.tools.AbstractPComponent;

public abstract class AbstractPInteractiveComponent extends AbstractPComponent implements PInteractiveComponent {
	
	protected final PSingleValueModelObs enableObs = this::onEnabledChange;
	protected PEnableModel enableModel;
	
	@Override
	public void setEnableModel(PEnableModel model) {
		if (getEnableModel() != null) {
			getEnableModel().removeObs(enableObs);
		}
		enableModel = model;
		if (getEnableModel() != null) {
			getEnableModel().addObs(enableObs);
		}
	}
	
	@Override
	public PEnableModel getEnableModel() {
		return enableModel;
	}
	
	@TemplateMethod
	protected void onEnabledChange(PSingleValueModel model, Object oldVal, Object newVal) {
		fireReRenderEvent();
	}
	
}