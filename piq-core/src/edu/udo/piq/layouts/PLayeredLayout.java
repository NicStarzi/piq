package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PLayeredLayout extends AbstractMapPLayout {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	public static final AlignmentX DEFAULT_ALIGNMENT_X = AlignmentX.CENTER;
	public static final AlignmentY DEFAULT_ALIGNMENT_Y = AlignmentY.CENTER;
	
	protected PInsets insets = DEFAULT_INSETS;
	protected AlignmentX alignX = DEFAULT_ALIGNMENT_X;
	protected AlignmentY alignY = DEFAULT_ALIGNMENT_Y;
	protected Object shownLayerKey = null;
	
	public PLayeredLayout(PComponent component) {
		super(component);
	}
	
	@Override
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
		return getStyleAttribute(ATTRIBUTE_KEY_INSETS, insets);
	}
	
	public void setAlignment(AlignmentX alignmentX, AlignmentY alignmentY) {
		setAlignmentX(alignmentX);
		setAlignmentY(alignmentY);
	}
	
	public void setAlignmentX(AlignmentX value) {
		ThrowException.ifNull(value, "value == null");
		if (alignX != value) {
			alignX = value;
			invalidate();
		}
	}
	
	public AlignmentX getAlignmentX() {
		return alignX;
	}
	
	public void setAlignmentY(AlignmentY value) {
		ThrowException.ifNull(value, "value == null");
		if (alignY != value) {
			alignY = value;
			invalidate();
		}
	}
	
	public AlignmentY getAlignmentY() {
		return alignY;
	}
	
	@Override
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null;
	}
	
	@Override
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
	
	@Override
	protected void layOutInternal() {
		if (getShownLayerKey() == null) {
			return;
		}
		PComponent content = getChildForConstraint(getShownLayerKey());
		if (content == null) {
			return;
		}
		PBounds ob = getOwner().getBounds();
		PInsets insets = getInsets();
		
		int x = ob.getX() + insets.getFromLeft();
		int y = ob.getY() + insets.getFromTop();
		int w = ob.getWidth() - insets.getWidth();
		int h = ob.getHeight() - insets.getHeight();
		
		PSize prefSize = getPreferredSizeOf(content);
		int prefW = prefSize.getWidth();
		int prefH = prefSize.getHeight();
		
		AlignmentX alignX = getAlignmentX();
		AlignmentY alignY = getAlignmentY();
		int childX = alignX.getLeftX(x, w, prefW);
		int childW = alignX.getWidth(x, w, prefW);
		int childY = alignY.getTopY(y, h, prefH);
		int childH = alignY.getHeight(y, h, prefH);
		
		setChildBounds(content, childX, childY, childW, childH);
	}
	
}