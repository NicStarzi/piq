package edu.udo.piq.components.popup;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PGlobalEventGenerator;
import edu.udo.piq.PGlobalEventProvider;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PClickable;
import edu.udo.piq.components.PIconLabel;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PPopupButton extends AbstractPLayoutOwner implements PPopupComponent, PClickable, PGlobalEventGenerator {
	
	public static final PColor DEFAULT_HIGHLIGHT_COLOR = PColor.BLUE;
	
	protected final ObserverList<PClickObs> obsListClick
			= PCompUtil.createDefaultObserverList();
	protected final ObserverList<PPopupComponentObs> obsListPopup
			= PCompUtil.createDefaultObserverList();
	protected PGlobalEventProvider globEvProv;
	protected boolean enabled = true;
	protected boolean pressed;
	protected boolean highlighted;
	
	public PPopupButton(Object iconImgID, Object labelModelValue) {
		this(new PIconLabel(new PPicture(iconImgID), 
				new PPopupLabel(labelModelValue)));
	}
	
	public PPopupButton(Object labelModelValue) {
		this(new PPopupLabel(labelModelValue));
	}
	
	public PPopupButton(PPopupComponent content) {
		this();
		setContent(content);
	}
	
	public PPopupButton() {
		super();
		setLayout(new PCentricLayout(this));
		getLayoutInternal().setInsets(new ImmutablePInsets(1));
		
		addObs(new PMouseObs() {
			public void onMouseMoved(PMouse mouse) {
				PPopupButton.this.onMouseMoved(mouse);
			}
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				PPopupButton.this.onMouseButtonTriggered(mouse, btn);
			}
			public void onButtonPressed(PMouse mouse, MouseButton btn) {
				PPopupButton.this.onMouseButtonPressed(mouse, btn);
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				PPopupButton.this.onMouseButtonReleased(mouse, btn);
			}
		});
	}
	
	public void setGlobalEventProvider(PGlobalEventProvider provider) {
		globEvProv = provider;
	}
	
	public PGlobalEventProvider getGlobalEventProvider() {
		return globEvProv;
	}
	
	protected PCentricLayout getLayoutInternal() {
		return (PCentricLayout) super.getLayout();
	}
	
	public void setContent(PComponent component) {
		getLayoutInternal().clearChildren();
		getLayoutInternal().addChild(component, null);
		highlightContent();
	}
	
	public PComponent getContent() {
		return getLayoutInternal().getContent();
	}
	
	protected void highlightContent() {
		PComponent content = getContent();
		if (content != null && content instanceof PPopupComponent) {
			PPopupComponent popupContent = (PPopupComponent) content;
			popupContent.setHighlighted(isHighlighted());
		}
	}
	
	public void setHighlighted(boolean value) {
		if (highlighted != value) {
			highlighted = value;
			if (isEnabled()) {
				fireReRenderEvent();
			}
			highlightContent();
		}
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}
	
	protected void setPressed(boolean isPressed) {
		if (pressed != isPressed) {
			pressed = isPressed;
			fireReRenderEvent();
		}
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public void setEnabled(boolean isEnabled) {
		if (enabled != isEnabled) {
			enabled = isEnabled;
			fireReRenderEvent();
			
			PComponent content = getContent();
			if (content != null && content instanceof PPopupComponent) {
				((PPopupComponent) content).setEnabled(isEnabled());
			}
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isFocusable() {
		return false;
	}
	
	public void defaultRender(PRenderer renderer) {
		if (isHighlighted()) {
			renderer.setColor(getHighlightColor());
			renderer.setRenderMode(renderer.getRenderModeFill());
			renderer.drawQuad(getBounds());
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return isHighlighted();
	}
	
	protected PColor getHighlightColor() {
		return DEFAULT_HIGHLIGHT_COLOR;
	}
	
	protected void onMouseMoved(PMouse mouse) {
		setHighlighted(isEnabled() && isMouseOverThisOrChild());
	}
	
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (isEnabled() && btn == MouseButton.LEFT 
				&& isMouseOverThisOrChild())
		{
			setPressed(true);
		}
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
		boolean oldPressed = isPressed();
		if (btn == MouseButton.LEFT && oldPressed) {
			setPressed(false);
			if (isEnabled() && isMouseOverThisOrChild()) {
				fireClickEvent();
			}
		}
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
		if (child instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) child;
			for (PPopupComponentObs obs : obsListPopup) {
				popupChild.addObs(obs);
			}
		}
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		if (child instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) child;
			for (PPopupComponentObs obs : obsListPopup) {
				popupChild.removeObs(obs);
			}
		}
	}
	
	public void addObs(PPopupComponentObs obs) {
		obsListPopup.add(obs);
		if (getContent() != null && getContent() instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) getContent();
			popupChild.addObs(obs);
		}
	}
	
	public void removeObs(PPopupComponentObs obs) {
		if (getContent() != null && getContent() instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) getContent();
			popupChild.removeObs(obs);
		}
		obsListPopup.remove(obs);
	}
	
	public void addObs(PClickObs obs) {
		obsListClick.add(obs);
	}
	
	public void removeObs(PClickObs obs) {
		obsListClick.remove(obs);
	}
	
	protected void fireClickEvent() {
		obsListClick.fireEvent((obs) -> obs.onClick(this));
		firePopupCloseEvent();
	}
	
	protected void firePopupCloseEvent() {
		obsListPopup.fireEvent((obs) -> obs.onClosePopup(this));
	}
	
}