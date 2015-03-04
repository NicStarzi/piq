package edu.udo.piq;

/**
 * A {@link PDesignSheet} defines a rule set for how each {@link PComponent} within a GUI 
 * is supposed to be rendered.<br>
 * It does so by returning instances of {@link PDesign}s for any component that 
 * is being passed to its {@link #getDesignFor(PComponent)} method.<br>
 * <br>
 * A design sheet should work for any number and any kind of components. It should not 
 * be assumed that a design sheet is only used by one GUI throughout its life cycle.<br>
 * The usage of the {@link PDesign#PASS_THROUGH_DESIGN} is encouraged for user defined 
 * components.<br>
 * 
 * @author Nic Starzi
 * 
 * @see PDesign
 * @see PDesignFactory
 * @see PDesign#PASS_THROUGH_DESIGN
 * @see PRoot
 * @see PRenderer
 */
//* A design sheet does also allow for customization by registering 
//* {@link PDesignFactory}s for certain classes of components. When a design factory is 
//* registered for a certain class of components any call to the 
//* {@link #getDesignFor(PComponent)} method will be delegated to the design factory for 
//* components of said class.<br>
public interface PDesignSheet {
//	 * If there is a {@link PDesignFactory} registered and the design factory 
//	 * covers the class of component then the returned design is retrieved from 
//	 * the factories {@link PDesignFactory#getDesignFor(PComponent)} method.<br>
//	 * @see #registerDesignFactory(Class, PDesignFactory)
//	 * @see #unregisterDesignFactory(Class)
//	 * @see PDesignFactory
	
	/**
	 * Returns the {@link PDesign} associated with the given {@link PComponent}.<br>
	 * If this {@link PDesignSheet} does not have an appropriate {@link PDesign} 
	 * for the component the {@link PDesign#PASS_THROUGH_DESIGN} is returned.<br>
	 * This method never returns null.<br>
	 * 
	 * @param component the component for which the design should be returned
	 * @return the design for the given component or the {@link PDesign#PASS_THROUGH_DESIGN}
	 * @throws NullPointerException if component is null
	 * @see PDesign
	 * @see PComponent#setDesign(PDesign)
	 * @see PComponent#getDesign()
	 */
	public default PDesign getDesignFor(PComponent component) 
			throws NullPointerException 
	{
		return PDesign.PASS_THROUGH_DESIGN;
	}
	
//	/**
//	 * Registers the given {@link PDesignFactory} with the given {@link PComponent} 
//	 * class.<br>
//	 * All future calls to {@link #getDesignFor(PComponent)} with a {@link PComponent} 
//	 * that is of class compClass will return a {@link PDesign} produced by the given 
//	 * factory.<br>
//	 * A call to this method will override the previous factory used for compClass.<br>
//	 * <br>
//	 * A factory should only be used for components that are exactly of the registered 
//	 * class and not of any subclass. This is important to allow users to more easily 
//	 * define their own custom components that are based on standard components.<br>
//	 * 
//	 * @param compClass the class of components for which the factory will be used
//	 * @param factory the factory to be used for compClass
//	 * @throws NullPointerException if compClass or factory is null
//	 * @see PDesign
//	 * @see PDesignFactory
//	 * @see PComponent
//	 * @see #unregisterDesignFactory(Class)
//	 */
//	public void registerDesignFactory(
//			Class<? extends PComponent> compClass, PDesignFactory factory) 
//			throws NullPointerException;
//	
//	/**
//	 * Unregisters the {@link PDesignFactory} which was previously registered for the 
//	 * component class compClass.<br>
//	 * If no factory was registered for compClass this method does nothing.<br>
//	 * After a call to this method any future calls to {@link #getDesignFor(PComponent)} 
//	 * with components of type compClass will now return the {@link PDesign} that this 
//	 * design sheet would normally return.<br>
//	 * 
//	 * @param compClass the class of components for which the factories should be unregistered
//	 * @throws NullPointerException if compClass is null
//	 * @see PDesign
//	 * @see PDesignFactory
//	 * @see PComponent
//	 * @see #registerDesignFactory(Class, PDesignFactory)
//	 */
//	public void unregisterDesignFactory(
//			Class<? extends PComponent> compClass) 
//			throws NullPointerException;
	
}