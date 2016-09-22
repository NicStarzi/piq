package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.util.ThrowException;

public class PFloatingPanel extends AbstractPFloatingPanel {
	
	protected int showX;
	protected int showY;
	protected PComponent target;
	
	public PFloatingPanel() {
		super();
		setLayout(new PCentricLayout(this));
	}
	
	public void setLocation(int posX, int posY) {
		setLocation(getTargetComponent(), posX, posY);
	}
	
	public void setLocation(PComponent component, int posX, int posY) {
		target = component;
		showX = posX;
		showY = posY;
	}
	
	public void setLocationByOffset(PComponent component, int offsetX, int offsetY) {
		ThrowException.ifNull(component, "component == null");
		ThrowException.ifNull(component.getBounds(), "component.getBounds() == null");
		target = component;
		PBounds bounds = target.getBounds();
		showX = bounds.getX() + offsetX;
		showY = bounds.getY() + offsetY;
	}
	
	public PComponent getTargetComponent() {
		return target;
	}
	
	public int getOverlayPositionX() {
		return showX;
	}
	
	public int getOverlayPositionY() {
		return showY;
	}
	
	public PRootOverlay getOverlay() {
		PComponent target = getTargetComponent();
		if (target == null || target.getRoot() == null) {
			return null;
		}
		return target.getRoot().getOverlay();
	}
	
	public PFreeLayout getOverlayLayout() {
		return super.getOverlayLayout();
	}
	
	public void setShown(boolean value) {
		if (value) {
			if (!isShown()) {
				addToOverlay();
			}
		} else {
			if (isShown()) {
				removeFromOverlay();
			}
		}
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		renderer.setColor(PColor.BLACK);
		renderer.strokeQuad(x, y, fx, fy);
	}
	
}