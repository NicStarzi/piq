package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PScrollBarHorizontal;
import edu.udo.piq.components.PScrollBarModel;
import edu.udo.piq.components.PScrollBarModelObs;
import edu.udo.piq.tools.AbstractPLayout;

public class PScrollLayout extends AbstractPLayout {
	
	private final PScrollBarModelObs scrollObs = new PScrollBarModelObs() {
		public void sizeChanged(PScrollBarModel model) {
		}
		public void scrollChanged(PScrollBarModel model) {
			fireInvalidateEvent();
		}
	};
	private PBounds parentBounds;
	private PComponent view;
	private PScrollBarHorizontal scrollH;
	private PScrollBarHorizontal scrollV;
	
	public PScrollLayout(PComponent component) {
		super(component);
		addObs(new PLayoutObs() {
			public void layoutInvalidated(PLayout layout) {
			}
			public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
			}
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
			if (scrollH != null) {
				scrollH.getModel().removeObs(scrollObs);
			}
			scrollH = (PScrollBarHorizontal) comp;
			if (scrollH != null) {
				scrollH.getModel().addObs(scrollObs);
			}
			break;
		case VERTICAL_SCROLL_BAR:
			if (scrollV != null) {
				scrollV.getModel().removeObs(scrollObs);
			}
			scrollV = (PScrollBarHorizontal) comp;
			if (scrollV != null) {
				scrollV.getModel().addObs(scrollObs);
			}
			break;
		}
	}
	
	public void setParentBounds(PBounds bounds) {
		this.parentBounds = bounds;
		fireInvalidateEvent();
	}
	
	public PBounds getParentBounds() {
		return parentBounds;
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
		PBounds ob = getParentBounds();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
//		int h = ob.getHeight();
		int fx = ob.getFinalX();
		int fy = ob.getFinalY();
		
		setChildBounds(scrollH, x, fy - 20, w, 20);
		
		if (view != null) {
			int viewX = x - getScrollOffsetX();
			int viewY = y - getScrollOffsetY();
			int viewFx = fx - 0;
			int viewFy = fy - 20;
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