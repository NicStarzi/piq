package edu.udo.piq.lwjgl3.renderer;

import org.lwjgl.opengl.GL11;

public class LwjglRmXor extends LwjglRmOutline implements LwjglRenderMode {
	
	public LwjglRmXor(LwjglPRendererBase renderer) {
		super(renderer);
	}
	
	@Override
	public void drawLineImmediate(
			float x1, float y1,
			float x2, float y2,
			float lineWidth)
	{
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_XOR);
		super.drawLineImmediate(x1, y1, x2, y2, lineWidth);
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
	}
	
	@Override
	public void drawPolygonImmediate(float[] xCoords, float[] yCoords) {
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_XOR);
		super.drawPolygonImmediate(xCoords, yCoords);
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
	}
	
	@Override
	public void drawTriangleImmediate(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3)
	{
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_XOR);
		super.drawTriangleImmediate(x1, y1, x2, y2, x3, y3);
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
	}
	
	@Override
	public void drawQuadImmediate(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4)
	{
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_XOR);
		super.drawQuadImmediate(x1, y1, x2, y2, x3, y3, x4, y4);
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
	}
	
	@Override
	public void drawEllipseImmediate(int x, int y, int width, int height) {
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_XOR);
		super.drawEllipseImmediate(x, y, width, height);
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
	}
	
}