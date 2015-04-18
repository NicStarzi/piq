package edu.udo.piq.components.defaults;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.components.util.PDictionary;

public class HashMapPDictionary implements PDictionary {
	
	private final Map<Object, String> transMap = new HashMap<>();
	
	public void setTranslation(Object symbol, String translation) {
		transMap.put(symbol, translation);
	}
	
	public String translate(Object value) {
		String result = transMap.get(value);
		if (result == null) {
			return "null";
		}
		return result;
	}
	
}