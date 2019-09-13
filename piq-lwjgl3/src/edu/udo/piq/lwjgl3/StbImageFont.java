package edu.udo.piq.lwjgl3;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.PSize;
import edu.udo.piq.lwjgl3.GlyphMap.Glyph;
import edu.udo.piq.lwjgl3.renderer.LwjglPRendererBase;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.MutablePSize;

public class StbImageFont implements GlfwFontResource {
	
	protected final StbImageResource glyphImg;
	protected final GlyphMap glyphMap;
	protected final String name;
	protected final Style style;
	protected final int pxSize;
	
	public StbImageFont(String fontName, Style fontStyle, int pixelSize, StbImageResource image, GlyphMap glyphMap) {
		name = fontName;
		style = fontStyle;
		pxSize = pixelSize;
		glyphImg = image;
		this.glyphMap = glyphMap;
	}
	
	public StbImageResource getGlyphImage() {
		return glyphImg;
	}
	
	public GlyphMap getGlyphMap() {
		return glyphMap;
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
			if (glyph == null) {
				System.out.println("c="+text.charAt(i)+"; str="+text);
				continue;
			}
			int glyphW = glyph.getWidth() + glyph.getTranslateX() + glyph.getAdvance();
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
	public void drawChars(LwjglPRendererBase renderer, char[] charArr, int from, int length, float x, float y) {
		StbImageResource glyphImg = getGlyphImage();
		float glyphImgW = glyphImg.getWidth();
		float glyphImgH = glyphImg.getHeight();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		glyphImg.bind();
		GL11.glBegin(GL11.GL_TRIANGLES);
		renderer.applyGlColor();
		GlyphMap glyphMap = getGlyphMap();
		for (int i = from; i < length; i++) {
			Glyph glyph = glyphMap.getGlyphFor(charArr[i]);
			if (glyph == null) {
				continue;
			}
			int glyphW = glyph.getWidth();
			int glyphH = glyph.getHeight();
			float dstX = x + glyph.getTranslateX();
			float dstY = y + glyph.getTranslateY();
			float dstFx = dstX + glyphW;
			float dstFy = dstY + glyphH;
			int srcX = glyph.getX();
			int srcY = glyph.getY();
			int srcFx = glyph.getFx();
			int srcFy = glyph.getFy();
			
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
			
			x = dstFx + glyph.getAdvance();
		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public final void drawString(LwjglPRendererBase renderer, String text, float x, float y) {
		StbImageResource glyphImg = getGlyphImage();
		float glyphImgW = glyphImg.getWidth();
		float glyphImgH = glyphImg.getHeight();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		glyphImg.bind();
		GL11.glBegin(GL11.GL_TRIANGLES);
		renderer.applyGlColor();
		GlyphMap glyphMap = getGlyphMap();
		for (int i = 0; i < text.length(); i++) {
			Glyph glyph = glyphMap.getGlyphFor(text.charAt(i));
			if (glyph == null) {
				continue;
			}
			int glyphW = glyph.getWidth();
			int glyphH = glyph.getHeight();
			float dstX = x + glyph.getTranslateX();
			float dstY = y + glyph.getTranslateY();
			float dstFx = dstX + glyphW;
			float dstFy = dstY + glyphH;
			int srcX = glyph.getX();
			int srcY = glyph.getY();
			int srcFx = glyph.getFx();
			int srcFy = glyph.getFy();
			
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
			
			x = dstFx + glyph.getAdvance();
		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
}