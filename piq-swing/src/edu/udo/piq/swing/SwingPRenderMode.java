package edu.udo.piq.swing;

import java.awt.Graphics2D;

import edu.udo.piq.PBounds;
import edu.udo.piq.PRenderMode;

public interface SwingPRenderMode extends PRenderMode {
	
	public void drawLine(Graphics2D g,
			float x1, float y1, float x2, float y2,
			float lineWidth);
	
	public void drawTriangle(Graphics2D g,
			float x1, float y1,
			float x2, float y2,
			float x3, float y3);
	
	public default void drawQuad(Graphics2D g, PBounds bounds) {
		drawQuad(g, bounds.getX(), bounds.getY(),
				bounds.getFinalX(), bounds.getFinalY());
	}
	
	public void drawQuad(Graphics2D g,
			float x, float y,
			float fx, float fy);
	
	public void drawQuad(Graphics2D g,
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4);
	
	public void drawPolygon(Graphics2D g, float[] xCoords, float[] yCoords);
	
	public void drawArc(Graphics2D g, float x, float y, float width, float height,
			float angleFrom, float angleArc);
	
	public void drawEllipse(Graphics2D g, float x, float y, float width, float height);
	
	public void drawRoundedRect(Graphics2D g, float x, float y, float fx, float fy, float arcW, float arcH);
	
}