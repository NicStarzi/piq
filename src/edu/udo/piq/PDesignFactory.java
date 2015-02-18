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
	 * @param component the component for which the design is queried
	 * @return the design for the given component
	 * @throws NullPointerException if component is null
	 * @throws IllegalArgumentException if this factory does not support the given component
	 * @see PDesign
	 * @see PDesignSheet
	 * @see PComponent
	 */
	public default PDesign getDesignFor(PComponent component) 
			throws NullPointerException, IllegalArgumentException 
	{
		return PDesign.PASS_THROUGH_DESIGN;
	}
	
}