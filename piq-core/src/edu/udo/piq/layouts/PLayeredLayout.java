package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PLayoutDesign;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.ImmutablePInsets;

public class PLayeredLayout extends AbstractMapPLayout {
	
	protected PInsets insets = new ImmutablePInsets(4);
	protected boolean growContent = false;
	protected Object shownLayerKey = null;
	
	public PLayeredLayout(PComponent component) {
		super(component);
	}
	
	protected void onChildAdded(PComponent component, Object constraint) {
		if (shownLayerKey == null) {
			shownLayerKey = constraint;
		}
	}
	
	public void setShownLayerKey(Object layerKey) {
		if (shownLayerKey != layerKey || (layerKey != null 
				&& !layerKey.equals(shownLayerKey))) 
		{
			shownLayerKey = layerKey;
			invalidate();
		}
	}
	
	public Object getShownLayerKey() {
		return shownLayerKey;
	}
	
	public void setInsets(PInsets insets) {
		this.insets = insets;
		invalidate();
	}
	
	public PInsets getInsets() {
		PLayoutDesign design = getDesign();
		if (design == null) {
			return insets;
		}
		Object maybeInsets = getDesign().getAttribute(ATTRIBUTE_KEY_INSETS);
		if (maybeInsets != null && maybeInsets instanceof PInsets) {
			return (PInsets) maybeInsets;
		}
		return insets;
	}
	
	public void setGrowContent(boolean isGrowContent) {
		if (growContent != isGrowContent) {
			growContent = isGrowContent;
			invalidate();
		}
	}
	
	public boolean isGrowContent() {
		return growContent;
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null;
	}
	
	protected void onInvalidated() {
		int prefW = 0;
		int prefH = 0;
		for (PComponent child : getChildren()) {
			PSize childSize = getPreferredSizeOf(child);
			int childW = childSize.getWidth();
			int childH = childSize.getHeight();
			if (prefW < childW) {
				prefW = childW;
			}
			if (prefH < childH) {
				prefH = childH;
			}
		}
		prefW += getInsets().getHorizontal();
		prefH += getInsets().getVertical();
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
	}
	
	protected void layOutInternal() {
		if (getShownLayerKey() == null) {
			return;
		}
		PComponent content = getChildForConstraint(getShownLayerKey());
		if (content != null) {
			PBounds ob = getOwner().getBounds();
			PInsets insets = getInsets();
			
			int x = ob.getX() + insets.getFromLeft();
			int y = ob.getY() + insets.getFromTop();
			int w = (ob.getFinalX() - insets.getFromRight()) - x;
			int h = (ob.getFinalY() - insets.getFromBottom()) - y;
			
			if (isGrowContent()) {
				setChildBounds(content, x, y, w, h);
			} else {
				PSize prefSize = getPreferredSizeOf(content);
				int prefW = prefSize.getWidth();
				int prefH = prefSize.getHeight();
				
				int compX;
				int compY;
				int compW;
				int compH;
				if (prefW > w) {
					compX = x;
					compW = w;
				} else {
					compX = x + w / 2 - prefW / 2;
					compW = prefW;
				}
				if (prefH > h) {
					compY = y;
					compH = h;
				} else {
					compY = y + h / 2 - prefH / 2;
					compH = prefH;
				}
				setChildBounds(content, compX, compY, compW, compH);
			}
		}
	}
	
}