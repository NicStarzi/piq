package edu.udo.piq.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;

public class CompositeAction extends AbstractPComponentAction implements PComponentAction {
	
	protected final PComponentActionObs actionObs = action -> fireChangeEvent();
	protected final List<PActionKey> delegateKeys = new ArrayList<>();
	
	public CompositeAction(PAccelerator accelerator, PActionKey firstKey, PActionKey secondKey) {
		super(accelerator);
		delegateKeys.add(firstKey);
		delegateKeys.add(secondKey);
	}
	
	public CompositeAction(PAccelerator accelerator, Iterable<PActionKey> actionKeys) {
		super(accelerator);
		for (PActionKey action : actionKeys) {
			delegateKeys.add(action);
		}
	}
	
	public CompositeAction(PAccelerator accelerator, Collection<PActionKey> actionKeys) {
		super(accelerator);
		delegateKeys.addAll(actionKeys);
	}
	
	public CompositeAction(PAccelerator accelerator, PActionKey ... actionKeys) {
		super(accelerator);
		delegateKeys.addAll(Arrays.asList(actionKeys));
	}
	
	@Override
	public AbstractPComponentAction clone() {
		return new CompositeAction(getAccelerator(), delegateKeys);
	}
	
	@Override
	public boolean isEnabled(PRoot root) {
		PComponent focusOwner = root.getLastStrongFocusOwner();
		if (focusOwner == null) {
			return false;
		}
		if (!focusOwner.hasAction(this)) {
			return false;
		}
		for (PActionKey key : delegateKeys) {
			PComponentAction delegate = focusOwner.getAction(key);
			if (delegate == null || !delegate.isEnabled(root)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void tryToPerform(PRoot root) {
		PComponent focusOwner = root.getLastStrongFocusOwner();
		for (PActionKey key : delegateKeys) {
			PComponentAction delegate = focusOwner.getAction(key);
			delegate.tryToPerform(root);
		}
	}
	
}