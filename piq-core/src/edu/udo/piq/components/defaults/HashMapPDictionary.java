package edu.udo.piq.components.defaults;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.components.util.PDictionary;
import edu.udo.piq.components.util.PDictionaryObs;
import edu.udo.piq.tools.AbstractMapDictionary;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class HashMapPDictionary extends AbstractMapDictionary<Object> implements PDictionary {
	
	protected final ObserverList<PDictionaryObs> obsList = PCompUtil
			.createDefaultObserverList();
	private final Map<Object, String> transMap = new HashMap<>();
	
	public String translate(Object value) {
		String result = transMap.get(value);
		if (result == null) {
			return "null";
		}
		return result;
	}
	
	protected Map<Object, String> getTranslationMap() {
		return transMap;
	}
	
}