package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.PRootLayout.Constraint;
import edu.udo.piq.tools.AbstractEnumPLayout;
import edu.udo.piq.util.ThrowException;

public class PRootLayout extends AbstractEnumPLayout<Constraint> {
	
	private final PRoot owner;
	
	public PRootLayout(PRoot owner) {
		super(owner, Constraint.class);
		this.owner = owner;
	}
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		
		PComponent overlay = getChildForConstraint(Constraint.OVERLAY);
		setChildBounds(overlay, x, y, w, h);
		
		PComponent menuBar = getMenuBar();
		PSize prefSizeMenuBar = getPreferredSizeOf(menuBar);
		int menuBarH = prefSizeMenuBar.getHeight();
		
		PComponent body = getBody();
		setChildBounds(menuBar, x, y, w, menuBarH);
		setChildBounds(body, x, y + menuBarH, w, h - menuBarH);
	}
	
	public PSize getPreferredSize() {
		return owner.getBounds();
	}
	
	public PRootOverlay getOverlay() {
		return (PRootOverlay) getChildForConstraint(Constraint.OVERLAY);
	}
	
	public PComponent getBody() {
		return getChildForConstraint(Constraint.BODY);
	}
	
	public PComponent getMenuBar() {
		return getChildForConstraint(Constraint.MENUBAR);
	}
	
	public PComponent getChildAt(int x, int y) {
		PRootOverlay overlay = getOverlay();
		if (overlay != null && overlay.getBounds().contains(x, y)) {
			PComponent overlayComp = overlay.getLayout().getChildAt(x, y);
			if (overlayComp != null) {
				return overlayComp;
			}
		}
		PComponent body = getBody();
		if (body != null && getChildBounds(body).contains(x, y)) {
			return body;
		}
		return null;
	}
	
	public void onChildPrefSizeChanged(PComponent child) {
		ThrowException.ifFalse(containsChild(child), "containsChild(child) == false");
		if (child == getMenuBar()) {
			invalidate();
		}
	}
	
	public static enum Constraint {
		BODY,
		MENUBAR,
		OVERLAY,
		;
	}
	
}