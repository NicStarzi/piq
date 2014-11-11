package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PLabelModel;
import edu.udo.piq.tools.AbstractPLabelModel;

public class DefaultPLabelModel extends AbstractPLabelModel implements PLabelModel {
	
	private Object content;
	
	public DefaultPLabelModel() {
		this(null);
	}
	
	public DefaultPLabelModel(Object content) {
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