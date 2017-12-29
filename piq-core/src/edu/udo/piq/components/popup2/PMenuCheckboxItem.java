package edu.udo.piq.components.popup2;

import java.util.function.Consumer;

import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.PComponentActionObs;
import edu.udo.piq.actions.PComponentBoolProperty;
import edu.udo.piq.components.popup2.OverwritePSingleValueModel.OverwritePCheckBoxModel;

public class PMenuCheckboxItem extends AbstractPMenuItem {
	
	protected final PComponentActionObs actionObs = this::onActionChanged;
	protected PComponentBoolProperty property;
	protected Consumer<PRoot> additionalAction;
	protected PMenuCheckBox compChkBx = new PMenuCheckBox();
	{
		compChkBx.setModel(new OverwritePCheckBoxModel());
		setComponent(MenuEntryPart.CHECKBOX, compChkBx);
	}
	
	public PMenuCheckboxItem(Object labelValue, Object iconValue, Consumer<PRoot> additionalAction) {
		this(null, additionalAction);
		setIconValue(iconValue);
		setLabelValue(labelValue);
	}
	
	public PMenuCheckboxItem(PComponentBoolProperty property) {
		this(property, null);
	}
	
	public PMenuCheckboxItem(PComponentBoolProperty property, Consumer<PRoot> additionalAction) {
		this.property = property;
		this.additionalAction = additionalAction;
	}
	
	@Override
	protected void onAddedToUi(PRoot newRoot) {
		if (property != null) {
			property.addObs(actionObs);
			onActionChanged(property);
		}
	}
	
	@Override
	protected void onRemovedFromUi(PRoot oldRoot) {
		if (property != null) {
			property.removeObs(actionObs);
		}
	}
	
	public PMenuCheckBox getCheckBoxComponent() {
		return compChkBx;
	}
	
	public void setCheckBoxChecked(boolean value) {
		AbstractPMenuItem.setOverwriteValue(getCheckBoxComponent().getModel(), value);
	}
	
	public boolean isCheckBoxChecked() {
		return getCheckBoxComponent().getModel().isChecked();
	}
	
	protected void onActionChanged(PComponentAction action) {
		PRoot root = getRoot();
		boolean enabled = action.isEnabled(root);
		try {
			PComponentBoolProperty boolProp = (PComponentBoolProperty) action;
			compChkBx.setModelValue(boolProp.getPropertyValue(root));
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		compChkBx.setEnabled(enabled);
//		compIcon.setModelValue(action.getIconValue(root));
		compIcon.setEnabled(enabled);
//		compLabel.setModelValue(action.getLabelValue(root));
		compLabel.setEnabled(enabled);
		compAccelerator.setModelValue(action.getAccelerator().getDefaultIdentifier());
		compAccelerator.setEnabled(enabled);
	}
	
	@Override
	public boolean isEnabled() {
		if (property != null) {
			return property.isEnabled(getRoot());
		}
		return true;
	}
	
	@Override
	protected void onMouseClick(PMouse mouse, MouseButton btn, int clickCount) {
		performAction();
	}
	
	public void performAction() {
		PRoot root = getRoot();
		if (property != null) {
			property.tryToPerform(root);
		}
		if (additionalAction != null) {
			additionalAction.accept(root);
		}
		fireActionEvent();
	}
	
//	public static class OverwriteCheckBoxModel extends OverwritePSingleValueModel.OverwritePCheckBoxModel {
//		
//		protected boolean overwriteValue = false;
//		protected boolean overwriteEnabled = false;
//		
//		@Override
//		public void clearOverwrite() {
//			if (overwriteEnabled) {
//				overwriteEnabled = false;
//				fireChangeEvent(Boolean.valueOf(overwriteEnabled));
//			}
//		}
//		
//		@Override
//		public void setOverwrite(Object value) {
//			if (!overwriteEnabled || !Objects.equals(overwriteValue, value)) {
//				overwriteEnabled = true;
//				overwriteValue = Boolean.TRUE.equals(value);
//				fireChangeEvent(super.getValue());
//			}
//		}
//		
//		@Override
//		public boolean isChecked() {
//			if (overwriteEnabled) {
//				return overwriteValue;
//			}
//			return super.isChecked();
//		}
//		
//		@Override
//		public void setValue(Object obj) {
//			super.setValue(obj);
//		}
//		
//		@Override
//		public Object getValue() {
//			return super.getValue();
//		}
//	}
	
}