package edu.udo.piq.lwjgl3;

import org.lwjgl.opengl.GL11;

import edu.udo.piq.lwjgl3.Lwjgl3PRenderer.RenderOp;

public class RenderScissor implements RenderOp {
	
	private final int x, y, w, h;
	
	public RenderScissor(int x, int y, int w, int h 
	) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}
	
	public void perform() {
		GL11.glScissor(x, y, w, h);
	}
	
}