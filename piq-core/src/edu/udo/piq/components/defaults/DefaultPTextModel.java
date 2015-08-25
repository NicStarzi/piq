package edu.udo.piq.components.defaults;

import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.components.util.PDictionary;
import edu.udo.piq.tools.AbstractPTextModel;

public class DefaultPTextModel extends AbstractPTextModel implements PTextModel {
	
	private PDictionary dct;
	private Object content;
	
	public DefaultPTextModel() {
		this(null);
	}
	
	public DefaultPTextModel(Object content) {
		setValue(content);
	}
	
	public void setDictionary(PDictionary dictionary) {
		if (dct == dictionary
			|| (dictionary != null && dictionary.equals(dct))) 
		{
			return;
		}
		dct = dictionary;
		fireTextChangeEvent();
	}
	
	public PDictionary getDictionary() {
		return dct;
	}
	
	public void setValue(Object value) {
		content = value;
		fireTextChangeEvent();
	}
	
	public Object getValue() {
		return content;
	}
	
	public String getText() {
		if (getDictionary() != null) {
			return getDictionary().translate(getValue());
		}
		if (getValue() == null) {
			return "null";
		}
		return getValue().toString();
	}
	
}