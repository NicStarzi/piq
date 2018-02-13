package edu.udo.piq.components.popup;

import java.util.Objects;

import edu.udo.piq.components.AbstractPSingleValueModel;
import edu.udo.piq.components.PCheckBoxModel;
import edu.udo.piq.components.PPictureModel;
import edu.udo.piq.components.PSingleValueModel;
import edu.udo.piq.components.PSingleValueModelObs;
import edu.udo.piq.components.defaults.DefaultPCheckBoxModel;
import edu.udo.piq.components.defaults.DefaultPPictureModel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.util.ThrowException;

public class OverwritePSingleValueModel<VALUE_TYPE> extends AbstractPSingleValueModel<VALUE_TYPE> {
	
	protected final PSingleValueModelObs<VALUE_TYPE> delegateObs = this::onDelegateChanged;
	protected PSingleValueModel<VALUE_TYPE> delegate;
	protected boolean overwriteEnabled;
	protected VALUE_TYPE overwriteValue;
	
	public OverwritePSingleValueModel() {
	}
	
	public OverwritePSingleValueModel(PSingleValueModel<VALUE_TYPE> delegateModel) {
		setDelegateModel(delegateModel);
	}
	
	public void setDelegateModel(PSingleValueModel<VALUE_TYPE> delegateModel) {
		VALUE_TYPE oldValue = getValue();
		if (getDelegateModel() != null) {
			getDelegateModel().removeObs(delegateObs);
		}
		delegate = delegateModel;
		if (getDelegateModel() != null) {
			getDelegateModel().addObs(delegateObs);
		}
		if (!isOverwriteEnabled() && !Objects.equals(oldValue, getValue())) {
			fireChangeEvent(oldValue);
		}
	}
	
	public PSingleValueModel<VALUE_TYPE> getDelegateModel() {
		return delegate;
	}
	
	public void clearOverwrite() {
		if (overwriteEnabled) {
			VALUE_TYPE oldVal = getValue();
			overwriteValue = null;
			overwriteEnabled = false;
			if (!Objects.equals(oldVal, getValue())) {
				fireChangeEvent(oldVal);
			}
		}
	}
	
	public void setOverwrite(VALUE_TYPE value) {
		if (!overwriteEnabled || !Objects.equals(overwriteValue, value)) {
			VALUE_TYPE oldVal = getValue();
			overwriteEnabled = true;
			overwriteValue = value;
			if (!Objects.equals(oldVal, getValue())) {
				fireChangeEvent(oldVal);
			}
		}
	}
	
	public boolean isOverwriteEnabled() {
		return overwriteEnabled;
	}
	
	public VALUE_TYPE getOverwriteValue() {
		return overwriteValue;
	}
	
	@Override
	public VALUE_TYPE getValue() {
		if (isOverwriteEnabled()) {
			return getOverwriteValue();
		}
		if (getDelegateModel() == null) {
			return null;
		}
		return getDelegateModel().getValue();
	}
	
	@Override
	protected void setValueInternal(Object newValue) {
		if (getDelegateModel() != null) {
			getDelegateModel().setValue(newValue);
		}
	}
	
	protected void onDelegateChanged(PSingleValueModel<VALUE_TYPE> model, VALUE_TYPE oldValue, VALUE_TYPE newValue) {
		if (!isOverwriteEnabled()) {
			fireChangeEvent(oldValue);
		}
	}
	
	public static class OverwritePTextModel extends OverwritePSingleValueModel<Object> implements PTextModel {
		
		public OverwritePTextModel() {
			super(new DefaultPTextModel());
		}
		
		@Override
		public void setDelegateModel(PSingleValueModel<Object> delegateModel) {
			ThrowException.ifTypeCastFails(delegateModel, PTextModel.class,
					"(delegateModel instanceof PTextModel) == false");
			super.setDelegateModel(delegateModel);
		}
		
		@Override
		public PTextModel getDelegateModel() {
			return (PTextModel) super.getDelegateModel();
		}
		
		@Override
		public String getText() {
			if (isOverwriteEnabled()) {
				return ""+getValue();
			}
			if (getDelegateModel() != null) {
				return getDelegateModel().getText();
			}
			return "";
		}
		
	}
	
	public static class OverwritePPictureModel extends OverwritePSingleValueModel<Object> implements PPictureModel {
		
		public OverwritePPictureModel() {
			super(new DefaultPPictureModel());
		}
		
		@Override
		public void setDelegateModel(PSingleValueModel<Object> delegateModel) {
			ThrowException.ifTypeCastFails(delegateModel, PPictureModel.class,
					"(delegateModel instanceof PPictureModel) == false");
			super.setDelegateModel(delegateModel);
		}
		
		@Override
		public PPictureModel getDelegateModel() {
			return (PPictureModel) super.getDelegateModel();
		}
		
	}
	
	public static class OverwritePCheckBoxModel extends OverwritePSingleValueModel<Boolean> implements PCheckBoxModel {
		
		public OverwritePCheckBoxModel() {
			super(new DefaultPCheckBoxModel());
		}
		
		@Override
		public void setDelegateModel(PSingleValueModel<Boolean> delegateModel) {
			ThrowException.ifTypeCastFails(delegateModel, PCheckBoxModel.class,
					"(delegateModel instanceof PCheckBoxModel) == false");
			super.setDelegateModel(delegateModel);
		}
		
		@Override
		public PCheckBoxModel getDelegateModel() {
			return (PCheckBoxModel) super.getDelegateModel();
		}
		
		@Override
		public void toggleChecked() {
			if (getDelegateModel() != null) {
				getDelegateModel().toggleChecked();
			}
		}
		
		@Override
		public boolean isChecked() {
			if (getDelegateModel() != null) {
				return getDelegateModel().isChecked();
			}
			return Boolean.TRUE.equals(getValue());
		}
		
	}
	
}