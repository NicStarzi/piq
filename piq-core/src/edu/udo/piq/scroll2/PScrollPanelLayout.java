package edu.udo.piq.scroll2;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.AbstractEnumPLayout;
import edu.udo.piq.layouts.Axis;
import edu.udo.piq.layouts.PComponentLayoutData;

public class PScrollPanelLayout extends AbstractEnumPLayout<PScrollPanelLayout.Constraint> {
	
	protected final PScrollPanel owner;
	protected final PScrollPanelViewport viewport;
	protected final PScrollBar barX;
	protected final PScrollBar barY;
	protected boolean needsToCheckScrollbars = true;
	
	public PScrollPanelLayout(PScrollPanel pScrollPanel) {
		super(pScrollPanel, Constraint.class);
		owner = pScrollPanel;
		viewport = new PScrollPanelViewport();
		barX = new PScrollBar(Axis.X);
		barY = new PScrollBar(Axis.Y);
	}
	
	@Override
	protected void onChildPrefSizeChanged(PComponent child) {
		super.onChildPrefSizeChanged(child);
		if (child == viewport) {
			owner.onBodyPrefSizeChanged();
		}
		needsToCheckScrollbars = true;
	}
	
	@Override
	protected void onChildRemoved(PComponentLayoutData data) {
		super.onChildRemoved(data);
		if (data.getConstraint() == Constraint.VIEWPORT) {
			needsToCheckScrollbars = true;
		}
	}
	
	@Override
	protected void onOwnerBoundsChanged() {
//		System.out.println("PScrollPanelLayout.onOwnerBoundsChanged()");
		super.onOwnerBoundsChanged();
		needsToCheckScrollbars = true;
	}
	
	@Override
	protected boolean canAdd(PComponent cmp, Object constraint) {
		if (!super.canAdd(cmp, constraint)) {
			return false;
		}
		if (constraint == Constraint.BAR_X || constraint == Constraint.BAR_Y) {
			return cmp instanceof PScrollBar;
		}
		return true;
	}
	
	@Override
	protected void onInvalidated() {
//		System.out.println("PScrollPanelLayout.onInvalidated()");
		prefSize.set(getPreferredSizeOf(getBody()));
//		System.out.println("prefSize1="+prefSize);
		addScrollBarsAsNecessary();
		prefSize.addWidth(getPreferredSizeOf(getScrollBarY()));
		prefSize.addHeight(getPreferredSizeOf(getScrollBarX()));
//		System.out.println("prefSize2="+prefSize);
//		System.out.println();
	}
	
	protected void addScrollBarsAsNecessary() {
		if (!needsToCheckScrollbars) {
			return;
		}
		PComponent body = getBody();
		if (body == null) {
			return;
		}
		PBounds scrollPnlBnds = getOwner().getBoundsWithoutBorder();
		if (scrollPnlBnds == null) {
			return;
		}
		needsToCheckScrollbars = false;
		PSize bodyPrefSize = body.getPreferredSize();
		
		boolean neededBarX = scrollPnlBnds.getWidth() < bodyPrefSize.getWidth();
		boolean hasBarX = hasScrollBarX();
		if (neededBarX && !hasBarX) {
			addChild(barX, Constraint.BAR_X);
		} else if (!neededBarX && hasBarX) {
			removeChild(Constraint.BAR_X);
		}
		boolean neededBarY = scrollPnlBnds.getHeight() < bodyPrefSize.getHeight();
		boolean hasBarY = hasScrollBarY();
		if (neededBarY && !hasBarY) {
			addChild(barY, Constraint.BAR_Y);
		} else if (!neededBarY && hasBarY) {
			removeChild(Constraint.BAR_Y);
		}
	}
	
	public void setBody(PComponent comp) {
		if (containsChild(Constraint.VIEWPORT)) {
			removeChild(Constraint.VIEWPORT);
			viewport.getLayoutInternal().clearAllDataInternal();
		}
		if (comp != null) {
			viewport.getLayoutInternal().addChild(comp, null);
			addChild(viewport, Constraint.VIEWPORT);
			onChildPrefSizeChanged(comp);
		}
	}
	
	public PScrollPanelViewport getViewport() {
		return (PScrollPanelViewport) getChildForConstraint(Constraint.VIEWPORT);
	}
	
	public PComponent getBody() {
		PScrollPanelViewport vp = getViewport();
		if (vp == null) {
			return null;
		}
		return vp.getBody();
	}
	
	public PScrollBar getScrollBarX() {
		return (PScrollBar) getChildForConstraint(Constraint.BAR_X);
	}
	
	public PScrollBar getScrollBarY() {
		return (PScrollBar) getChildForConstraint(Constraint.BAR_Y);
	}
	
	public boolean hasScrollBarX() {
		return containsChild(Constraint.BAR_X);
	}
	
	public boolean hasScrollBarY() {
		return containsChild(Constraint.BAR_Y);
	}
	
	@Override
	protected void layOutInternal() {
		PScrollPanelViewport viewPort = getViewport();
		if (viewPort == null) {
			return;
		}
		PBounds ob = getOwner().getBoundsWithoutBorder();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		int fx = ob.getFinalX();
		int fy = ob.getFinalY();
		
		PScrollBar barX = getScrollBarX();
		PScrollBar barY = getScrollBarY();
		int barPrefW = getPreferredSizeOf(barY).getWidth();
		int barPrefH = getPreferredSizeOf(barX).getHeight();
		setChildCellFilled(barX, x, fy - barPrefH, w - barPrefW, barPrefH);
		setChildCellFilled(barY, fx - barPrefW, y, barPrefW, h - barPrefH);
		w -= barPrefW;
		h -= barPrefH;
		
		int offsetX = 0;
		if (barX != null) {
			offsetX = barX.getScroll();
		}
		int offsetY = 0;
		if (barY != null) {
			offsetY = barY.getScroll();
		}
		setChildCellFilled(viewPort, x, y, w, h);
		
		PSize bodyPrefSize = viewPort.getBody().getPreferredSize();
		int bodyX = x - offsetX;
		int bodyY = y - offsetY;
		int bodyW = bodyPrefSize.getWidth();
		int bodyH = bodyPrefSize.getHeight();
		viewPort.getLayoutInternal().setBodyCellFilled(bodyX, bodyY, bodyW, bodyH);
		
		owner.onBodyLaidOut(w, h);
	}
	
	public static enum Constraint {
		BAR_X, BAR_Y, VIEWPORT,
		;
	}
	
}