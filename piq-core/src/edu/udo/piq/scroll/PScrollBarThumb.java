package edu.udo.piq.scroll;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PButtonModel;
import edu.udo.piq.components.PButtonModelObs;
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PScrollBarThumb extends AbstractPComponent {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
//	private final PMouseObs mouseObs = new PMouseObs() {
//		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
//			if (isActive() && btn == MouseButton.LEFT && isMouseOver()) {
//				getModel().setPressed(true);
//				pressX = mouse.getX();
//				pressY = mouse.getY();
////				PBounds bounds = getBounds();
////				pressX = mouse.getX() - bounds.getX();
////				pressY = mouse.getY() - bounds.getY();
////				System.out.println("px="+pressX+", py="+pressY);
//			}
//		}
//		public void onButtonReleased(PMouse mouse, MouseButton btn) {
//			if (isActive() && btn == MouseButton.LEFT) {
//				getModel().setPressed(false);
//			}
//		}
//		public void onMouseMoved(PMouse mouse) {
//			if (isPressed()) {
//				int mx = mouse.getX();
//				int my = mouse.getY();
//				int tx = mx - pressX;
//				int ty = my - pressY;
//				System.out.println("tx="+tx+", ty="+ty);
//			}
//		}
//	};
	protected final PButtonModelObs modelObs = new PButtonModelObs() {
		public void onChange(PButtonModel model) {
//			System.out.println("PScrollBarThumb.onChange()");
			fireReRenderEvent();
		}
	};
	protected PButtonModel model;
	protected boolean active;
	protected int pressX;
	protected int pressY;
	
	public PScrollBarThumb() {
		super();
		setModel(new DefaultPButtonModel());
//		addObs(mouseObs);
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
	
	public void setActive(boolean value) {
		active = value;
		getModel().setPressed(false);
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean isPressed() {
		if (getModel() == null) {
			return false;
		}
		return isActive() && getModel().isPressed();
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		if (isActive()) {
			renderer.setColor(PColor.BLACK);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			renderer.setColor(PColor.WHITE);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(PColor.GREY25);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			renderer.setColor(PColor.GREY875);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
}