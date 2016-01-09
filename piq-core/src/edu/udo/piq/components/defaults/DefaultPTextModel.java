package edu.udo.piq.components.defaults;

import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.components.util.PDictionary;
import edu.udo.piq.components.util.PDictionaryObs;
import edu.udo.piq.tools.AbstractPTextModel;

public class DefaultPTextModel extends AbstractPTextModel implements PTextModel {
	
	private PDictionaryObs dctObs;
	private PDictionary dct;
	private Object content;
	
	public DefaultPTextModel() {
		this(null);
	}
	
	public DefaultPTextModel(Object content) {
		setValue(content);
	}
	
	public void setDictionary(PDictionary dictionary) {
		if (getDictionary() == dictionary
			|| (dictionary != null 
				&& dictionary.equals(getDictionary()))) 
		{
			return;
		}
		if (getDictionary() != null) {
			getDictionary().removeObs(dctObs);
		}
		dct = dictionary;
		if (getDictionary() != null) {
			if (dctObs == null) {
				dctObs = new PDictionaryObs() {
					public void onTranslationChanged(Object value) {
						if (value == content || value.equals(content)) {
							fireTextChangeEvent();
						}
					}
					public void onDictionaryChanged() {
						fireTextChangeEvent();
					}
				};
			}
			getDictionary().addObs(dctObs);
		} else {
			dctObs = null;
		}
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