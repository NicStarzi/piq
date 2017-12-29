package edu.udo.piq.components.popup2;

import java.util.function.Consumer;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.AbstractPComponentAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.PComponentActionObs;
import edu.udo.piq.util.ThrowException;

public class PMenuItem extends AbstractPMenuItem {
	
	public static final Object DEFAULT_ACTION_KEY_ENTER = new Object();
	
	protected final PComponentActionObs actionObs = this::refreshComponents;
	protected PComponentActionIndicator indicator;
	protected PComponentAction cachedAction;
	protected Consumer<PRoot> additionalAction;
	
	{
		addActionMapping(DEFAULT_ACTION_KEY_ENTER, new ActionTriggerOnEnter());
	}
	
	public PMenuItem(Object labelValue, Object iconValue, Consumer<PRoot> additionalAction) {
		this(null, additionalAction);
		setIconValue(iconValue);
		setLabelValue(labelValue);
	}
	
	public PMenuItem(PComponentActionIndicator actionIndicator) {
		this(actionIndicator, null);
	}
	
	public PMenuItem(PComponentActionIndicator actionIndicator, Consumer<PRoot> additionalAction) {
		indicator = actionIndicator;
		this.additionalAction = additionalAction;
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
		if (indicator == null) {
			return;
		}
		PRoot root = getRoot();
		boolean enabled = action != null && action.isEnabled(root);
		
		compIcon.setModelValue(indicator.getIconValue(root, action));
		compIcon.setEnabled(enabled);
		compLabel.setModelValue(indicator.getLabelValue(root, action));
		compLabel.setEnabled(enabled);
		
		PAccelerator accel;
		if (action != null) {
			accel = action.getAccelerator();
		} else {
			accel = indicator.getDefaultAccelerator();
		}
		compAccelerator.setModelValue(accel);
		compAccelerator.setEnabled(enabled);
	}
	
	public PComponentAction getAction(PRoot root) {
		if (indicator == null || root == null) {
			return null;
		}
		PComponent focusOwner = root.getLastStrongFocusOwner();
		if (focusOwner == null) {
			return null;
		}
		return focusOwner.getAction(indicator.getActionKey());
	}
	
	@Override
	public boolean isEnabled() {
		PRoot root = getRoot();
		PComponentAction action = getAction(root);
		if (action != null) {
			return action.isEnabled(root);
		}
		return true;
	}
	
	@Override
	protected void onMouseClick(PMouse mouse, MouseButton btn, int clickCount) {
		if (isEnabled()) {
			performAction();
		}
	}
	
	public void performAction() {
		PRoot root = getRoot();
		PComponentAction action = getAction(root);
		if (action != null) {
			action.tryToPerform(root);
		}
		if (additionalAction != null) {
			additionalAction.accept(root);
		}
		fireActionEvent();
	}
	
	public class ActionTriggerOnEnter extends AbstractPComponentAction implements PComponentAction {
		
		{
			setAccelerator(ActualKey.ENTER, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS);
		}
		
		@Override
		public void tryToPerform(PRoot root) {
			performAction();
		}
		
	}
	
}