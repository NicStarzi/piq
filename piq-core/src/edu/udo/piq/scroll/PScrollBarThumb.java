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

public class PScrollBarThumb extends AbstractPComponent {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
	private final PMouseObs mouseObs = new PMouseObs() {
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && isMouseOver()) {
				getModel().setPressed(true);
			}
		}
		public void onButtonReleased(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT) {
				getModel().setPressed(false);
			}
		}
	};
	protected final PButtonModelObs modelObs = new PButtonModelObs() {
		public void onChange(PButtonModel model) {
			fireReRenderEvent();
		}
	};
	protected PButtonModel model;
	
	public PScrollBarThumb() {
		super();
		setModel(new DefaultPButtonModel());
		addObs(mouseObs);
	}
	
//	public double getScroll() {
//		return 0.75;
//	}
//	
//	public double getSize() {
//		return 0.1;
//	}
	
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
		
		renderer.setColor(PColor.BLACK);
		renderer.strokeBottom(x, y, fx, fy);
		renderer.strokeRight(x, y, fx, fy);
		renderer.setColor(PColor.WHITE);
		renderer.strokeTop(x, y, fx, fy);
		renderer.strokeLeft(x, y, fx, fy);
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
}