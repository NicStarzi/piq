package edu.udo.piq.lwjgl3;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.MutablePSize;

public class StbTtFontResource implements PFontResource {
	
	public static final int CHAR_DATA_MALLOC_SIZE = 96;
	public static final int FONT_TEX_W = 512;
	public static final int FONT_TEX_H = FONT_TEX_W;
	public static final int BAKE_FONT_FIRST_CHAR = 32;
	public static final int GLYPH_COUNT = CHAR_DATA_MALLOC_SIZE;
	
	protected final STBTTBakedChar.Buffer charData;
	protected final STBTTFontinfo fontInfo;
//	protected final GlyphDim[] glyphSizes = new GlyphDim[GLYPH_COUNT];
	protected final int fontHeight;
	protected final int texGlName;
	protected final float ascent;
	protected final float descent;
	protected final float lineGap;
	protected final String fontName;
	protected boolean disposed;
//	protected float whiteSpaceWidth = 4;
	
	public StbTtFontResource(File ttfFile, int fontHeight) {
		fontName = ttfFile.getName();
		this.fontHeight = fontHeight;
		charData = STBTTBakedChar.malloc(CHAR_DATA_MALLOC_SIZE);
		fontInfo = STBTTFontinfo.create();
		int texGlName = 0;
		float ascent = 0, descent = 0, lineGap = 0;
		try {
			ByteBuffer ttfFileData = GlfwPRoot.loadFileToByteBuffer(ttfFile);
			ByteBuffer texData = BufferUtils.createByteBuffer(FONT_TEX_W * FONT_TEX_H);
			int result = STBTruetype.stbtt_BakeFontBitmap(ttfFileData, fontHeight, texData, FONT_TEX_W, FONT_TEX_H, BAKE_FONT_FIRST_CHAR, charData);
			if (result < 1) {
				System.err.println("stbtt_BakeFontBitmap failed with return value: "+result);
			}
//			System.out.println("StbTtPFontResource.StbTtPFontResource() BakeFontBitmap="+result);
			
			try (MemoryStack stack = MemoryStack.stackPush()) {
//				STBTTFontinfo fontInfo = STBTTFontinfo.mallocStack(stack);
				STBTruetype.stbtt_InitFont(fontInfo, ttfFileData);
				float pixelScale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, fontHeight);
				
				IntBuffer bufAscent = stack.ints(0);
				IntBuffer bufDescent = stack.ints(0);
				IntBuffer bufLineGap = stack.ints(0);
				STBTruetype.stbtt_GetFontVMetrics(fontInfo, bufAscent, bufDescent, bufLineGap);
				ascent = bufAscent.get(0) * pixelScale;
				descent = bufDescent.get(0) * pixelScale;
				lineGap = bufLineGap.get(0) * pixelScale;
//				System.out.println("ascent="+ascent+"; descent="+descent+"; lineGap="+lineGap);

//				IntBuffer bufX0 = stack.ints(0);
//				IntBuffer bufX1 = stack.ints(0);
//				IntBuffer bufY0 = stack.ints(0);
//				IntBuffer bufY1 = stack.ints(0);
//				int firstCP = BAKE_FONT_FIRST_CHAR;
//				int lastCP = BAKE_FONT_FIRST_CHAR + GLYPH_COUNT - 1;
//				for (int codePoint = firstCP;
//						codePoint <= lastCP;
//						codePoint++)
//				{
//					STBTruetype.stbtt_GetCodepointBox(fontInfo, codePoint, bufX0, bufY0, bufX1, bufY1);
//					float x0 = pixelScale * bufX0.get(0);
//					float y0 = pixelScale * bufY0.get(0);
//					float x1 = pixelScale * bufX1.get(0);
//					float y1 = pixelScale * bufY1.get(0);
//					STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, codePoint, bufX0, bufX1);
//					float adv = pixelScale * bufX0.get(0);
//					float lsb = pixelScale * bufX1.get(0);
//					float glyphW = Math.max(Math.abs(x0 - x1), Math.abs(adv + lsb));
//					float glyphH = Math.max(Math.abs(y0 - y1), fontHeight);
//					GlyphDim glyphDim = new GlyphDim(glyphW, glyphH);
//					glyphSizes[codePoint - firstCP] = glyphDim;
////					System.out.println("c="+(char)codePoint+"="+glyphDim+"; w="+(adv + lsb));
//				}
//				FloatBuffer bufX = stack.floats(0);
//				FloatBuffer bufY = stack.floats(0);
//
//				STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
//				int firstCP = BAKE_FONT_FIRST_CHAR;
//				int lastCP = BAKE_FONT_FIRST_CHAR + GLYPH_COUNT - 1;
//				for (int codePoint = firstCP;
//						codePoint <= lastCP;
//						codePoint++)
//				{
//					STBTruetype.stbtt_GetBakedQuad(charData,
//							FONT_TEX_W, FONT_TEX_H,
//							codePoint - firstCP,
//							bufX, bufY, q, true);
//					float glyphW = Math.abs(q.x0() - q.x1());
//					float glyphH = Math.max(Math.abs(q.y0() - q.y1()), fontHeight);
//					GlyphDim glyphDim = new GlyphDim(glyphW, glyphH);
//					glyphSizes[codePoint - firstCP] = glyphDim;
////					System.out.println("SIZE \t c="+((char) codePoint)+"; w="+glyphW+"; h="+Math.abs(q.y0() - q.y1()));
//				}
//				GlyphDim glyphDimWS = glyphSizes['t' - firstCP];
//				glyphSizes[' ' - firstCP] = glyphDimWS;
//				System.out.println("SIZE \t WS="+glyphDimWS);
			}
			
			texGlName = GL11.glGenTextures();
			GL11.glBindTexture(GL_TEXTURE_2D, texGlName);
			GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, FONT_TEX_W, FONT_TEX_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, texData);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
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
	
//	public GlyphDim getGlyphSizeFor(int codePoint) {
//		return glyphSizes[codePoint - BAKE_FONT_FIRST_CHAR];
//	}
	
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
		fontInfo.free();
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
	
