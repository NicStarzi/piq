package edu.udo.piq.components.defaults;

import java.util.EnumMap;
import java.util.Map;

import edu.udo.piq.components.util.PDictionary;
import edu.udo.piq.components.util.PDictionaryObs;
import edu.udo.piq.tools.AbstractMapDictionary;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class EnumPDictionary<E extends Enum<E>> extends AbstractMapDictionary<E> implements PDictionary {
	
	protected final ObserverList<PDictionaryObs> obsList = PCompUtil
			.createDefaultObserverList();
	private final Class<E> enumClass;
	private final Map<E, String> transMap;
	
	public EnumPDictionary(Class<E> enumClass) {
		this.enumClass = enumClass;
		transMap = new EnumMap<>(enumClass);
	}
	
	public String translate(Object value) {
		if (value == null) {
			return "";
		}
		if (value.getClass() == enumClass) {
			return transMap.get(value);
		}
		return value.toString();
	}
	
	protected Map<E, String> getTranslationMap() {
		return transMap;
	}
	
}