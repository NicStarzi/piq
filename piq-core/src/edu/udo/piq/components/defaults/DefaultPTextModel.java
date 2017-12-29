package edu.udo.piq.components.defaults;

import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.tools.AbstractPTextModel;

public class DefaultPTextModel extends AbstractPTextModel implements PTextModel {
	
	protected Object content;
	protected String cachedStr = "";
	
	public DefaultPTextModel() {
	}
	
	public DefaultPTextModel(Object content) {
		this();
		setValue(content);
	}
	
	@Override
	protected void setValueInternal(Object newValue) {
		content = newValue;
		cachedStr = null;
	}
	
	@Override
	public Object getValue() {
		return content;
	}
	
	@Override
	public String getText() {
		if (cachedStr != null) {
			return cachedStr;
		}
		if (getValue() != null) {
			refreshCachedStringValue();
		}
		return cachedStr;
	}
	
	protected void refreshCachedStringValue() {
		cachedStr = getValue().toString();
	}
	
}