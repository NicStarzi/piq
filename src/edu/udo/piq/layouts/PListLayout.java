package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.AbstractPLayoutObs;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.MutablePSize;

public class PListLayout extends AbstractPLayout {
	
	public static final ListAlignment DEFAULT_ALIGNMENT = ListAlignment.FROM_TOP;
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
	protected ListAlignment align = ListAlignment.FROM_TOP;
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
		
		addObs(new AbstractPLayoutObs() {
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				compList.add(child);
			}
			public void childRemoved(PLayout layout, PComponent child, Object constraint) {
				compList.remove(child);
			}
		});
		
		setAlignment(alignment);
		setGap(gap);
	}
	
	public void setGap(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("value="+gap);
		}
		gap = value;
		fireInvalidateEvent();
	}
	
	public int getGap() {
		return gap;
	}
	
	public void setAlignment(ListAlignment alignment) {
		if (alignment == null) {
			throw new NullPointerException();
		}
		align = alignment;
		fireInvalidateEvent();
	}
	
	public ListAlignment getAlignment() {
		return align;
	}
	
	public void setInsets(PInsets insets) {
		if (insets == null) {
			throw new NullPointerException();
		}
		this.insets = new ImmutablePInsets(insets);
		fireInvalidateEvent();
	}
	
	public PInsets getInsets() {
		return insets;
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return constraint == null && !compList.contains(cmp);
	}
	
	public void layOut() {
		PInsets insets = getInsets();
		PBounds ob = getOwnerBounds();
		int gap = getGap();
		int minX = ob.getX() + insets.getFromLeft();
		int minY = ob.getY() + insets.getFromTop();
		int alignedX = minX;
		int alignedY = minY;
		
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
		case CENTERED_HORIZONTAL:
			alignedX = ob.getWidth() / 2 - prefW / 2;
			break;
		case CENTERED_VERTICAL:
			alignedY = ob.getHeight() / 2 - prefH / 2;
			break;
		case FROM_BOTTOM:
			alignedY = (ob.getFinalY() - insets.getFromBottom()) - prefH;
			break;
		case FROM_RIGHT:
			alignedX = (ob.getFinalX() - insets.getFromRight()) - prefW;
			break;
		default:
		}
		int x = Math.max(alignedX, minX);
		int y = Math.max(alignedY, minY);
		int w = ob.getWidth() - insets.getHorizontal();
		int h = ob.getHeight() - insets.getVertical();
		
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = compPrefSizes[i];
			
			if (isHorizontal) {
				int compPrefW = compPrefSize.getWidth();
				setChildBounds(comp, x, y, compPrefW, h);
				x += compPrefW + gap;
			} else {
				int compPrefH = compPrefSize.getHeight();
				setChildBounds(comp, x, y, w, compPrefH);
				y += compPrefH + gap;
			}
		}
	}
	
	public PSize getPreferredSize() {
		int prefW = getInsets().getHorizontal();
		int prefH = getInsets().getVertical();
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
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	public static enum ListAlignment {
		FROM_LEFT,
		FROM_TOP,
		FROM_RIGHT,
		FROM_BOTTOM,
		CENTERED_HORIZONTAL,
		CENTERED_VERTICAL,
		;
		
		public boolean isHorizontal() {
			return this == FROM_LEFT || this == FROM_RIGHT || this == CENTERED_HORIZONTAL;
		}
		
		public boolean isVertical() {
			return !isHorizontal();
		}
	}
	
}