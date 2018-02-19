package edu.udo.piq.tools;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageRenderer;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;

public class DelegatePRenderer implements PImageRenderer {
	
	private final PRenderer delegate;
	private float ox, oy;
	private float facW, facH;
	
	public DelegatePRenderer(PRenderer delegateRenderer) {
		if (delegateRenderer == null) {
			throw new IllegalArgumentException();
		}
		delegate = delegateRenderer;
	}
	
	public void setWidthFactor(float value) {
		facW = value;
	}
	
	public float getWidthFactor() {
		return facW;
	}
	
	public void setHeightFactor(float value) {
		facH = value;
	}
	
	public float getHeightFactor() {
		return facH;
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
	
	@Override
	public void setClipBounds(PBounds bounds) {
//		float ox = getPositionOffsetX();
//		float oy = getPositionOffsetY();
//		if (ox != 0 || oy != 0) {
//			int bndX = (int) (bounds.getX() + ox);
//			int bndY = (int) (bounds.getY() + oy);
//			int bndW = bounds.getWidth();
//			int bndH = bounds.getHeight();
//			bounds = new ImmutablePBounds(bndX, bndY, bndW, bndH);
//		}
		delegate.setClipBounds(bounds);
	}
	
	@Override
	public void setClipBounds(int x, int y, int width, int height) {
//		x += getPositionOffsetX();
//		y += getPositionOffsetY();
		delegate.setClipBounds(x, y, width, height);
	}
	
	@Override
	public void intersectClipBounds(int x, int y, int width, int height) {
		delegate.intersectClipBounds(x, y, width, height);
	}
	
	@Override
	public void setColor(PColor color) {
		delegate.setColor(color);
	}
	
	@Override
	public void setColor255(int r, int g, int b, int a) {
		delegate.setColor255(r, g, b, a);
	}
	
	@Override
	public void setColor1(float r, float g, float b, float a) {
		delegate.setColor1(r, g, b, a);
	}
	
	@Override
	public void drawImage(PImageResource imgRes, float x, float y, float fx, float fy) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		fx += ox;
		y += oy;
		fy += oy;
		delegate.drawImage(imgRes, x, y, fx, fy);
	}
	
	@Override
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
	
	@Override
	public void drawLine(float x1, float y1, float x2, float y2, float lineWidth) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x1 += ox;
		x2 += ox;
		y1 += oy;
		y2 += oy;
		delegate.drawLine(x1, y1, x2, y2, lineWidth);
	}
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
	public void drawPolygon(float[] xCoords, float[] yCoords) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		for (int i = 0; i < xCoords.length; i++) {
			xCoords[i] += ox;
			yCoords[i] += oy;
		}
		delegate.drawPolygon(xCoords, yCoords);
	}
	
	@Override
	public boolean isFontSupported(PFontResource font) {
		return delegate.isFontSupported(font);
	}
	
	@Override
	public void drawString(PFontResource font, String text, float x, float y) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		y += oy;
		delegate.drawString(font, text, x, y);
	}
	
	@Override
	public void drawArc(float x, float y, float width, float height, float angleFrom, float angleArc) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		y += oy;
		delegate.drawEllipse(x, y, width, height);
	}
	
	@Override
	public void drawEllipse(float x, float y, float width, float height) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		y += oy;
		delegate.drawEllipse(x, y, width, height);
	}
	
	@Override
	public void drawRoundedRect(float x, float y, float fx, float fy, float arcW, float arcH) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		y += oy;
		fx += ox;
		fy += oy;
		delegate.drawRoundedRect(ox, oy, fx, fy, arcW, arcH);
	}
	
	@Override
	public void drawRoundedRect(float x, float y, float fx, float fy, PSize arcSize) {
		float ox = getPositionOffsetX();
		float oy = getPositionOffsetY();
		x += ox;
		y += oy;
		fx += ox;
		fy += oy;
		delegate.drawRoundedRect(ox, oy, fx, fy, arcSize.getWidth(), arcSize.getHeight());
	}
	
	@Override
	public void setRenderMode(PRenderMode mode) {
		delegate.setRenderMode(mode);
	}
	
	@Override
	public PRenderMode getActiveRenderMode() {
		return delegate.getActiveRenderMode();
	}
	
	@Override
	public PRenderMode getRenderModeFill() {
		return delegate.getRenderModeFill();
	}
	
	@Override
	public PRenderMode getRenderModeOutline() {
		return delegate.getRenderModeOutline();
	}
	
	@Override
	public PRenderMode getRenderModeOutlineDashed() {
		return delegate.getRenderModeOutlineDashed();
	}
	
	@Override
	public PRenderMode getRenderModeXOR() {
		return delegate.getRenderModeXOR();
	}
	
}