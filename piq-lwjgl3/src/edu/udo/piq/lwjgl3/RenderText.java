package edu.udo.piq.lwjgl3;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.lwjgl3.Lwjgl3PRenderer.RenderOp;

public class RenderText implements RenderOp {
	
	private final Lwjgl3PFont font;
	private final float x, y;
	private final float r, g, b, a;
	
	public RenderText(Lwjgl3PFont font, String text, 
			float x, float y, 
			float r, float g, float b, float a) 
	{
		this.font = font;
		this.x = x;
		this.y = y;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void perform() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		font.bind();
		
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glColor4f(r, g, b, a);
		
//		GL11.glTexCoord2f(	u , v );
		GL11.glVertex2f(	x , y );
//		GL11.glTexCoord2f(	u , fv);
//		GL11.glVertex2f(	x , fy);
//		GL11.glTexCoord2f(	fu, fv);
//		GL11.glVertex2f(	fx, fy);
//		GL11.glTexCoord2f(	fu, fv);
//		GL11.glVertex2f(	fx, fy);
//		GL11.glTexCoord2f(	fu, v );
//		GL11.glVertex2f(	fx, y );
//		GL11.glTexCoord2f(	u , v );
//		GL11.glVertex2f(	x , y );
		
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
}