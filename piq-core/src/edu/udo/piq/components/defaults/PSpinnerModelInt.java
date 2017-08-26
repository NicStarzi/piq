package edu.udo.piq.components.defaults;

import java.util.function.Function;

import edu.udo.piq.tools.AbstractPSpinnerModel;
import edu.udo.piq.util.ThrowException;

public class PSpinnerModelInt extends AbstractPSpinnerModel {
	
	public static final int DEFAULT_VALUE = 0;
	public static final int DEFAULT_MINIMUM = Integer.MIN_VALUE;
	public static final int DEFAULT_MAXIMUM = Integer.MAX_VALUE;
	public static final int DEFAULT_STEP = 1;
	
	protected Integer max = Integer.valueOf(DEFAULT_MAXIMUM);
	protected Integer min = Integer.valueOf(DEFAULT_MINIMUM);
	protected Integer val = Integer.valueOf(DEFAULT_VALUE);
	protected Function<String, Object> decoder;
	protected int step = DEFAULT_STEP;
	
	public PSpinnerModelInt() {
		this(DEFAULT_VALUE);
	}
	
	public PSpinnerModelInt(int value) {
		this(value, DEFAULT_MINIMUM, DEFAULT_MAXIMUM);
	}
	
	public PSpinnerModelInt(int value, int min, int max) {
		this(value, min, max, DEFAULT_STEP);
	}
	
	public PSpinnerModelInt(int value, int min, int max, int step) {
		ThrowException.ifLess(min, max, "max < min");
		ThrowException.ifLess(0, step, "step < 0");
		ThrowException.ifNotWithin(min, max, value, "value < min || value > max");
		this.max = Integer.valueOf(max);
		this.min = Integer.valueOf(min);
		val = Integer.valueOf(value);
		this.step = step;
	}
	
	public void setInputDecoder(Function<String, Object> stringDecoder) {
		decoder = stringDecoder;
	}
	
	public Function<String, Object> getInputDecoder() {
		return decoder;
	}
	
	public void setMaximum(Integer value) {
		ThrowException.ifLess(getMinimum(), value, "max < min");
		max = value;
		if (getValue() > getMaximum()) {
			setValue(getMaximum());
		}
	}
	
	public Integer getMaximum() {
		return max;
	}
	
	public void setMinimum(Integer value) {
		ThrowException.ifLess(value, getMaximum(), "max < min");
		min = value;
		if (getValue() < getMinimum()) {
			setValue(getMinimum());
		}
	}
	
	public Integer getMinimum() {
		return min;
	}
	
	public void setStep(int value) {
		ThrowException.ifLess(0, step, "step < 0");
		step = value;
	}
	
	public int getStep() {
		return step;
	}
	
	@Override
	public boolean hasNext() {
		if (getMaximum() == null) {
			return getValue().intValue() < Integer.MAX_VALUE;
		}
		return getValue().intValue() < getMaximum().intValue();
	}
	
	@Override
	public boolean hasPrevious() {
		if (getMinimum() == null) {
			return getValue().intValue() > Integer.MIN_VALUE;
		}
		return getValue().intValue() > getMinimum().intValue();
	}
	
	@Override
	public Integer getNext() {
		int maxVal = getMaximum() == null ? Integer.MAX_VALUE : getMaximum().intValue();
		int curVal = getValue().intValue();
		int stepVal = curVal + getStep() <= maxVal ? getStep() : maxVal - curVal;
		return Integer.valueOf(curVal + stepVal);
	}
	
	@Override
	public Integer getPrevious() {
		int minVal = getMinimum() == null ? Integer.MIN_VALUE : getMinimum().intValue();
		int curVal = getValue().intValue();
		int stepVal = curVal - getStep() >= minVal ? getStep() : curVal - minVal;
		return Integer.valueOf(curVal - stepVal);
	}
	
	@Override
	public boolean canSetValue(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			String strVal = (String) obj;
			if (getInputDecoder() != null) {
				obj = getInputDecoder().apply(strVal);
			} else {
				try {
					int parsedInt = Integer.parseInt(strVal);
					obj = Integer.valueOf(parsedInt);
				} catch (NumberFormatException e) {
					return false;
				}
			}
		}
		if (!(obj instanceof Integer)) {
			return false;
		}
		Integer value = (Integer) obj;
		if (getMaximum() != null
				&& value.intValue() > getMaximum().intValue())
		{
			return false;
		}
		if (getMinimum() != null
				&& value.intValue() < getMinimum().intValue())
		{
			return false;
		}
		return true;
	}
	
	@Override
	public void setValue(Object obj) {
		ThrowException.ifFalse(canSetValue(obj), "canSetValue(value) == false");
		if (obj instanceof String) {
			String strVal = (String) obj;
			if (getInputDecoder() != null) {
				obj = getInputDecoder().apply(strVal);
			} else {
				obj = Integer.valueOf(Integer.parseInt(strVal));
			}
		}
		Integer newValue = (Integer) obj;
		if (!newValue.equals(getValue())) {
			Integer oldValue = getValue();
			val = newValue;
			fireValueChangedEvent(oldValue);
		}
	}
	
	@Override
	public Integer getValue() {
		return val;
	}
	
}