package edu.udo.piq.components.defaults;

import edu.udo.piq.components.util.StrToObj;
import edu.udo.piq.tools.AbstractPSpinnerModel;
import edu.udo.piq.util.ThrowException;

public class PSpinnerModelDouble extends AbstractPSpinnerModel {
	
	public static final double DEFAULT_VALUE = 0;
	public static final double DEFAULT_MINIMUM = -Double.MAX_VALUE;
	public static final double DEFAULT_MAXIMUM = Double.MAX_VALUE;
	public static final double DEFAULT_STEP = 1;
	
	protected Double max = Double.valueOf(DEFAULT_MAXIMUM);
	protected Double min = Double.valueOf(DEFAULT_MINIMUM);
	protected Double val = Double.valueOf(DEFAULT_VALUE);
	private StrToObj decoder;
	protected double step = DEFAULT_STEP;
	
	public PSpinnerModelDouble() {
		this(DEFAULT_VALUE);
	}
	
	public PSpinnerModelDouble(double value) {
		this(value, DEFAULT_MINIMUM, DEFAULT_MAXIMUM);
	}
	
	public PSpinnerModelDouble(double value, double min, double max) {
		this(value, min, max, DEFAULT_STEP);
	}
	
	public PSpinnerModelDouble(double value, double min, double max, double step) {
		ThrowException.ifLess(min, max, "max < min");
		ThrowException.ifLess(0, step, "step < 0");
		ThrowException.ifNotWithin(min, max, value, "value < min || value > max");
		this.max = Double.valueOf(max);
		this.min = Double.valueOf(min);
		this.val = Double.valueOf(value);
		this.step = step;
	}
	
	public void setInputDecoder(StrToObj stringDecoder) {
		decoder = stringDecoder;
	}
	
	public StrToObj getInputDecoder() {
		return decoder;
	}
	
	public void setMaximum(Double value) {
		ThrowException.ifLess(getMinimum(), value, "max < min");
		max = value;
		if (getValue() > getMaximum()) {
			setValue(getMaximum());
		}
	}
	
	public Double getMaximum() {
		return max;
	}
	
	public void setMinimum(Double value) {
		ThrowException.ifLess(value, getMaximum(), "max < min");
		min = value;
		if (getValue() < getMinimum()) {
			setValue(getMinimum());
		}
	}
	
	public Double getMinimum() {
		return min;
	}
	
	public void setStep(double value) {
		ThrowException.ifLess(0, step, "step < 0");
		step = value;
	}
	
	public double getStep() {
		return step;
	}
	
	public boolean hasNext() {
		if (getMaximum() == null) {
			return getValue().doubleValue() < Double.MAX_VALUE;
		}
		return getValue().doubleValue() < getMaximum().doubleValue();
	}
	
	public boolean hasPrevious() {
		if (getMinimum() == null) {
			return getValue().doubleValue() > Double.MIN_VALUE;
		}
		return getValue().doubleValue() > getMinimum().doubleValue();
	}
	
	public Double getNext() {
		double maxVal = getMaximum() == null ? Double.MAX_VALUE : getMaximum().doubleValue();
		double curVal = getValue().doubleValue();
		double stepVal = curVal + getStep() <= maxVal ? getStep() : maxVal - curVal;
		return Double.valueOf(curVal + stepVal);
	}
	
	public Double getPrevious() {
		double minVal = getMinimum() == null ? Double.MIN_VALUE : getMinimum().doubleValue();
		double curVal = getValue().doubleValue();
		double stepVal = curVal - getStep() >= minVal ? getStep() : curVal - minVal;
		return Double.valueOf(curVal - stepVal);
	}
	
	public boolean canSetValue(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			String strVal = (String) obj;
			try {
				if (getInputDecoder() != null) {
					obj = getInputDecoder().parse(strVal);
				} else {
					double parsedInt = Double.parseDouble(strVal);
					obj = Double.valueOf(parsedInt);
				}
			} catch (Exception e) {
				return false;
			}
		}
		if (!(obj instanceof Double)) {
			return false;
		}
		Double value = (Double) obj;
		if (getMaximum() != null 
				&& value.doubleValue() > getMaximum().doubleValue()) 
		{
			return false;
		}
		if (getMinimum() != null 
				&& value.doubleValue() < getMinimum().doubleValue()) 
		{
			return false;
		}
		return true;
	}
	
	public void setValue(Object obj) {
		ThrowException.ifFalse(canSetValue(obj), "canSetValue(value) == false");
		if (obj instanceof String) {
			String strVal = (String) obj;
			try {
				if (getInputDecoder() != null) {
					obj = getInputDecoder().parse(strVal);
				} else {
					obj = Double.valueOf(Double.parseDouble(strVal));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		Double newValue = (Double) obj;
		if (!newValue.equals(getValue())) {
			Double oldValue = getValue();
			val = newValue;
			fireValueChangedEvent(oldValue);
		}
	}
	
	public Double getValue() {
		return val;
	}
	
}