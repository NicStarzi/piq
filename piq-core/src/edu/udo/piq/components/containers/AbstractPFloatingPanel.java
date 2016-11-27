package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.PAnchorLayout;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPFloatingPanel extends PPanel {
	
	public AbstractPFloatingPanel() {
		super();
		setLayout(new PAnchorLayout(this));
	}
	
	protected abstract PRootOverlay getOverlay();
	
	protected abstract int getOverlayPositionX();
	
	protected abstract int getOverlayPositionY();
	
	public boolean isShown() {
		return getParent() != null;
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		renderer.setColor(PColor.BLACK);
		renderer.strokeQuad(x, y, fx, fy);
	}
	
	protected void repositionOnOverlay() {
		ThrowException.ifNull(getParent(), "getParent() == null");
		
		PSize ownSize = getPreferredSize();
		int ownX = getOverlayPositionX();
		int ownY = getOverlayPositionY();
		int ownW = ownSize.getWidth();
		int ownH = ownSize.getHeight();
		
		PRootOverlay overlay = getOverlay();
		ThrowException.ifNull(overlay, "overlay == null");
		
		PBounds overlayBounds = overlay.getBounds();
		int overlayX = overlayBounds.getX();
		int overlayY = overlayBounds.getY();
		int overlayW = overlayBounds.getWidth();
		int overlayH = overlayBounds.getHeight();
		
		if (ownW > overlayW) {
			ownX = overlayX;
		} else if (ownX + ownW > overlayX + overlayW) {
			ownX = (overlayX + overlayW) - ownW;
		}
		if (ownH > overlayH) {
			ownY = overlayY;
		} else if (ownY + ownH > overlayY + overlayH) {
			ownY = (overlayY + overlayH) - ownH;
		}
		PFreeLayout overlayLayout = overlay.getLayout();
		FreeConstraint constr = new FreeConstraint(ownX, ownY);
		overlayLayout.updateConstraint(this, constr);
	}
	
	protected void addToOverlay() {
		ThrowException.ifNotNull(getParent(), "getParent() != null");
		
		PFreeLayout overlayLayout = getOverlayLayout();
		ThrowException.ifNull(overlayLayout, "overlayLayout == null");
		
		int posX = getOverlayPositionX();
		int posY = getOverlayPositionY();
		FreeConstraint constr = new FreeConstraint(posX, posY);
		overlayLayout.addChild(this, constr);
		repositionOnOverlay();
	}
	
	protected void removeFromOverlay() {
		ThrowException.ifNull(getParent(), "getParent() == null");
		
		PFreeLayout overlayLayout = getOverlayLayout();
		ThrowException.ifNull(overlayLayout, "overlayLayout == null");
		
		overlayLayout.removeChild(this);
	}
	
	protected PFreeLayout getOverlayLayout() {
		PRootOverlay overlay = getOverlay();
		if (overlay == null) {
			return null;
		}
		return overlay.getLayout();
	}
	
}