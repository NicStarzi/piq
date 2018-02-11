package edu.udo.piq.lwjgl3.renderer;

import edu.udo.piq.PRenderMode;

public interface LwjglRenderMode extends PRenderMode {
	
	public void drawLineImmediate(
			float x1, float y1,
			float x2, float y2,
			float lineWidth);
	
	public default Runnable drawLine(
			float x1, float y1,
			float x2, float y2,
			float lineWidth)
	{
		return () -> drawLineImmediate(x1, y1, x2, y2, lineWidth);
	}
	
	public void drawPolygonImmediate(float[] xCoords, float[] yCoords);
	
	public default Runnable drawPolygon(float[] xCoords, float[] yCoords) {
		return () -> drawPolygonImmediate(xCoords, yCoords);
	}
	
	public void drawTriangleImmediate(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3);
	
	public default Runnable drawTriangle(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3)
	{
		return () -> drawTriangleImmediate(x1, y1, x2, y2, x3, y3);
	}
	
	public void drawQuadImmediate(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4);
	
	public default void drawQuadImmediate(
			float x, float y,
			float fx, float fy)
	{
		drawQuadImmediate(x, y, x, fy, fx, fy, fx, y);
	}
	
	public default Runnable drawQuad(
			float x, float y,
			float fx, float fy)
	{
		return () -> drawQuadImmediate(x, y, x, fy, fx, fy, fx, y);
	}
	
	public default Runnable drawQuad(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4)
	{
		return () -> drawQuadImmediate(x1, y1, x2, y2, x3, y3, x4, y4);
	}
	
	public void drawEllipseImmediate(float x, float y, float width, float height);
	
	public default Runnable drawEllipse(float x, float y, float width, float height) {
		return () -> drawEllipseImmediate(x, y, width, height);
	}
	
}