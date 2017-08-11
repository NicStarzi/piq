package edu.udo.piq.lwjgl3.renderer;

import org.lwjgl.opengl.GL11;

public class LwjglRmOutline implements LwjglRenderMode {
	
	protected LwjglPRendererBase renderer;
	
	public LwjglRmOutline(LwjglPRendererBase renderer) {
		this.renderer = renderer;
	}
	
	@Override
	public void drawLineImmediate(
			float x1, float y1,
			float x2, float y2,
			float lineWidth)
	{
		GL11.glLineWidth(lineWidth);
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4f(renderer.curColorR, renderer.curColorG,
				renderer.curColorB, renderer.curColorA);
		
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y2);
		
		GL11.glEnd();
	}
	
	@Override
	public void drawPolygonImmediate(float[] xCoords, float[] yCoords) {
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(renderer.curColorR, renderer.curColorG,
				renderer.curColorB, renderer.curColorA);
		
		int length = xCoords.length;
		for (int i = 0; i < length; i++) {
			GL11.glVertex2f(xCoords[i], yCoords[i]);
		}
		GL11.glEnd();
	}
	
	@Override
	public void drawTriangleImmediate(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3)
	{
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(renderer.curColorR, renderer.curColorG,
				renderer.curColorB, renderer.curColorA);
		
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y2);
		GL11.glVertex2f(x3, y3);
		GL11.glVertex2f(x1, y1);
		
		GL11.glEnd();
	}
	
	@Override
	public void drawQuadImmediate(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4)
	{
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(renderer.curColorR, renderer.curColorG,
				renderer.curColorB, renderer.curColorA);
		
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y2);
		GL11.glVertex2f(x3, y3);
		GL11.glVertex2f(x4, y4);
		GL11.glVertex2f(x1, y1);
		
		GL11.glEnd();
	}
	
	@Override
	public void drawEllipseImmediate(int x, int y, int width, int height) {
		float halfW = width / 2f;
		float halfH = height / 2f;
		float x0 = x + halfW;
		float y0 = y + halfH;
		
		double k = 1 - (0.5 / Math.max(halfW, halfH));
		double th = Math.acos(2 * k * k - 1);
		
		int vertexCount = (int) Math.max(2 * Math.PI / th + 0.5, 4);
		
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(renderer.curColorR, renderer.curColorG,
				renderer.curColorB, renderer.curColorA);
		
		float radStep = (float) Math.toRadians(360.0 / vertexCount);
		float rad = 0;
		for (int i = 0; i < vertexCount; i++) {
			float xi = x0 + (float) Math.cos(rad) * halfW;
			float yi = y0 + (float) Math.sin(rad) * halfH;
			GL11.glVertex2f(xi, yi);
			rad += radStep;
		}
		float xn = x0 + (float) Math.cos(0) * halfW;
		float yn = y0 + (float) Math.sin(0) * halfH;
		GL11.glVertex2f(xn, yn);
		GL11.glEnd();
	}
	
}