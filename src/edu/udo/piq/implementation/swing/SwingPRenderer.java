package edu.udo.piq.implementation.swing;

import java.awt.BasicStroke;
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
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.ImmutablePColor;

public class SwingPRenderer implements PRenderer {
	
	private final int[] xCoords = new int[4];
	private final int[] yCoords = new int[4];
	private Graphics2D graphics;
	
	public void setGraphics(Graphics2D g) {
		graphics = g;
	}
	
	public void setClipBounds(PBounds bounds) {
		setClipBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}
	
	public void setClipBounds(int x, int y, int width, int height) {
		graphics.setClip(x, y, width, height);
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
				(int) (fx - x), (int) (fy - y), 
				u, v, 
				fu - u, fv - v, 
				null);
	}
	
	public void drawLine(
			float x1, float y1, 
			float x2, float y2, 
			float lineWidth) 
	{
		graphics.setStroke(new BasicStroke(lineWidth));
		graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}
	
	public void drawTriangle(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3) 
	{
		drawPolygon(x1, y1, x2, y2, x3, y3, 0, 0, 3);
	}
	
	public void drawQuad(float x, float y, float fx, float fy) {
		graphics.fillRect((int) x, (int) y, (int) (fx - x), (int) (fy - y));
//		if (img == null) {
//			
//		} else {
//			graphics.drawImage(img.getBufferedImage(), 
//					(int) x, (int) y, (int) fx, (int) fy, 
//					uv[U1], uv[V1], uv[U3], uv[V2], 
//					null);
//		}
	}
	
	public void drawQuad(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3, 
			float x4, float y4) 
	{
		drawPolygon(x1, y1, x2, y2, x3, y3, x4, y4, 3);
	}
	
	private void drawPolygon(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3, 
			float x4, float y4, 
			int number) 
	{
		xCoords[0] = (int) x1;
		xCoords[1] = (int) x2;
		xCoords[2] = (int) x3;
		xCoords[3] = (int) x4;
		yCoords[0] = (int) y1;
		yCoords[1] = (int) y2;
		yCoords[2] = (int) y3;
		yCoords[3] = (int) y4;
		graphics.fillPolygon(xCoords, xCoords, number);
	}
	
	public void drawLetter(PFontResource font, char c, float x, float y) {
		drawString(font, Character.toString(c), x, y);
	}
	
	public void drawString(PFontResource font, String text, float x, float y) {
		Font awtFont = ((AwtPFontResource) font).getAwtFont();
		FontMetrics fm = graphics.getFontMetrics(awtFont);
		LineMetrics lm = fm.getLineMetrics(text, graphics);
		y += lm.getAscent() + lm.getLeading();
		graphics.setFont(awtFont);
		graphics.drawString(text, x, y);
	}
	
}