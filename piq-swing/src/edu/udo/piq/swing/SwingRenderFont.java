package edu.udo.piq.swing;

import java.awt.Graphics2D;

public interface SwingRenderFont {
	
	public void renderTo(Graphics2D graphics, String text, float x, float y);
	
}