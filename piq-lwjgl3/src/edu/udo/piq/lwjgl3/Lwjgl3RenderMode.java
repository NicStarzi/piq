package edu.udo.piq.lwjgl3;

import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderMode;
import edu.udo.piq.lwjgl3.Lwjgl3PRenderer.RenderOp;

public class Lwjgl3RenderMode implements PRenderMode {
	
	protected Lwjgl3PRenderer renderer;
	
	public Lwjgl3RenderMode(Lwjgl3PRenderer renderer) {
		this.renderer = renderer;
	}
	
	public RenderOp setClipBounds(int x, int y, int width, int height) {
		return new RenderScissor(x, y, width, height);
	}
	
	public RenderOp drawImage(PImageResource imgRes, 
			int u, int v, int fu, int fv,
			float x, float y, float fx, float fy) 
	{
		return new RenderImage(
				(Lwjgl3PImage) imgRes, 
				u, v, fu, fv, 
				x, y, fx, fy);
	}
	
	public RenderOp drawLine(
			float x1, float y1, 
			float x2, float y2, 
			float lineWidth) 
	{
		float r = renderer.currentColorR;
		float g = renderer.currentColorG;
		float b = renderer.currentColorB;
		float a = renderer.currentColorA;
		return new RenderLine(
				r, g, b, a, 
				x1, x2, y1, y2, 
				lineWidth);
	}
	
	public RenderOp drawPolygon(float[] xCoords, float[] yCoords) {
		float r = renderer.currentColorR;
		float g = renderer.currentColorG;
		float b = renderer.currentColorB;
		float a = renderer.currentColorA;
		return new RenderPolygon(r, g, b, a, xCoords, yCoords);
	}
	
	public RenderOp drawTriangle(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3) 
	{
		float r = renderer.currentColorR;
		float g = renderer.currentColorG;
		float b = renderer.currentColorB;
		float a = renderer.currentColorA;
		return new RenderTriangle(
				r, g, b, a, 
				x1, x2, x3, 
				y1, y2, y3);
	}
	
	public RenderOp drawQuad(
			float x, float y, 
			float fx, float fy) 
	{
		float r = renderer.currentColorR;
		float g = renderer.currentColorG;
		float b = renderer.currentColorB;
		float a = renderer.currentColorA;
		return new RenderRectangle(
				r, g, b, a, 
				x, y, fx, fy);
	}
	
	public RenderOp drawQuad(
			float x1, float y1, 
			float x2, float y2, 
			float x3, float y3, 
			float x4, float y4) 
	{
		float r = renderer.currentColorR;
		float g = renderer.currentColorG;
		float b = renderer.currentColorB;
		float a = renderer.currentColorA;
		return new RenderQuad(
				r, g, b, a, 
				x1, x2, x3, x4, 
				y1, y2, y3, y4);
	}
	
	public RenderOp drawString(PFontResource font, String text, float x, float y) {
		Lwjgl3PFont lwjglFont = (Lwjgl3PFont) font;
		
		float r = renderer.currentColorR;
		float g = renderer.currentColorG;
		float b = renderer.currentColorB;
		float a = renderer.currentColorA;
		return new RenderText(
				lwjglFont, text, x, y, 
				r, g, b, a);
	}
	
	public RenderOp drawEllipse(int x, int y, int width, int height) {
		float r = renderer.currentColorR;
		float g = renderer.currentColorG;
		float b = renderer.currentColorB;
		float a = renderer.currentColorA;
		return new RenderEllipse(r, g, b, a, 
				x, y, width, height);
	}
	
}