package edu.udo.piq.components.popup;

import edu.udo.piq.PRoot;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;

public class ImmutablePActionIndicator implements PComponentActionIndicator {
	
	protected final PActionKey actKey;
	protected final Object iconValEnabled;
	protected final Object iconValDisabled;
	protected final Object labelValEnabled;
	protected final Object labelValDisabled;
	protected final PAccelerator defaultAccel;
	
	public ImmutablePActionIndicator(PActionKey actionKey, Object labelValue) {
		this(actionKey, labelValue, labelValue, null, null, null);
	}
	
	public ImmutablePActionIndicator(PActionKey actionKey, Object labelValue, Object iconValue) {
		this(actionKey, labelValue, labelValue, iconValue, iconValue, null);
	}
	
	public ImmutablePActionIndicator(PActionKey actionKey,
			Object labelValue, Object iconValue, PAccelerator accelerator)
	{
		this(actionKey, labelValue, labelValue, iconValue, iconValue, accelerator);
	}
	
	public ImmutablePActionIndicator(PActionKey actionKey,
			Object labelValueEnabled, Object labelValueDisabled,
			Object iconValueEnabled, Object iconValueDisabled,
			PAccelerator accelerator)
	{
		actKey = actionKey;
		iconValEnabled = iconValueEnabled;
		iconValDisabled = iconValueDisabled;
		labelValEnabled = labelValueEnabled;
		labelValDisabled = labelValueDisabled;
		defaultAccel = accelerator;
	}
	
	@Override
	public PActionKey getActionKey() {
		return actKey;
	}
	
	@Override
	public Object getIconValue(PRoot root, PComponentAction action) {
		if (action != null && action.isEnabled(root)) {
			return iconValEnabled;
		}
		return iconValDisabled;
	}
	
	@Override
	public Object getLabelValue(PRoot root, PComponentAction action) {
		if (action != null && action.isEnabled(root)) {
			return labelValEnabled;
		}
		return labelValDisabled;
	}
	
	@Override
	public PAccelerator getDefaultAccelerator() {
		return defaultAccel;
	}
	
	@Override
	public String toString() {
		Object actionKey = getActionKey();
		String actionKeyString = actionKey == null ? "null" : actionKey.toString();
		
		int strLength = "[ActionKey=".length() + actionKeyString.length() + "]".length();
		StringBuilder sb = new StringBuilder(strLength);
		sb.append("[ActionKey=");
		sb.append(getActionKey());
		sb.append(']');
		return sb.toString();
	}
	
}