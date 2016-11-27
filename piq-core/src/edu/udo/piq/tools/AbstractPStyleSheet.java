package edu.udo.piq.tools;

import java.util.Objects;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootObs;
import edu.udo.piq.PStyleComponent;
import edu.udo.piq.PStyleSheet;

public class AbstractPStyleSheet implements PStyleSheet {
	
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
	
	@Override
	public void setRoot(PRoot root) {
		if (Objects.equals(getRoot(), root)) {
			return;
		}
		if (getRoot() != null) {
			getRoot().removeObs(rootObs);
		}
		this.root = root;
		if (getRoot() != null) {
			getRoot().addObs(rootObs);
		}
	}
	
	@Override
	public PRoot getRoot() {
		return root;
	}
	
	@Override
	public PStyleComponent getStyleFor(PComponent component) {
		return PStyleComponent.DEFAULT_COMPONENT_STYLE;
	}
	
	protected void onComponentAdded(PComponent addedComponent) {}
	
	protected void onComponentRemoved(PComponent parent, PComponent removedComponent) {}
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