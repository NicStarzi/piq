package edu.udo.piq.swing;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class SwingPRenderModeOutlineDashed implements SwingPRenderMode {
	
	private final Stroke dashed = new BasicStroke(1, 
			BasicStroke.CAP_BUTT, 
			BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
	private int[] xCoordsInternal = new int[4];
	private int[] yCoordsInternal = new int[4];
	
	public void drawLine(Graphics2D g, 
			float x1, float y1, float x2, float y2, 
			float lineWidth) 
	{
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(lineWidth, 
				BasicStroke.CAP_BUTT, 
				BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
		g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		g.setStroke(oldStroke);
	}
	
	public void drawTriangle(Graphics2D g, 
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3) 
	{
		drawPolygon(g, x1, y1, x2, y2, x3, y3, 0, 0, 3);
	}
	
	public void drawQuad(Graphics2D g, float x, float y, float fx, float fy) {
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		g.drawRect((int) x, (int) y, (int) (fx - x) - 1, (int) (fy - y) - 1);
		g.setStroke(oldStroke);
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
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		g.drawOval(x, y, width, height);
		g.setStroke(oldStroke);
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
		
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		g.drawPolygon(xCoordsInternal, xCoordsInternal, count);
		g.setStroke(oldStroke);
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
		
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		g.drawPolygon(xCoordsInternal, yCoordsInternal, number);
		g.setStroke(oldStroke);
	}
	
}