package edu.udo.piq.components.defaults;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import edu.udo.piq.components.util.PDictionary;
import edu.udo.piq.tools.AbstractMapDictionary;
import edu.udo.piq.util.ThrowException;

public class EnumPDictionary<E extends Enum<E>> extends AbstractMapDictionary<E> implements PDictionary {
	
	protected final Class<E> enumClass;
	protected final Map<E, String> transMap;
	protected Function<E, String> defaultTranslator;
	protected String defaultString = "null";
	
	public EnumPDictionary(Class<E> enumClass) {
		this.enumClass = enumClass;
		transMap = new EnumMap<>(enumClass);
	}
	
	public void setDefaultString(String value) {
		ThrowException.ifNull(value, "value == null");
		defaultString = value;
	}
	
	public String getDefaultString() {
		return defaultString;
	}
	
	public void setDefaultTranslator(Function<E, String> defaultTranslator) {
		this.defaultTranslator = defaultTranslator;
	}
	
	public Function<E, String> getDefaultTranslator() {
		return defaultTranslator;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String translate(Object value) {
		if (value == null) {
			return defaultString;
		}
		if (enumClass.isInstance(value)) {
			String translation = transMap.get(value);
			if (translation == null) {
				if (defaultTranslator == null) {
					return defaultString;
				}
				return defaultTranslator.apply((E) value);
			}
			return translation;
		}
		return value.toString();
	}
	
	@Override
	protected Map<E, String> getTranslationMap() {
		return transMap;
	}
	
}