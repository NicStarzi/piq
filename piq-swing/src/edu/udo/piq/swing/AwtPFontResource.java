package edu.udo.piq.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePSize;

public class AwtPFontResource implements PFontResource {
	
	private final Font font;
	private final FontMetrics metrics;
	private final Graphics2D graphics;
	
	public AwtPFontResource(Font font) {
		this.font = font;
		
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		graphics = img.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.setFont(font);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setComposite(AlphaComposite.SrcOut);
		metrics = graphics.getFontMetrics();
	}
	
	public Font getAwtFont() {
		return font;
	}
	
	public String getName() {
		return font.getName();
	}
	
	public double getPointSize() {
		return font.getSize2D();
	}
	
	public PSize getSize(String str) {
		Rectangle2D rect = metrics.getStringBounds(str, graphics);
		int w = (int) (rect.getWidth() + 0.5);
		int h = (int) (rect.getHeight() + 0.5);
		return new ImmutablePSize(w, h);
	}
	
	public Style getStyle() {
		int awtStyle = font.getStyle();
		switch (awtStyle) {
		case Font.ITALIC:
			return Style.ITALIC;
		case Font.BOLD:
			return Style.BOLD;
		case Font.ITALIC | Font.BOLD:
			return Style.BOLD_ITALIC;
		case Font.PLAIN:
		default:
			return Style.PLAIN;
		}
	}
	
	public void dispose() {
	}
	
}