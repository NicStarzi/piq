package edu.udo.piq.lwjgl3;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.lwjgl3.Lwjgl3PRenderer.RenderOp;

public class RenderEllipse implements RenderOp {
	
	private final float r, g, b, a;
	private final float[] xCoords, yCoords;
	
	public RenderEllipse(
		float r, float g, float b, float a, 
		float x, float y, float w, float h
	) {
		/*
float[] a = new float[3*361]; // 3-coordinates and 361 angles
for (int i=0; i<=360; i+=3) {
    a[i+0] = x+(float)cos(i*PI/180)*rx; // X
    a[i+1] = y+(float)sin(i*PI/180)*ry; // Y
    a[i+2] = z;                         // Z
}
		 */
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		
		xCoords = new float[361];
		yCoords = new float[361];
		for (int i = 0; i <= 360; i += 3) {
			float xi = x + (float) Math.cos(i * Math.PI / 180) * w;
			float yi = y + (float) Math.sin(i * Math.PI / 180) * h;
			xCoords[i] = xi;
			yCoords[i] = yi;
		}
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