package edu.udo.piq.tools;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageRenderer;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;

public class DoNothingRenderer implements PImageRenderer {
	
	private static PRenderMode RM_FILL = new PRenderMode() {};
	private PRenderMode renderMode = getRenderModeFill();
	
	@Override
	public void setRenderMode(PRenderMode mode) {
		renderMode = mode;
	}
	
	@Override
	public PRenderMode getActiveRenderMode() {
		return renderMode;
	}
	
	@Override
	public PRenderMode getRenderModeFill() {
		return RM_FILL;
	}
	
	@Override
	public PRenderMode getRenderModeOutline() {
		return RM_FILL;
	}
	
	@Override
	public PRenderMode getRenderModeOutlineDashed() {
		return RM_FILL;
	}
	
	@Override
	public PRenderMode getRenderModeXOR() {
		return RM_FILL;
	}
	
	@Override
	public void setClipBounds(int x, int y, int width, int height) {}
	
	@Override
	public void intersectClipBounds(int x, int y, int width, int height) {}
	
	@Override
	public void setColor1(float r, float g, float b, float a) {}
	
	@Override
	public void drawImage(PImageResource imgRes, int u, int v, int fu, int fv,
			float x, float y, float fx, float fy) {}
	
	@Override
	public void drawLine(float x1, float y1, float x2, float y2, float lineWidth) {}
	
	@Override
	public void drawPolygon(float[] xCoords, float[] yCoords) {}
	
	@Override
	public boolean isFontSupported(PFontResource font) {
		return true;
	}
	
	@Override
	public void drawString(PFontResource font, String text, float x, float y) {}
	
	@Override
	public void drawArc(float x, float y, float width, float height, float angleFrom, float angleArc) {}
	
	@Override
	public void dispose() {}
	
}