package edu.udo.piq.components.defaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import edu.udo.piq.tools.AbstractPSpinnerModel;
import edu.udo.piq.util.ThrowException;

public class PSpinnerModelList extends AbstractPSpinnerModel {
	
	protected List<Object> valList;
	protected Function<String, Object> decoder;
	protected int index;
	
	public PSpinnerModelList(Object ... values) {
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
	
	@SuppressWarnings("unlikely-arg-type")
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
	
	public void setInputDecoder(Function<String, Object> stringDecoder) {
		decoder = stringDecoder;
	}
	
	public Function<String, Object> getInputDecoder() {
		return decoder;
	}
	
	public List<Object> getValueList() {
		return valList;
	}
	
	public int getValueIndex() {
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
	public Object getNext() {
		if (getValueIndex() == getValueList().size() - 1) {
			return getValueList().get(getValueIndex());
		}
		return getValueList().get(getValueIndex() + 1);
	}
	
	@Override
	public Object getPrevious() {
		if (getValueIndex() == 0) {
			return getValueList().get(getValueIndex());
		}
		return getValueList().get(getValueIndex() - 1);
	}
	
	@Override
	public boolean canSetValue(Object obj) {
		if (getValueList().contains(obj)) {
			return true;
		}
		if (getInputDecoder() != null && obj instanceof String) {
			return getValueList().contains(
					getInputDecoder().apply((String) obj));
		}
		return false;
	}
	
	@Override
	public void setValue(Object obj) {
		ThrowException.ifFalse(canSetValue(obj),
				"canSetValue(value) == false");
		if (!getValueList().contains(obj)) {
			obj = getInputDecoder().apply((String) obj);
		}
		if (!obj.equals(getValue())) {
			Object oldValue = getValue();
			index = getValueList().indexOf(obj);
			fireValueChangedEvent(oldValue);
		}
	}
	
	@Override
	public Object getValue() {
		return getValueList().get(getValueIndex());
	}
	
}