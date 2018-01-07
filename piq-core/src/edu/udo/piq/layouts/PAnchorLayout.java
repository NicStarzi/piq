package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.util.ThrowException;

public class PAnchorLayout extends AbstractArrayPLayout {
	
	public static final PInsets DEFAULT_INSETS = PInsets.ZERO_INSETS;
	public static final AlignmentX DEFAULT_ALIGN_X = AlignmentX.PREFERRED_OR_CENTER;
	public static final AlignmentY DEFAULT_ALIGN_Y = AlignmentY.PREFERRED_OR_CENTER;
	
	protected PInsets insets = DEFAULT_INSETS;
	protected AlignmentX alignX = DEFAULT_ALIGN_X;
	protected AlignmentY alignY = DEFAULT_ALIGN_Y;
	
	public PAnchorLayout(PComponent component) {
		super(component, 1);
	}
	
	public PAnchorLayout(PComponent component, AlignmentX alignX, AlignmentY alignY) {
		this(component);
		setAlignmentX(alignX);
		setAlignmentY(alignY);
	}
	
	public void setInsets(PInsets value) {
		ThrowException.ifNull(value, "value == null");
		if (!insets.equals(value)) {
			insets = value;
			invalidate();
		}
	}
	
	public PInsets getInsets() {
		return getStyleAttribute(PReadOnlyLayout.ATTRIBUTE_KEY_INSETS, insets);
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
		return getStyleAttribute(PReadOnlyLayout.ATTRIBUTE_KEY_ALIGNMENT_X, alignX);
	}
	
	public void setAlignmentY(AlignmentY value) {
		ThrowException.ifNull(value, "value == null");
		if (alignY != value) {
			alignY = value;
			invalidate();
		}
	}
	
	public AlignmentY getAlignmentY() {
		return getStyleAttribute(PReadOnlyLayout.ATTRIBUTE_KEY_ALIGNMENT_Y, alignY);
	}
	
	public void setContent(PComponent component) {
		if (getContent() != null) {
			removeChild(component);
		}
		addChild(component, null);
	}
	
	public PComponent getContent() {
		return getComponentForIndex(0);
	}
	
	@Override
	public boolean containsChild(PComponent child) {
		ThrowException.ifNull(child, "child == null");
		return child == getContent();
	}
	
	@Override
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint == null;
	}
	
	@Override
	protected void onInvalidated() {
		PInsets insets = getInsets();
		PSize contentSize = getPreferredSizeOf(getContent());
		int prefW = contentSize.getWidth() + insets.getHorizontal();
		int prefH = contentSize.getHeight() + insets.getVertical();
		prefSize.set(prefW, prefH);
	}
	
	@Override
	protected void layOutInternal() {
		PComponent child = getContent();
		if (child == null) {
			return;
		}
		
		PInsets insets = getInsets();
		PBounds bounds = getOwner().getBoundsWithoutBorder();
		int x = bounds.getX() + insets.getFromLeft();
		int y = bounds.getY() + insets.getFromTop();
		int w = bounds.getWidth() - insets.getHorizontal();
		int h = bounds.getHeight() - insets.getVertical();
		
		AlignmentX alignX = getAlignmentX();
		AlignmentY alignY = getAlignmentY();
		
		setChildCell(child, x, y, w, h, alignX, alignY);
	}
	
	@Override
	protected int getIndexFor(Object constr) {
		if (constr == null) {
			return 0;
		}
		return -1;
	}
	
}