package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PTextModel;
import edu.udo.piq.tools.AbstractPTextModel;

public class DefaultPTextModel extends AbstractPTextModel implements PTextModel {
	
	private Object content;
	
	public DefaultPTextModel() {
		this(null);
	}
	
	public DefaultPTextModel(Object content) {
		setText(content);
	}
	
	public void setText(Object text) {
		content = text;
		fireTextChangeEvent();
	}
	
	public Object getText() {
		return content;
	}
	
}