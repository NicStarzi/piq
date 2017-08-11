package edu.udo.piq.swing;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Window;

import edu.udo.piq.PBounds;
import edu.udo.piq.tools.AbstractPBounds;

public class JCompPBounds extends AbstractPBounds implements PBounds {
	
	protected final Component comp;
	protected final boolean isWindow;
	protected int insetsW;
	protected int insetsH;
	
	public JCompPBounds(Component component) {
		comp = component;
		isWindow = comp instanceof Window;
	}
	
	@Override
	public int getX() {
		return 0;
	}
	
	@Override
	public int getY() {
		return 0;
	}
	
	@Override
	public int getWidth() {
		int w = comp.getWidth();
		return w - insetsW;
	}
	
	@Override
	public int getHeight() {
		int h = comp.getHeight();
		return h - insetsH;
	}
	
	public void refreshInsets() {
		if (isWindow) {
			Insets insets = ((Window) comp).getInsets();
			insetsW = insets.left + insets.right;
			insetsH = insets.top + insets.bottom;
		}
	}
}