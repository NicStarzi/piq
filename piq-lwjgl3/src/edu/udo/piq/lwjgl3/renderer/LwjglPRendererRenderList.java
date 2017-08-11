package edu.udo.piq.lwjgl3.renderer;

import java.util.ArrayList;
import java.util.List;

public class LwjglPRendererRenderList extends LwjglPRendererBase {
	
	protected final List<Runnable> renderList = new ArrayList<>(80);
	protected int curSciX, curSciY, curSciW, curSciH;
	protected int drawCallsSinceLastScissor;
	
	@Override
	public void beginReRender() {
		super.beginReRender();
		renderList.clear();
		curSciX = curSciY = curSciW = curSciH = -1;
		drawCallsSinceLastScissor = 0;
	}
	
	@Override
	public void endReRender() {
	}
	
	@Override
	public void renderAll() {
		setGlScissor(0, 0, viewportW, viewportH);
		renderList.forEach(op -> op.run());
		System.out.println(renderList);
	}
	
	protected static interface Scissor extends Runnable {}
	
	@Override
	public void setClipBounds(int x, int y, int width, int height) {
		setGlScissor(x, y, width, height);
//		System.out.println("LwjglPRenderer.setClipBounds()");
		if (x != curSciX || y != curSciY || width != curSciW || height != curSciH) {
			if (drawCallsSinceLastScissor == 0) {
				boolean notAdded = true;
				for (int i = renderList.size() - 1; i >= 0; i--) {
					if (renderList.get(i) instanceof Scissor) {
						renderList.set(i, (Scissor) () -> setGlScissor(x, y, width, height));
						notAdded = false;
						break;
					}
				}
				if (notAdded) {
					renderList.add((Scissor) () -> setGlScissor(x, y, width, height));
				}
			} else {
				renderList.add((Scissor) () -> setGlScissor(x, y, width, height));
			}
			drawCallsSinceLastScissor = 0;
			curSciX = x;
			curSciY = y;
			curSciW = width;
			curSciH = height;
		}
	}
	
	@Override
	public void drawLine(float x1, float y1, float x2, float y2, float lineWidth) {
//		System.out.println("LwjglPRenderer.drawLine()");
		renderList.add(curMode.drawLine(x1, y1, x2, y2, lineWidth));
		drawCallsSinceLastScissor++;
	}
	
	@Override
	public void drawTriangle(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3)
	{
//		System.out.println("LwjglPRenderer.drawTriangle()");
		renderList.add(curMode.drawTriangle(x1, y1, x2, y2, x3, y3));
		drawCallsSinceLastScissor++;
	}
	
	@Override
	public void drawQuad(
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4)
	{
//		System.out.println("LwjglPRenderer.drawQuad()");
		renderList.add(curMode.drawQuad(x1, y1, x2, y2, x3, y3, x4, y4));
		drawCallsSinceLastScissor++;
	}
	
	@Override
	public void drawQuad(
			float x, float y,
			float fx, float fy)
	{
//		System.out.println("LwjglPRenderer.drawQuad()");
		renderList.add(curMode.drawQuad(x, y, fx, fy));
		drawCallsSinceLastScissor++;
	}
	
	@Override
	public void drawPolygon(float[] xCoords, float[] yCoords) {
//		System.out.println("LwjglPRenderer.drawPolygon()");
		renderList.add(curMode.drawPolygon(xCoords, yCoords));
		drawCallsSinceLastScissor++;
	}
	
	@Override
	public void drawEllipse(int x, int y, int width, int height) {
//		System.out.println("LwjglPRenderer.drawEllipse()");
		renderList.add(curMode.drawEllipse(x, y, width, height));
		drawCallsSinceLastScissor++;
	}
	
}