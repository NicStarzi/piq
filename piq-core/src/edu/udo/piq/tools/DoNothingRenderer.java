package edu.udo.piq.tools;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageRenderer;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;

public class DoNothingRenderer implements PImageRenderer {
	
	private static PRenderMode RM_FILL = new PRenderMode() {};
	private PRenderMode renderMode = getRenderModeFill();
	
	public void setRenderMode(PRenderMode mode) {
		renderMode = mode;
	}
	
	public PRenderMode getActiveRenderMode() {
		return renderMode;
	}
	
	public PRenderMode getRenderModeFill() {
		return RM_FILL;
	}
	
	public PRenderMode getRenderModeOutline() {
		return RM_FILL;
	}
	
	public PRenderMode getRenderModeOutlineDashed() {
		return RM_FILL;
	}
	
	public PRenderMode getRenderModeXOR() {
		return RM_FILL;
	}
	
	public void setClipBounds(int x, int y, int width, int height) {
	}
	
	public void setColor1(double r, double g, double b, double a) {
	}
	
	public void drawImage(PImageResource imgRes, int u, int v, int fu, int fv,
			float x, float y, float fx, float fy) {
	}
	
	public void drawLine(float x1, float y1, float x2, float y2, float lineWidth) {
	}
	
	public void drawPolygon(float[] xCoords, float[] yCoords) {
	}
	
	public boolean isFontSupported(PFontResource font) {
		return true;
	}
	
	public void drawString(PFontResource font, String text, float x, float y) {
	}
	
	public void drawEllipse(int x, int y, int width, int height) {
	}
	
	public void dispose() {
	}
	
}