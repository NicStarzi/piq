package edu.udo.piq.components.defaults;

import java.util.Arrays;
import java.util.List;

import edu.udo.piq.components.util.StrToObj;
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
	
	private final List<E> values;
	private StrToObj decoder;
	private int index;
	
	public PSpinnerModelEnum(Class<E> enumClass, int selectedIndex) {
		ThrowException.ifNull(enumClass, "enumClass == null");
		ThrowException.ifFalse(enumClass.isEnum(), "enumClass.isEnum() == false");
		values = Arrays.asList(enumClass.getEnumConstants());
		ThrowException.ifNotWithin(0, values.size(), selectedIndex, 
				"selectedIndex < 0 || selectedIndex >= values.size()");
		index = selectedIndex;
	}
	
	public PSpinnerModelEnum(Class<E> enumClass, E selectedValue) {
		this(enumClass, indexOf(enumClass, selectedValue));
	}
	
	public void setInputDecoder(StrToObj stringDecoder) {
		decoder = stringDecoder;
	}
	
	public StrToObj getInputDecoder() {
		return decoder;
	}
	
	protected List<E> getValueList() {
		return values;
	}
	
	protected int getValueIndex() {
		return index;
	}
	
	public boolean hasNext() {
		return getValueIndex() < getValueList().size() - 1;
	}
	
	public boolean hasPrevious() {
		return getValueIndex() > 0;
	}
	
	public E getNext() {
		return getValueList().get(getValueIndex() + 1);
	}
	
	public E getPrevious() {
		return getValueList().get(getValueIndex() - 1);
	}
	
	public boolean canSetValue(Object obj) {
		if (getValueList().contains(obj)) {
			return true;
		}
		if (decoder != null && obj instanceof String) {
			return getValueList().contains(
					decoder.parse((String) obj));
		}
		return false;
	}
	
	public void setValue(Object obj) {
		ThrowException.ifFalse(canSetValue(obj), 
				"canSetValue(value) == false");
		if (!getValueList().contains(obj)) {
			obj = decoder.parse((String) obj);
		}
		if (!obj.equals(getValue())) {
			Object oldValue = getValue();
			index = getValueList().indexOf(obj);
			fireValueChangedEvent(oldValue);
		}
	}
	
	public E getValue() {
		return getValueList().get(getValueIndex());
	}
	
}