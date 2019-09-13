package edu.udo.piq.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PSize;
import edu.udo.piq.swing.util.SwingImageUtil;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.MutablePSize;

public class AwtImageFontMonoSpaced implements PFontResource, SwingRenderFont {
	
	protected final BufferedImage glyphImg;
	protected final int glyphW, glyphH;
	protected final String name;
	protected final Style style;
	
	protected Map<Color, BufferedImage> tintedImgMap;
	private final int[] glyphLookupTable = new int[256];
	
	public AwtImageFontMonoSpaced(String fontName, Style fontStyle, BufferedImage image, int glyphWidth, int glyphHeight, String letters) {
		name = fontName;
		style = fontStyle;
		glyphImg = image;
		glyphW = glyphWidth;
		glyphH = glyphHeight;
		
//		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!?.:,()[]/&%#<>*$=+-°~'_; ";
		int unknownIndex = letters.indexOf('?');
		Arrays.fill(glyphLookupTable, unknownIndex);
		int maxChar = 0;
		for (int i = 0; i < letters.length(); i++) {
			char c = letters.charAt(i);
			glyphLookupTable[c] = i;
			if (maxChar < c) {
				maxChar = c;
			}
		}
	}
	
	public BufferedImage getGlyphImage() {
		return glyphImg;
	}
	
	public int getGlyphWidth() {
		return glyphW;
	}
	
	public int getGlyphHeight() {
		return glyphH;
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
		return glyphH;
	}
	
	@Override
	public Style getStyle() {
		return style;
	}
	
	@Override
	public PSize getSize(String text, MutablePSize result) {
		int length = text.length();
		int textW = length * glyphW;
		int textH = glyphH;
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
		int glyphW = getGlyphWidth();
		int glyphH = getGlyphHeight();
		int glyphsInW = bufImg.getWidth() / glyphW;
		
		float txtAlpha = color.getAlpha() / 255.0f;
		boolean hasAlpha = txtAlpha != 1.0;
		Composite oldAc = null;
		if (hasAlpha) {
			oldAc = graphics.getComposite();
			AlphaComposite ac = AlphaComposite.SrcOver.derive(txtAlpha);
			graphics.setComposite(ac);
		}
		int glyphCount = glyphLookupTable.length;
		for (int i = from; i < length; i++) {
			int c = text[i];
			if (c >= glyphCount) {
				continue;
			}
			int glyphIdx = glyphLookupTable[c];
			
			int dstX = (int) x;
			int dstY = (int) y;
			int dstFx = dstX + glyphW;
			int dstFy = dstY + glyphH;
			int srcX = (glyphIdx % glyphsInW) * glyphW;
			int srcY = (glyphIdx / glyphsInW) * glyphH;
			int srcFx = srcX + glyphW;
			int srcFy = srcY + glyphH;
			
			graphics.drawImage(bufImg, dstX, dstY, dstFx, dstFy,
					srcX, srcY, srcFx, srcFy, null);
			
			x += glyphW;
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
		int glyphW = getGlyphWidth();
		int glyphH = getGlyphHeight();
		int glyphsInW = bufImg.getWidth() / glyphW;
		
		float txtAlpha = color.getAlpha() / 255.0f;
		boolean hasAlpha = txtAlpha != 1.0;
		Composite oldAc = null;
		if (hasAlpha) {
			oldAc = graphics.getComposite();
			AlphaComposite ac = AlphaComposite.SrcOver.derive(txtAlpha);
			graphics.setComposite(ac);
		}
		int glyphCount = glyphLookupTable.length;
		for (int i = 0; i < text.length(); i++) {
			int c = text.charAt(i);
			if (c >= glyphCount) {
				continue;
			}
			int glyphIdx = glyphLookupTable[c];
			
			int dstX = (int) x;
			int dstY = (int) y;
			int dstFx = dstX + glyphW;
			int dstFy = dstY + glyphH;
			int srcX = (glyphIdx % glyphsInW) * glyphW;
			int srcY = (glyphIdx / glyphsInW) * glyphH;
			int srcFx = srcX + glyphW;
			int srcFy = srcY + glyphH;
			
			graphics.drawImage(bufImg, dstX, dstY, dstFx, dstFy,
					srcX, srcY, srcFx, srcFy, null);
			
			x += glyphW;
		}
		if (hasAlpha) {
			graphics.setComposite(oldAc);
		}
	}
	
}