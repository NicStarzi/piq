package edu.udo.piq.swing;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

public class SwingPRenderModeOutline implements SwingPRenderMode {
	
	private int[] xCoordsInternal = new int[4];
	private int[] yCoordsInternal = new int[4];
	
	@Override
	public void drawLine(Graphics2D g,
			float x1, float y1, float x2, float y2,
			float lineWidth)
	{
		g.setStroke(new BasicStroke(lineWidth));
		g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}
	
	@Override
	public void drawTriangle(Graphics2D g,
			float x1, float y1,
			float x2, float y2,
			float x3, float y3)
	{
		drawPolygon(g, x1, y1, x2, y2, x3, y3, 0, 0, 3);
	}
	
	@Override
	public void drawQuad(Graphics2D g, float x, float y, float fx, float fy) {
		g.drawRect((int) x, (int) y, (int) (fx - x) - 1, (int) (fy - y) - 1);
	}
	
	@Override
	public void drawQuad(Graphics2D g,
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4)
	{
		drawPolygon(g, x1, y1, x2, y2, x3, y3, x4, y4, 3);
	}
	
	@Override
	public void drawRoundedRect(Graphics2D g, float x, float y, float fx, float fy, float arcW, float arcH) {
		g.drawRoundRect((int) x, (int) y, (int) (fx - x), (int) (fy - y), (int) arcW, (int) arcH);
	}
	
	@Override
	public void drawArc(Graphics2D g, float x, float y,
			float width, float height, float angleFrom, float angleArc)
	{
		g.drawArc((int) x, (int) y, (int) width, (int) height, (int) angleFrom, (int) angleArc);
	}
	
	@Override
	public void drawEllipse(Graphics2D g, float x, float y, float width, float height) {
		g.drawOval((int) x, (int) y, (int) width, (int) height);
	}
	
	@Override
	public void drawPolygon(Graphics2D g, float[] xCoords, float[] yCoords) {
		int count = Math.min(xCoords.length, yCoords.length);
		if (count > xCoordsInternal.length) {
			xCoordsInternal = new int[count];
			yCoordsInternal = new int[count];
		}
		for (int i = 0; i < count; i++) {
			xCoordsInternal[i] = Math.round(xCoords[i]);
			yCoordsInternal[i] = Math.round(yCoords[i]);
		}
		g.drawPolygon(xCoordsInternal, xCoordsInternal, count);
	}
	
	private void drawPolygon(Graphics2D g,
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4,
			int number)
	{
		xCoordsInternal[0] = (int) x1;
		xCoordsInternal[1] = (int) x2;
		xCoordsInternal[2] = (int) x3;
		xCoordsInternal[3] = (int) x4;
		yCoordsInternal[0] = (int) y1;
		yCoordsInternal[1] = (int) y2;
		yCoordsInternal[2] = (int) y3;
		yCoordsInternal[3] = (int) y4;
		g.drawPolygon(xCoordsInternal, yCoordsInternal, number);
	}
	
}