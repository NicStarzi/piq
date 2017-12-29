package edu.udo.piq.components.defaults;

import java.util.Objects;

import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.components.util.PDictionary;
import edu.udo.piq.components.util.PDictionaryObs;
import edu.udo.piq.tools.AbstractPTextModel;

public class DictionaryPTextModel extends AbstractPTextModel implements PTextModel {
	
	protected PDictionaryObs dctObs;
	protected PDictionary dct;
	protected Object content;
	protected String cachedStr;
	
	public DictionaryPTextModel() {
	}
	
	public DictionaryPTextModel(PDictionary dictionary) {
		this(dictionary, null);
	}
	
	public DictionaryPTextModel(Object content) {
		this(null, content);
	}
	
	public DictionaryPTextModel(PDictionary dictionary, Object content) {
		this();
		setDictionary(dictionary);
		setValue(content);
	}
	
	public void setDictionary(PDictionary dictionary) {
		PDictionary oldDictionary = getDictionary();
		if (Objects.equals(oldDictionary, dictionary)) {
			return;
		}
		if (oldDictionary != null) {
			oldDictionary.removeObs(dctObs);
		}
		dct = dictionary;
		Object oldValue = cachedStr;
		if (getDictionary() != null) {
			if (dctObs == null) {
				dctObs = new PDictionaryObs() {
					@Override
					public void onTranslationChangedFor(Object value) {
						Object oldValue = cachedStr;
						if (Objects.equals(value, content)) {
							fireChangeEvent(oldValue);
						}
					}
					@Override
					public void onDictionaryChanged() {
						fireChangeEvent(null);
					}
				};
			}
			getDictionary().addObs(dctObs);
			
			String newStr = getDictionary().translate(getValue());
			if (!Objects.equals(cachedStr, newStr)) {
				cachedStr = newStr;
				fireChangeEvent(oldValue);
			}
		} else {
			dctObs = null;
			cachedStr = null;
			fireChangeEvent(oldValue);
		}
	}
	
	public PDictionary getDictionary() {
		return dct;
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
		Object value = getValue();
		PDictionary dictionary = getDictionary();
		if (dictionary != null) {
			cachedStr = dictionary.translate(value);
		}
		return cachedStr;
	}
	
}