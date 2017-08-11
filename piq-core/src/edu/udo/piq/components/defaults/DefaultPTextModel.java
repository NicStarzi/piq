package edu.udo.piq.components.defaults;

import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.tools.AbstractPTextModel;

public class DefaultPTextModel extends AbstractPTextModel implements PTextModel {
	
	protected Object content;
	protected String cachedStr;
	
	public DefaultPTextModel() {
	}
	
	public DefaultPTextModel(Object content) {
		this();
		setValue(content);
	}
	
	@Override
	public void setValue(Object value) {
		content = value;
		cachedStr = null;
		fireTextChangeEvent();
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
		Object value = getValue();
		if (value != null) {
			cachedStr = value.toString();
		}
		return cachedStr;
	}
	
}