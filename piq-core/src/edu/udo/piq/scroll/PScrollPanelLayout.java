package edu.udo.piq.scroll;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;

public class PScrollPanelLayout extends AbstractPLayout implements PLayout {
	
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
	
	protected boolean canAdd(PComponent component, Object constraint) {
		if (constraint == null || !(constraint instanceof Constraint)) {
			return false;
		}
		return getChildForConstraint(constraint) == null 
				&& constraint != Constraint.BODY == component instanceof PScrollBar;
	}
	
	public void layOut() {
		System.out.println("PScrollPanelLayout.layOut");
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
			int barPrefH = getPreferredSizeOf(barH).getHeight();
			int barPrefW = getPreferredSizeOf(barV).getWidth();
			setChildBounds(barH, x, fy - barPrefH, w - barPrefW, barPrefH);
			setChildBounds(barV, fx - barPrefW, y, barPrefW, h - barPrefH);
			w -= barPrefW;
			h -= barPrefH;
			
			int offsetX = 0;
			if (barH != null) {
				offsetX = (int) (w * barH.getModel().getScroll());
			}
			int offsetY = 0;
			if (barV != null) {
				offsetY = (int) (h * barV.getModel().getScroll());
			}
			x -= offsetX;
			y -= offsetY;
			w += offsetX;
			h += offsetY;
			setChildBounds(body, x, y, w, h);
		}
	}
	
	public PSize getPreferredSize() {
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