package edu.udo.piq.tutorial;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.dnd.PDnDIndicator;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.DelegatePRenderer;

public class DnDCompPreview extends AbstractPComponent implements PDnDIndicator {
	
	private PComponent original;
	private boolean dropPossible;
	
	public DnDCompPreview(PComponent other) {
		if (other == null) {
			throw new IllegalArgumentException();
		}
		original = other;
	}
	
	public void setDropPossible(boolean value) {
		if (isDropPossible() == value) {
			return;
		}
		dropPossible = value;
	}
	
	public boolean isDropPossible() {
		return dropPossible;
	}
	
	public boolean isIgnoredByPicking() {
		return true;
	}
	
	public boolean isFocusable() {
		return false;
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds ownBnds = getBounds();
		PBounds origBnds = original.getBounds();
		int ox = ownBnds.getX() - origBnds.getX();
		int oy = ownBnds.getY() - origBnds.getY();
		
		DelegatePRenderer wrapRenderer = new DelegatePRenderer(renderer);
		wrapRenderer.setPositionOffsetX(ox);
		wrapRenderer.setPositionOffsetY(oy);
		original.defaultRender(wrapRenderer);
		
		for (PComponent child : original.getChildren()) {
//			PBounds clipBnds = ownBnds.getIntersection(child.getBounds());
//			if (clipBnds != null) {
//				wrapRenderer.setClipBounds(clipBnds);
//			}
			child.defaultRender(wrapRenderer);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		return original.getDefaultPreferredSize();
	}
	
	public boolean defaultFillsAllPixels() {
		return original.defaultFillsAllPixels();
	}
	
}