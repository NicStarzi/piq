package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PInsets;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PDropDown extends AbstractPLayoutOwner {
	
	private static final int TRIANGLE_MIN_W = 12;
	
	protected final ObserverList<PDropDownObs> obsList
		= PCompUtil.createDefaultObserverList();
	private final PMouseObs mouseObs = new PMouseObs() {
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && getModel() != null && isMouseOverThisOrChild()) {
				getModel().setPressed(true);
//				setPressed(true);
			}
		}
		public void buttonReleased(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && getModel() != null) {
				boolean oldPressed = isPressed();
//				setPressed(false);
				getModel().setPressed(false);
				if (oldPressed && isMouseOverThisOrChild()) {
					if (bodyShown) {
						hideDropDown();
					} else {
						showDropDown();
					}
				}
			}
		}
	};
	private final PComponentObs containerObs = new PComponentObs() {
		public void preferredSizeChanged(PComponent component) {
			if (bodyShown) {
				repositionDropDownContainer();
			}
		}
	};
	private final PButtonModelObs modelObs = new PButtonModelObs() {
		public void onChange(PButtonModel model) {
			fireReRenderEvent();
		}
	};
	private final PDropDownContainer dropDownContainer;
	private PButtonModel model;
	private boolean bodyShown = false;
	
	public PDropDown() {
		super();
		dropDownContainer = new PDropDownContainer(this);
		dropDownContainer.addObs(containerObs);
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PButtonModel defaultModel = new DefaultPButtonModel();
		if (modelFac != null) {
			defaultModel = (PButtonModel) modelFac.getModelFor(this, defaultModel);
		}
		
		setLayout(new PCentricLayout(this));
		getLayoutInternal().setInsets(new ImmutablePInsets(4, 4, 4, TRIANGLE_MIN_W + 2 + 2 + 4));
		setModel(defaultModel);
		
		addObs(mouseObs);
	}
	
	protected PCentricLayout getLayoutInternal() {
		return (PCentricLayout) super.getLayout();
	}
	
	public void setPreview(PComponent component) {
		getLayoutInternal().setContent(component);
	}
	
	public PComponent getPreview() {
		return getLayoutInternal().getContent();
	}
	
	public void setBody(PComponent component) {
		dropDownContainer.setContent(component);
	}
	
	public PComponent getBody() {
		return dropDownContainer.getContent();
	}
	
	public boolean isBodyVisible() {
		return bodyShown;
	}
	
	public void setModel(PButtonModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		fireReRenderEvent();
	}
	
	public PButtonModel getModel() {
		return model;
	}
	
	public boolean isPressed() {
		if (getModel() == null) {
			return false;
		}
		return getModel().isPressed();
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		if (isPressed()) {
			renderer.setColor(PColor.GREY25);
			renderer.strokeQuad(x, y, fx, fy);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(PColor.BLACK);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			renderer.setColor(PColor.WHITE);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		}
		
		PInsets insets = getLayoutInternal().getInsets();
		int btnX = fx - (insets.getFromRight() - 2);
		int btnY = y + insets.getFromTop();
		int btnFx = btnX + (insets.getFromRight() - 2 - insets.getFromLeft());
		int btnFy = fy - insets.getFromBottom();
		
		renderer.setColor(PColor.BLACK);
		renderer.strokeBottom(btnX, btnY, btnFx, btnFy);
		renderer.strokeRight(btnX, btnY, btnFx, btnFy);
		renderer.setColor(PColor.WHITE);
		renderer.strokeTop(btnX, btnY, btnFx, btnFy);
		renderer.strokeLeft(btnX, btnY, btnFx, btnFy);
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(btnX + 1, btnY + 1, btnFx - 1, btnFy - 1);
		
		int triaX1 = btnX + 4;
		int triaY1 = btnY + 4;
		int triaX2 = btnX + (btnFx - btnX) / 2;
		int triaY2 = btnFy - 4;
		int triaX3 = btnFx - 4;
		int triaY3 = triaY1;
		renderer.setColor(PColor.BLACK);
		renderer.drawTriangle(triaX1, triaY1, triaX2, triaY2, triaX3, triaY3);
	}
	
	public PSize getDefaultPreferredSize() {
		return getLayout().getPreferredSize();
	}
	
	private void showDropDown() {
		if (getBody() != null) {
			FreeConstraint constraint = new FreeConstraint(0, 0);
			getRoot().getOverlay().getLayout().addChild(dropDownContainer, constraint);
			bodyShown = true;
			repositionDropDownContainer();
			getRoot().setFocusOwner(null);
			fireShowEvent();
		}
	}
	
	private void hideDropDown() {
		dropDownContainer.getRoot().getOverlay().getLayout().removeChild(dropDownContainer);
		bodyShown = false;
		fireHideEvent();
	}
	
	private void repositionDropDownContainer() {
		PSize ownSize = PCompUtil.getPreferredSizeOf(dropDownContainer);
		int ownX = getBounds().getX();
		int ownY = getBounds().getFinalY();
		int ownW = ownSize.getWidth();
		int ownH = ownSize.getHeight();
		
		PRootOverlay overlay = getRoot().getOverlay();
		PBounds overlayBounds = overlay.getBounds();
		int overlayX = overlayBounds.getX();
		int overlayY = overlayBounds.getY();
		int overlayW = overlayBounds.getWidth();
		int overlayH = overlayBounds.getHeight();
		
		if (ownX + ownW > overlayX + overlayW) {
			ownX = (overlayX + overlayW) - ownW;
		}
		if (ownY + ownH > overlayY + overlayH) {
			ownY = getBounds().getY() - ownH;
			if (ownY < overlayY) {
				ownY = overlayY;
			}
		}
		PFreeLayout overlayLayout = overlay.getLayout();
		FreeConstraint constr = new FreeConstraint(ownX, ownY);//, ownW, ownH
		overlayLayout.updateConstraint(dropDownContainer, constr);
	}
	
	public void addObs(PDropDownObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PDropDownObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireShowEvent() {
		for (PDropDownObs obs : obsList) {
			obs.bodyShown(this);
		}
	}
	
	protected void fireHideEvent() {
		for (PDropDownObs obs : obsList) {
			obs.bodyHidden(this);
		}
	}
	
	protected static class PDropDownContainer extends AbstractPLayoutOwner {
		
		public PDropDownContainer(PDropDown dropDown) {
			super();
			setLayout(new PCentricLayout(this));
			getLayout().setInsets(new ImmutablePInsets(1));
			addObs(new PMouseObs() {
				public void buttonTriggered(PMouse mouse, MouseButton btn) {
					if (!dropDown.isMouseOverThisOrChild() && !isMouseOverThisOrChild()) {
						dropDown.hideDropDown();
					}
				}
			});
		}
		
		public PCentricLayout getLayout() {
			return (PCentricLayout) super.getLayout();
		}
		
		public void setContent(PComponent component) {
			getLayout().setContent(component);
		}
		
		public PComponent getContent() {
			return getLayout().getContent();
		}
		
		public void defaultRender(PRenderer renderer) {
			PBounds bnds = getBounds();
			int x = bnds.getX();
			int y = bnds.getY();
			int fx = bnds.getFinalX();
			int fy = bnds.getFinalY();
			
			renderer.setColor(PColor.BLACK);
			renderer.strokeQuad(x, y, fx, fy);
			renderer.setColor(PColor.WHITE);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		}
		
		public PSize getDefaultPreferredSize() {
			return getLayout().getPreferredSize();
		}
		
	}
	
}