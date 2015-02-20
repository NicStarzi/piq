package edu.udo.piq.designs.coolblue;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PButton;
import static edu.udo.piq.designs.coolblue.CoolBluePDesignSheet.*;

public class CoolBluePButtonDesign implements PDesign {
	
	public PSize getPreferredSize(PComponent component) {
		return component.getLayout().getPreferredSize();
	}
	
	public void render(PRenderer renderer, PComponent component) {
		PButton btn = (PButton) component;
		PBounds bnds = btn.getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		if (btn.isPressed()) {
			renderer.setColor(DARK_BLUE);
			renderer.strokeQuad(x, y, fx, fy);
			renderer.setColor(MEDIUM_BLUE);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(DARK_BLUE);
			renderer.strokeBottom(x, y, fx, fy, 2);
			renderer.strokeRight(x, y, fx, fy, 2);
			renderer.setColor(LIGHT_BLUE);
			renderer.strokeTop(x, y, fx - 1, fy);
			renderer.strokeLeft(x, y, fx, fy - 1);
			renderer.setColor(MEDIUM_BLUE);
			renderer.drawQuad(x + 1, y + 1, fx - 2, fy - 2);
		}
		if (btn.hasFocus()) {
			int innerX = x + 4;
			int innerY = y + 4;
			int innerFx = fx - 5;
			int innerFy = fy - 5;
			renderer.setColor(LIGHT_BLUE);
			renderer.strokeQuad(innerX, innerY, innerFx, innerFy);
		}
	}
	
	public boolean fillsAllPixels(PComponent component) {
		return true;
	}
	
}