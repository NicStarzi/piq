package edu.udo.piq.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;

public class CompositePComponentAction extends AbstractPComponentAction implements PComponentAction {
	
	protected final PComponentActionObs actionObs = action -> fireChangeEvent();
	protected final List<Object> delegateKeys = new ArrayList<>();
	
	public CompositePComponentAction(Object firstKey, Object secondKey) {
		delegateKeys.add(firstKey);
		delegateKeys.add(secondKey);
	}
	
	public CompositePComponentAction(Iterable<Object> actionKeys) {
		for (Object action : actionKeys) {
			delegateKeys.add(action);
		}
	}
	
	public CompositePComponentAction(Collection<Object> actionKeys) {
		delegateKeys.addAll(actionKeys);
	}
	
	public CompositePComponentAction(Object ... actionKeys) {
		delegateKeys.addAll(Arrays.asList(actionKeys));
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
		for (Object key : delegateKeys) {
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
		for (Object key : delegateKeys) {
			PComponentAction delegate = focusOwner.getAction(key);
			delegate.tryToPerform(root);
		}
	}
	
}