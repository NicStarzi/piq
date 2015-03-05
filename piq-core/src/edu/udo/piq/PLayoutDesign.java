package edu.udo.piq;

public interface PLayoutDesign {
	
	/**
	 * The default layout design.<br>
	 * This design will always return null for the value of every attribute.<br>
	 * If there is no better design available this design could be used for 
	 * any {@link PReadOnlyLayout layout}.<br>
	 */
	public static final PLayoutDesign NULL_ATTRIBUTE_DESIGN = new PLayoutDesign() {};
	
	/**
	 * Returns the value associated with the given attributeKey.<br>
	 * If no such value exists null is returned.<br>
	 * <br>
	 * What keys are in use depends on the {@link PReadOnlyLayout layout} that 
	 * this design is supposed to be used for.<br>
	 * A layout is supposed to define its attribute keys as public static 
	 * variables.<br>
	 * 
	 * @param attributeKey the key for the attribute
	 * @return the value for the attribute or null if the attribute is not supported
	 * @throws IllegalArgumentException if attributeKey is null
	 * @see PDesignSheet#getDesignFor(PReadOnlyLayout)
	 * @see PReadOnlyLayout#setDesign(PLayoutDesign)
	 * @see PReadOnlyLayout#getDesign()
	 */
	public default Object getAttribute(Object attributeKey) 
			throws IllegalArgumentException
	{
		return null;
	}
	
}