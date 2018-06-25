package edu.udo.piq.swing;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PSize;
import edu.udo.piq.swing.util.SwingImageUtil;
import edu.udo.piq.tools.ImmutablePColor;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.MutablePSize;

public class ImagePFont implements PFontResource, SwingRenderFont {
	
	protected Map<PColor, BufferedImage> tintedImgMap;
	protected final BufferedImage glyphImg;
	protected final int glyphW, glyphH;
	protected final String name;
	protected final Style style;
	
	public ImagePFont(String fontName, Style fontStyle, BufferedImage image, int glyphWidth, int glyphHeight) {
		name = fontName;
		style = fontStyle;
		glyphImg = image;
		glyphW = glyphWidth;
		glyphH = glyphHeight;
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
	
	public BufferedImage getTintedImage(PColor color) {
		if (tintedImgMap == null) {
			tintedImgMap = new HashMap<>();
		}
		// We make sure color is immutable to make it usable as a key into the tintedImgMap hashMap.
		if (!(color instanceof ImmutablePColor)) {
			color = new ImmutablePColor(color);
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
		// TODO
	}
	
	@Override
	public void renderTo(Graphics2D graphics, String text, float x, float y) {
		// TODO
	}
	
}