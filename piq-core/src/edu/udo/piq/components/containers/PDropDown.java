package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonModel;
import edu.udo.piq.components.PButtonModelObs;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.layouts.PTupleLayout.Distribution;
import edu.udo.piq.layouts.PTupleLayout.Orientation;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.MutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PDropDown extends AbstractPLayoutOwner {
	
//	private static final int TRIANGLE_MIN_W = 12;
	
	protected final ObserverList<PDropDownObs> obsList
		= PCompUtil.createDefaultObserverList();
	private final PMouseObs mouseObs = new PMouseObs() {
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			PDropDown.this.onMouseButtonTriggered(mouse, btn);
		}
		public void onButtonReleased(PMouse mouse, MouseButton btn) {
			PDropDown.this.onMouseButtonReleased(mouse, btn);
		}
	};
	private final PComponentObs containerObs = new PComponentObs() {
		public void onPreferredSizeChanged(PComponent component) {
			PDropDown.this.onPreferredSizeChanged();
		}
	};
	private final PButtonModelObs modelObs = (mdl) -> PDropDown.this.onModelChange();
	private final PButtonObs btnObs = (btn) -> onButtonClick();
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
		
		setLayout(new PTupleLayout(this));
		getLayoutInternal().setOrientation(Orientation.LEFT_TO_RIGHT);
		getLayoutInternal().setInsets(new ImmutablePInsets(4));
		getLayoutInternal().setDistribution(Distribution.RESPECT_SECOND);
		getLayoutInternal().setSecondaryDistribution(Distribution.RESPECT_NONE);
		setButton(new PDropDownButton());
		
		setModel(defaultModel);
		
		addObs(mouseObs);
	}
	
	protected PTupleLayout getLayoutInternal() {
		return (PTupleLayout) super.getLayout();
	}
	
	public void setPreview(PComponent component) {
		if (getPreview() != null) {
			getLayoutInternal().removeChild(Constraint.FIRST);
		}
		getLayoutInternal().addChild(component, Constraint.FIRST);
	}
	
	public PComponent getPreview() {
		return getLayoutInternal().getFirst();
	}
	
	public void setButton(PDropDownButton button) {
		if (getButton() != null) {
			getButton().removeObs(btnObs);
			getLayoutInternal().removeChild(Constraint.SECOND);
		}
		getLayoutInternal().addChild(button, Constraint.SECOND);
		if (getButton() != null) {
			getButton().addObs(btnObs);
		}
	}
	
	public PDropDownButton getButton() {
		return (PDropDownButton) getLayoutInternal().getSecond();
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
	}
	
	public PSize getDefaultPreferredSize() {
		return getLayout().getPreferredSize();
	}
	
	protected void showDropDown() {
		if (getBody() != null) {
			FreeConstraint constraint = new FreeConstraint(0, 0);
			getRoot().getOverlay().getLayout().addChild(dropDownContainer, constraint);
			bodyShown = true;
			repositionDropDownContainer();
			getRoot().setFocusOwner(null);
			fireShowEvent();
		}
	}
	
	protected void hideDropDown() {
		dropDownContainer.getRoot().getOverlay().getLayout().removeChild(dropDownContainer);
		bodyShown = false;
		fireHideEvent();
	}
	
	protected void repositionDropDownContainer() {
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
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && getModel() != null && isMouseOverThisOrChild()) {
			getModel().setPressed(true);
		}
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
		boolean oldPressed = isPressed();
		if (btn == MouseButton.LEFT && oldPressed) {
			getModel().setPressed(false);
			if (isMouseOverThisOrChild()) {
				if (isBodyVisible()) {
					hideDropDown();
				} else {
					showDropDown();
				}
			}
		}
	}
	
	protected void onPreferredSizeChanged() {
		if (isBodyVisible()) {
			repositionDropDownContainer();
		}
	}
	
	protected void onModelChange() {
		fireReRenderEvent();
	}
	
	protected void onButtonClick() {
	}
	
	public void addObs(PDropDownObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PDropDownObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireShowEvent() {
		obsList.fireEvent((obs) -> obs.onBodyShown(this));
	}
	
	protected void fireHideEvent() {
		obsList.fireEvent((obs) -> obs.onBodyHidden(this));
	}
	
	protected static class PDropDownContainer extends AbstractPLayoutOwner {
		
		public PDropDownContainer(PDropDown dropDown) {
			super();
			setLayout(new PCentricLayout(this));
			getLayout().setInsets(new ImmutablePInsets(1));
			addObs(new PMouseObs() {
				public void onButtonTriggered(PMouse mouse, MouseButton btn) {
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
	
	protected static class PDropDownButton extends PButton {
		
		public static final PSize DEFAULT_PREF_SIZE = new ImmutablePSize(14, 14);
		private MutablePSize prefSize = null;
		
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
			
			int triaX1 = x + 4;
			int triaY1 = y + 4;
			int triaX2 = x + (fx - x) / 2;
			int triaY2 = fy - 4;
			int triaX3 = fx - 4;
			int triaY3 = triaY1;
			renderer.setColor(PColor.BLACK);
			renderer.drawTriangle(triaX1, triaY1, triaX2, triaY2, triaX3, triaY3);
		}
		
		public PSize getDefaultPreferredSize() {
			PBounds bnds = getBounds();
			if (bnds != null && (bnds.getWidth() > DEFAULT_PREF_SIZE.getWidth() 
					|| bnds.getHeight() > DEFAULT_PREF_SIZE.getHeight())) 
			{
				if (prefSize == null) {
					prefSize = new MutablePSize(DEFAULT_PREF_SIZE);
				}
				int max = Math.max(bnds.getWidth(), bnds.getHeight());
				prefSize.setWidth(max);
				prefSize.setHeight(max);
				return prefSize;
			} else {
				return DEFAULT_PREF_SIZE;
			}
		}
	}
	
}