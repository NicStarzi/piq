package edu.udo.piq.lwjgl3;

import edu.udo.piq.PFontResource;
import edu.udo.piq.lwjgl3.renderer.LwjglPRendererBase;

public interface GlfwFontResource extends PFontResource {
	
	public void drawChars(LwjglPRendererBase renderer, char[] charArr, int from, int length, float x, float y);
	
	public void drawString(LwjglPRendererBase renderer, String text, float x, float y);
	
}