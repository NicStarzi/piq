package edu.udo.piq.scroll;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.PButtonModel;
import edu.udo.piq.components.PButtonModelObs;
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PScrollBarButton extends AbstractPComponent {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
	private final PMouseObs mouseObs = new PMouseObs() {
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && isMouseOver()) {
				model.setPressed(true);
			}
		}
		public void buttonReleased(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT) {
				model.setPressed(false);
			}
		}
	};
	protected final PButtonModelObs modelObs = new PButtonModelObs() {
		public void onChange(PButtonModel model) {
			fireReRenderEvent();
		}
	};
	protected PButtonModel model;
	protected Direction dir = Direction.UP;
	
	public PScrollBarButton() {
		super();
		setModel(new DefaultPButtonModel());
		addObs(mouseObs);
	}
	
	public void setDirection(Direction direction) {
		dir = direction;
		fireReRenderEvent();
	}
	
	public Direction getDirection() {
		return dir;
	}
	
	protected void setModel(PButtonModel model) {
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
		int padding = 3;
		int w = bnds.getWidth() - padding * 2;
		int h = bnds.getHeight() - padding * 2;
		int ax1 = 0;
		int ay1 = 0;
		int ax2 = 0;
		int ay2 = 0;
		int ax3 = 0;
		int ay3 = 0;
		switch (getDirection()) {
		case DOWN:
			ax1 = x + padding;
			ay1 = y + padding;
			ax2 = x + padding + w / 2;
			ay2 = fy - padding;
			ax3 = fx - padding;
			ay3 = ay1;
			break;
		case LEFT:
			ax1 = x + padding;
			ay1 = y + padding + h / 2;
			ax2 = fx - padding;
			ay2 = fy - padding;
			ax3 = ax2;
			ay3 = y + padding;
			break;
		case RIGHT:
			ax1 = x + padding;
			ay1 = y + padding;
			ax2 = ax1;
			ay2 = fy - padding;
			ax3 = fx - padding;
			ay3 = y + padding + h / 2;
			break;
		case UP:
			ax1 = x + padding + w / 2;
			ay1 = y + padding;
			ax2 = x + padding;
			ay2 = fy - padding;
			ax3 = fx - padding;
			ay3 = ay2;
			break;
		default:
			break;
		}
		renderer.setColor(PColor.BLACK);
		renderer.drawTriangle(ax1, ay1, ax2, ay2, ax3, ay3);
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public static enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT,
		;
	}
	
}