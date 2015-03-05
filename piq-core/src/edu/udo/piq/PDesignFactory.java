package edu.udo.piq;

/**
 * A {@link PDesignFactory} can be registered at a {@link PDesignSheet} to 
 * provide {@link PDesign}s for {@link PComponent}s as needed.<br>
 * This feature can be used to customize the design of a GUI on the fly.<br>
 * 
 * @author Nic Starzi
 * 
 * @see PDesign
 * @see PDesignSheet
 * @see PDesignSheet#registerDesignFactory(Class, PDesignFactory)
 * @see PDesignSheet#unregisterDesignFactory(Class)
 */
public interface PDesignFactory {
	
	/**
	 * Returns a {@link PDesign} associated with the given {@link PComponent}.<br>
	 * 
	 * @param component the component for which the design is returned
	 * @return the design for the given component
	 * @throws NullPointerException if component is null
	 * @throws IllegalArgumentException if this factory does not support the given component
	 * @see PDesign
	 * @see PDesignSheet#getDesignFor(PComponent)
	 * @see PComponent#getDesign()
	 */
	public default PDesign getDesignFor(PComponent component) 
			throws NullPointerException, IllegalArgumentException 
	{
		return PDesign.PASS_THROUGH_DESIGN;
	}
	
	/**
	 * Returns a {@link PLayoutDesign} associated with the given {@link PReadOnlyLayout layout}.<br>
	 * 
	 * @param layout the layout for which the design is returned
	 * @return the design for the given layout
	 * @throws NullPointerException if layout is null
	 * @throws IllegalArgumentException if this factory does not support the given layout
	 * @see PLayoutDesign
	 * @see PDesignSheet#getDesignFor(PReadOnlyLayout)
	 * @see PReadOnlyLayout#getDesign()
	 */
	public default PLayoutDesign getDesignFor(PReadOnlyLayout layout) 
			throws NullPointerException, IllegalArgumentException 
	{
		return PLayoutDesign.NULL_ATTRIBUTE_DESIGN;
	}
	
}