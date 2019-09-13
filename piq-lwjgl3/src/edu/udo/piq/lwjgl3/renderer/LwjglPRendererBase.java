package edu.udo.piq.lwjgl3.renderer;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;
import edu.udo.piq.PRenderer;
import edu.udo.piq.lwjgl3.GlfwFontResource;
import edu.udo.piq.lwjgl3.StbImageResource;
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
	
	public float getColorRed() {
		return curColorR;
	}
	
	public float getColorGreen() {
		return curColorG;
	}
	
	public float getColorBlue() {
		return curColorB;
	}
	
	public float getColorAlpha() {
		return curColorA;
	}
	
	public void applyGlColor() {
		GL11.glColor4f(curColorR, curColorG, curColorB, curColorA);
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
	public final void setColor1(float r, float g, float b, float a) {
		curColorR = r;
		curColorG = g;
		curColorB = b;
		curColorA = a;
	}
	
	public final void drawImageTinted(PImageResource imgRes, float x, float y, float fx, float fy) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		((StbImageResource) imgRes).bind();
		
		LwjglPRendererBase.renderTriangleQuad(
				curColorR, curColorG, curColorB, curColorA,
				0, 0, 1, 1,
				x, y, fx, fy);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public final void drawImageTinted(PImageResource imgRes, int u, int v, int fu, int fv, float x, float y, float fx, float fy) {
		StbImageResource stbImgRes = (StbImageResource) imgRes;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		stbImgRes.bind();
		float imgW = stbImgRes.getWidth();
		float imgH = stbImgRes.getHeight();
		float u_f = u / imgW;
		float v_f = v / imgH;
		float fu_f = fu / imgW;
		float fv_f = fv / imgH;
		
		LwjglPRendererBase.renderTriangleQuad(
				curColorR, curColorG, curColorB, curColorA,
				u_f, v_f, fu_f, fv_f,
				x, y, fx, fy);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public final void drawImage(PImageResource imgRes, int u, int v, int fu, int fv, float x, float y, float fx, float fy) {
		StbImageResource stbImgRes = (StbImageResource) imgRes;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		stbImgRes.bind();
		float imgW = stbImgRes.getWidth();
		float imgH = stbImgRes.getHeight();
		float u_f = u / imgW;
		float v_f = v / imgH;
		float fu_f = fu / imgW;
		float fv_f = fv / imgH;
		
		LwjglPRendererBase.renderTriangleQuad(1, 1, 1, 1,
				u_f, v_f, fu_f, fv_f,
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
		return font instanceof GlfwFontResource;
	}
	
	@Override
	public void drawChars(PFontResource font, char[] charArr, int from, int length, float x, float y) {
		GlfwFontResource glfwFont = (GlfwFontResource) font;
		glfwFont.drawChars(this, charArr, from, length, x, y);
	}
	
	@Override
	public final void drawString(PFontResource font, String text, float x, float y) {
		GlfwFontResource glfwFont = (GlfwFontResource) font;
		glfwFont.drawString(this, text, x, y);
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