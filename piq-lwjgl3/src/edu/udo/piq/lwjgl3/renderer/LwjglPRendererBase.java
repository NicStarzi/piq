package edu.udo.piq.lwjgl3.renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;
import edu.udo.piq.PRenderer;
import edu.udo.piq.lwjgl3.StbImageResource;
import edu.udo.piq.lwjgl3.StbTtFontResource;
import edu.udo.piq.util.ThrowException;

public abstract class LwjglPRendererBase implements PRenderer {
	
	protected final LwjglRmFill		RENDER_MODE_FILL	= new LwjglRmFill(this);
	protected final LwjglRmOutline	RENDER_MODE_OUTLINE	= new LwjglRmOutline(this);
	protected final LwjglRmDashed	RENDER_MODE_DASHED	= new LwjglRmDashed(this);
	protected final LwjglRmXor		RENDER_MODE_XOR		= new LwjglRmXor(this);
	
	protected LwjglRenderMode curMode;
	protected float curColorR, curColorG, curColorB, curColorA;
	protected int accSciX, accSciY, accSciFx, accSciFy;
	protected int viewportW, viewportH;
	
	public void beginReRender() {
		accSciX = accSciY = accSciFx = accSciFy = 0;
		curColorR = curColorG = curColorB = curColorA = 0;
	}
	
	public abstract void endReRender();
	
	public abstract void renderAll();
	
	public void setViewportSize(int width, int height) {
		viewportW = width;
		viewportH = height;
	}
	
	@Override
	public final void setRenderMode(PRenderMode mode) {
		curMode = ThrowException.ifTypeCastFails(mode,
				LwjglRenderMode.class, "!(mode instanceof LwjglRenderMode)");
	}
	
	@Override
	public final LwjglRenderMode getActiveRenderMode() {
		return curMode;
	}
	
	@Override
	public final LwjglRmFill getRenderModeFill() {
		return RENDER_MODE_FILL;
	}
	
	@Override
	public final LwjglRmOutline getRenderModeOutline() {
		return RENDER_MODE_OUTLINE;
	}
	
	@Override
	public final LwjglRmDashed getRenderModeOutlineDashed() {
		return RENDER_MODE_DASHED;
	}
	
	@Override
	public final LwjglRmXor getRenderModeXOR() {
		return RENDER_MODE_XOR;
	}
	
	public final void setGlScissor(int x, int y, int width, int height) {
		accSciX = x;
		accSciY = y;
		accSciFx = x + width;
		accSciFy = y + height;
		int sciY = viewportH - (y + height);
		GL11.glScissor(x, sciY, width, height);
	}
	
	@Override
	public void setClipBounds(int x, int y, int width, int height) {
		setGlScissor(x, y, width, height);
	}
	
	@Override
	public void intersectClipBounds(int x, int y, int width, int height) {
		accSciX = Math.max(accSciX, x);
		accSciY = Math.max(accSciY, y);
		accSciFx = Math.min(accSciFx, x + width);
		accSciFy = Math.min(accSciFy, y + height);
		int scissorW = accSciFx - accSciX;
		int scissorH = accSciFy - accSciY;
		if (scissorW < 0 || scissorH < 0) {
			GL11.glScissor(0, 0, 0, 0);
		} else {
			int sciY = viewportH - (accSciY + scissorH);
			GL11.glScissor(accSciX, sciY, scissorW, scissorH);
		}
	}
	
	@Override
	public final void setColor1(double r, double g, double b, double a) {
		curColorR = (float) r;
		curColorG = (float) g;
		curColorB = (float) b;
		curColorA = (float) a;
	}
	
