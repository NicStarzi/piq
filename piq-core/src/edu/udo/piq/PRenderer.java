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
public interface PRenderer extends PDisposable {
	
	/**
	 * Sets the platform specific {@link PRenderMode} for this {@link PRenderer}.
	 * All following primitive rendering operations will use this {@link PRenderMode}
	 * when being performed.<br>
	 * Primitive rendering operations are {@link #drawEllipse(int, int, int, int)},
	 * {@link #drawLine(float, float, float, float, float)},
	 * {@link #drawPolygon(float[], float[])},
	 * {@link #drawTriangle(float, float, float, float, float, float)} and all kinds of
	 * {@link #drawQuad(PBounds)}.<br>
	 * Non-primitive rendering operations are
	 * {@link #drawImage(PImageResource, float, float, float, float)},
	 * {@link #drawImage(PImageResource, int, int, int, int, float, float, float, float)},
	 * and {@link #drawString(PFontResource, String, float, float)}.<br>
	 * @param mode						the new render mode that is to be used
	 * @throws NullPointerException		if mode is null
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawPolygon(float[], float[])
	 * @see #drawEllipse(int, int, int, int)
	 * @see #getActiveRenderMode()
	 * @see #getRenderModeFill()
	 * @see #getRenderModeOutline()
	 * @see #getRenderModeOutlineDashed()
	 */
	public void setRenderMode(PRenderMode mode);
	
	/**
	 * Returns the currently active {@link PRenderMode} as used by this {@link PRenderer}.
	 * The returned value will never be null.<br>
	 * @return	a non-null instance of {@link PRenderMode}
	 */
	public PRenderMode getActiveRenderMode();
	
	/**
	 * Returns the platform specific {@link PRenderMode} that can be used to completely fill
	 * the inside of a primitive shape (triangles, quads, ellipses and polygons are primitive
	 * shapes). <br>
	 * The returned value is never null. This rendering mode must always be supported.<br>
	 * @return a non-null instance of {@link PRenderMode}
	 */
	public PRenderMode getRenderModeFill();
	
	public PRenderMode getRenderModeOutline();
	
	public PRenderMode getRenderModeOutlineDashed();
	
	public PRenderMode getRenderModeXOR();
	
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
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawString(PFontResource, String, float, float)
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
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawString(PFontResource, String, float, float)
	 */
	public void setClipBounds(int x, int y, int width, int height);
	
	public void intersectClipBounds(int x, int y, int width, int height);
	
	/**
	 * Sets the {@link PColor} that will be used by any subsequent
	 * rendering operations.<br>
	 * The color should be used when rendering geometry or text.<br>
	 * 
	 * @param color the new color being used for rendering operations
	 * @throws NullPointerException if color is null
	 * @see #setColor1(double, double, double, double)
	 * @see #setColor255(int, int, int, int)
	 * @see PColor
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawString(PFontResource, String, float, float)
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
	 * @see PColor
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawString(PFontResource, String, float, float)
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
	 * @see PColor
	 * @see #drawLine(float, float, float, float, float)
	 * @see #drawTriangle(float, float, float, float, float, float)
	 * @see #drawQuad(float, float, float, float)
	 * @see #drawQuad(float, float, float, float, float, float, float, float)
	 * @see #drawString(PFontResource, String, float, float)
	 */
	public void setColor1(double r, double g, double b, double a);
	
	public default void drawImage(PImageResource imgRes, float x, float y, float fx, float fy) {
		PSize size = imgRes.getSize();
		drawImage(imgRes, 0, 0, size.getWidth(), size.getHeight(), x, y, fx, fy);
	}
	
	public void drawImage(PImageResource imgRes, int u, int v, int fu, int fv,
			float x, float y, float fx, float fy);
	
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
	
	public default void drawQuad(PBounds bounds) {
		drawQuad(bounds.getX(), bounds.getY(), bounds.getFinalX(), bounds.getFinalY());
	}
	
	public default void drawQuad(
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
	
	public boolean isFontSupported(PFontResource font);
	
	public void drawString(PFontResource font, String text, float x, float y);
	
	public void drawEllipse(int x, int y, int width, int height);
	
	public default void strokeQuad(PBounds bounds) {
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		strokeQuad(x, y, fx, fy);
	}
	
	public default void strokeQuad(PBounds bounds, int lineWidth) {
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		strokeQuad(x, y, fx, fy, lineWidth);
	}
	
	public default void strokeQuad(int x, int y, int fx, int fy) {
		strokeQuad(x, y, fx, fy, 1);
	}
	
	public default void strokeQuad(int x, int y, int fx, int fy, int lineWidth) {
		strokeTop(x, y, fx, fy, lineWidth);
		strokeBottom(x, y, fx, fy, lineWidth);
		strokeLeft(x, y, fx, fy, lineWidth);
		strokeRight(x, y, fx, fy, lineWidth);
	}
	
	public default void strokeTop(PBounds bounds) {
		strokeTop(bounds.getX(), bounds.getY(), bounds.getFinalX(), bounds.getFinalY());
	}
	
	public default void strokeTop(PBounds bounds, int lineWidth) {
		strokeTop(bounds.getX(), bounds.getY(), bounds.getFinalX(), bounds.getFinalY(), lineWidth);
	}
	
	public default void strokeTop(int x, int y, int fx, int fy) {
		strokeTop(x, y, fx, fy, 1);
	}
	
	public default void strokeTop(int x, int y, int fx, int fy, int lineWidth) {
		drawQuad(x, y, fx, y + lineWidth);
	}
	
	public default void strokeBottom(PBounds bounds) {
		strokeBottom(bounds.getX(), bounds.getY(), bounds.getFinalX(), bounds.getFinalY());
	}
	
	public default void strokeBottom(PBounds bounds, int lineWidth) {
		strokeBottom(bounds.getX(), bounds.getY(), bounds.getFinalX(), bounds.getFinalY(), lineWidth);
	}
	
	public default void strokeBottom(int x, int y, int fx, int fy) {
		strokeBottom(x, y, fx, fy, 1);
	}
	
	public default void strokeBottom(int x, int y, int fx, int fy, int lineWidth) {
		drawQuad(x, fy - lineWidth, fx, fy);
	}
	
	public default void strokeLeft(PBounds bounds) {
		strokeLeft(bounds.getX(), bounds.getY(), bounds.getFinalX(), bounds.getFinalY());
	}
	
	public default void strokeLeft(PBounds bounds, int lineWidth) {
		strokeLeft(bounds.getX(), bounds.getY(), bounds.getFinalX(), bounds.getFinalY(), lineWidth);
	}
	
	public default void strokeLeft(int x, int y, int fx, int fy) {
		strokeLeft(x, y, fx, fy, 1);
	}
	
	public default void strokeLeft(int x, int y, int fx, int fy, int lineWidth) {
		drawQuad(x, y, x + lineWidth, fy);
	}
	
	public default void strokeRight(PBounds bounds) {
		strokeRight(bounds.getX(), bounds.getY(), bounds.getFinalX(), bounds.getFinalY());
	}
	
	public default void strokeRight(PBounds bounds, int lineWidth) {
		strokeRight(bounds.getX(), bounds.getY(), bounds.getFinalX(), bounds.getFinalY(), lineWidth);
	}
	
	public default void strokeRight(int x, int y, int fx, int fy) {
		strokeRight(x, y, fx, fy, 1);
	}
	
	public default void strokeRight(int x, int y, int fx, int fy, int lineWidth) {
		drawQuad(fx - lineWidth, y, fx, fy);
	}
	
}