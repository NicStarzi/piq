package edu.udo.piq.components;

import edu.udo.piq.TemplateMethod;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.util.PModelFactory;

public abstract class AbstractPInteractiveComponent extends AbstractPComponent implements PInteractiveComponent {
	
	protected final PSingleValueModelObs enableObs = this::onEnabledChange;
	protected PEnableModel enableModel;
	
	{
		setEnableModel(PModelFactory.createModelFor(this, DefaultPEnableModel::new, PEnableModel.class));
	}
	
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
	
	@Override
	public boolean isFocusable() {
		return isEnabled();
	}
	
	@TemplateMethod
	protected void onEnabledChange(PSingleValueModel model, Object oldVal, Object newVal) {
		if (hasFocus() && !isEnabled()) {
			releaseFocus();
		}
		fireReRenderEvent();
	}
	
}