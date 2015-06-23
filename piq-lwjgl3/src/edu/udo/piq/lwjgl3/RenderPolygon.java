package edu.udo.piq.lwjgl3;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.lwjgl3.Lwjgl3PRenderer.RenderOp;

public class RenderPolygon implements RenderOp {
	
	private final float r, g, b, a;
	private final float[] xCoords;
	private final float[] yCoords;
	
	public RenderPolygon(
		float r, float g, float b, float a, 
		float[] x, float[] y  
	) {
		if (x == null || y == null) {
			throw new IllegalArgumentException("x="+x+", y="+y);
		}
		if (x.length != y.length) {
			throw new IllegalArgumentException("x.length="+x.length+", y.length="+y.length);
		}
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.xCoords = x;
		this.yCoords = y;
	}
	
	public void perform() {
		GL11.glBegin(GL11.GL_POLYGON);
		GL11.glColor4f(r, g, b, a);
		
		int length = xCoords.length;
		for (int i = 0; i < length; i++) {
			GL11.glVertex2f(xCoords[i], yCoords[i]);
		}
		GL11.glEnd();
	}
	
}