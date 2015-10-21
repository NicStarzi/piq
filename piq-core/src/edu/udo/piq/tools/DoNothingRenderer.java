package edu.udo.piq.tools;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;
import edu.udo.piq.PRenderer;

public class DoNothingRenderer implements PRenderer {
	
	private static PRenderMode RM_FILL = new PRenderMode() {};
	private static PRenderMode RM_OUTLINE;
	private static PRenderMode RM_OUTLINE_DASHED;
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
		if (RM_OUTLINE == null) {
			RM_OUTLINE = new PRenderMode() {};
		}
		return RM_OUTLINE;
	}
	
	public PRenderMode getRenderModeOutlineDashed() {
		if (RM_OUTLINE_DASHED == null) {
			RM_OUTLINE_DASHED = new PRenderMode() {};
		}
		return RM_OUTLINE_DASHED;
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
	
	public void drawString(PFontResource font, String text, float x, float y) {
	}
	
	public void drawEllipse(int x, int y, int width, int height) {
	}
	
}