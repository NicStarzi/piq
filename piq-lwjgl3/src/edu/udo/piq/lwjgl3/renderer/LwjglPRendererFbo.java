package edu.udo.piq.lwjgl3.renderer;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.lwjgl3.Fbo;

public class LwjglPRendererFbo extends LwjglPRendererBase {
	
	protected Fbo fbo;
	
	@Override
	public void beginReRender() {
//		System.out.println("LwjglPRendererFbo.beginReRender()");
		super.beginReRender();
		if (fbo == null) {
			fbo = new Fbo(viewportW, viewportH);
		}
		fbo.bind();
		fbo.unbindTexture();
//		GlfwPRoot.checkGlError("LwjglPRendererFbo.beginReRender");
	}
	
	@Override
	public void endReRender() {
//		System.out.println("LwjglPRendererFbo.endReRender()");
		fbo.unbind();
//		GlfwPRoot.checkGlError("LwjglPRendererFbo.endReRender");
	}
	
	@Override
	public void renderAll() {
//		System.out.println("LwjglPRendererFbo.renderAll()");
		setGlScissor(0, 0, viewportW, viewportH);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		fbo.bindTexture();
		
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glColor4f(1, 1, 1, 1);
		
		float x = 0;
		float y = viewportH;
		float fx = viewportW;
		float fy = 0;
		GL11.glTexCoord2f(	0 , 0 );
		GL11.glVertex2f(	x , y );
		GL11.glTexCoord2f(	0 , 1);
		GL11.glVertex2f(	x , fy);
		GL11.glTexCoord2f(	1 , 1 );
		GL11.glVertex2f(	fx, fy);
		GL11.glTexCoord2f(	1 , 1 );
		GL11.glVertex2f(	fx, fy);
		GL11.glTexCoord2f(	1 , 0 );
		GL11.glVertex2f(	fx, y );
		GL11.glTexCoord2f(	0 , 0 );
		GL11.glVertex2f(	x , y );
		
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
//		GlfwPRoot.checkGlError("LwjglPRendererFbo.renderAll");
	}
	
	@Override
	public void setViewportSize(int width, int height) {
		super.setViewportSize(width, height);
		if (fbo != null && viewportW > 0 && viewportH > 0) {
			fbo.resize(viewportW, viewportH);
		}
//		GlfwPRoot.checkGlError("LwjglPRendererFbo.setViewportSize");
	}
	
	@Override
	public void drawLine(float x1, float y1, float x2, float y2, float lineWidth) {
		curMode.drawLineImmediate(x1, y1, x2, y2, lineWidth);
	}
	
	@Override
	public void drawTriangle(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3)
	{
		curMode.drawTriangleImmediate(x1, y1, x2, y2, x3, y3);
	}
	
	@Override
	public void drawQuad(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4)
	{
		curMode.drawQuadImmediate(x1, y1, x2, y2, x3, y3, x4, y4);
	}
	
	@Override
	public void drawQuad(
			float x, float y,
			float fx, float fy)
	{
		curMode.drawQuadImmediate(x, y, fx, fy);
	}
	
	@Override
	public void drawPolygon(float[] xCoords, float[] yCoords) {
		curMode.drawPolygonImmediate(xCoords, yCoords);
	}
	
	@Override
	public void drawEllipse(float x, float y, float width, float height) {
		curMode.drawEllipseImmediate(x, y, width, height);
	}
	
}