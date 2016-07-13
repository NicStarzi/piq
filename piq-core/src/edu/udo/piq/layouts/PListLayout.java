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
import edu.udo.piq.tools.MutablePSize;

public class PListLayout extends AbstractMapPLayout {
	
	public static final ListAlignment DEFAULT_ALIGNMENT = ListAlignment.TOP_TO_BOTTOM;
	public static final int DEFAULT_GAP = 2;
	
	/**
	 * Manages all components as a list.
	 */
	protected final List<PComponent> compList;
	/**
	 * To save memory the preferred size of the layout 
	 * is an instance of MutablePSize which is updated 
	 * and returned by the {@link #getPreferredSize()} 
	 * method.<br>
	 */
	protected final MutablePSize prefSize;
	protected ListAlignment align = ListAlignment.TOP_TO_BOTTOM;
	protected PInsets insets = new ImmutablePInsets(4);
	protected int gap = 2;
	
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
		compList = new ArrayList<>();
		prefSize = new MutablePSize();
		
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
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		int index = compList.indexOf(child);
		compList.remove(index);
		for (int i = index; i < compList.size(); i++) {
			Integer con = Integer.valueOf(i);
			setChildConstraint(compList.get(i), con);
		}
	}
	
	public void setGap(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("value="+gap);
		}
		gap = value;
		invalidate();
	}
	
	public int getGap() {
		return gap;
	}
	
	public void setAlignment(ListAlignment alignment) {
		if (alignment == null) {
			throw new NullPointerException();
		}
		align = alignment;
		invalidate();
	}
	
	public ListAlignment getAlignment() {
		return align;
	}
	
	public void setInsets(PInsets insets) {
		if (insets == null) {
			throw new NullPointerException();
		}
		this.insets = new ImmutablePInsets(insets);
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
			if (bnds.getFinalY() >= y) {
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
	
	public void layOut() {
		PInsets insets = getInsets();
		PBounds ob = getOwner().getBounds();
		int gap = getGap();
		int minX = ob.getX() + insets.getFromLeft();
		int minY = ob.getY() + insets.getFromTop();
		int alignedX = minX;
		int alignedY = minY;
		int w = ob.getWidth() - insets.getHorizontal();
		int h = ob.getHeight() - insets.getVertical();
		
		int prefW = 0;
		int prefH = 0;
		PSize[] compPrefSizes = new PSize[compList.size()];
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = getPreferredSizeOf(comp);
			compPrefSizes[i] = compPrefSize;
			prefW += compPrefSize.getWidth() + gap;
			prefH += compPrefSize.getHeight() + gap;
		}
		if (compPrefSizes.length > 0) {
			prefW -= gap;
			prefH -= gap;
		}
		
		ListAlignment align = getAlignment();
		boolean isHorizontal = align.isHorizontal();
		switch (align) {
		case CENTERED_LEFT_TO_RIGHT:
			alignedX = ob.getWidth() / 2 - prefW / 2;
			break;
		case CENTERED_TOP_TO_BOTTOM:
			alignedY = ob.getHeight() / 2 - prefH / 2;
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
			PSize compPrefSize = compPrefSizes[i];
			int compPrefW = compPrefSize.getWidth();
			int compPrefH = compPrefSize.getHeight();
			
			if (isHorizontal) {
				setChildBounds(comp, x, y, compPrefW, h);
//				setChildBounds(comp, x, y, compPrefW, compPrefH);
				x += compPrefW + gap;
			} else {
				setChildBounds(comp, x, y, w, compPrefH);
//				setChildBounds(comp, x, y, compPrefW, compPrefH);
				y += compPrefH + gap;
			}
		}
	}
	
	public PSize getPreferredSize() {
		int prefW = 0;
		int prefH = 0;
		boolean isHorizontal = getAlignment().isHorizontal();
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = getPreferredSizeOf(comp);
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
		prefH += getInsets().getVertical();
		prefW += getInsets().getHorizontal();
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
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