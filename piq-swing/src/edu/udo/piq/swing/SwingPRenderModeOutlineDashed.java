package edu.udo.piq.swing;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class SwingPRenderModeOutlineDashed implements SwingPRenderMode {
	
	private final SwingPRenderModeOutline delegate;
	private final Stroke dashed = new BasicStroke(1,
			BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
	private final float[] lineDashArg = new float[]{9};
	
	public SwingPRenderModeOutlineDashed(SwingPRenderModeOutline outlineMode) {
		this.delegate = outlineMode;
	}
	
	@Override
	public void drawLine(Graphics2D g,
			float x1, float y1, float x2, float y2,
			float lineWidth)
	{
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(lineWidth,
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, lineDashArg, 0));
		delegate.drawLine(g, x1, y1, x2, y2, lineWidth);
		g.setStroke(oldStroke);
	}
	
	@Override
	public void drawTriangle(Graphics2D g,
			float x1, float y1,
			float x2, float y2,
			float x3, float y3)
	{
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		delegate.drawTriangle(g, x1, y1, x2, y2, x3, y3);
		g.setStroke(oldStroke);
	}
	
	@Override
	public void drawQuad(Graphics2D g, float x, float y, float fx, float fy) {
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		delegate.drawQuad(g, x, y, fx, fy);
		g.setStroke(oldStroke);
	}
	
	@Override
	public void drawQuad(Graphics2D g,
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4)
	{
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		delegate.drawQuad(g, x1, y1, x2, y2, x3, y3, x4, y4);
		g.setStroke(oldStroke);
	}
	
	@Override
	public void drawRoundedRect(Graphics2D g, float x, float y, float fx, float fy, float arcW, float arcH) {
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		delegate.drawRoundedRect(g, x, y, fx, fy, arcW, arcH);
		g.setStroke(oldStroke);
	}
	
	@Override
	public void drawArc(Graphics2D g, float x, float y,
			float width, float height, float angleFrom, float angleArc)
	{
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		delegate.drawArc(g, x, y, width, height, angleFrom, angleArc);
		g.setStroke(oldStroke);
	}
	
	@Override
	public void drawEllipse(Graphics2D g, float x, float y, float width, float height) {
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		delegate.drawEllipse(g, x, y, width, height);
		g.setStroke(oldStroke);
	}
	
	@Override
	public void drawPolygon(Graphics2D g, float[] xCoords, float[] yCoords) {
		Stroke oldStroke = g.getStroke();
		g.setStroke(dashed);
		delegate.drawPolygon(g, xCoords, yCoords);
		g.setStroke(oldStroke);
	}
	
}