package edu.udo.piq.components.popup;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.PComponentActionObs;
import edu.udo.piq.util.Throw;
import edu.udo.piq.util.ThrowException;

public class PMenuItemComponentAction extends AbstractPMenuItem {
	
	protected final PComponentActionObs actionObs = this::refreshComponents;
	protected PComponentActionIndicator indicator;
	protected PComponentAction cachedAction;
	
	public PMenuItemComponentAction(PComponentActionIndicator actionIndicator) {
		Throw.ifNull(actionIndicator, "actionIndicator == null");
		indicator = actionIndicator;
	}
	
	@Override
	protected void onAddedToUi(PRoot newRoot) {
		ThrowException.ifNotNull(cachedAction, "cachedAction != null");
		cachedAction = getAction(newRoot);
		if (cachedAction != null) {
			cachedAction.addObs(actionObs);
		}
		refreshComponents(cachedAction);
	}
	
	@Override
	protected void onRemovedFromUi(PRoot oldRoot) {
		if (cachedAction != null) {
			cachedAction.removeObs(actionObs);
			cachedAction = null;
		}
	}
	
	protected void refreshComponents(PComponentAction action) {
		PRoot root = getRoot();
		boolean enabled = isEnabled(root, action);
		
		compIcon.setModelValue(indicator.getIconValue(root, action));
		compIcon.setEnabled(enabled);
		compLabel.setModelValue(indicator.getLabelValue(root, action));
		compLabel.setEnabled(enabled);
		
		PAccelerator accel;
		if (action == null) {
			accel = indicator.getDefaultAccelerator();
		} else {
			accel = action.getAccelerator();
		}
		compAccelerator.setModelValue(accel);
		compAccelerator.setEnabled(enabled);
	}
	
	public PComponentAction getAction(PRoot root) {
		if (root == null) {
			return null;
		}
		PComponent focusOwner = root.getLastStrongFocusOwner();
		if (focusOwner == null) {
			return null;
		}
		return focusOwner.getAction(indicator.getActionKey());
	}
	
	protected boolean isEnabled(PRoot root, PComponentAction action) {
		return action != null && action.isEnabled(root);
	}
	
	@Override
	public boolean isEnabled() {
		PRoot root = getRoot();
		PComponentAction action = getAction(root);
		return isEnabled(root, action);
	}
	
	@Override
	public void performAction() {
		PRoot root = getRoot();
		getAction(root).tryToPerform(root);
		fireActionEvent();
	}
	
}