package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PLayoutDesign;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PListLayout extends AbstractMapPLayout {
	
	public static final ListAlignment DEFAULT_ALIGNMENT = ListAlignment.TOP_TO_BOTTOM;
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	public static final int DEFAULT_GAP = 2;
	
	/**
	 * Manages all components as a list.
	 */
	protected final List<PComponent> compList = new ArrayList<>();
	protected PSize[] cachedPrefSizes;
	protected ListAlignment align = DEFAULT_ALIGNMENT;
	protected PInsets insets = DEFAULT_INSETS;
	protected int gap = DEFAULT_GAP;
	
	public PListLayout(PComponent owner) {
		this(owner, DEFAULT_ALIGNMENT, DEFAULT_GAP);
	}
	
	public PListLayout(PComponent owner, ListAlignment alignment) {
		this(owner, alignment, DEFAULT_GAP);
	}
	
	public PListLayout(PComponent owner, int gap) {
		this(owner, DEFAULT_ALIGNMENT, gap);
	}
	
	public PListLayout(PComponent owner, ListAlignment alignment, int gap) {
		super(owner);
		
		setAlignment(alignment);
		setGap(gap);
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
		if (constraint == null) {
			compList.add(child);
		} else {
			compList.add((Integer) constraint, child);
		}
		int index = compList.indexOf(child);
		for (int i = index; i < compList.size(); i++) {
			Integer con = Integer.valueOf(i);
			setChildConstraint(compList.get(i), con);
		}
		invalidate();
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		int index = compList.indexOf(child);
		compList.remove(index);
		for (int i = index; i < compList.size(); i++) {
			Integer con = Integer.valueOf(i);
			setChildConstraint(compList.get(i), con);
		}
		invalidate();
	}
	
	protected void clearAllInfosInternal() {
		super.clearAllInfosInternal();
		compList.clear();
	}
	
	public void setGap(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (getGap() != value) {
			gap = value;
			invalidate();
		}
	}
	
	public int getGap() {
		return gap;
	}
	
	public void setAlignment(ListAlignment value) {
		ThrowException.ifNull(value, "value == null");
		if (getAlignment() != value) {
			align = value;
			invalidate();
		}
	}
	
	public ListAlignment getAlignment() {
		return align;
	}
	
	public void setInsets(PInsets value) {
		ThrowException.ifNull(value, "value == null");
		if (!getInsets().equals(value)) {
			insets = new ImmutablePInsets(value);
			invalidate();
		}
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
	
	public PComponent getChild(int index) {
		if (index < 0 || index >= compList.size()) {
			return null;
		}
		return compList.get(index);
	}
	
	public PComponent getChildAt(int x, int y) {
		for (int i = 0; i < compList.size(); i++) {
			PComponent child = compList.get(i);
			PBounds bnds = getChildBounds(child);
			if (bnds.contains(x, y)) {
				return child;
			}
			// This optimization only works for ListAlignment.FROM_TOP...
//			int cfy = bnds.getFinalY();
//			if (cfy >= y) {
//				int cx = bnds.getX();
//				int cy = bnds.getY();
//				int cfx = bnds.getFinalX();
//				if (cy <= y && cfx >= x && cx <= x) {
//					return child;
//				} else {
//					return null;
//				}
//			}
		}
		return null;
	}
	
	public int getIndexAt(int x, int y) {
		for (int i = 0; i < compList.size(); i++) {
			PComponent child = compList.get(i);
			PBounds bnds = getChildBounds(child);
			if (bnds.contains(x, y)) {
				return i;
			}
		}
		return compList.size();
	}
	
	public int getChildIndex(PComponent child) {
		if (child == null) {
			throw new NullPointerException();
		}
		return compList.indexOf(child);
	}
	
	public Object getChildConstraint(PComponent child) throws NullPointerException {
		return Integer.valueOf(getChildIndex(child));
	}
	
	public PComponent getChildForConstraint(Object constraint) {
		return getChild((Integer) constraint);
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return (constraint == null || constraint instanceof Integer) && !compList.contains(cmp);
	}
	
	protected void onInvalidated() {
		int gap = getGap();
		int prefW = 0;
		int prefH = 0;
		boolean isHorizontal = getAlignment().isHorizontal();
		
		if (cachedPrefSizes == null || cachedPrefSizes.length < compList.size()) {
			cachedPrefSizes = new PSize[compList.size()];
		}
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = getPreferredSizeOf(comp);
			cachedPrefSizes[i] = compPrefSize;
			int compPrefW = compPrefSize.getWidth();
			int compPrefH = compPrefSize.getHeight();
			
			if (isHorizontal) {
				prefW += compPrefW + gap;
				if (prefH < compPrefH) {
					prefH = compPrefH;
				}
			} else {
				prefH += compPrefH + gap;
				if (prefW < compPrefW) {
					prefW = compPrefW;
				}
			}
		}
		if (!compList.isEmpty()) {
			if (isHorizontal) {
				prefW -= gap;
			} else {
				prefH -= gap;
			}
		}
		prefW += getInsets().getHorizontal();
		prefH += getInsets().getVertical();
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
	}
	
	protected void layOutInternal() {
		PInsets insets = getInsets();
		PBounds ob = getOwner().getBounds();
		int gap = getGap();
		int minX = ob.getX() + insets.getFromLeft();
		int minY = ob.getY() + insets.getFromTop();
		int alignedX = minX;
		int alignedY = minY;
		int w = ob.getWidth() - insets.getHorizontal();
		int h = ob.getHeight() - insets.getVertical();
		
		int prefW = prefSize.getWidth();
		int prefH = prefSize.getHeight();
		
		ListAlignment align = getAlignment();
		boolean isHorizontal = align.isHorizontal();
		switch (align) {
		case CENTERED_LEFT_TO_RIGHT:
			alignedX = minX + w / 2 - (prefW - insets.getHorizontal()) / 2;
			break;
		case CENTERED_TOP_TO_BOTTOM:
			alignedY = minY + h / 2 - (prefH - insets.getVertical()) / 2;
			break;
		case BOTTOM_TO_TOP:
			alignedY = (ob.getFinalY() - insets.getFromBottom()) - prefH;
			break;
		case RIGHT_TO_LEFT:
			alignedX = (ob.getFinalX() - insets.getFromRight()) - prefW;
			break;
		case LEFT_TO_RIGHT:
		case TOP_TO_BOTTOM:
		default:
		}
		int x = Math.max(alignedX, minX);
		int y = Math.max(alignedY, minY);
		
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = cachedPrefSizes[i];
			int compPrefW = compPrefSize.getWidth();
			int compPrefH = compPrefSize.getHeight();
			
			if (isHorizontal) {
				setChildBounds(comp, x, y, compPrefW, h);
				x += compPrefW + gap;
			} else {
				setChildBounds(comp, x, y, w, compPrefH);
				y += compPrefH + gap;
			}
		}
	}
	
	public static enum ListAlignment {
		LEFT_TO_RIGHT,
		TOP_TO_BOTTOM,
		RIGHT_TO_LEFT,
		BOTTOM_TO_TOP,
		CENTERED_LEFT_TO_RIGHT,
		CENTERED_TOP_TO_BOTTOM,
		;
		
		public boolean isHorizontal() {
			return this == LEFT_TO_RIGHT || this == RIGHT_TO_LEFT 
					|| this == CENTERED_LEFT_TO_RIGHT;
		}
		
		public boolean isVertical() {
			return !isHorizontal();
		}
	}
	
}