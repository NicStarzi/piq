package edu.udo.piq.implementation.swing;

import javax.swing.JPanel;

import edu.udo.piq.PBounds;
import edu.udo.piq.tools.AbstractPBounds;

public class JPanelPBounds extends AbstractPBounds implements PBounds {
	
	protected final JPanel panel;
	
	public JPanelPBounds(JPanel panel) {
		this.panel = panel;
	}
	
	public int getX() {
		return 0;
	}
	
	public int getY() {
		return 0;
	}
	
	public int getWidth() {
		return panel.getWidth();
	}
	
	public int getHeight() {
		return panel.getHeight();
	}
}