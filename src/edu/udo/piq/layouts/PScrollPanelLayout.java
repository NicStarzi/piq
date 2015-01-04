package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PScrollBarHorizontal;
import edu.udo.piq.components.PScrollBarVertical;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.AbstractPLayoutObs;

public class PScrollPanelLayout extends AbstractPLayout {
	
	public static final int SCROLL_BAR_SIZE = 12;
	
	private PComponent view;
	private PScrollBarHorizontal scrollH;
	private PScrollBarVertical scrollV;
	
	public PScrollPanelLayout(PComponent component) {
		super(component);
		addObs(new AbstractPLayoutObs() {
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				Constraint constr = (Constraint) constraint;
				setComp(constr, child);
			}
			public void childRemoved(PLayout layout, PComponent child, Object constraint) {
				Constraint constr = (Constraint) constraint;
				setComp(constr, null);
			}
		});
	}
	
	private void setComp(Constraint constr, PComponent comp) {
		switch (constr) {
		case VIEW:
			view = comp;
			break;
		case HORIZONTAL_SCROLL_BAR:
			scrollH = (PScrollBarHorizontal) comp;
			break;
		case VERTICAL_SCROLL_BAR:
			scrollV = (PScrollBarVertical) comp;
			break;
		}
	}
	
//	public void setParentBounds(PBounds bounds) {
//		this.parentBounds = bounds;
//		fireInvalidateEvent();
//	}
//	
//	public PBounds getParentBounds() {
//		return parentBounds;
//	}
	
	public void scrollChanged() {
		fireInvalidateEvent();
	}
	
	public int getScrollOffsetX() {
		if (scrollH == null) {
			return 0;
		}
		return scrollH.getModel().getScroll();
	}
	
	public int getScrollOffsetY() {
		if (scrollV == null) {
			return 0;
		}
		return scrollV.getModel().getScroll();
	}
	
	public void setView(PComponent component) {
		if (getView() != null) {
			removeChild(getView());
		}
		addChild(component, Constraint.VIEW);
	}
	
	public PComponent getView() {
		return view;
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof Constraint;
	}
	
	public void layOut() {
		PBounds ob = getOwnerBounds();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		int fx = ob.getFinalX();
		int fy = ob.getFinalY();
		
		int offsetX;
		int offsetY;
		if (view != null && scrollH != null && scrollH.getModel().getMaxScroll() > 0) {
			setChildBounds(scrollH, x, fy - SCROLL_BAR_SIZE, w - SCROLL_BAR_SIZE, SCROLL_BAR_SIZE);
			offsetY = SCROLL_BAR_SIZE;
		} else {
			setChildBounds(scrollH, x, fy, 0, 0);
			offsetY = 0;
		}
		if (view != null && scrollV != null && scrollV.getModel().getMaxScroll() > 0) {
			setChildBounds(scrollV, fx - SCROLL_BAR_SIZE, y, SCROLL_BAR_SIZE, h - SCROLL_BAR_SIZE);
			offsetX = SCROLL_BAR_SIZE;
		} else {
			setChildBounds(scrollV, fx, x, 0, 0);
			offsetX = 0;
		}
		
		if (view != null) {
			int viewX = x - getScrollOffsetX();
			int viewY = y - getScrollOffsetY();
			int viewFx = fx - offsetX;
			int viewFy = fy - offsetY;
			int viewW = viewFx - viewX;
			int viewH = viewFy - viewY;
			setChildBounds(view, viewX, viewY, viewW, viewH);
		}
	}
	
	public PSize getPreferredSize() {
		if (view != null) {
			return getPreferredSizeOf(view);
		}
		return PSize.NULL_SIZE;
	}
	
	public static enum Constraint {
		VIEW,
		HORIZONTAL_SCROLL_BAR,
		VERTICAL_SCROLL_BAR,
		;
	}
	
}