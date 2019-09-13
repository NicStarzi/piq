package edu.udo.piq.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PSize;
import edu.udo.piq.swing.GlyphMap.Glyph;
import edu.udo.piq.swing.util.SwingImageUtil;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.MutablePSize;

public class AwtImageFont implements PFontResource, SwingRenderFont {
	
	protected final BufferedImage glyphImg;
	protected final GlyphMap glyphMap;
	protected final String name;
	protected final Style style;
	protected final int pxSize;
	protected Map<Color, BufferedImage> tintedImgMap;
	
	public AwtImageFont(String fontName, Style fontStyle, int pixelSize, BufferedImage image, GlyphMap glyphMap) {
		name = fontName;
		style = fontStyle;
		pxSize = pixelSize;
		glyphImg = image;
		this.glyphMap = glyphMap;
	}
	
	public BufferedImage getGlyphImage() {
		return glyphImg;
	}
	
	public GlyphMap getGlyphMap() {
		return glyphMap;
	}
	
	public BufferedImage getTintedImage(Color color) {
		if (tintedImgMap == null) {
			tintedImgMap = new HashMap<>();
		}
		BufferedImage awtImg = tintedImgMap.get(color);
		if (awtImg == null) {
			awtImg = SwingImageUtil.createTintedCopy(glyphImg, color);
			tintedImgMap.put(color, awtImg);
		}
		return awtImg;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int getPixelSize() {
		return pxSize;
	}
	
	@Override
	public Style getStyle() {
		return style;
	}
	
	@Override
	public PSize getSize(String text, MutablePSize result) {
		int textW = 0;
		int textH = 0;
		GlyphMap glyphMap = getGlyphMap();
		for (int i = 0; i < text.length(); i++) {
			Glyph glyph = glyphMap.getGlyphFor(text.charAt(i));
			int glyphW = glyph.getWidth() + glyph.getTranslateX();
			int glyphH = glyph.getHeight() + Math.abs(glyph.getTranslateY());
			
			textW += glyphW;
			if (textH < glyphH) {
				textH = glyphH;
			}
		}
		if (result == null) {
			return new ImmutablePSize(textW, textH);
		} else {
			result.set(textW, textH);
			return result;
		}
	}
	
	@Override
	public void renderTo(Graphics2D graphics, char[] text, int from, int length, float x, float y) {
		Color color = graphics.getColor();
		BufferedImage bufImg;
		if (color.equals(Color.WHITE)) {
			bufImg = getGlyphImage();
		} else {
			bufImg = getTintedImage(color);
		}
		
		float txtAlpha = color.getAlpha() / 255.0f;
		boolean hasAlpha = txtAlpha != 1.0;
		Composite oldAc = null;
		if (hasAlpha) {
			oldAc = graphics.getComposite();
			AlphaComposite ac = AlphaComposite.SrcOver.derive(txtAlpha);
			graphics.setComposite(ac);
		}
		GlyphMap glyphMap = getGlyphMap();
		for (int i = from; i < length; i++) {
			Glyph glyph = glyphMap.getGlyphFor(text[i]);
			int glyphW = glyph.getWidth();
			int glyphH = glyph.getHeight();
			
			int dstX = glyph.getTranslateX() + (int) x;
			int dstY = glyph.getTranslateY() + (int) y;
			int dstFx = dstX + glyphW;
			int dstFy = dstY + glyphH;
			int srcX = glyph.getX();
			int srcY = glyph.getY();
			int srcFx = srcX + glyphW;
			int srcFy = srcY + glyphH;
			
			graphics.drawImage(bufImg, dstX, dstY, dstFx, dstFy,
					srcX, srcY, srcFx, srcFy, null);
			
			x = dstFx;
		}
		if (hasAlpha) {
			graphics.setComposite(oldAc);
		}
	}
	
	@Override
	public void renderTo(Graphics2D graphics, String text, float x, float y) {
		Color color = graphics.getColor();
		BufferedImage bufImg;
		if (color.equals(Color.WHITE)) {
			bufImg = getGlyphImage();
		} else {
			bufImg = getTintedImage(color);
		}
		
		float txtAlpha = color.getAlpha() / 255.0f;
		boolean hasAlpha = txtAlpha != 1.0;
		Composite oldAc = null;
		if (hasAlpha) {
			oldAc = graphics.getComposite();
			AlphaComposite ac = AlphaComposite.SrcOver.derive(txtAlpha);
			graphics.setComposite(ac);
		}
		GlyphMap glyphMap = getGlyphMap();
		for (int i = 0; i < text.length(); i++) {
			Glyph glyph = glyphMap.getGlyphFor(text.charAt(i));
			int glyphW = glyph.getWidth();
			int glyphH = glyph.getHeight();
			
			int dstX = glyph.getTranslateX() + (int) x;
			int dstY = glyph.getTranslateY() + (int) y;
			int dstFx = dstX + glyphW;
			int dstFy = dstY + glyphH;
			int srcX = glyph.getX();
			int srcY = glyph.getY();
			int srcFx = srcX + glyphW;
			int srcFy = srcY + glyphH;
			
			graphics.drawImage(bufImg, dstX, dstY, dstFx, dstFy,
					srcX, srcY, srcFx, srcFy, null);
			
			x = dstFx;
		}
		if (hasAlpha) {
			graphics.setComposite(oldAc);
		}
	}
	
}