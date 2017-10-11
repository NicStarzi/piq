package edu.udo.piq.components.util;

import edu.udo.piq.util.ThrowException;

public class SymbolicImageKey {
	
	private final String str;
	
	public SymbolicImageKey(Class<?> cls) {
		this(cls.getName());
	}
	
	public SymbolicImageKey(String value) {
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
		if (obj instanceof SymbolicImageKey) {
			SymbolicImageKey other = (SymbolicImageKey) obj;
			return str.equals(other.str);
		}
		return false;
	}
	
	public String toString() {
		return str;
	}
	
}