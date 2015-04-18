package edu.udo.piq.components.defaults;

import java.util.EnumMap;
import java.util.Map;

import edu.udo.piq.components.util.PDictionary;

public class EnumPDictionary<E extends Enum<E>> implements PDictionary {
	
	private final Class<E> enumClass;
	private final Map<E, String> transMap;
	
	public EnumPDictionary(Class<E> enumClass) {
		this.enumClass = enumClass;
		transMap = new EnumMap<>(enumClass);
	}
	
	public void setTranslation(E symbol, String translation) {
		transMap.put(symbol, translation);
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
	
}