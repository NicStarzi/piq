package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PLayout;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimer;
import edu.udo.piq.PTimerCallback;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.PCompUtil;

public class PToolTip extends AbstractPLayoutOwner {
	
	public static final int DEFAULT_SHOW_DELAY = 60;
	
	private final PMouseObs mouseObs = new PMouseObs() {
		public void mouseMoved(PMouse mouse) {
			if (isShown) {
				if (!isMouseWithinClippedBounds()) {
					removeFromOverlay();
				}
			} else {
				boolean isWithin = target.isMouseWithinClippedBounds();
				int newShowX = mouse.getX() - 4;
				int newShowY = mouse.getY() - 4;
				if (showX != newShowX || showY != newShowY) {
					showX = newShowX;
					showY = newShowY;
					if (showTimer.isRunning()) {
						showTimer.restart();
					}
				}
				if (isWithin && !showTimer.isRunning()) {
					showTimer.start();
				} else if (!isWithin && showTimer.isRunning()) {
					showTimer.stop();
				}
			}
		}
	};
	private final PTimer showTimer = new PTimer(new PTimerCallback() {
		public void action() {
			addToOverlay();
		}
	});
	private int showX;
	private int showY;
	private boolean isShown = false;
	private PComponent target;
	
	public PToolTip() {
		super();
		setLayout(new PCentricLayout(this));
		showTimer.setRepeating(false);
		showTimer.setDelay(DEFAULT_SHOW_DELAY);
		
		addObs(new PComponentObs() {
			public void preferredSizeChanged(PComponent component) {
				if (isShown) {
					repositionOnOverlay();
				}
			}
		});
	}
	
	public PToolTip(PTextModel textModel) {
		this();
		setContent(new PLabel(textModel));
	}
	
	public void setShowDelay(int value) {
		showTimer.setDelay(value);
	}
	
	public int getShowDelay() {
		return showTimer.getDelay();
	}
	
	public void setTooltipComponent(PComponent component) {
		if (target != null) {
			target.removeObs(mouseObs);
		}
		target = component;
		showTimer.setOwner(target);
		if (target != null) {
			target.addObs(mouseObs);
		}
	}
	
	public PComponent getTooltipComponent() {
		return target;
	}
	
	public void setContent(PComponent component) {
		getLayout().setContent(component);
	}
	
	public PComponent getContent() {
		return getLayout().getContent();
	}
	
	public PCentricLayout getLayout() {
		return (PCentricLayout) super.getLayout();
	}
	
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
	
	public PSize getDefaultPreferredSize() {
		if (getLayout() != null) {
			return getLayout().getPreferredSize();
		}
		return PSize.NULL_SIZE;
	}
	
	protected void repositionOnOverlay() {
		PSize ownSize = PCompUtil.getPreferredSizeOf(this);
		int ownX = showX;
		int ownY = showY;
		int ownW = ownSize.getWidth();
		int ownH = ownSize.getHeight();
		
		PRootOverlay overlay = target.getRoot().getOverlay();
		PBounds overlayBounds = overlay.getBounds();
		int overlayX = overlayBounds.getX();
		int overlayY = overlayBounds.getY();
		int overlayW = overlayBounds.getWidth();
		int overlayH = overlayBounds.getHeight();
		
		if (ownX + ownW > overlayX + overlayW) {
			ownX = (overlayX + overlayW) - ownW;
		}
		if (ownY + ownH > overlayY + overlayH) {
			ownY = (overlayY + overlayH) - ownH;
		}
		PFreeLayout overlayLayout = overlay.getLayout();
		FreeConstraint constr = new FreeConstraint(ownX, ownY);
		overlayLayout.updateConstraint(this, constr);
	}
	
	protected void addToOverlay() {
		PLayout overlayLayout = target.getRoot().getOverlay().getLayout();
		FreeConstraint constr = new FreeConstraint(showX, showY);
		overlayLayout.addChild(this, constr);
		isShown = true;
		repositionOnOverlay();
	}
	
	protected void removeFromOverlay() {
		PLayout overlayLayout = target.getRoot().getOverlay().getLayout();
		overlayLayout.removeChild(this);
		isShown = false;
	}
	
}