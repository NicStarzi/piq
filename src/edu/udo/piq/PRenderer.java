package edu.udo.piq;

/**
 * This interface defines a platform independent rendering API.<br>
 * All {@link PComponent}s in the GUI will be rendered using the 
 * methods provided by this interface. A platform dependent 
 * implementation of the renderer is supposed to implement all 
 * methods of this interface on a best effort basis.<br>
 * By its nature the exact outcome of many of these methods is 
 * defined very broadly. Small difference between platform 
 * dependent implementations are to be expected but should be 
 * kept at a minimum at all cost to provide for a similar 
 * user experience on different platforms.<br>
 * A {@link PRenderer} is not supposed to work with platform 
 * specific implementations of resources (such as image resources, 
 * or font resources) for other platforms. It will usually only 
 * work with its own implementations of these and no others.<br>
 * <br>
 * {@link PComponent}s and {@link PDesign}s should not make any 
 * assumptions about the internal mechanics of a renderer and not 
 * try to do any premature optimization. Its the job of the 
 * renderers implementation to improve the performance of the 
 * standard components as best as possible.<br>
 * 
 * @author Nic Starzi
 * 
 * @see PComponent#defaultRender(PRenderer)
 * @see PDesign#render(PRenderer, PComponent)
 * @see PColor
 * @see PBounds
 * @see PFontResource
 * @see PImageResource
 */
public interface PRenderer {
	
	/**
	 * Sets the clipping bounds that will be used for any subsequent 
	 * rendering operations.<br>
	 * Any rendering being done outside of the clip bounds will be 
	 * ignored and only pixels within the clip bounds will be rendered 
	 * to screen.<br>
	 * 
	 * @param bounds the new clip bounds used by subsequent rendering operations
	 * @throws NullPointerException if bounds are null
	 * @see PBounds
	 * @see #setClipBounds(int, int, int, int)
	 * @see #getClipBounds()
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawLetter(PFontResource, char, float, float)
	 * @see #drawString(PFontResource, String, float, float)
	 */
	public void setClipBounds(PBounds bounds) throws NullPointerException;
	
	/**
	 * Sets the clipping bounds that will be used for any subsequent 
	 * rendering operations.<br>
	 * Any rendering being done outside of the clip bounds will be 
	 * ignored and only pixels within the clip bounds will be rendered 
	 * to screen.<br>
	 * 
	 * @param bounds the new clip bounds used by subsequent rendering operations
	 * @see PBounds
	 * @see #setClipBounds(PBounds)
	 * @see #getClipBounds()
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawLetter(PFontResource, char, float, float)
	 * @see #drawString(PFontResource, String, float, float)
	 */
	public void setClipBounds(int x, int y, int width, int height);
	
	/**
	 * Returns the current clip bounds that are being used for any 
	 * subsequent rendering operations.<br>
	 * Any rendering being done outside of the clip bounds will be 
	 * ignored and only pixels within the clip bounds will be rendered 
	 * to screen.<br>
	 * This method never returns null.<br>
	 * 
	 * @return the current clip bounds
	 * @see PBounds
	 * @see #setClipBounds(PBounds)
	 * @see #setClipBounds(int, int, int, int)
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawLetter(PFontResource, char, float, float)
	 * @see #drawString(PFontResource, String, float, float)
	 * @see #drawImage(PImageResource, float, float, float, float)
	 * @see #drawImage(PImageResource, int, int, int, int, float, float, float, float)
	 */
	public PBounds getClipBounds();
	
	/**
	 * Sets the {@link PColor} that will be used by any subsequent 
	 * rendering operations.<br>
	 * The color should be used when rendering geometry or text.<br> 
	 * 
	 * @param color the new color being used for rendering operations
	 * @throws NullPointerException if color is null
	 * @see #setColor1(double, double, double, double)
	 * @see #setColor255(int, int, int, int)
	 * @see #getColor()
	 * @see PColor
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawLetter(PFontResource, char, float, float)
	 * @see #drawString(PFontResource, String, float, float)
	 */
	public void setColor(PColor color);
	
	/**
	 * Sets the {@link PColor} that will be used by any subsequent 
	 * rendering operations.<br>
	 * The color should be used when rendering geometry or text.<br> 
	 * 
	 * @param r the red color component as an integer between 0 and 255 inclusive
	 * @param g the green color component as an integer between 0 and 255 inclusive
	 * @param b the blue color component as an integer between 0 and 255 inclusive
	 * @param a the alpha color component as an integer between 0 and 255 inclusive
	 * @see #setColor(PColor)
	 * @see #setColor1(double, double, double, double)
	 * @see #getColor()
	 * @see PColor
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawLetter(PFontResource, char, float, float)
	 * @see #drawString(PFontResource, String, float, float)
	 */
	public void setColor255(int r, int g, int b, int a);
	
	/**
	 * Sets the {@link PColor} that will be used by any subsequent 
	 * rendering operations.<br>
	 * The color should be used when rendering geometry or text.<br> 
	 * 
	 * @param r the red color component as a floating point number between 0.0 and 1.0 inclusive
	 * @param g the green color component as a floating point number between 0.0 and 1.0 inclusive
	 * @param b the blue color component as a floating point number between 0.0 and 1.0 inclusive
	 * @param a the alpha color component as a floating point number between 0.0 and 1.0 inclusive
	 * @see #setColor(PColor)
	 * @see #setColor255(int, int, int, int)
	 * @see #getColor()
	 * @see PColor
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawLetter(PFontResource, char, float, float)
	 * @see #drawString(PFontResource, String, float, float)
	 */
	public void setColor1(double r, double g, double b, double a);
	
	/**
	 * Returns the current {@link PColor} being used for rendering 
	 * geometry or text.<br>
	 * This method will never return null.<br>
	 * 
	 * @return the color used for drawing geometry or text
	 * @see #setColor(PColor)
	 * @see #setColor1(double, double, double, double)
	 * @see #setColor255(int, int, int, int)
	 * @see PColor
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawLetter(PFontResource, char, float, float)
	 * @see #drawString(PFontResource, String, float, float)
	 */
	public PColor getColor();
	
	public void drawImage(PImageResource imgRes, float x, float y, float fx, float fy);
	
	public void drawImage(PImageResource imgRes, int u, int v, int fu, int fv, float x, float y, float fx, float fy);
	
	public void drawLine(float x1, float y1, float x2, float y2, float lineWidth);
	
	public void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3);
	
	public void drawQuad(float x, float y, float fx, float fy);
	
	public void drawQuad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);
	
	public void drawLetter(PFontResource font, char c, float x, float y);
	
	public void drawString(PFontResource font, String text, float x, float y);
	
}