package edu.udo.piq.components;

import edu.udo.piq.TemplateMethod;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.PModelFactory;

public abstract class AbstractPInteractiveLayoutOwner extends AbstractPLayoutOwner implements PInteractiveComponent {
	
	protected final PSingleValueModelObs<Boolean> enableObs = this::onEnabledChange;
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
	protected void onEnabledChange(PSingleValueModel<Boolean> model, Boolean oldVal, Boolean newVal) {
		if (hasFocus() && !newVal) {
			releaseFocus();
		}
		fireReRenderEvent();
	}
	
}