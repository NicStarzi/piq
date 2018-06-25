package edu.udo.piq.swing;

import java.awt.Graphics2D;

public interface SwingRenderFont {
	
	public default void renderTo(Graphics2D graphics, char[] text, float x, float y) {
		renderTo(graphics, text, 0, text.length, x, y);
	}
	
	public void renderTo(Graphics2D graphics, char[] text, int from, int length, float x, float y);
	
	public void renderTo(Graphics2D graphics, String text, float x, float y);
	
}