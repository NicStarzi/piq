package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;

public class PExpandButton extends PCheckBox {
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.BLACK);
		if (isChecked()) {
			int x2 = x + bnds.getWidth() / 2;
			renderer.drawTriangle(x, y, x2, fy, fx, y);
		} else {
			int y3 = y + bnds.getHeight() / 2;
			renderer.drawTriangle(x, y, x, fy, fx, y3);
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}