package edu.udo.piq.style;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import edu.udo.piq.PComponent;
import edu.udo.piq.tools.AbstractPStyleSheet;

public class DefaultPStyleSheet extends AbstractPStyleSheet implements PStyleSheet {
	
	protected final Map<Object, Supplier<PStyleComponent>> styleMap = new HashMap<>();
//	protected final ClassMap<Supplier<PStyleComponent>> clsToStyleMap = new ClassMap<>();
	
	@Override
	public PStyleComponent getStyleFor(PComponent component) {
		return createStyleBasedOnID(component.getStyleID());
	}
	
	protected PStyleComponent createStyleBasedOnID(Object styleID) {
		Supplier<PStyleComponent> fact = styleMap.get(styleID);
		if (fact == null) {
			return PStyleComponent.DEFAULT_COMPONENT_STYLE;
		}
		return fact.get();
	}
	
//	protected PStyleComponent createStyleBasedOnClass(Class<?> compClass) {
//		Supplier<PStyleComponent> fact = clsToStyleMap.search(compClass);
//		if (fact == null) {
//			return PStyleComponent.DEFAULT_COMPONENT_STYLE;
//		}
//		return fact.get();
//	}
	
	public void setStyleFor(Object styleID, Supplier<PStyleComponent> factory) {
		styleMap.put(styleID, factory);
//		clsToStyleMap.put(componentClass, factory);
	}
	
	public Supplier<PStyleComponent> getStyleFor(Object styleID) {//Class<?> componentClass
//		return clsToStyleMap.get(componentClass);
		return styleMap.get(styleID);
	}
	
//	public void setCheckSuperClassesEnabled(boolean value) {
//		clsToStyleMap.setCheckSuperClassesEnabled(value);
//	}
//
//	public boolean isCheckSuperClassesEnabled() {
//		return clsToStyleMap.isCheckSuperClassesEnabled();
//	}
//
//	public void setCheckInterfacesEnabled(boolean value) {
//		clsToStyleMap.setCheckInterfacesEnabled(value);
//	}
//
//	public boolean isCheckInterfacesEnabled() {
//		return clsToStyleMap.isCheckInterfacesEnabled();
//	}
	
}