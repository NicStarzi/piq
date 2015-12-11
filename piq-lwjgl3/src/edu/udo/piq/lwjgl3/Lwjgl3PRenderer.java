package edu.udo.piq.lwjgl3;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;
import edu.udo.piq.PRenderer;
import edu.udo.piq.util.ThrowException;

public class Lwjgl3PRenderer implements PRenderer {
	
	private final Lwjgl3RenderMode RENDER_MODE_FILL = new Lwjgl3RenderMode(this);
	private final Lwjgl3RenderMode RENDER_MODE_OUTLINE = new Lwjgl3RenderMode(this);
	private final Lwjgl3RenderMode RENDER_MODE_DASHED = new Lwjgl3RenderMode(this);
	
	protected final List<RenderOp> renderList = new ArrayList<>();
	protected Lwjgl3RenderMode mode = getRenderModeFill();
	protected float currentColorR;
	protected float currentColorG;
	protected float currentColorB;
	protected float currentColorA;
	
	public void setRenderMode(PRenderMode mode) {
		this.mode = ThrowException.ifTypeCastFails(mode, 
				Lwjgl3RenderMode.class, 
				"mode.getClass() != Lwjgl3RenderMode.class");
	}
	
	public Lwjgl3RenderMode getActiveRenderMode() {
		return mode;
	}
	
	public Lwjgl3RenderMode getRenderModeFill() {
		return RENDER_MODE_FILL;
	}
	
	public Lwjgl3RenderMode getRenderModeOutline() {
		return RENDER_MODE_OUTLINE;
	}
	
	public Lwjgl3RenderMode getRenderModeOutlineDashed() {
		return RENDER_MODE_DASHED;
	}
	
	public void startRendering() {
		renderList.clear();
	}
	
	public void endRendering() {
	}
	
	public void renderAll() {
		for (RenderOp op : renderList) {
			op.perform();
		}
	}
	
	public void setClipBounds(int x, int y, int width, int height) {
		renderList.add(mode.setClipBounds(x, y, width, height));
	}
	
	public void setColor1(double r, double g, double b, double a) {
		currentColorR = (float) r;
		currentColorG = (float) g;
		currentColorB = (float) b;
		currentColorA = (float) a;
	}
	
	public void drawImage(PImageResource imgRes, 
			int u, int v, int fu, int fv,
			float x, float y, float fx, float fy) 
	{
		renderList.add(mode.drawImage(imgRes, u, v, fu, fv, x, y, fx, fy));
	}
	
	public void drawLine(
			float x1, float y1, 
			float x2, float y2, 
			float lineWidth) 
	{
		renderList.add(mode.drawLine(x1, y1, x2, y2, lineWidth));
	}
	
	public void drawPolygon(float[] xCoords, float[] yCoords) {
		renderList.add(mode.drawPolygon(xCoords, yCoords));
	}
	
	public void drawTriangle(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3) 
	{
		renderList.add(mode.drawTriangle(x1, y1, x2, y2, x3, y3));
	}
	
	public void drawQuad(
			float x, float y, 
			float fx, float fy) 
	{
		renderList.add(mode.drawQuad(x, y, fx, fy));
	}
	
	public void drawQuad(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3, 
			float x4, float y4) 
	{
		renderList.add(mode.drawQuad(x1, y1, x2, y2, x3, y3, x4, y4));
	}
	
	public void drawString(PFontResource font, String text, float x, float y) {
		Lwjgl3PFont lwjglFont = (Lwjgl3PFont) font;
		renderList.add(mode.drawString(lwjglFont, text, x, y));
	}
	
	public void drawEllipse(int x, int y, int width, int height) {
		renderList.add(mode.drawEllipse(x, y, width, height));
	}
	
	protected static interface RenderOp {
		public void perform();
	}
}