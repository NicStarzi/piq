package edu.udo.piq;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Implements a {@link UserDataStore} by using a {@link HashMap}.
 * <p>Data type inconsistencies are ignored by this implementation.
 */
public class DefaultUserDataStore implements UserDataStore {
	
	protected final Map<String, Object> map = new HashMap<>();
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T set(UserDataKey<T> key, T value) {
		Object oldValue = map.get(key.getKeyName());
		map.put(key.getKeyName(), value);
		
		if (oldValue == null || !key.getDataType().isInstance(oldValue)) {
			return key.getDefaultValue();
		}
		return (T) oldValue;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(UserDataKey<T> key) {
		String idf = key.getKeyName();
		
		Object value = map.get(idf);
		if (value == null || !key.getDataType().isInstance(value)) {
			map.put(idf, key.getDefaultValue());
			return key.getDefaultValue();
		}
		return (T) value;
	}
	
	@Override
	public Map<String, Object> asMap() {
		return Collections.unmodifiableMap(map);
	}
	
//	protected String errorMessageBadTypeRead(UserDataKey<?> key, Object value) {
//		return "Data store contains a value of invalid type for the given key. "
//				+ "This happens when two keys with identical names are used with different data types. "
//				+ "Expected type: "+key.getDataType()+"; actual type: "+value.getClass();
//	}
	
}