package edu.udo.piq.style;

import java.util.function.Supplier;

import edu.udo.piq.PComponent;
import edu.udo.piq.tools.AbstractPStyleSheet;
import edu.udo.piq.util.ClassMap;

public class DefaultPStyleSheet extends AbstractPStyleSheet implements PStyleSheet {
	
	protected final ClassMap<Supplier<PStyleComponent>> clsToStyleMap = new ClassMap<>();
	
	@Override
	public PStyleComponent getStyleFor(PComponent component) {
		return createStyleBasedOnClass(component.getClass());
	}
	
	protected PStyleComponent createStyleBasedOnClass(Class<?> compClass) {
		Supplier<PStyleComponent> fact = clsToStyleMap.search(compClass);
		if (fact == null) {
			return PStyleComponent.DEFAULT_COMPONENT_STYLE;
		}
		return fact.get();
	}
	
	public void setStyleFor(Class<?> componentClass, Supplier<PStyleComponent> factory) {
		clsToStyleMap.put(componentClass, factory);
	}
	
	public Supplier<?> getStyleFor(Class<?> componentClass) {
		return clsToStyleMap.get(componentClass);
	}
	
	public void setCheckSuperClassesEnabled(boolean value) {
		clsToStyleMap.setCheckSuperClassesEnabled(value);
	}
	
	public boolean isCheckSuperClassesEnabled() {
		return clsToStyleMap.isCheckSuperClassesEnabled();
	}
	
	public void setCheckInterfacesEnabled(boolean value) {
		clsToStyleMap.setCheckInterfacesEnabled(value);
	}
	
	public boolean isCheckInterfacesEnabled() {
		return clsToStyleMap.isCheckInterfacesEnabled();
	}
	
}