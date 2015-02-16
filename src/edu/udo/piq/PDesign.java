package edu.udo.piq;

import edu.udo.piq.util.PCompUtil;

/**
 * A {@link PDesign} defines the {@link PSize} of a {@link PComponent} and how 
 * it is supposed to be rendered.<br>
 * A PDesign is usually part of a {@link PDesignSheet} or is constructed by a 
 * {@link PDesignFactory} at request. However it is also possible for a design 
 * to be installed at a component directly if this particular component is 
 * supposed to have a unique design.<br>
 * 
 * @author Nic Starzi
 * 
 * @see PDesignSheet
 * @see PDesignSheet#getDesignFor(PComponent)
 * @see PDesignFactory
 * @see PDesignFactory#getDesignFor(PComponent)
 * @see PComponent#setDesign(PDesign)
 * @see PComponent#getDesign()
 */
public interface PDesign {
	
	/**
	 * The default, pass-through design.<br>
	 * This design works for every {@link PComponent} by delegating its methods 
	 * to the passed component.<br>
	 * If there is no better design available this design could be used for 
	 * a component.<br>
	 */
	public static final PDesign PASS_THROUGH_DESIGN = new PDesign() {};
	
	/**
	 * Returns the preferred size for the given {@link PComponent} as is needed 
	 * by this {@link PDesign} to display the component correctly.<br>
	 * The preferred size should usually be as small as possible but as big as 
	 * needed.<br>
	 * This method might take the {@link PReadOnlyLayout} of the component into
	 * consideration but is not forced to.<br>
	 * This method will never return null.<br> 
	 * 
	 * @param component the component for which the size is queried
	 * @return the preferred size for the component
	 * @throws NullPointerException if component is null
	 * @throws IllegalArgumentException if this design is not intended to be used with the given component
	 * @see PComponent
	 * @see PComponent#getDefaultPreferredSize()
	 * @see PReadOnlyLayout
	 * @see PReadOnlyLayout#getPreferredSize()
	 */
	public default PSize getPreferredSize(PComponent component) 
			throws NullPointerException, IllegalArgumentException 
	{
		return component.getDefaultPreferredSize();
	}
	
	/**
	 * Renders the given {@link PComponent} at the given {@link PRenderer}.<br>
	 * This method should honor the {@link PBounds} of the component as it is 
	 * returned by the components parents layout.<br>
	 * 
	 * @param renderer
	 * @param component
	 * @throws NullPointerException if component is null
	 * @throws IllegalArgumentException if this design is not intended to be used with the given component
	 * @see PComponent
	 * @see PReadOnlyLayout
	 * @see PReadOnlyLayout#getChildBounds(PComponent)
	 * @see PCompUtil#getBoundsOf(PComponent)
	 */
	public default void render(PRenderer renderer, PComponent component) 
			throws NullPointerException, IllegalArgumentException 
	{
		component.defaultRender(renderer);
	}
	
	/**
	 * Returns true if the given component fills all pixels within its 
	 * {@link PBounds} when the {@link #render(PRenderer, PComponent)} 
	 * method is invoked.<br>
	 * For a component which is translucent or has transparent parts 
	 * this method should always return false.<br>
	 * 
	 * @return true if the given component is completely opaque when rendered with this design
	 * @throws NullPointerException if component is null
	 * @throws IllegalArgumentException if this design is not intended to be used with the given component
	 * @see #render(PRenderer, PComponent)
	 * @see PRenderer
	 * @see PComponent#defaultFillsAllPixels()
	 * @see PCompUtil#fillsAllPixels(PComponent)
	 */
	public default boolean fillsAllPixels(PComponent component) 
			throws NullPointerException 
	{
		return component.defaultFillsAllPixels();
	}
	
}