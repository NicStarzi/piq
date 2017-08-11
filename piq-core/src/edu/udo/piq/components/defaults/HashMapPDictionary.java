package edu.udo.piq.components.defaults;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.components.util.PDictionary;
import edu.udo.piq.tools.AbstractMapDictionary;

public class HashMapPDictionary extends AbstractMapDictionary<Object> implements PDictionary {
	
	protected final Map<Object, String> transMap = new HashMap<>();
	protected String defaultString = "null";
	
	@Override
	public String translate(Object value) {
		String result = transMap.get(value);
		if (result == null) {
			return defaultString;
		}
		return result;
	}
	
	@Override
	protected Map<Object, String> getTranslationMap() {
		return transMap;
	}
	
}