package edu.udo.piq.components.defaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.components.util.StrToObj;
import edu.udo.piq.tools.AbstractPSpinnerModel;
import edu.udo.piq.util.ThrowException;

public class PSpinnerModelList extends AbstractPSpinnerModel {
	
	protected List<Object> valList;
	protected StrToObj decoder;
	protected int index;
	
	public PSpinnerModelList(Object[] values) {
		this(Arrays.asList(values), 0);
	}
	
	public PSpinnerModelList(Object[] values, Object selectedValue) {
		this(Arrays.asList(values), selectedValue);
	}
	
	public PSpinnerModelList(Object[] values, int selectedIndex) {
		this(Arrays.asList(values), selectedIndex);
	}
	
	public PSpinnerModelList(List<?> values) {
		this(values, 0);
	}
	
	public PSpinnerModelList(List<?> values, Object selectedValue) {
		this(values, values.indexOf(selectedValue));
	}
	
	public PSpinnerModelList(List<?> values, int selectedIndex) {
		ThrowException.ifNull(values, "values == null");
		ThrowException.ifNotWithin(0, values.size() - 1, selectedIndex, 
				"selectedIndex < 0 || selectedIndex >= values.size()");
		valList = Collections.unmodifiableList(values);
		index = selectedIndex;
	}
	
	public void setInputDecoder(StrToObj stringDecoder) {
		decoder = stringDecoder;
	}
	
	public StrToObj getInputDecoder() {
		return decoder;
	}
	
	public List<Object> getValueList() {
		return valList;
	}
	
	public int getValueIndex() {
		return index;
	}
	
	public boolean hasNext() {
		return getValueIndex() < getValueList().size() - 1;
	}
	
	public boolean hasPrevious() {
		return getValueIndex() > 0;
	}
	
	public Object getNext() {
		if (getValueIndex() == getValueList().size() - 1) {
			return getValueList().get(getValueIndex());
		}
		return getValueList().get(getValueIndex() + 1);
	}
	
	public Object getPrevious() {
		if (getValueIndex() == 0) {
			return getValueList().get(getValueIndex());
		}
		return getValueList().get(getValueIndex() - 1);
	}
	
	public boolean canSetValue(Object obj) {
		if (getValueList().contains(obj)) {
			return true;
		}
		if (getInputDecoder() != null && obj instanceof String) {
			return getValueList().contains(
					getInputDecoder().parse((String) obj));
		}
		return false;
	}
	
	public void setValue(Object obj) {
		ThrowException.ifFalse(canSetValue(obj), 
				"canSetValue(value) == false");
		if (!getValueList().contains(obj)) {
			obj = getInputDecoder().parse((String) obj);
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
	
}