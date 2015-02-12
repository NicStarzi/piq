package edu.udo.piq.basicdesign;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.util.PCompUtil;

public class BasicPCheckBoxDesign implements PDesign {
	
	protected PColor borderColorChecked = PColor.BLACK;
	protected PColor borderColorUnchecked = PColor.BLACK;
	protected PColor backgroundColorChecked = PColor.WHITE;
	protected PColor backgroundColorUnchecked = PColor.WHITE;
	protected PColor markColor = PColor.BLACK;
	
	public PColor getBorderColorChecked() {
		return borderColorChecked;
	}
	
	public void setBorderColorChecked(PColor color) {
		borderColorChecked = color;
	}
	
	public PColor getBorderColorUnchecked() {
		return borderColorUnchecked;
	}
	
	public void setBorderColorUnchecked(PColor color) {
		borderColorUnchecked = color;
	}
	
	public PColor getBackgroundColorChecked() {
		return backgroundColorChecked;
	}
	
	public void setBackgroundColorChecked(PColor color) {
		backgroundColorChecked = color;
	}
	
	public PColor getBackgroundColorUnchecked() {
		return backgroundColorUnchecked;
	}
	
	public void setBackgroundColorUnchecked(PColor color) {
		backgroundColorUnchecked = color;
	}
	
	public PColor getMarkColor() {
		return markColor;
	}
	
	public void setMarkColor(PColor color) {
		markColor = color;
	}
	
	public PSize getPreferredSize(PComponent component)
			throws NullPointerException, IllegalArgumentException {
		return PCompUtil.getPreferredSizeOf(component);
	}
	
	public void render(PRenderer renderer, PComponent component)
			throws NullPointerException, IllegalArgumentException {
		
		PCheckBox box = (PCheckBox) component;
		PBounds bnds = box.getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		if (box.isChecked()) {
			renderer.setColor(getBorderColorChecked());
			renderer.drawQuad(x, y, fx, fy);
			renderer.setColor(getBackgroundColorChecked());
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
			
			int gapW = bnds.getWidth() / 4;
			int gapH = bnds.getHeight() / 4;
			
			renderer.setColor(getMarkColor());
			renderer.drawQuad(x + gapW, y + gapH, fx - gapW, fy - gapH);
		} else {
			renderer.setColor(getBorderColorUnchecked());
			renderer.drawQuad(x, y, fx, fy);
			renderer.setColor(getBackgroundColorUnchecked());
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		}
	}
	
	public boolean fillsAllPixels(PComponent component) {
		return true;
	}
	
}