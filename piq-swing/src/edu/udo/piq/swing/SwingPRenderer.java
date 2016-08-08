package edu.udo.piq.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.ImmutablePColor;

public class SwingPRenderer implements PRenderer {
	
	private static final SwingPRenderMode MODE_FILL = new SwingPRenderModeFill();
	private static final SwingPRenderMode MODE_OUTLINE = new SwingPRenderModeOutline();
	private static final SwingPRenderMode MODE_DASHED = new SwingPRenderModeOutlineDashed();
	private static final SwingPRenderMode MODE_XOR = new SwingPRenderModeXOR();
	
	private SwingPRenderMode renderMode = MODE_FILL;
	private Graphics2D graphics;
	
	public void setGraphics(Graphics2D g) {
		graphics = g;
	}
	
	/*
	 * Metadata
	 */
	
	public void setClipBounds(int x, int y, int width, int height) {
		graphics.setClip(x, y, width, height);
	}
	
	public void intersectClipBounds(int x, int y, int width, int height) {
		graphics.clipRect(x, y, width, height);
	}
	
	public PBounds getClipBounds() {
		Rectangle r = graphics.getClipBounds();
		return new ImmutablePBounds(r.x, r.y, r.width, r.height);
	}
	
	public void setColor(PColor color) {
		setColor255(color.getRed255(), color.getGreen255(), color.getBlue255(), color.getAlpha255());
	}
	
	public void setColor255(int r, int g, int b, int a) {
		Color c = new Color(r, g, b, a);
		graphics.setColor(c);
	}
	
	public void setColor1(double r, double g, double b, double a) {
		Color c = new Color((float) r, (float) g, (float) b, (float) a);
		graphics.setColor(c);
	}
	
	public PColor getColor() {
		Color c = graphics.getColor();
		return new ImmutablePColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}
	
	/*
	 * Primitive rendering
	 */
	
	public void drawLine(
			float x1, float y1, 
			float x2, float y2, 
			float lineWidth) 
	{
		renderMode.drawLine(graphics, x1, y1, x2, y2, lineWidth);
	}
	
	public void drawTriangle(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3) 
	{
		renderMode.drawTriangle(graphics, x1, y1, x2, y2, x3, y3);
	}
	
	public void drawQuad(PBounds bounds) {
		renderMode.drawQuad(graphics, bounds);
	}
	
	public void drawQuad(float x, float y, float fx, float fy) {
		renderMode.drawQuad(graphics, x, y, fx, fy);
	}
	
	public void drawQuad(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3, 
			float x4, float y4) 
	{
		renderMode.drawQuad(graphics, x1, y1, x2, y2, x3, y3, x4, y4);
	}
	
	public void drawPolygon(float[] xCoords, float[] yCoords) {
		renderMode.drawPolygon(graphics, xCoords, yCoords);
	}
	
	public void drawEllipse(int x, int y, int width, int height) {
		renderMode.drawEllipse(graphics, x, y, width, height);
	}
	
	/*
	 * Non-Primitive rendering
	 */
	
	public void drawImage(PImageResource imgRes, float x, float y, float fx, float fy) {
		BufferedImage bufImg = ((BufferedPImageResource) imgRes).getBufferedImage();
		if (bufImg == null) {
			return;
		}
		graphics.drawImage(bufImg, 
				(int) x, (int) y, 
				(int) (fx - x), (int) (fy - y), 
				null);
	}
	
	public void drawImage(PImageResource imgRes, int u, int v, int fu, int fv, float x, float y, float fx, float fy) {
		BufferedImage bufImg = ((BufferedPImageResource) imgRes).getBufferedImage();
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
	
	public void drawLetter(PFontResource font, char c, float x, float y) {
		drawString(font, Character.toString(c), x, y);
	}
	
	public boolean isFontSupported(PFontResource font) {
		return font instanceof AwtPFontResource;
	}
	
	public void drawString(PFontResource font, String text, float x, float y) {
		Font awtFont = ((AwtPFontResource) font).getAwtFont();
		FontMetrics fm = graphics.getFontMetrics(awtFont);
		LineMetrics lm = fm.getLineMetrics(text, graphics);
		y += lm.getAscent() + lm.getLeading();
		graphics.setFont(awtFont);
		graphics.drawString(text, x, y);
	}
	
	/*
	 * Render modes
	 */
	
	public void setRenderMode(PRenderMode mode) {
		if (mode == null) {
			throw new NullPointerException("mode");
		}
		if (!(mode instanceof SwingPRenderMode)) {
			throw new IllegalArgumentException("mode="+mode);
		}
		renderMode = (SwingPRenderMode) mode;
	}
	
	public PRenderMode getActiveRenderMode() {
		return renderMode;
	}
	
	public PRenderMode getRenderModeFill() {
		return MODE_FILL;
	}
	
	public PRenderMode getRenderModeOutline() {
		return MODE_OUTLINE;
	}
	
	public PRenderMode getRenderModeOutlineDashed() {
		return MODE_DASHED;
	}
	
	public PRenderMode getRenderModeXOR() {
		return MODE_XOR;
	}
	
}