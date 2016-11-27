package edu.udo.piq;

public interface PStyleLayout {
	
	public static final PStyleLayout DEFAULT_LAYOUT_STYLE = new PStyleLayout() {};
	
	public default <E> E getAttribute(PReadOnlyLayout layout,
			Object attrKey, E defaultValue)
	{
		return defaultValue;
	}
	
}