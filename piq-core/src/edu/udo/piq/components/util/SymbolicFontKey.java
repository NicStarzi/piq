package edu.udo.piq.components.util;

import edu.udo.piq.util.ThrowException;

public class SymbolicFontKey {
	
	private final String str;
	
	public SymbolicFontKey(Class<?> cls) {
		this(cls.getName());
	}
	
	public SymbolicFontKey(String value) {
		ThrowException.ifNull(value, "value == null");
		str = value;
	}
	
	public int hashCode() {
		return str.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof SymbolicFontKey) {
			SymbolicFontKey other = (SymbolicFontKey) obj;
			return str.equals(other.str);
		}
		return false;
	}
	
	public String toString() {
		return str;
	}
	
}