package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.ImmutablePSize;

public class PListLayout extends AbstractPLayout {
	
	public static final Orientation DEFAULT_ORIENTATION = Orientation.TOP_TO_BOTTOM;
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4, 4);
	public static final int DEFAULT_GAP = 8;
	
	protected final List<PComponent> components;
	protected PInsets insets;
	protected Orientation ori;
	protected int prefW;
	protected int prefH;
	protected int gap;
	
	public PListLayout(PComponent owner, Orientation orientation, int gap) {
		super(owner);
		insets = DEFAULT_INSETS;
		components = new ArrayList<>();
		ori = orientation;
		this.gap = gap;
		
		addObs(new PLayoutObs() {
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				components.add(child);
			}
			public void childRemoved(PLayout layout, PComponent child, Object constraint) {
				components.remove(child);
			}
			public void layoutInvalidated(PLayout layout) {
			}
			public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
			}
		});
	}
	
	public PListLayout(PComponent owner, Orientation orientation) {
		this(owner, orientation, DEFAULT_GAP);
	}
	
	public PListLayout(PComponent owner, int gap) {
		this(owner, DEFAULT_ORIENTATION, gap);
	}
	
	public PListLayout(PComponent owner) {
		this(owner, DEFAULT_ORIENTATION, DEFAULT_GAP);
	}
	
	public void setOrientation(Orientation orientation) {
		if (orientation == null) {
			throw new IllegalArgumentException("orientation="+orientation);
		}
		ori = orientation;
		fireInvalidateEvent();
	}
	
	public Orientation getOrientation() {
		return ori;
	}
	
	public void setGap(int value) {
		gap = value;
		fireInvalidateEvent();
	}
	
	public int getGap() {
		return gap;
	}
	
	public void setInsets(PInsets value) {
		if (value == null) {
			throw new IllegalArgumentException("insets="+value);
		}
		insets = value;
		fireInvalidateEvent();
	}
	
	public PInsets getInsets() {
		return insets;
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return !containsChild(cmp) && constraint == null;
	}
	
	public void layOut() {
		Orientation ori = getOrientation();
		PBounds ob = getOwnerBounds();
		PInsets insets = getInsets();
		int x = ori.getInitialX(ob, insets);
		int y = ori.getInitialY(ob, insets);
		prefW = 0;
		prefH = 0;
		
		for (PComponent cmp : components) {
			PSize prefSize = getPreferredSizeOf(cmp);
			int cmpPrefW = prefSize.getWidth();
			int cmpPrefH = prefSize.getHeight();
//			int cmpPrefW = getPreferredWidthOf(cmp);
//			int cmpPrefH = getPreferredHeightOf(cmp);
			
			int advance;
			switch (getOrientation()) {
			case TOP_TO_BOTTOM:
				setChildBounds(cmp, x, y, cmpPrefW, cmpPrefH);
				advance = cmpPrefH + getGap();
				y += advance;
				prefH += advance;
				
				if (prefW < cmpPrefW) {
					prefW = cmpPrefW;
				}
				break;
			case BOTTOM_TO_TOP:
				setChildBounds(cmp, x, y - cmpPrefH, cmpPrefW, cmpPrefH);
				advance = cmpPrefH + getGap();
				y -= advance;
				prefH += advance;
				
				if (prefW < cmpPrefW) {
					prefW = cmpPrefW;
				}
				break;
			case LEFT_TO_RIGHT:
				setChildBounds(cmp, x, y, cmpPrefW, cmpPrefH);
				advance = cmpPrefW + getGap();
				x += advance;
				prefW += advance;
				
				if (prefH < cmpPrefH) {
					prefH = cmpPrefH;
				}
				break;
			case RIGHT_TO_LEFT:
				setChildBounds(cmp, x - cmpPrefW, y, cmpPrefW, cmpPrefH);
				advance = cmpPrefW + getGap();
				x -= advance;
				prefW += advance;
				
				if (prefH < cmpPrefH) {
					prefH = cmpPrefH;
				}
				break;
			}
		}
		prefW += insets.getHorizontal();
		prefH += insets.getVertical();
	}
	
	public PSize getPreferredSize() {
		return new ImmutablePSize(prefW, prefH);
	}
	
//	public int getPreferredWidth() {
//		return prefW;
//	}
//	
//	public int getPreferredHeight() {
//		return prefH;
//	}
	
	public static enum Orientation {
		LEFT_TO_RIGHT,
		RIGHT_TO_LEFT,
		TOP_TO_BOTTOM,
		BOTTOM_TO_TOP,
		;
		
		public int getInitialX(PBounds bounds, PInsets insets) {
			switch (this) {
			case TOP_TO_BOTTOM:
				return bounds.getX() + insets.getFromLeft();
			case BOTTOM_TO_TOP:
				return bounds.getX() + insets.getFromLeft();
			case LEFT_TO_RIGHT:
				return bounds.getX() + insets.getFromLeft();
			case RIGHT_TO_LEFT:
				return bounds.getFinalX() - insets.getFromRight();
			}
			return 0;
		}
		public int getInitialY(PBounds bounds, PInsets insets) {
			switch (this) {
			case TOP_TO_BOTTOM:
				return bounds.getY() + insets.getFromTop();
			case BOTTOM_TO_TOP:
				return bounds.getFinalY() - insets.getFromBottom();
			case LEFT_TO_RIGHT:
				return bounds.getY() + insets.getFromTop();
			case RIGHT_TO_LEFT:
				return bounds.getY() + insets.getFromTop();
			}
			return 0;
		}
	}
	
}