	public static int getCodePoint(String text, int length, int index, IntBuffer out) {
		char c1 = text.charAt(index);
		if (Character.isHighSurrogate(c1) && index + 1 < length) {
			char c2 = text.charAt(index + 1);
			if (Character.isLowSurrogate(c2)) {
				out.put(0, Character.toCodePoint(c1, c2));
				return 2;
			}
		}
		out.put(0, c1);
		return 1;
	}
	
	@Override
	public PSize getSize(String text, MutablePSize result) {
		int width = 0;
		int idx = 0;
		int length = text.length();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer codePnt	= stack.mallocInt(1);
			IntBuffer adv		= stack.mallocInt(1);
			IntBuffer lsb		= stack.mallocInt(1);
			
			while (idx < length) {
				idx += StbTtFontResource.getCodePoint(text, length, idx, codePnt);
				int codepoint = codePnt.get(0);
				
				STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, codepoint, adv, lsb);
				width += adv.get(0);
			}
		}
		width = (int) (width * STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, fontHeight) + 0.5f);
		if (result == null) {
			return new ImmutablePSize(width, fontHeight);
		} else {
			result.set(width, fontHeight);
			return result;
		}
//        int width = 0;
//
//        try (MemoryStack stack = stackPush()) {
//            IntBuffer pCodePoint       = stack.mallocInt(1);
//            IntBuffer pAdvancedWidth   = stack.mallocInt(1);
//            IntBuffer pLeftSideBearing = stack.mallocInt(1);
//
//            int i = from;
//            while (i < to) {
//                i += getCP(text, to, i, pCodePoint);
//                int cp = pCodePoint.get(0);
//
//                stbtt_GetCodepointHMetrics(info, cp, pAdvancedWidth, pLeftSideBearing);
//                width += pAdvancedWidth.get(0);
//
//                if (isKerningEnabled() && i < to) {
//                    getCP(text, to, i, pCodePoint);
//                    width += stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0));
//                }
//            }
//        }
//
//        return width * stbtt_ScaleForPixelHeight(info, fontHeight);
	}
	
//	@Override
//	public PSize getSize(String text) {
//		if (text.length() == 1 && text.charAt(0) == ' ') {
////			System.out.println("1) whiteSpaceWidth="+whiteSpaceWidth);
//			return new ImmutablePSize((int) (whiteSpaceWidth + 0.5f), fontHeight);
//		}
//		float txtW = 0;
//		float txtH = fontHeight;
//		int firstCP = BAKE_FONT_FIRST_CHAR;
//		int lastCP = BAKE_FONT_FIRST_CHAR + GLYPH_COUNT - 1;
//		try (MemoryStack stack = MemoryStack.stackPush()) {
//			FloatBuffer bufX = stack.floats(0);
//			FloatBuffer bufY = stack.floats(0);
//			STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
//			float oldX1 = 0;
//			for (int i = 0; i < text.length(); i++) {
//				int codePoint = text.codePointAt(i);
//				if (codePoint < firstCP || codePoint > lastCP) {
//					continue;
//				}
//				STBTruetype.stbtt_GetBakedQuad(charData,
//					FONT_TEX_W, FONT_TEX_H,
//					codePoint - firstCP,
//					bufX, bufY, q, true);
//				float x0 = q.x0(), x1 = q.x1();
//				float glyphW = (x1 - x0) + (x0 - oldX1);
//				oldX1 = x1;
//				float glyphH = q.y1() - q.y0();
//				txtW += glyphW;
//				if (txtH < glyphH) {
//					txtH = glyphH;
//				}
//				if (codePoint == ' ' && glyphW > whiteSpaceWidth) {
//					whiteSpaceWidth = glyphW;
//				}
//			}
//		}
//		if (text.charAt(text.length() - 1) == ' ') {
////			System.out.println("2) whiteSpaceWidth="+whiteSpaceWidth);
//			txtW += whiteSpaceWidth;
//		}
//		PSize result = new ImmutablePSize((int) (txtW + 0.9f), (int) (txtH + 0.9f));
////		System.out.println("StbTtFontResource.getSize("+text+")="+result);
//		return result;
//	}
	
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