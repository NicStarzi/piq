package edu.udo.piq.basicdesign;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PButton;
import edu.udo.piq.util.PCompUtil;

public class BasicPButtonDesign implements PDesign {
	
	protected PColor backgroundColorPressed = PColor.WHITE;
	protected PColor backgroundColorUnpressed = PColor.GREY75;
	
	public void setBackgroundColorPressed(PColor color) {
		backgroundColorPressed = color;
	}
	
	public PColor getBackgroundColorPressed() {
		return backgroundColorPressed;
	}
	
	public void setBackgroundColorUnpressed(PColor color) {
		backgroundColorUnpressed = color;
	}
	
	public PColor getBackgroundColorUnpressed() {
		return backgroundColorUnpressed;
	}
	
	public PSize getPreferredSize(PComponent component)
			throws NullPointerException, IllegalArgumentException {
		return PCompUtil.getPreferredSizeOf(component);
	}
	
	public void render(PRenderer renderer, PComponent component)
			throws NullPointerException, IllegalArgumentException {
		
		PButton btn = (PButton) component;
		PBounds bnds = btn.getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		if (btn.isPressed()) {
			renderer.setColor(PColor.GREY25);
			renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
			renderer.setColor(getBackgroundColorPressed());
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(PColor.BLACK);
			renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
			renderer.setColor(PColor.WHITE);
			renderer.drawQuad(x + 0, y + 0, fx - 1, fy - 1);
			renderer.setColor(getBackgroundColorUnpressed());
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		}
	}
	
	public boolean isOpaque(PComponent component) {
		return true;
	}
	
}