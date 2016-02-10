package edu.udo.piq.components.popup;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimer;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.tools.MutablePSize;

public class PPopupSubMenu extends PPopupLabel implements PPopupComponent {
	
	public static final int DEFAULT_HOVER_TIMER_DELAY = 24;
	public static final int DEFAULT_TEXT_TRIANGLE_GAP = 2;
	public static final int DEFAULT_TRIANGLE_WIDTH = 8;
	public static final int DEFAULT_TRIANGLE_HEIGHT = DEFAULT_TRIANGLE_WIDTH;
	public static final PColor DISABLED_TEXT_COLOR = PColor.GREY50;
	
	protected final PMouseObs mouseObs = new PMouseObs() {
		public void onMouseMoved(PMouse mouse) {
			PPopupSubMenu.this.onMouseMoved(mouse);
		}
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			PPopupSubMenu.this.onMouseButtonTriggered(mouse, btn);
		}
		public void onButtonPressed(PMouse mouse, MouseButton btn) {
			PPopupSubMenu.this.onMouseButtonPressed(mouse, btn);
		}
		public void onButtonReleased(PMouse mouse, MouseButton btn) {
			PPopupSubMenu.this.onMouseButtonReleased(mouse, btn);
		}
	};
	protected final PTimer hoverTimer = new PTimer(this, () -> showSubMenu());
	protected final MutablePSize prefSize = new MutablePSize();
	protected boolean enabled = true;
	protected boolean subMenuShown = false;
	
	public PPopupSubMenu(Object defaultModelValue) {
		this();
		getModel().setValue(defaultModelValue);
	}
	
	public PPopupSubMenu(PTextModel model) {
		this();
		setModel(model);
	}
	
	public PPopupSubMenu() {
		super();
		addObs(mouseObs);
		getHoverTimer().setDelay(DEFAULT_HOVER_TIMER_DELAY);
		getHoverTimer().setRepeating(false);
	}
	
	public void setEnabled(boolean value) {
		enabled = value;
		if (!isEnabled()) {
			hideSubMenu();
		}
		fireReRenderEvent();
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isSubMenuShown() {
		return subMenuShown;
	}
	
	public PSize getDefaultPreferredSize() {
		PSize superSize = super.getDefaultPreferredSize();
		prefSize.setWidth(superSize.getWidth() + 
				DEFAULT_TEXT_TRIANGLE_GAP + DEFAULT_TRIANGLE_WIDTH);
		prefSize.setHeight(Math.max(superSize.getHeight(), DEFAULT_TRIANGLE_HEIGHT));
		return prefSize;
	}
	
	public void setHighlighted(boolean value) {
		boolean prevIsHighlighted = isHighlighted();
		super.setHighlighted(value);
		boolean nowIsHighlighted = isHighlighted();
		
		if (prevIsHighlighted == nowIsHighlighted) {
			return;
		}
		if (nowIsHighlighted) {
			if (getHoverTimer().isStarted()) {
				getHoverTimer().restart();
			} else {
				getHoverTimer().start();
			}
		} else {
			getHoverTimer().stop();
			hideSubMenu();
		}
	}
	
	public boolean isHighlighted() {
		return isEnabled() && (isSubMenuShown() || super.isHighlighted());
	}
	
	public void defaultRender(PRenderer renderer) {
		super.defaultRender(renderer);
		
		PBounds bnds = getBounds();
		int triangleFx = bnds.getFinalX();
		int triangleX = triangleFx - DEFAULT_TRIANGLE_WIDTH;
		int triangleY = bnds.getY() + bnds.getHeight() / 2 - DEFAULT_TRIANGLE_HEIGHT / 2;
		int triangleFy = triangleY + DEFAULT_TRIANGLE_HEIGHT;
		
		renderer.setRenderMode(renderer.getRenderModeFill());
		renderer.setColor(getDefaultTextColor());
		renderer.drawTriangle(triangleX, triangleY, 
				triangleX, triangleFy, 
				triangleFx, triangleY + DEFAULT_TRIANGLE_HEIGHT / 2);
	}
	
	protected PColor getDefaultTextColor() {
		if (isEnabled()) {
			return super.getDefaultTextColor();
		}
		return DISABLED_TEXT_COLOR;
	}
	
	protected PTimer getHoverTimer() {
		return hoverTimer;
	}
	
	protected void showSubMenu() {
		System.out.println("PPopupSubMenu.showSubMenu()");
	}
	
	protected void hideSubMenu() {
		
	}
	
	protected void onMouseMoved(PMouse mouse) {
		if (isEnabled()) {
			setHighlighted(isMouseOverThisOrChild());
		}
	}
	
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
	}
	
}