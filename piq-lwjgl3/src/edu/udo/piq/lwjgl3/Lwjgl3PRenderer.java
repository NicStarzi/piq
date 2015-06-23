package edu.udo.piq.lwjgl3;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderer;

public class Lwjgl3PRenderer implements PRenderer {
	
	private final List<RenderOp> renderList = new ArrayList<>();
	private float currentColorR;
	private float currentColorG;
	private float currentColorB;
	private float currentColorA;
	
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
		renderList.add(new RenderScissor(x, y, width, height));
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
		renderList.add(new RenderImage(
				(Lwjgl3PImage) imgRes, 
				u, v, fu, fv, 
				x, y, fx, fy));
	}
	
	public void drawLine(
			float x1, float y1, 
			float x2, float y2, 
			float lineWidth) 
	{
		float r = currentColorR;
		float g = currentColorG;
		float b = currentColorB;
		float a = currentColorA;
		renderList.add(new RenderLine(
				r, g, b, a, 
				x1, x2, y1, y2, 
				lineWidth));
	}
	
	public void drawPolygon(float[] xCoords, float[] yCoords) {
		float r = currentColorR;
		float g = currentColorG;
		float b = currentColorB;
		float a = currentColorA;
		renderList.add(new RenderPolygon(r, g, b, a, xCoords, yCoords));
	}
	
	public void drawTriangle(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3) 
	{
		float r = currentColorR;
		float g = currentColorG;
		float b = currentColorB;
		float a = currentColorA;
		renderList.add(new RenderTriangle(
				r, g, b, a, 
				x1, x2, x3, 
				y1, y2, y3));
	}
	
	public void drawQuad(
			float x, float y, 
			float fx, float fy) 
	{
		float r = currentColorR;
		float g = currentColorG;
		float b = currentColorB;
		float a = currentColorA;
		renderList.add(new RenderRectangle(
				r, g, b, a, 
				x, y, fx, fy));
	}
	
	public void drawQuad(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3, 
			float x4, float y4) 
	{
		float r = currentColorR;
		float g = currentColorG;
		float b = currentColorB;
		float a = currentColorA;
		renderList.add(new RenderQuad(
				r, g, b, a, 
				x1, x2, x3, x4, 
				y1, y2, y3, y4));
	}
	
	public void drawString(PFontResource font, String text, float x, float y) {
		Lwjgl3PFont lwjglFont = (Lwjgl3PFont) font;
		
		float r = currentColorR;
		float g = currentColorG;
		float b = currentColorB;
		float a = currentColorA;
		renderList.add(new RenderText(
				lwjglFont, text, x, y, 
				r, g, b, a));
	}
	
	public void drawEllipse(int x, int y, int width, int height) {
		float r = currentColorR;
		float g = currentColorG;
		float b = currentColorB;
		float a = currentColorA;
		renderList.add(new RenderEllipse(r, g, b, a, 
				x, y, width, height));
	}
	
	protected static interface RenderOp {
		public void perform();
	}
}