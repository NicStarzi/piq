package edu.udo.piq.components.defaults;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import edu.udo.piq.tools.AbstractPSpinnerModel;
import edu.udo.piq.util.ThrowException;

public class PSpinnerModelEnum<E extends Enum<E>> extends AbstractPSpinnerModel {
	
	protected static <E> int indexOf(Class<E> enumClass, E selectedValue) {
		ThrowException.ifNull(enumClass, "enumClass == null");
		ThrowException.ifNull(selectedValue, "selectedValue == null");
		ThrowException.ifFalse(enumClass.isEnum(), "enumClass.isEnum() == false");
		E[] constants = enumClass.getEnumConstants();
		for (int i = 0; i < constants.length; i++) {
			if (constants[i] == selectedValue) {
				return i;
			}
		}
		// This should be impossible!
		return -1;
	}
	
	protected final List<E> values;
	protected Function<String, Object> decoder;
	protected int index;
	
	public PSpinnerModelEnum(Class<E> enumClass, int selectedIndex) {
		ThrowException.ifNull(enumClass, "enumClass == null");
		ThrowException.ifFalse(enumClass.isEnum(), "enumClass.isEnum() == false");
		values = Arrays.asList(enumClass.getEnumConstants());
		ThrowException.ifNotWithin(0, values.size(), selectedIndex,
				"selectedIndex < 0 || selectedIndex >= values.size()");
		index = selectedIndex;
	}
	
	public PSpinnerModelEnum(Class<E> enumClass, E selectedValue) {
		this(enumClass, PSpinnerModelEnum.indexOf(enumClass, selectedValue));
	}
	
	public void setInputDecoder(Function<String, Object> stringDecoder) {
		decoder = stringDecoder;
	}
	
	public Function<String, Object> getInputDecoder() {
		return decoder;
	}
	
	protected List<E> getValueList() {
		return values;
	}
	
	protected int getValueIndex() {
		return index;
	}
	
	@Override
	public boolean hasNext() {
		return getValueIndex() < getValueList().size() - 1;
	}
	
	@Override
	public boolean hasPrevious() {
		return getValueIndex() > 0;
	}
	
	@Override
	public E getNext() {
		return getValueList().get(getValueIndex() + 1);
	}
	
	@Override
	public E getPrevious() {
		return getValueList().get(getValueIndex() - 1);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean canSetValue(Object obj) {
		if (getValueList().contains(obj)) {
			return true;
		}
		if (decoder != null && obj instanceof String) {
			return getValueList().contains(
					decoder.apply((String) obj));
		}
		return false;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void setValue(Object obj) {
		ThrowException.ifFalse(canSetValue(obj),
				"canSetValue(value) == false");
		if (!getValueList().contains(obj)) {
			obj = decoder.apply((String) obj);
		}
		if (!obj.equals(getValue())) {
			Object oldValue = getValue();
			index = getValueList().indexOf(obj);
			fireValueChangedEvent(oldValue);
		}
	}
	
	@Override
	public E getValue() {
		return getValueList().get(getValueIndex());
	}
	
}