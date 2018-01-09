package edu.udo.piq.style;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import edu.udo.piq.PComponent;
import edu.udo.piq.tools.AbstractPStyleSheet;

public class DefaultPStyleSheet extends AbstractPStyleSheet implements PStyleSheet {
	
	protected final Map<Class<?>, Supplier<PStyleComponent>> classToStyleFactoryMap = new HashMap<>();
	protected boolean checkSuperClasses = true;
	protected boolean checkInterfaces = true;
	
	@Override
	public PStyleComponent getStyleFor(PComponent component) {
		return createStyleBasedOnClass(component.getClass());
	}
	
	protected PStyleComponent createStyleBasedOnClass(Class<?> objClass) {
		Supplier<PStyleComponent> fact = classToStyleFactoryMap.get(objClass);
		if (fact != null) {
			return fact.get();
		}
		if (isCheckInterfacesEnabled()) {
			for (Class<?> ifaceClass : objClass.getInterfaces()) {
				fact = classToStyleFactoryMap.get(ifaceClass);
				if (fact != null) {
					classToStyleFactoryMap.put(objClass, fact);
					return fact.get();
				}
			}
		}
		if (isCheckSuperClassesEnabled()) {
			Class<?> current = objClass.getSuperclass();
			while (current != null) {
				fact = classToStyleFactoryMap.get(current);
				if (fact != null) {
					classToStyleFactoryMap.put(objClass, fact);
					return fact.get();
				}
				if (isCheckInterfacesEnabled()) {
					for (Class<?> ifaceClass : current.getInterfaces()) {
						fact = classToStyleFactoryMap.get(ifaceClass);
						if (fact != null) {
							classToStyleFactoryMap.put(objClass, fact);
							return fact.get();
						}
					}
				}
				current = current.getSuperclass();
			}
		}
		return PStyleComponent.DEFAULT_COMPONENT_STYLE;
	}
	
	public void setDefaultModelFactoryFor(Class<?> componentClass, Supplier<PStyleComponent> modelFactory) {
		classToStyleFactoryMap.put(componentClass, modelFactory);
	}
	
	public Supplier<?> getDefaultModelFactoryFor(Class<?> componentClass) {
		return classToStyleFactoryMap.get(componentClass);
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