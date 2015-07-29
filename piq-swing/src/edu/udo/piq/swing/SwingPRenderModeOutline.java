package edu.udo.piq.swing;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

public class SwingPRenderModeOutline implements SwingPRenderMode {
	
	private int[] xCoordsInternal = new int[4];
	private int[] yCoordsInternal = new int[4];
	
	public void drawLine(Graphics2D g, 
			float x1, float y1, float x2, float y2, 
			float lineWidth) 
	{
		g.setStroke(new BasicStroke(lineWidth));
		g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}
	
	public void drawTriangle(Graphics2D g, 
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3) 
	{
		drawPolygon(g, x1, y1, x2, y2, x3, y3, 0, 0, 3);
	}
	
	public void drawQuad(Graphics2D g, float x, float y, float fx, float fy) {
		g.drawRect((int) x, (int) y, (int) (fx - x), (int) (fy - y));
	}
	
	public void drawQuad(Graphics2D g, 
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3, 
			float x4, float y4) 
	{
		drawPolygon(g, x1, y1, x2, y2, x3, y3, x4, y4, 3);
	}
	
	public void drawEllipse(Graphics2D g, int x, int y, int width, int height) {
		g.drawOval(x, y, width, height);
	}
	
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