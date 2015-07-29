package edu.udo.piq.util;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;
import edu.udo.piq.PRenderer;

public class DelegatePRenderer implements PRenderer {
	
	private final PRenderer delegate;
	private float ox, oy;
	private float wFac, hFac;
	
	public DelegatePRenderer(PRenderer delegateRenderer) {
		if (delegateRenderer == null) {
			throw new IllegalArgumentException();
		}
		delegate = delegateRenderer;
	}
	
	public void setWidthFactor(float value) {
		wFac = value;
	}
	
	public float getWidthFactor() {
		return wFac;
	}
	
	public void setHeightFactor(float value) {
		hFac = value;
	}
	
	public float getHeightFactor() {
		return hFac;
	}
	
	public void setPositionOffsetX(float value) {
		ox = value;
	}
	
	public float getPositionOffsetX() {
		return ox;
	}
	
	public void setPositionOffsetY(float value) {
		oy = value;
	}
	
	public float getPositionOffsetY() {
		return oy;
	}
	
	public void setClipBounds(PBounds bounds) {
		delegate.setClipBounds(bounds);
	}
	
	public void setClipBounds(int x, int y, int width, int height) {
		delegate.setClipBounds(x, y, width, height);
	}
	
	public void setColor(PColor color) {
		delegate.setColor(color);
	}
	
	public void setColor255(int r, int g, int b, int a) {
		delegate.setColor255(r, g, b, a);
	}
	
	public void setColor1(double r, double g, double b, double a) {
		delegate.setColor1(r, g, b, a);
	}
	
	public void drawImage(PImageResource imgRes, float x, float y, float fx, float fy) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		fx += ox;
		y += oy;
		fy += oy;
		delegate.drawImage(imgRes, x, y, fx, fy);
	}
	
	public void drawImage(PImageResource imgRes, int u, int v, int fu, int fv,
			float x, float y, float fx, float fy) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		fx += ox;
		y += oy;
		fy += oy;
		delegate.drawImage(imgRes, u, v, fu, fv, x, y, fx, fy);
	}
	
	public void drawLine(float x1, float y1, float x2, float y2, float lineWidth) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x1 += ox;
		x2 += ox;
		y1 += oy;
		y2 += oy;
		delegate.drawLine(x1, y1, x2, y2, lineWidth);
	}
	
	public void drawTriangle(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3) 
	{
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x1 += ox;
		x2 += ox;
		x3 += ox;
		y1 += oy;
		y2 += oy;
		y3 += oy;
		delegate.drawTriangle(x1, y1, x2, y2, x3, y3);
	}
	
//	public void drawQuad(PBounds bounds) {
//		delegate.drawQuad(bounds);
//	}
	
	public void drawQuad(
			float x, float y, 
			float fx, float fy) 
	{
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		fx += ox;
		y += oy;
		fy += oy;
		delegate.drawQuad(x, y, fx, fy);
	}
	
	public void drawQuad(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3, 
			float x4, float y4) 
	{
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x1 += ox;
		x2 += ox;
		x3 += ox;
		x4 += ox;
		y1 += oy;
		y2 += oy;
		y3 += oy;
		y4 += oy;
		delegate.drawQuad(x1, y1, x2, y2, x3, y3, x4, y4);
	}
	
	public void drawPolygon(float[] xCoords, float[] yCoords) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		for (int i = 0; i < xCoords.length; i++) {
			xCoords[i] += ox;
			yCoords[i] += oy;
		}
		delegate.drawPolygon(xCoords, yCoords);
	}
	
	public void drawString(PFontResource font, String text, float x, float y) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		y += oy;
		delegate.drawString(font, text, x, y);
	}
	
	public void drawEllipse(int x, int y, int width, int height) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		y += oy;
		delegate.drawEllipse(x, y, width, height);
	}
	
	public void setRenderMode(PRenderMode mode) {
		delegate.setRenderMode(mode);
	}
	
	public PRenderMode getActiveRenderMode() {
		return delegate.getActiveRenderMode();
	}
	
	public PRenderMode getRenderModeFill() {
		return delegate.getRenderModeFill();
	}
	
	public PRenderMode getRenderModeOutline() {
		return delegate.getRenderModeOutline();
	}
	
	public PRenderMode getRenderModeOutlineDashed() {
		return delegate.getRenderModeOutlineDashed();
	}
	
}