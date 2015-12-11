package edu.udo.piq.lwjgl3;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.lwjgl3.Lwjgl3PRenderer.RenderOp;

public class RenderRectangle implements RenderOp {
	
	private final float r, g, b, a;
	private final float x, y, fx, fy;
	
	public RenderRectangle(
		float r, float g, float b, float a, 
		float x, float y, float fx, float fy 
	) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.x = x;
		this.y = y;
		this.fx = fx;
		this.fy = fy;
	}
	
	public void perform() {
		System.out.println("x="+x+", y="+y+", fx="+fx+", fy="+fy);
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glColor4f(r, g, b, a);
		
		GL11.glVertex2f(x , y );
		GL11.glVertex2f(x , fy);
		GL11.glVertex2f(fx, fy);
		GL11.glVertex2f(fx, fy);
		GL11.glVertex2f(fx, y );
		GL11.glVertex2f(x , y );
		
		GL11.glEnd();
	}
	
}