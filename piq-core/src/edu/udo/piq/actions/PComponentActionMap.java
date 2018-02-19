package edu.udo.piq.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PAccelerator.KeyInputType;

public class PComponentActionMap {
	
	protected final PComponent owner;
	/**
	 * Lazily initialized in the {@link #addAction(PActionKey, PComponentAction)}
	 * method. <br>This will usually be a HashMap since the nature of the keys is not known.
	 */
	protected Map<PActionKey, PComponentAction> actionMap;
	/**
	 * Lazily initialized in the {@link #addAction(PActionKey, PComponentAction)}
	 * method.
	 */
	protected PKeyboardObs keyObs;
	/**
	 * The most permissive {@link FocusPolicy} of all {@link PAccelerator key inputs}. This
	 * is used to filter out unneeded events before iterating over all known inputs.<br>
	 * This value needs to be updated each time an input is added or removed.<br>
	 */
	protected FocusPolicy mostPermissivePlcy = FocusPolicy.NEVER;
	
	public PComponentActionMap(PComponent component) {
		owner = component;
	}
	
	public PComponent getOwner() {
		return owner;
	}
	
	public PComponentAction getAction(PActionKey key) {
		if (actionMap == null) {
			return null;
		}
		return actionMap.get(key);
	}
	
	public boolean hasAction(PActionKey key) {
		return getAction(key) != null;
	}
	
	public Collection<PComponentAction> getActions() {
		return actionMap.values();
	}
	
	public boolean hasAction(PComponentAction action) {
		return actionMap != null && actionMap.containsValue(action);
	}
	
	public void addAction(PActionKey key, PComponentAction action) {
		if (actionMap == null) {
			actionMap = new HashMap<>();
		}
		actionMap.put(key, action);
		if (action.getAccelerator() == null) {
			return;
		}
		
		// Update most permissive focus policy
		FocusPolicy newFocusPlcy = action.getAccelerator().getFocusPolicy();
		if (mostPermissivePlcy.ordinal() < newFocusPlcy.ordinal()) {
			mostPermissivePlcy = newFocusPlcy;
		}
		
		// Register keyboard observer if needed
		refreshKeyObsAsNeeded();
	}
	
	public void removeAction(PActionKey key) {
		if (actionMap == null) {
			return;
		}
		PComponentAction action = actionMap.remove(key);
		if (action.getAccelerator() == null) {
			return;
		}
		
		// Update most permissive focus policy
		mostPermissivePlcy = FocusPolicy.NEVER;
		for (PComponentAction otherAction : actionMap.values()) {
			FocusPolicy plcy = otherAction.getAccelerator().getFocusPolicy();
			if (mostPermissivePlcy.ordinal() < plcy.ordinal()) {
				mostPermissivePlcy = plcy;
			}
		}
		
		// Unregister keyboard observer if it is no longer needed
		refreshKeyObsAsNeeded();
		if (actionMap.isEmpty()) {
			actionMap = null;
		}
	}
	
	protected void refreshKeyObsAsNeeded() {
		boolean oldRegistered = keyObs != null;
		boolean newRegistered = actionMap != null
								&& !actionMap.isEmpty()
								&& mostPermissivePlcy != FocusPolicy.NEVER;
		
		if (oldRegistered != newRegistered) {
			if (oldRegistered) {
				owner.removeObs(keyObs);
				keyObs = null;
			} else {
				keyObs = new PKeyboardObs() {
					@Override
					public void onKeyPressed(PKeyboard keyboard, ActualKey key) {
						PComponentActionMap.this.onEvent(keyboard, key, KeyInputType.PRESS);
					}
					@Override
					public void onKeyTriggered(PKeyboard keyboard, ActualKey key) {
						PComponentActionMap.this.onEvent(keyboard, key, KeyInputType.TRIGGER);
					}
					@Override
					public void onKeyReleased(PKeyboard keyboard, ActualKey key) {
						PComponentActionMap.this.onEvent(keyboard, key, KeyInputType.RELEASE);
					}
				};
				owner.addObs(keyObs);
			}
		}
	}
	
	public void clear() {
		if (keyObs != null) {
			owner.removeObs(keyObs);
			keyObs = null;
		}
		actionMap.clear();
		actionMap = null;
		mostPermissivePlcy = FocusPolicy.NEVER;
	}
	
	protected void onEvent(PKeyboard kb, ActualKey key, KeyInputType inputType) {
		if (actionMap == null) {
			throw new IllegalArgumentException("An event was recorded without having any "
					+ "key input defined. This can only happen when the "+PComponentActionMap.class.getSimpleName()
					+ " class has been sub classed and its methods overwritten incorrectly.");
		}
		PComponent owner = getOwner();
		// Early exit
		if (!mostPermissivePlcy.canTriggerFor(owner)) {
			return;
		}
		PRoot root = owner.getRoot();
		for (PComponentAction action : actionMap.values()) {
			if (canTrigger(root, action, kb, key, inputType)) {
				action.tryToPerform(root);
			}
		}
	}
	
	protected boolean canTrigger(PRoot root, PComponentAction action,
			PKeyboard kb, ActualKey triggerKey, KeyInputType inputType)
	{
		PAccelerator accelerator = action.getAccelerator();
		if (accelerator == null) {
			return false;
		}
		if (!accelerator.getFocusPolicy().canTriggerFor(owner)) {
			return false;
		}
		if (accelerator.getKeyInputType() != inputType) {
			return false;
		}
		for (int i = 0; i < accelerator.getModifierCount(); i++) {
			if (!kb.isModifierToggled(accelerator.getModifier(i))) {
				return false;
			}
		}
		for (int i = 0; i < accelerator.getKeyCount(); i++) {
			Key key = accelerator.getKey(i);
			if (!kb.isEqualKey(triggerKey, key) && !kb.isPressed(key)) {
				return false;
			}
		}
		return action.isEnabled(root);
	}
	
}