package edu.udo.piq.lwjgl3;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.PSize;
import edu.udo.piq.lwjgl3.renderer.LwjglPRendererBase;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.MutablePSize;

public class StbImageFontMonoSpaced implements GlfwFontResource {
	
	protected final StbImageResource glyphImg;
	protected final int glyphW, glyphH;
	protected final String name;
	protected final Style style;
	
	private final int[] glyphLookupTable = new int[256];
	
	public StbImageFontMonoSpaced(String fontName, Style fontStyle, StbImageResource image, int glyphWidth, int glyphHeight, String letters) {
		name = fontName;
		style = fontStyle;
		glyphImg = image;
		glyphW = glyphWidth;
		glyphH = glyphHeight;
		
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
	
	public StbImageResource getGlyphImage() {
		return glyphImg;
	}
	
	public int getGlyphWidth() {
		return glyphW;
	}
	
	public int getGlyphHeight() {
		return glyphH;
	}
	
	public int getGlyphIndex(int codepoint) {
		return glyphLookupTable[codepoint];
	}
	
	public int getMaxGlyphIndex() {
		return glyphLookupTable.length - 1;
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
	public void drawChars(LwjglPRendererBase renderer, char[] charArr, int from, int length, float x, float y) {
		StbImageResource glyphImg = getGlyphImage();
		float glyphImgW = glyphImg.getWidth();
		float glyphImgH = glyphImg.getHeight();
		int glyphW = getGlyphWidth();
		int glyphH = getGlyphHeight();
		int glyphsInW = (int) (glyphImgW / glyphW);
		
		int glyphCount = getMaxGlyphIndex();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		glyphImg.bind();
		GL11.glBegin(GL11.GL_TRIANGLES);
		renderer.applyGlColor();
		for (int i = from; i < length; i++) {
			int c = charArr[i];
			if (c > glyphCount) {
				continue;
			}
			int glyphIdx = getGlyphIndex(c);
			
			float dstX = x;
			float dstY = y;
			float dstFx = dstX + glyphW;
			float dstFy = dstY + glyphH;
			float srcX = (glyphIdx % glyphsInW) * glyphW;
			float srcY = (glyphIdx / glyphsInW) * glyphH;
			float srcFx = srcX + glyphW;
			float srcFy = srcY + glyphH;
			float u = srcX / glyphImgW;
			float v = srcY / glyphImgH;
			float fu = srcFx / glyphImgW;
			float fv = srcFy / glyphImgH;
			
			GL11.glTexCoord2f(	u , v );
			GL11.glVertex2f(	dstX , dstY );
			GL11.glTexCoord2f(	u , fv);
			GL11.glVertex2f(	dstX , dstFy);
			GL11.glTexCoord2f(	fu, fv);
			GL11.glVertex2f(	dstFx, dstFy);
			GL11.glTexCoord2f(	fu, fv);
			GL11.glVertex2f(	dstFx, dstFy);
			GL11.glTexCoord2f(	fu, v );
			GL11.glVertex2f(	dstFx, dstY );
			GL11.glTexCoord2f(	u , v );
			GL11.glVertex2f(	dstX , dstY );
			
			x += glyphW;
		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public final void drawString(LwjglPRendererBase renderer, String text, float x, float y) {
		StbImageResource glyphImg = getGlyphImage();
		float glyphImgW = glyphImg.getWidth();
		float glyphImgH = glyphImg.getHeight();
		int glyphW = getGlyphWidth();
		int glyphH = getGlyphHeight();
		int glyphsInW = (int) (glyphImgW / glyphW);
		
		int glyphCount = getMaxGlyphIndex();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		glyphImg.bind();
		GL11.glBegin(GL11.GL_TRIANGLES);
		renderer.applyGlColor();
		for (int i = 0; i < text.length(); i++) {
			int c = text.charAt(i);
			if (c > glyphCount) {
				continue;
			}
			int glyphIdx = getGlyphIndex(c);
			
			float dstX = x;
			float dstY = y;
			float dstFx = dstX + glyphW;
			float dstFy = dstY + glyphH;
			float srcX = (glyphIdx % glyphsInW) * glyphW;
			float srcY = (glyphIdx / glyphsInW) * glyphH;
			float srcFx = srcX + glyphW;
			float srcFy = srcY + glyphH;
			float u = srcX / glyphImgW;
			float v = srcY / glyphImgH;
			float fu = srcFx / glyphImgW;
			float fv = srcFy / glyphImgH;
			
			GL11.glTexCoord2f(	u , v );
			GL11.glVertex2f(	dstX , dstY );
			GL11.glTexCoord2f(	u , fv);
			GL11.glVertex2f(	dstX , dstFy);
			GL11.glTexCoord2f(	fu, fv);
			GL11.glVertex2f(	dstFx, dstFy);
			GL11.glTexCoord2f(	fu, fv);
			GL11.glVertex2f(	dstFx, dstFy);
			GL11.glTexCoord2f(	fu, v );
			GL11.glVertex2f(	dstFx, dstY );
			GL11.glTexCoord2f(	u , v );
			GL11.glVertex2f(	dstX , dstY );
			
			x += glyphW;
		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
}