package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PTimer;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.layouts.PCentricLayout;

public class PToolTip extends AbstractPFloatingPanel {
	
	public static final int DEFAULT_SHOW_DELAY = 60;
	
	protected final PMouseObs mouseObs = new PMouseObs() {
		public void onMouseMoved(PMouse mouse) {
			PToolTip.this.onMouseMoved(mouse);
		}
	};
	protected final PTimer showTimer = new PTimer(this::addToOverlay);
	protected int showX;
	protected int showY;
	protected boolean isShown = false;
	protected PComponent target;
	
	public PToolTip() {
		super();
		setLayout(new PCentricLayout(this));
		showTimer.setRepeating(false);
		showTimer.setDelay(DEFAULT_SHOW_DELAY);
		
		addObs(new PComponentObs() {
			public void onPreferredSizeChanged(PComponent component) {
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
	
	public PToolTip(Object initialContent) {
		this();
		setContent(new PLabel(initialContent));
	}
	
	public void setShowDelay(int value) {
		showTimer.setDelay(value);
	}
	
	public int getShowDelay() {
		return showTimer.getDelay();
	}
	
	public void setTooltipTarget(PComponent component) {
		if (getTooltipTarget() != null) {
			getTooltipTarget().removeObs(mouseObs);
		}
		target = component;
		showTimer.setOwner(getTooltipTarget());
		if (getTooltipTarget() != null) {
			getTooltipTarget().addObs(mouseObs);
		}
	}
	
	public PComponent getTooltipTarget() {
		return target;
	}
	
	public void setContent(PComponent component) {
		getLayout().setContent(component);
	}
	
	public PComponent getContent() {
		return getLayout().getContent();
	}
	
	public int getOverlayPositionX() {
		return showX;
	}
	
	public int getOverlayPositionY() {
		return showY;
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
	
	protected void onMouseMoved(PMouse mouse) {
		if (isShown) {
			if (!isMouseWithinClippedBounds()) {
				removeFromOverlay();
			}
		} else {
			boolean isWithin = target.isMouseWithinClippedBounds();
			if (isWithin) {
				int newShowX = mouse.getX() - 4;
				int newShowY = mouse.getY() - 4;
				if (showX != newShowX || showY != newShowY) {
					showX = newShowX;
					showY = newShowY;
					if (showTimer.isStarted()) {
						showTimer.restart();
					}
				}
				showTimer.start();
			} else {
				showTimer.stop();
			}
		}
	}
	
	protected void addToOverlay(double deltaTime) {
		super.addToOverlay();
		isShown = true;
	}
	
	protected void removeFromOverlay() {
		super.removeFromOverlay();
		isShown = false;
	}
	
	protected PRootOverlay getOverlay() {
		if (target == null || target.getRoot() == null) {
			return null;
		}
		return target.getRoot().getOverlay();
	}
	
}