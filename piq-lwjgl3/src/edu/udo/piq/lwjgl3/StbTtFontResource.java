package edu.udo.piq.lwjgl3;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointBox;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePSize;

public class StbTtFontResource implements PFontResource {
	
	public static final int CHAR_DATA_MALLOC_SIZE = 96;
	public static final int FONT_TEX_W = 512;
	public static final int FONT_TEX_H = FONT_TEX_W;
	public static final int BAKE_FONT_FIRST_CHAR = 32;
	public static final int GLYPH_COUNT = CHAR_DATA_MALLOC_SIZE;
	
	protected final STBTTBakedChar.Buffer charData;
	protected final GlyphDim[] glyphSizes = new GlyphDim[GLYPH_COUNT];
	protected final int fontHeight;
	protected final int texGlName;
	protected final float ascent;
	protected final float descent;
	protected final float lineGap;
	protected final String fontName;
	protected boolean disposed;
	
	public StbTtFontResource(File ttfFile, int fontHeight) {
		fontName = ttfFile.getName();
		this.fontHeight = fontHeight;
		charData = STBTTBakedChar.malloc(CHAR_DATA_MALLOC_SIZE);
		int texGlName = 0;
		float ascent = 0, descent = 0, lineGap = 0;
		try {
			ByteBuffer ttfFileData = GlfwPRoot.loadFileToByteBuffer(ttfFile);
			ByteBuffer texData = BufferUtils.createByteBuffer(FONT_TEX_W * FONT_TEX_H);
			int result = stbtt_BakeFontBitmap(ttfFileData, fontHeight, texData, FONT_TEX_W, FONT_TEX_H, BAKE_FONT_FIRST_CHAR, charData);
			System.out.println("StbTtPFontResource.StbTtPFontResource() BakeFontBitmap="+result);
			
			try (MemoryStack stack = MemoryStack.stackPush()) {
				STBTTFontinfo fontInfo = STBTTFontinfo.mallocStack(stack);
				stbtt_InitFont(fontInfo, ttfFileData);
				float pixelScale = stbtt_ScaleForPixelHeight(fontInfo, fontHeight);
				
				IntBuffer bufAscent = stack.ints(0);
				IntBuffer bufDescent = stack.ints(0);
				IntBuffer bufLineGap = stack.ints(0);
				stbtt_GetFontVMetrics(fontInfo, bufAscent, bufDescent, bufLineGap);
				ascent = bufAscent.get(0) * pixelScale;
				descent = bufDescent.get(0) * pixelScale;
				lineGap = bufLineGap.get(0) * pixelScale;
				System.out.println("ascent="+ascent+"; descent="+descent+"; lineGap="+lineGap);

				IntBuffer bufX0 = stack.ints(0);
				IntBuffer bufX1 = stack.ints(0);
				IntBuffer bufY0 = stack.ints(0);
				IntBuffer bufY1 = stack.ints(0);
				int firstCP = BAKE_FONT_FIRST_CHAR;
				int lastCP = BAKE_FONT_FIRST_CHAR + GLYPH_COUNT - 1;
				for (int codePoint = firstCP;
						codePoint <= lastCP;
						codePoint++)
				{
					stbtt_GetCodepointBox(fontInfo, codePoint, bufX0, bufY0, bufX1, bufY1);
					float x0 = pixelScale * bufX0.get(0);
					float y0 = pixelScale * bufY0.get(0);
					float x1 = pixelScale * bufX1.get(0);
					float y1 = pixelScale * bufY1.get(0);
					stbtt_GetCodepointHMetrics(fontInfo, codePoint, bufX0, bufX1);
					float adv = pixelScale * bufX0.get(0);
					float lsb = pixelScale * bufX1.get(0);
					float glyphW = Math.max(Math.abs(x0 - x1), Math.abs(adv + lsb));
					float glyphH = Math.max(Math.abs(y0 - y1), fontHeight);
					GlyphDim glyphDim = new GlyphDim(glyphW, glyphH);
					glyphSizes[codePoint - firstCP] = glyphDim;
//					System.out.println("c="+(char)codePoint+"="+glyphDim+"; w="+(adv + lsb));
				}
//				glyphSizes[' ' - firstCP] = glyphSizes['W' - firstCP];
			}
			
			texGlName = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texGlName);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, FONT_TEX_W, FONT_TEX_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, texData);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.texGlName = texGlName;
		this.ascent = ascent;
		this.descent = descent;
		this.lineGap = lineGap;
	}
	
	public int getGlName() {
		return texGlName;
	}
	
	public STBTTBakedChar.Buffer getBakedCharData() {
		return charData;
	}
	
	public GlyphDim getGlyphSizeFor(int codePoint) {
		return glyphSizes[codePoint - BAKE_FONT_FIRST_CHAR];
	}
	
	public float getAscent() {
		return ascent;
	}
	
	public float getDescent() {
		return descent;
	}
	
	public float getLineGap() {
		return lineGap;
	}
	
	@Override
	public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		charData.free();
		if (texGlName != 0) {
			GL11.glDeleteTextures(texGlName);
		}
	}
	
	@Override
	protected void finalize() {
		dispose();
	}
	
	@Override
	public String getName() {
		return fontName;
	}
	
	@Override
	public int getPixelSize() {
		return fontHeight;
	}
	
	@Override
	public PSize getSize(String text) {
		float txtW = 0;
		float txtH = 0;
		int firstCP = BAKE_FONT_FIRST_CHAR;
		int lastCP = BAKE_FONT_FIRST_CHAR + GLYPH_COUNT - 1;
		for (int i = 0; i < text.length(); i++) {
			int codePoint = text.codePointAt(i);
			if (codePoint < firstCP || codePoint > lastCP) {
				continue;
			}
			GlyphDim dim = glyphSizes[codePoint - firstCP];
			txtW += dim.width;
			if (txtH < dim.height) {
				txtH = dim.height;
			}
		}
		PSize result = new ImmutablePSize((int) (txtW + 0.9f), (int) (txtH + 0.9f));
//		System.out.println("StbTtFontResource.getSize("+text+")="+result);
		return result;
//		return PSize.ZERO_SIZE;
	}
	
	@Override
	public Style getStyle() {
		return Style.PLAIN;
	}
	
	public static class GlyphDim {
		public final float width, height;
		public GlyphDim(float x0, float y0, float x1, float y1) {
			width = Math.abs(x0 - x1);
			height = Math.abs(y0 - y1);
		}
		public GlyphDim(float w, float h) {
			width = w;
			height = h;
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(width);
			sb.append(", ");
			sb.append(height);
			sb.append(")");
			return sb.toString();
		}
	}
	
}