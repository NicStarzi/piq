package edu.udo.piq.lwjgl3;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.lwjgl3.Lwjgl3PRenderer.RenderOp;

public class RenderLine implements RenderOp {
	
	private final float r, g, b, a;
	private final float x1, x2;
	private final float y1, y2;
	private final float width;
	
	public RenderLine(
		float r, float g, float b, float a, 
		float x1, float x2, 
		float y1, float y2, 
		float lineWidth
	) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.width = lineWidth;
	}
	
	public void perform() {
		GL11.glLineWidth(width);
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4f(r, g, b, a);
		
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y2);
		
		GL11.glEnd();
	}
	
}