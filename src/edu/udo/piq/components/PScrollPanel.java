package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.layouts.PScrollPanelLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.PCompUtil;

public class PScrollPanel extends AbstractPLayoutOwner {
	
	public PScrollPanel() {
		super();
		setLayout(new PScrollPanelLayout(this));
		
		final PScrollBarHorizontal scrollH = new PScrollBarHorizontal();
		scrollH.getModel().addObs(new PScrollBarModelObs() {
			public void sizeChanged(PScrollBarModel model) {
			}
			public void scrollChanged(PScrollBarModel model) {
				getLayout().scrollChanged();
			}
		});
		final PScrollBarVertical scrollV = new PScrollBarVertical();
		scrollV.getModel().addObs(new PScrollBarModelObs() {
			public void sizeChanged(PScrollBarModel model) {
			}
			public void scrollChanged(PScrollBarModel model) {
				getLayout().scrollChanged();
			}
		});
		getLayout().addObs(new PLayoutObs() {
			public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
				if (constraint == PScrollPanelLayout.Constraint.VIEW) {
					scrollH.getModel().setContentSize(child.getDefaultPreferredSize().getWidth());
					scrollH.getModel().setViewportSize(PCompUtil.getBoundsOf(getParent()).getWidth());
					
					scrollV.getModel().setContentSize(child.getDefaultPreferredSize().getHeight());
					scrollV.getModel().setViewportSize(PCompUtil.getBoundsOf(getParent()).getHeight());
				}
			}
		});
		
		getLayout().addChild(scrollH, PScrollPanelLayout.Constraint.HORIZONTAL_SCROLL_BAR);
		getLayout().addChild(scrollV, PScrollPanelLayout.Constraint.VERTICAL_SCROLL_BAR);
	}
	
	public PScrollPanelLayout getLayout() {
		return (PScrollPanelLayout) super.getLayout();
	}
	
	public void setView(PComponent component) {
		getLayout().setView(component);
	}
	
	public PComponent getView() {
		return getLayout().getView();
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.BLACK);
		renderer.drawQuad(x, y, fx, fy);
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
	}
	
}