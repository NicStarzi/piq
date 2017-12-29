package edu.udo.piq.components.textbased;

import java.util.Arrays;
import java.util.Objects;

import edu.udo.piq.tools.AbstractPTextModel;

public class PPasswordTextModel extends AbstractPTextModel {
	
	public static final char DEFAULT_MASK_SYMBOL = '*';
	
	protected Object val;// actual unmasked value
	protected String pwStr;// cached masked value; must be set to null when the actual value changes
	protected char maskSym = DEFAULT_MASK_SYMBOL;
	
	public void setMaskSymbol(char value) {
		if (maskSym != value) {
			String oldStr = getText();
			maskSym = value;
			pwStr = null;
			fireChangeEvent(oldStr);
		}
	}
	
	public char getMaskSymbol() {
		return maskSym;
	}
	
	@Override
	public String getText() {
		if (pwStr == null) {
			String str = getPasswordString();
			int length = str.length();
			char[] mask = new char[length];
			Arrays.fill(mask, getMaskSymbol());
			pwStr = new String(mask);
		}
		return pwStr;
	}
	
	@Override
	public Object getValue() {
		return getText();
	}
	
	public Object getPasswordValue() {
		return val;
	}
	
	public String getPasswordString() {
		return Objects.toString(getPasswordValue());
	}
	
	@Override
	protected void setValueInternal(Object newValue) {
		val = newValue;
		pwStr = null;
	}
	
}