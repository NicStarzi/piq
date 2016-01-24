package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.tools.AbstractPSpinnerModel;
import edu.udo.piq.util.ThrowException;

public class PSpinnerModelList extends AbstractPSpinnerModel {
	
	private final List<Object> valList;
	private StringDecoder decoder;
	private int index;
	
	public PSpinnerModelList(Object value, Object[] values) {
		ThrowException.ifNull(value, "value == null");
		ThrowException.ifNull(values, "values == null");
		valList = new ArrayList<>(values.length);
		for (Object obj : values) {
			valList.add(obj);
		}
		index = valList.indexOf(value);
		ThrowException.ifLess(0, index, "values.contains(value) == false");
	}
	
	public void setStringDecoder(StringDecoder stringDecoder) {
		decoder = stringDecoder;
	}
	
	public StringDecoder getStringDecoder() {
		return decoder;
	}
	
	protected List<Object> getValueList() {
		return valList;
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
	
	public Object getNext() {
		return getValueList().get(getValueIndex() + 1);
	}
	
	public Object getPrevious() {
		return getValueList().get(getValueIndex() - 1);
	}
	
	public boolean canSetValue(Object obj) {
		if (getValueList().contains(obj)) {
			return true;
		}
		if (decoder != null && obj instanceof String) {
			return getValueList().contains(
					decoder.fromString((String) obj));
		}
		return false;
	}
	
	public void setValue(Object obj) {
		ThrowException.ifFalse(canSetValue(obj), 
				"canSetValue(value) == false");
		if (!getValueList().contains(obj)) {
			obj = decoder.fromString((String) obj);
		}
		if (!obj.equals(getValue())) {
			Object oldValue = getValue();
			index = getValueList().indexOf(obj);
			fireValueChangedEvent(oldValue);
		}
	}
	
	public Object getValue() {
		return getValueList().get(getValueIndex());
	}
	
	public static interface StringDecoder {
		public Object fromString(String str);
	}
	
	public static class EnumDecoder<K extends Enum<K>> implements StringDecoder {
		
		protected final Class<K> enumCls;
		
		public EnumDecoder(Class<K> enumClass) {
			ThrowException.ifNull(enumClass, "enumClass == null");
			enumCls = enumClass;
		}
		
		public Object fromString(String str) {
			K[] enumConsts = enumCls.getEnumConstants();
			for (K enumObj : enumConsts) {
				String enumName = enumObj.name();
				if (enumName.toLowerCase().equals(str.toLowerCase())) {
					return enumObj;
				}
			}
			return null;
		}
	}
	
}