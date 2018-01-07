package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PListLayout extends AbstractMapPLayout {
	
	public static final ListAlignment DEFAULT_ALIGNMENT = ListAlignment.TOP_TO_BOTTOM;
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	public static final AlignmentX DEFAULT_ALIGN_X = AlignmentX.PREFERRED_OR_CENTER;
	public static final AlignmentY DEFAULT_ALIGN_Y = AlignmentY.PREFERRED_OR_CENTER;
	public static final int DEFAULT_GAP = 2;
	
	/**
	 * Manages all components as a list.
	 */
	protected final List<PComponent> compList = new ArrayList<>();
	protected PSize[] cachedPrefSizes;
	protected ListAlignment align = DEFAULT_ALIGNMENT;
	protected AlignmentX alignX = DEFAULT_ALIGN_X;
	protected AlignmentY alignY = DEFAULT_ALIGN_Y;
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
	
	@Override
	protected void onChildAdded(PComponentLayoutData data) {
		if (data.getConstraint() == null) {
			compList.add(data.getComponent());
		} else {
			compList.add((Integer) data.getConstraint(), data.getComponent());
		}
		int index = compList.indexOf(data.getComponent());
		for (int i = index; i < compList.size(); i++) {
			Integer con = Integer.valueOf(i);
			setChildConstraint(compList.get(i), con);
		}
		invalidate();
	}
	
	@Override
	protected void onChildRemoved(PComponentLayoutData data) {
		int index = (Integer) data.getConstraint();
		ThrowException.ifNotEqual(data.getComponent(), compList.get(index),
				"compList.get(index) != removedComponent");
		compList.remove(index);
		for (int i = index; i < compList.size(); i++) {
			Integer con = Integer.valueOf(i);
			setChildConstraint(compList.get(i), con);
		}
		invalidate();
	}
	
	@Override
	protected void clearAllDataInternal() {
		super.clearAllDataInternal();
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
		return getStyleAttribute(ATTRIBUTE_KEY_GAP, gap);
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
	
	public void setInsets(PInsets value) {
		ThrowException.ifNull(value, "value == null");
		if (!getInsets().equals(value)) {
			insets = new ImmutablePInsets(value);
			invalidate();
		}
	}
	
	public PInsets getInsets() {
		return getStyleAttribute(ATTRIBUTE_KEY_INSETS, insets);
	}
	
	public PComponent getChild(int index) {
		if (index < 0 || index >= compList.size()) {
			return null;
		}
		return compList.get(index);
	}
	
//	@Override
//	public PComponent getChildAt(int x, int y) {
//		for (int i = 0; i < compList.size(); i++) {
//			PComponent child = compList.get(i);
//			PBounds bnds = getChildBounds(child);
//			if (bnds.contains(x, y)) {
//				return child;
//			}
//			// This optimization only works for ListAlignment.FROM_TOP...
////			int cfy = bnds.getFinalY();
////			if (cfy >= y) {
////				int cx = bnds.getX();
////				int cy = bnds.getY();
////				int cfx = bnds.getFinalX();
////				if (cy <= y && cfx >= x && cx <= x) {
////					return child;
////				} else {
////					return null;
////				}
////			}
//		}
//		return null;
//	}
	
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
	
	@Override
	public Object getChildConstraint(PComponent child) throws NullPointerException {
		return Integer.valueOf(getChildIndex(child));
	}
	
	@Override
	public PComponent getChildForConstraint(Object constraint) {
		return getChild((Integer) constraint);
	}
	
	@Override
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return (constraint == null || constraint instanceof Integer) && !compList.contains(cmp);
	}
	
	@Override
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
		prefSize.set(prefW, prefH);
		prefSize.add(getInsets());
	}
	
	@Override
	protected void layOutInternal() {
		PInsets insets = getInsets();
		PBounds ob = getOwner().getBoundsWithoutBorder();
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
		
		AlignmentX alignX = getAlignmentX();
		AlignmentY alignY = getAlignmentY();
		
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = cachedPrefSizes[i];
			int compPrefW = compPrefSize.getWidth();
			int compPrefH = compPrefSize.getHeight();
			
			if (isHorizontal) {
				setChildCell(comp, x, y, compPrefW, h, alignX, alignY);
				x += compPrefW + gap;
			} else {
				setChildCell(comp, x, y, w, compPrefH, alignX, alignY);
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
		
		public int getSize(PSize size) {
			if (isHorizontal()) {
				return size.getWidth();
			} else {
				return size.getHeight();
			}
		}
		
	}
	
}