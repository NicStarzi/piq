package edu.udo.piq.lwjgl3;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.lwjgl3.Lwjgl3PRenderer.RenderOp;

public class RenderQuad implements RenderOp {
	
	private final float r, g, b, a;
	private final float x1, x2, x3, x4;
	private final float y1, y2, y3, y4;
	
	public RenderQuad(
		float r, float g, float b, float a, 
		float x1, float x2, float x3, float x4, 
		float y1, float y2, float y3, float y4  
	) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.x1 = x1;
		this.x2 = x2;
		this.x3 = x3;
		this.x4 = x4;
		this.y1 = y1;
		this.y2 = y2;
		this.y3 = y3;
		this.y4 = y4;
	}
	
	public void perform() {
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glColor4f(r, g, b, a);
		
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y2);
		GL11.glVertex2f(x3, y3);
		GL11.glVertex2f(x3, y3);
		GL11.glVertex2f(x4, y4);
		GL11.glVertex2f(x1, y1);
		
		GL11.glEnd();
	}
	
}