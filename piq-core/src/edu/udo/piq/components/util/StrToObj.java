package edu.udo.piq.components.util;

import edu.udo.piq.util.ThrowException;

public interface StrToObj {
	
	public Object parse(String str);
	
	public static class EnumDecoder<E extends Enum<E>> implements StrToObj {
		
		protected final E[] values;
		
		public EnumDecoder(Class<E> enumClass) {
			ThrowException.ifNull(enumClass, "enumClass == null");
			ThrowException.ifFalse(enumClass.isEnum(), "enumClass.isEnum() == false");
			values = enumClass.getEnumConstants();
		}
		
		public Object parse(String str) {
			str = str.toLowerCase();
			for (E enumObj : values) {
				String enumName = enumObj.name();
				if (enumName.toLowerCase().equals(str)) {
					return enumObj;
				}
			}
			return null;
		}
	}
	
}