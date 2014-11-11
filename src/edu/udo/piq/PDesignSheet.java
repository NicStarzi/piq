package edu.udo.piq;

/**
 * A {@link PDesignSheet} defines a rule set for how each {@link PComponent} within a GUI 
 * is supposed to be rendered.<br>
 * It does so by returning instances of {@link PDesign}s for any component that 
 * is being passed to its {@link #getDesignFor(PComponent)} method.<br>
 * A design sheet does also allow for customizations by registering 
 * {@link PDesignFactory}s for certain classes of components. When a design factory is 
 * registered for a certain class of components any call to the 
 * {@link #getDesignFor(PComponent)} method will be delegated to the design factory for 
 * components of said class.<br>
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
public interface PDesignSheet {
	
	/**
	 * Returns the {@link PDesign} associated with the given {@link PComponent}.<br>
	 * If there is no such design the {@link DefaultPDesign} will be returned.<br>
	 * This method never returns null.<br>
	 * 
	 * @param component the component for which the design is queried
	 * @return the design for the given component
	 * @throws NullPointerException if component is null
	 * @see PDesign
	 * @see PDesignFactory
	 * @see PComponent
	 * @see #registerDesignFactory(Class, PDesignFactory)
	 * @see #unregisterDesignFactory(Class)
	 */
	public PDesign getDesignFor(PComponent component) throws NullPointerException;
	
	/**
	 * Registers the given {@link PDesignFactory} with the given {@link PComponent} 
	 * class.<br>
	 * All future calls to {@link #getDesignFor(PComponent)} with a {@link PComponent} 
	 * that is a subclass of compClass or compClass will return a {@link PDesign} 
	 * produced by the given factory.<br>
	 * A call to this method will override the previous factory used for compClass.<br>
	 * 
	 * @param compClass the class of components for which the factory will be used
	 * @param factory the factory to be used for compClass
	 * @throws NullPointerException if compClass or factory is null
	 * @see PDesign
	 * @see PDesignFactory
	 * @see PComponent
	 * @see #unregisterDesignFactory(Class)
	 */
	public void registerDesignFactory(Class<? extends PComponent> compClass, PDesignFactory factory) throws NullPointerException;
	
	
	/**
	 * Unregisters the {@link PDesignFactory} which was previously registered for the 
	 * component class compClass.<br>
	 * If no factory was registered for compClass this method does nothing.<br>
	 * After a call to this method any future calls to {@link #getDesignFor(PComponent)} 
	 * with components of type compClass will now return the {@link PDesign} that this 
	 * design sheet would normally return.<br>
	 * 
	 * @param compClass the class of components for which the factories should be unregistered
	 * @throws NullPointerException if compClass is null
	 * @see PDesign
	 * @see PDesignFactory
	 * @see PComponent
	 * @see #registerDesignFactory(Class, PDesignFactory)
	 */
	public void unregisterDesignFactory(Class<? extends PComponent> compClass) throws NullPointerException;
	
}