package edu.udo.piq.lwjgl3.renderer;

import org.lwjgl.opengl.GL11;

public class LwjglRmDashed implements LwjglRenderMode {
	
	protected LwjglPRendererBase renderer;
	protected float lengthDash = 3;
	protected float lengthGap = 2;
	
	public LwjglRmDashed(LwjglPRendererBase renderer) {
		this.renderer = renderer;
	}
	
	public void renderDashedLine(float x1, float y1, float x2, float y2) {
		double lengthLine = Math.hypot(Math.abs(x1 - x2), Math.abs(y1 - y2));
		float lengthSegment = lengthDash + lengthGap;
		int segmentCount = (int) (lengthLine / lengthSegment + 0.5);
		float advSegX = ((x2 - x1) / segmentCount);
		float advSegY = ((y2 - y1) / segmentCount);
		float dashSegmentLengthFactor = lengthDash / lengthSegment;
		float advDashX = advSegX * dashSegmentLengthFactor;
		float advDashY = advSegY * dashSegmentLengthFactor;
		float drawX = x1;
		float drawY = y1;
		for (int i = 0; i < segmentCount; i++) {
			GL11.glVertex2f(drawX + advDashX, drawY + advDashY);
			drawX += advSegX;
			drawY += advSegY;
		}
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
		
		renderDashedLine(x1, y1, x2, y2);
		
		GL11.glEnd();
	}
	
	@Override
	public void drawPolygonImmediate(float[] xCoords, float[] yCoords) {
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4f(renderer.curColorR, renderer.curColorG,
				renderer.curColorB, renderer.curColorA);
		
		int lastIdx = xCoords.length - 1;
		for (int i = 0; i < lastIdx; i++) {
			renderDashedLine(xCoords[i], yCoords[i], xCoords[i + 1], yCoords[i + 1]);
		}
		renderDashedLine(xCoords[lastIdx], yCoords[lastIdx], xCoords[0], yCoords[0]);
		
		GL11.glEnd();
	}
	
	@Override
	public void drawTriangleImmediate(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3)
	{
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4f(renderer.curColorR, renderer.curColorG,
				renderer.curColorB, renderer.curColorA);
		
		renderDashedLine(x1, y1, x2, y2);
		renderDashedLine(x2, y2, x3, y3);
		renderDashedLine(x3, y3, x1, y1);
		
		GL11.glEnd();
	}
	
	@Override
	public void drawQuadImmediate(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4)
	{
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4f(renderer.curColorR, renderer.curColorG,
				renderer.curColorB, renderer.curColorA);
		
		renderDashedLine(x1, y1, x2, y2);
		renderDashedLine(x2, y2, x3, y3);
		renderDashedLine(x3, y3, x4, y4);
		renderDashedLine(x4, y4, x1, y1);
		
		GL11.glEnd();
	}
	
	@Override
	public void drawEllipseImmediate(float x, float y, float width, float height) {
		float halfW = width / 2f;
		float halfH = height / 2f;
		float x0 = x + halfW;
		float y0 = y + halfH;
		
		double k = 1 - (0.5 / Math.max(halfW, halfH));
		double th = Math.acos(2 * k * k - 1);
		
		int vertexCount = (int) Math.max(2 * Math.PI / th + 0.5, 4);
		
		GL11.glBegin(GL11.GL_LINES);
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
	
	@Override
	public void drawArcImmediate(float x, float y, float width, float height, float angleFrom, float angleArc) {
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
		GL11.glVertex2f(x0, y0);
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