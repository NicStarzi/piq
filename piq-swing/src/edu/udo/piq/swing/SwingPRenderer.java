package edu.udo.piq.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.ImmutablePColor;
import edu.udo.piq.util.Throw;

public class SwingPRenderer implements PRenderer {
	
	public static final SwingPRenderMode MODE_FILL		= new SwingPRenderModeFill();
	public static final SwingPRenderMode MODE_OUTLINE	= new SwingPRenderModeOutline();
	public static final SwingPRenderMode MODE_DASHED	= new SwingPRenderModeOutlineDashed((SwingPRenderModeOutline) MODE_OUTLINE);
	public static final SwingPRenderMode MODE_XOR		= new SwingPRenderModeXOR();
	
	protected Map<PColor, Color> pColorToAwtColorMap = new HashMap<>();
	protected SwingPRenderMode renderMode = MODE_FILL;
	protected Graphics2D graphics;
	
	public void setAwtGraphics(Graphics2D g) {
		graphics = g;
	}
	
	public Graphics2D getAwtGraphics() {
		return graphics;
	}
	
	/*
	 * Metadata
	 */
	
	@Override
	public void setClipBounds(int x, int y, int width, int height) {
		graphics.setClip(x, y, width, height);
	}
	
	@Override
	public void intersectClipBounds(int x, int y, int width, int height) {
		graphics.clipRect(x, y, width, height);
	}
	
	public PBounds getClipBounds() {
		Rectangle r = graphics.getClipBounds();
		return new ImmutablePBounds(r.x, r.y, r.width, r.height);
	}
	
	public Color getAwtColorForPColor(PColor pColor) {
		if (pColorToAwtColorMap == null) {
			int r = pColor.getRed255();
			int g = pColor.getGreen255();
			int b = pColor.getBlue255();
			int a = pColor.getAlpha255();
			return new Color(r, g, b, a);
		}
		Color awtColor = pColorToAwtColorMap.get(pColor);
		if (awtColor == null) {
			int r = pColor.getRed255();
			int g = pColor.getGreen255();
			int b = pColor.getBlue255();
			int a = pColor.getAlpha255();
			awtColor = new Color(r, g, b, a);
			pColorToAwtColorMap.put(pColor, awtColor);
		}
		return awtColor;
	}
	
	@Override
	public void setColor(PColor pColor) {
		Color awtColor = getAwtColorForPColor(pColor);
		graphics.setColor(awtColor);
	}
	
	@Override
	public void setColor255(int r, int g, int b, int a) {
		Color c = new Color(r, g, b, a);
		graphics.setColor(c);
	}
	
	@Override
	public void setColor1(float r, float g, float b, float a) {
		Color c = new Color(r, g, b, a);
		graphics.setColor(c);
	}
	
	public PColor getColor() {
		Color c = graphics.getColor();
		return new ImmutablePColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}
	
	/*
	 * Primitive rendering
	 */
	
	@Override
	public void drawLine(
			float x1, float y1,
			float x2, float y2,
			float lineWidth)
	{
		renderMode.drawLine(graphics, x1, y1, x2, y2, lineWidth);
	}
	
	@Override
	public void drawTriangle(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3)
	{
		renderMode.drawTriangle(graphics, x1, y1, x2, y2, x3, y3);
	}
	
	@Override
	public void drawQuad(PBounds bounds) {
		renderMode.drawQuad(graphics, bounds);
	}
	
	@Override
	public void drawQuad(float x, float y, float fx, float fy) {
		renderMode.drawQuad(graphics, x, y, fx, fy);
	}
	
	@Override
	public void drawQuad(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4)
	{
		renderMode.drawQuad(graphics, x1, y1, x2, y2, x3, y3, x4, y4);
	}
	
	@Override
	public void drawPolygon(float[] xCoords, float[] yCoords) {
		renderMode.drawPolygon(graphics, xCoords, yCoords);
	}
	
	@Override
	public void drawRoundedRect(float x, float y, float fx, float fy, float arcW, float arcH) {
		renderMode.drawRoundedRect(graphics, x, y, fx, fy, arcW, arcH);
	}
	
	@Override
	public void drawEllipse(float x, float y, float width, float height) {
		renderMode.drawEllipse(graphics, x, y, width, height);
	}
	
	@Override
	public void drawArc(float x, float y, float width, float height,
			float angleFrom, float angleArc)
	{
		renderMode.drawArc(graphics, x, y, width, height, angleFrom, angleArc);
	}
	
	/*
	 * Non-Primitive rendering
	 */
	
	@Override
	public void drawImage(PImageResource imgRes, float x, float y, float fx, float fy) {
		if (imgRes == null) {
			return;
		}
		BufferedImage bufImg = ((AwtPImageResource) imgRes).getBufferedImage();
		if (bufImg == null) {
			return;
		}
		graphics.drawImage(bufImg,
				(int) x, (int) y,
				(int) (fx - x), (int) (fy - y),
				null);
	}
	
	@Override
	public void drawImage(PImageResource imgRes, int u, int v, int fu, int fv, float x, float y, float fx, float fy) {
		BufferedImage bufImg = ((AwtPImageResource) imgRes).getBufferedImage();
		if (bufImg == null) {
			return;
		}
		graphics.drawImage(bufImg,
				(int) x, (int) y,
				(int) fx, (int) fy,
				u, v,
				fu, fv,
				null);
	}
	
	@Override
	public boolean isFontSupported(PFontResource font) {
		return font instanceof SwingRenderFont;
	}
	
	public void drawLetter(PFontResource font, char c, float x, float y) {
		drawChars(font, new char[] {c}, 0, 1, x, y);
	}
	
	@Override
	public void drawChars(PFontResource font, char[] charArr, int from, int length, float x, float y) {
		SwingRenderFont renderFont = (SwingRenderFont) font;
		renderFont.renderTo(getAwtGraphics(), charArr, from, length, x, y);
	}
	
	@Override
	public void drawString(PFontResource font, String text, float x, float y) {
		SwingRenderFont renderFont = (SwingRenderFont) font;
		renderFont.renderTo(getAwtGraphics(), text, x, y);
	}
	
	/*
	 * Render modes
	 */
	
	@Override
	public void setRenderMode(PRenderMode mode) {
		Throw.ifNull(mode, "mode == null");
		Throw.ifTypeCastFails(mode, SwingPRenderMode.class, () -> "mode == " + (mode).getClass().getSimpleName() + " != SwingPRenderMode");
		renderMode = (SwingPRenderMode) mode;
	}
	
	@Override
	public PRenderMode getActiveRenderMode() {
		return renderMode;
	}
	
	@Override
	public PRenderMode getRenderModeFill() {
		return MODE_FILL;
	}
	
	@Override
	public PRenderMode getRenderModeOutline() {
		return MODE_OUTLINE;
	}
	
	@Override
	public PRenderMode getRenderModeOutlineDashed() {
		return MODE_DASHED;
	}
	
	@Override
	public PRenderMode getRenderModeXOR() {
		return MODE_XOR;
	}
	
}