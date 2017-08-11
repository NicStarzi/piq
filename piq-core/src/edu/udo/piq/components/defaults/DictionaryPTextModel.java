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
		if (getDictionary() != null) {
			if (dctObs == null) {
				dctObs = new PDictionaryObs() {
					@Override
					public void onTranslationChangedFor(Object value) {
						if (Objects.equals(value, content)) {
							fireTextChangeEvent();
						}
					}
					@Override
					public void onDictionaryChanged() {
						fireTextChangeEvent();
					}
				};
			}
			getDictionary().addObs(dctObs);
			
			String newStr = getDictionary().translate(getValue());
			if (!Objects.equals(cachedStr, newStr)) {
				cachedStr = newStr;
				fireTextChangeEvent();
			}
		} else {
			dctObs = null;
			cachedStr = null;
			fireTextChangeEvent();
		}
	}
	
	public PDictionary getDictionary() {
		return dct;
	}
	
	@Override
	public void setValue(Object value) {
		if (Objects.equals(content, value)) {
			return;
		}
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
		PDictionary dictionary = getDictionary();
		if (dictionary != null) {
			cachedStr = dictionary.translate(value);
		}
		return cachedStr;
	}
	
}