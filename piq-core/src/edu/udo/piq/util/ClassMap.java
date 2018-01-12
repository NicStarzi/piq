package edu.udo.piq.util;

import java.util.HashMap;
import java.util.Map;

public class ClassMap<E> {
	
	protected final Map<Class<?>, E> map = new HashMap<>();
	protected boolean checkSuperClasses = true;
	protected boolean checkInterfaces = true;
	
	public E search(Class<?> clazz) {
		E fact = map.get(clazz);
		if (fact != null) {
			return fact;
		}
		if (isCheckInterfacesEnabled()) {
			for (Class<?> ifaceClass : clazz.getInterfaces()) {
				fact = map.get(ifaceClass);
				if (fact != null) {
					map.put(clazz, fact);
					return fact;
				}
			}
		}
		if (isCheckSuperClassesEnabled()) {
			Class<?> current = clazz.getSuperclass();
			while (current != null) {
				fact = map.get(current);
				if (fact != null) {
					map.put(clazz, fact);
					return fact;
				}
				if (isCheckInterfacesEnabled()) {
					for (Class<?> ifaceClass : current.getInterfaces()) {
						fact = map.get(ifaceClass);
						if (fact != null) {
							map.put(clazz, fact);
							return fact;
						}
					}
				}
				current = current.getSuperclass();
			}
		}
		return null;
	}
	
	public void put(Class<?> clazz, E value) {
		map.put(clazz, value);
	}
	
	public E get(Class<?> clazz) {
		return map.get(clazz);
	}
	
	public void setCheckSuperClassesEnabled(boolean value) {
		checkSuperClasses = value;
	}
	
	public boolean isCheckSuperClassesEnabled() {
		return checkSuperClasses;
	}
	
	public void setCheckInterfacesEnabled(boolean value) {
		checkInterfaces = value;
	}
	
	public boolean isCheckInterfacesEnabled() {
		return checkInterfaces;
	}
	
}