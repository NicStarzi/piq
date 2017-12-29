package edu.udo.piq.scroll;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.AbstractMapPLayout;

public class PScrollPanelLayout extends AbstractMapPLayout implements PLayout {
	
	public PScrollPanelLayout(PComponent component) {
		super(component);
	}
	
	public PScrollBar getHorizontalScrollBar() {
		return (PScrollBar) getChildForConstraint(Constraint.BAR_H);
	}
	
	public PScrollBar getVerticalScrollBar() {
		return (PScrollBar) getChildForConstraint(Constraint.BAR_V);
	}
	
	public void setBody(PComponent component) {
		PComponent oldBody = getBody();
		if (component != oldBody) {
			if (oldBody != null) {
				removeChild(oldBody);
			}
			if (component != null) {
				addChild(component, Constraint.BODY);
			}
		}
	}
	
	public PComponent getBody() {
		return getChildForConstraint(Constraint.BODY);
	}
	
	@Override
	protected boolean canAdd(PComponent component, Object constraint) {
		if (!(constraint instanceof Constraint)) {
			return false;
		}
		return getChildForConstraint(constraint) == null
				&& constraint != Constraint.BODY == component instanceof PScrollBar;
	}
	
	@Override
	protected void layOutInternal() {
//		System.out.println("PScrollPanelLayout.layOut");
		PComponent body = getBody();
		if (body != null) {
			PBounds ob = getOwner().getBounds();
			int x = ob.getX();
			int y = ob.getY();
			int w = ob.getWidth();
			int h = ob.getHeight();
			int fx = ob.getFinalX();
			int fy = ob.getFinalY();
			
			PScrollBar barH = getHorizontalScrollBar();
			PScrollBar barV = getVerticalScrollBar();
			int barPrefW = getPreferredSizeOf(barV).getWidth();
			int barPrefH = getPreferredSizeOf(barH).getHeight();
			setChildCellFilled(barH, x, fy - barPrefH, w - barPrefW, barPrefH);
			setChildCellFilled(barV, fx - barPrefW, y, barPrefW, h - barPrefH);
			w -= barPrefW;
			h -= barPrefH;
			
			PSize bodyPrefSize = getPreferredSizeOf(body);
			int bodyPrefW = bodyPrefSize.getWidth();
			int bodyPrefH = bodyPrefSize.getHeight();
			int maxOffsetX = bodyPrefW - w;
			int maxOffsetY = bodyPrefH - h;
			
			int offsetX = 0;
			if (barH != null) {
				offsetX = (int) (maxOffsetX * barH.getModel().getScroll());
//				System.out.println("bodyPrefW="+bodyPrefW+", w="+w+", maxOffsetX="+maxOffsetX+", offsetX="+offsetX);
			}
			int offsetY = 0;
			if (barV != null) {
				offsetY = (int) (maxOffsetY * barV.getModel().getScroll());
//				System.out.println("bodyPrefH="+bodyPrefH+", h="+h+", maxOffsetY="+maxOffsetY+", offsetY="+offsetY);
			}
			x -= offsetX;
			y -= offsetY;
			w += offsetX;
			h += offsetY;
			setChildCellFilled(body, x, y, w, h);
		}
	}
	
	@Override
	public PSize getPreferredSizeInternal() {
		PComponent bodyCmp = getChildForConstraint(Constraint.BODY);
		return getPreferredSizeOf(bodyCmp);
	}
	
	public static enum Constraint {
		BODY,
		BAR_H,
		BAR_V,
		;
	}
	
}