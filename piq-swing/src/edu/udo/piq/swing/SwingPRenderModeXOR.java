package edu.udo.piq.swing;

import java.awt.Color;
import java.awt.Graphics2D;

public class SwingPRenderModeXOR implements SwingPRenderMode {
	
	private static final Color XOR_COLOR = Color.RED;
	
	private final SwingPRenderMode delegate = new SwingPRenderModeOutline();
	
	@Override
	public void drawLine(Graphics2D g,
			float x1, float y1, float x2, float y2,
			float lineWidth)
	{
		g.setXORMode(XOR_COLOR);
		delegate.drawLine(g, x1, y1, x2, y2, lineWidth);
		g.setPaintMode();
	}
	
	@Override
	public void drawTriangle(Graphics2D g,
			float x1, float y1,
			float x2, float y2,
			float x3, float y3)
	{
		g.setXORMode(XOR_COLOR);
		delegate.drawTriangle(g, x1, y1, x2, y2, x3, y3);
		g.setPaintMode();
	}
	
	@Override
	public void drawQuad(Graphics2D g, float x, float y, float fx, float fy) {
		g.setXORMode(XOR_COLOR);
		delegate.drawQuad(g, x, y, fx, fy);
		g.setPaintMode();
	}
	
	@Override
	public void drawQuad(Graphics2D g,
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4)
	{
		g.setXORMode(XOR_COLOR);
		delegate.drawQuad(g, x1, y1, x2, y2, x3, y3, x4, y4);
		g.setPaintMode();
	}
	
	@Override
	public void drawRoundedRect(Graphics2D g, float x, float y, float fx, float fy, float arcW, float arcH) {
		g.setXORMode(XOR_COLOR);
		delegate.drawRoundedRect(g, x, y, fx, fy, arcW, arcH);
		g.setPaintMode();
	}
	
	@Override
	public void drawArc(Graphics2D g, float x, float y,
			float width, float height, float angleFrom, float angleArc)
	{
		g.setXORMode(XOR_COLOR);
		delegate.drawArc(g, x, y, width, height, angleFrom, angleArc);
		g.setPaintMode();
	}
	
	@Override
	public void drawEllipse(Graphics2D g, float x, float y, float width, float height) {
		g.setXORMode(XOR_COLOR);
		delegate.drawEllipse(g, x, y, width, height);
		g.setPaintMode();
	}
	
	@Override
	public void drawPolygon(Graphics2D g, float[] xCoords, float[] yCoords) {
		g.setXORMode(XOR_COLOR);
		delegate.drawPolygon(g, xCoords, yCoords);
		g.setPaintMode();
	}
	
}