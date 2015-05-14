package edu.udo.piq.experimental;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PSize;

public interface PRenderer2 {
	
	/**
	 * Sets the clipping bounds that will be used for any subsequent 
	 * rendering operations.<br>
	 * Any rendering being done outside of the clip bounds will be 
	 * ignored and only pixels within the clip bounds will be rendered 
	 * to screen.<br>
	 * 
	 * @param bounds					the clipping region
	 * @throws NullPointerException		if bounds is null
	 * @see PBounds
	 * @see #setClipBounds(PBounds)
	 * @see #getClipBounds()
	 */
	public default void setClipBounds(PBounds bounds) 
			throws NullPointerException 
	{
		setClipBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}
	
	/**
	 * Sets the clipping bounds that will be used for any subsequent 
	 * rendering operations.<br>
	 * Any rendering being done outside of the clip bounds will be 
	 * ignored and only pixels within the clip bounds will be rendered 
	 * to screen.<br>
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @see PBounds
	 * @see #setClipBounds(PBounds)
	 * @see #getClipBounds()
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
	 */
	public default void setColor(PColor color) {
		setColor1(color.getRed1(), color.getGreen1(), color.getBlue1(), color.getAlpha1());
	}
	
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
	 */
	public default void setColor255(int r, int g, int b, int a) {
		setColor1(r / 255.0, g / 255.0, b / 255.0, a / 255.0);
	}
	
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
	 */
	public PColor getColor();
	
	/**
	 * Sets the {@link PDrawMode} used for all subsequent drawing 
	 * operations. Before rendering a component always make sure the 
	 * right draw mode is selected.<br>
	 * It may not be that all draw modes are supported by every 
	 * implementation of the {@link PRenderer2}. If a draw mode is 
	 * not supported an other draw mode will be used instead. You 
	 * can test whether a draw mode is supported or not with the 
	 * {@link #isDrawModeSupported(PDrawMode)} method.<br>
	 * 
	 * @param mode						the draw mode to be used
	 * @throws NullPointerException		if mode is null
	 * @see PDrawMode
	 * @see #isDrawModeSupported(PDrawMode)
	 */
	public void setDrawMode(PDrawMode mode) throws NullPointerException;
	
	/**
	 * Returns true if the given draw mode is supported by this 
	 * implementation of {@link PRenderer2}.<br>
	 * If a draw mode is not supported a different draw mode will 
	 * be used instead when trying to draw anything with it.<br>
	 * 
	 * @param mode						the draw mode to be used
	 * @throws NullPointerException		if mode is null
	 * @see PDrawMode
	 * @see #setDrawMode(PDrawMode)
	 */
	public boolean isDrawModeSupported(PDrawMode mode) throws NullPointerException;
	
	public void drawLine(float x1, float y1, float x2, float y2, float lineWidth);
	
	public default void drawTriangle(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3) 
	{
		float[] xCoords = new float[] {x1, x2, x3};
		float[] yCoords = new float[] {y1, y2, y3};
		drawPolygon(xCoords, yCoords);
	}
	
	public void drawEllipse(int x, int y, int width, int height);
	
	public default void drawRect(PBounds bounds) {
		drawRect(bounds.getX(), bounds.getY(), bounds.getFinalX(), bounds.getFinalY());
	}
	
	public default void drawRect(
			float x, float y, 
			float fx, float fy) 
	{
		drawQuad(x, y, x, fy, fx, fy, fx, y);
	}
	
	public default void drawQuad(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3, 
			float x4, float y4) 
	{
		float[] xCoords = new float[] {x1, x2, x3, x4};
		float[] yCoords = new float[] {y1, y2, y3, y4};
		drawPolygon(xCoords, yCoords);
	}
	
	public void drawPolygon(float[] xCoords, float[] yCoords);
	
	public void drawString(PFontResource font, String text, float x, float y);
	
	public default void drawImage(PImageResource imgRes, float x, float y, float fx, float fy) {
		PSize size = imgRes.getSize();
		drawImage(imgRes, 0, 0, size.getWidth(), size.getHeight(), x, y, fx, fy);
	}
	
	public void drawImage(PImageResource imgRes, int u, int v, int fu, int fv, float x, float y, float fx, float fy);
	
}