package edu.udo.piq;

import edu.udo.piq.util.PCompUtil;

/**
 * This interface defines abstract platform independent font resources.<br>
 * A font resource offers methods to query its font-name, style and point 
 * size.<br>
 * It also calculates the size for a text that is being rendered with this 
 * font. Since this depends on the {@link PRenderer} being used as well as 
 * internal settings this is indented to be implemented by platform dependent  
 * classes.<br>
 * Instances of font resources can be obtained from a {@link PRoot} 
 * implementation.<br>
 * 
 * @author Nic Starzi
 * 
 * @see PRenderer
 */
public interface PFontResource {
	
	/**
	 * Returns the name of the font, for example Arial or Times New Roman.<br>
	 * This method never returns null.<br>
	 * 
	 * @return the name of the font
	 */
	public String getName();
	
	/**
	 * Returns the point size of the font.<br>
	 * The point size is always positive.<br>
	 * 
	 * @return point size of the font
	 */
	public double getPointSize();
	
	/**
	 * Returns the {@link PSize} that the given text would have when rendered 
	 * with this font.<br>
	 * This method is useful for {@link PDesign}s and all text based components 
	 * that need to calculate their preferred sizes.<br>
	 * This method should never return null.<br>
	 * <br>
	 * For empty texts (or texts with no size) the usage of {@link PSize#ZERO_SIZE} 
	 * is encouraged.<br>
	 * 
	 * @param text the string for which the size is to be determined
	 * @return the minimum size for the given string when rendered with this font
	 * @throws NullPointerException if string is null
	 * @see PSize
	 * @see PSize#ZERO_SIZE
	 * @see PComponent#getDefaultPreferredSize()
	 * @see PDesign#getPreferredSize(PComponent)
	 * @see PReadOnlyLayout#getPreferredSize()
	 * @see PCompUtil#getPreferredSizeOf(PComponent)
	 */
	public PSize getSize(String text);
	
	/**
	 * Returns the {@link Style} of the font.<br>
	 * This is either PLAIN, BOLD, ITALIC or BOLD_ITALIC for fonts that are both 
	 * bold and italic.<br>
	 * This method never returns null.<br>
	 * 
	 * @return the style of the font
	 */
	public Style getStyle();
	
	/**
	 * The style of a font resource.<br>
	 * This is either PLAIN, BOLD, ITALIC or BOLD_ITALIC for fonts that are both 
	 * bold and italic.<br>
	 */
	public static enum Style {
		PLAIN,
		BOLD,
		ITALIC,
		BOLD_ITALIC;
	}
	
}