	@Override
	public final void drawImage(PImageResource imgRes, int u, int v, int fu, int fv, float x, float y, float fx, float fy) {
		StbImageResource stbImgRes = (StbImageResource) imgRes;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		stbImgRes.bind();
		u /= stbImgRes.getWidth();
		v /= stbImgRes.getHeight();
		fu /= stbImgRes.getWidth();
		fv /= stbImgRes.getHeight();
		
		LwjglPRendererBase.renderTriangleQuad(1, 1, 1, 1,
				u, v, fu, fv,
				x, y, fx, fy);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public final void drawImage(PImageResource imgRes, float x, float y, float fx, float fy) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		((StbImageResource) imgRes).bind();
		
		LwjglPRendererBase.renderTriangleQuad(1, 1, 1, 1,
				0, 0, 1, 1,
				x, y, fx, fy);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public final boolean isFontSupported(PFontResource font) {
		return font instanceof StbTtFontResource;
	}
	
	@Override
	public final void drawString(PFontResource font, String text, float x, float y) {
		StbTtFontResource fontRes = (StbTtFontResource) font;
		int fontSize = fontRes.getPixelSize();
		y += fontRes.getAscent();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer bufX = stack.floats(x);
			FloatBuffer bufY = stack.floats(y);
			
			STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
			STBTTBakedChar.Buffer charData = fontRes.getBakedCharData();
			
			GL11.glEnable(GL_TEXTURE_2D);
			GL11.glBindTexture(GL_TEXTURE_2D, fontRes.getGlName());
			
			GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glColor4f(curColorR, curColorG, curColorB, curColorA);
			
			int firstCP = StbTtFontResource.BAKE_FONT_FIRST_CHAR;
			int lastCP = StbTtFontResource.BAKE_FONT_FIRST_CHAR + StbTtFontResource.GLYPH_COUNT - 1;
			for (int i = 0; i < text.length(); i++) {
				int codePoint = text.codePointAt(i);
				if (codePoint == '\n') {
					bufX.put(0, x);
					bufY.put(0, y + bufY.get(0) + fontSize);
					continue;
				} else if (codePoint < firstCP || codePoint > lastCP) {
					continue;
				}
				STBTruetype.stbtt_GetBakedQuad(charData,
						StbTtFontResource.FONT_TEX_W, StbTtFontResource.FONT_TEX_H,
						codePoint - firstCP,
						bufX, bufY, q, true);
//				System.out.println("RENDER \t c="+((char) codePoint)+"; w="+(x1 - x0)+"; w2="+(x0 - oldX1));
				
				GL11.glTexCoord2f(q.s0(), q.t0());
				GL11.glVertex2f(q.x0(), q.y0());
				
				GL11.glTexCoord2f(q.s0(), q.t1());
				GL11.glVertex2f(q.x0(), q.y1());
				
				GL11.glTexCoord2f(q.s1(), q.t1());
				GL11.glVertex2f(q.x1(), q.y1());
				
				GL11.glTexCoord2f(q.s1(), q.t1());
				GL11.glVertex2f(q.x1(), q.y1());
				
				GL11.glTexCoord2f(q.s1(), q.t0());
				GL11.glVertex2f(q.x1(), q.y0());
				
				GL11.glTexCoord2f(q.s0(), q.t0());
				GL11.glVertex2f(q.x0(), q.y0());
			}
			GL11.glEnd();
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}
	
	public static void renderTriangleQuad(
			float r, float g, float b, float a,
			float u, float v, float fu, float fv,
			float x, float y, float fx, float fy)
	{
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glColor4f(r, g, b, a);
		
		GL11.glTexCoord2f(	u , v );
		GL11.glVertex2f(	x , y );
		GL11.glTexCoord2f(	u , fv);
		GL11.glVertex2f(	x , fy);
		GL11.glTexCoord2f(	fu, fv);
		GL11.glVertex2f(	fx, fy);
		GL11.glTexCoord2f(	fu, fv);
		GL11.glVertex2f(	fx, fy);
		GL11.glTexCoord2f(	fu, v );
		GL11.glVertex2f(	fx, y );
		GL11.glTexCoord2f(	u , v );
		GL11.glVertex2f(	x , y );
		
		GL11.glEnd();
	}
	
}