package edu.udo.piq.lwjgl3;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.lwjgl3.Lwjgl3PRenderer.RenderOp;

public class RenderImage implements RenderOp {
	
	private final Lwjgl3PImage texture;
	private final float u, v, fu, fv;
	private final float x, y, fx, fy;
	
	public RenderImage(Lwjgl3PImage image, 
		float u, float v, float fu, float fv, 
		float x, float y, float fx, float fy 
	) {
		texture = image;
		this.u = u;
		this.v = v;
		this.fu = fu;
		this.fv = fv;
		this.x = x;
		this.y = y;
		this.fx = fx;
		this.fy = fy;
	}
	
	public void perform() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		texture.bind();
		
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glColor4f(1, 1, 1, 1);
		
		GL11.glTexCoord2f(	u , v );
		GL11.glVertex2f(	x , y );
		GL11.glTexCoord2f(	u , fv);
		GL11.glVertex2f(	x , fy);
		GL11.glTexCoord2f(	fu, fv);
		GL11.glVertex2f(	fx, fy);
		GL11.glTexCoord2f(	fu, fv);
		GL11.glVertex2f(	fx, fy);
		GL11.glTexCoord2f(	fu, v );
		GL11.glVertex2f(	fx, y );
		GL11.glTexCoord2f(	u , v );
		GL11.glVertex2f(	x , y );
		
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
}