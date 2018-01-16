package edu.udo.piq.tools;

import java.util.Iterator;

import edu.udo.piq.CallSuper;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootObs;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.style.PStyleSheet;
import edu.udo.piq.util.Throw;

public abstract class AbstractPStyleSheet implements PStyleSheet {
	
	protected final PRootObs rootObs = new PRootObs() {
		@Override
		public void onComponentRemovedFromGui(PComponent parent,
				PComponent removedComponent)
		{
			AbstractPStyleSheet.this.onComponentRemoved(parent, removedComponent);
		}
		@Override
		public void onComponentAddedToGui(PComponent addedComponent) {
			AbstractPStyleSheet.this.onComponentAdded(addedComponent);
		}
	};
	protected PRoot root;
	
	@TemplateMethod
	@CallSuper
	@Override
	public void onAddedToRoot(PRoot root) {
		Throw.ifNotNull(getRoot(), () -> "getRoot() != null");
		this.root = root;
		addAllComponents();
		getRoot().addObs(rootObs);
	}
	
	@TemplateMethod
	@CallSuper
	@Override
	public void onRemovedFromRoot(PRoot root) {
		Throw.ifNull(getRoot(), "getRoot() == null");
		getRoot().removeObs(rootObs);
		removeAllComponents();
		this.root = null;
	}
	
	public PRoot getRoot() {
		return root;
	}
	
	protected void addAllComponents() {
		Iterator<PComponent> iter = getRoot().getDescendants().iterator();
		iter.next();//remove root
		while (iter.hasNext()) {
			PComponent component = iter.next();
			component.setInheritedStyle(getStyleFor(component));
		}
	}
	
	protected void removeAllComponents() {
		Iterator<PComponent> iter = getRoot().getDescendants().iterator();
		iter.next();//remove root
		while (iter.hasNext()) {
			PComponent component = iter.next();
			component.setInheritedStyle(null);
		}
	}
	
	@TemplateMethod
	@CallSuper
	protected void onComponentAdded(PComponent addedComponent) {
		addedComponent.setInheritedStyle(getStyleFor(addedComponent));
	}
	
	@TemplateMethod
	@CallSuper
	protected void onComponentRemoved(PComponent parent, PComponent removedComponent) {
		removedComponent.setInheritedStyle(null);
	}
//
//	/**
//	 * Contains all registered {@link PDesignFactory}s.<br>
//	 */
//	protected final Map<Class<? extends PComponent>, PDesignFactory> factoryMap = new HashMap<>();
//
//	/**
//	 * If a {@link PDesignFactory} is registered at this {@link PDesignSheet} and
//	 * the factory covers the class of the component then this call will be delegated
//	 * to the factory.<br>
//	 * If no such factory was registered an instance of {@link PDesign}
//	 * will be returned.<br>
//	 *
//	 * @see #registerDesignFactory(Class, PDesignFactory)
//	 * @see #unregisterDesignFactory(Class)
//	 * @see PDesign
//	 * @see PDesignFactory
//	 * @see PDesign#PASS_THROUGH_DESIGN
//	 */
//	public PDesign getDesignFor(PComponent component) throws NullPointerException {
//		PDesignFactory factory = factoryMap.get(component.getClass());
//		if (factory != null) {
//			return factory.getDesignFor(component);
//		}
//		return getDesignInternally(component);
//	}
//
//	/**
//	 * This method is used by the {@link #getDesignFor(PComponent)}
//	 * implementation to get the {@link PDesign} that should be used
//	 * to render the given {@link PComponent}.<br>
//	 * This method will only be called if there is no registered
//	 * {@link PDesignFactory} that will service the component.<br>
//	 * The default implementation of this method always returns
//	 * the {@link PDesign#PASS_THROUGH_DESIGN}.<br>
//	 * The argument will always be a valid, non-null PComponent.<br>
//	 *
//	 * @param component a component that needs a design.
//	 * @return the correct PDesign for the given PComponent
//	 */
//	protected PDesign getDesignInternally(PComponent component) {
//		return PDesign.PASS_THROUGH_DESIGN;
//	}
//
//	/**
//	 * Registers the factory for all {@link PComponent}s of class compClass as is
//	 * defined by the {@link PDesignSheet} interfaces
//	 * {@link PDesignSheet#registerDesignFactory(Class, PDesignFactory)} method.<br>
//	 */
//	public void registerDesignFactory(Class<? extends PComponent> compClass, PDesignFactory factory) throws NullPointerException {
//		if (compClass == null || factory == null) {
//			throw new NullPointerException("compClass="+compClass+", factory="+factory);
//		}
//		factoryMap.put(compClass, factory);
//	}
//
//	/**
//	 * Unregisters any registered factory for all {@link PComponent}s of class compClass
//	 * as is defined by the {@link PDesignSheet} interfaces
//	 * {@link PDesignSheet#unregisterDesignFactory(Class)} method.<br>
//	 */
//	public void unregisterDesignFactory(Class<? extends PComponent> compClass) throws NullPointerException, IllegalArgumentException {
//		if (compClass == null) {
//			throw new NullPointerException("compClass="+compClass);
//		}
//		factoryMap.remove(compClass);
//	}
}