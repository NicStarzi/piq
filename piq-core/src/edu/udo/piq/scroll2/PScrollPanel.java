package edu.udo.piq.scroll2;

import edu.udo.piq.CallSuper;
import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.layouts.Axis;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PScrollPanel extends AbstractPLayoutOwner implements PScrollComponent {
	
	public PScrollPanel() {
		super();
		setLayout(new PScrollPanelLayout(this));
	}
	
	@Override
	protected void checkForPreferredSizeChange() {
		super.checkForPreferredSizeChange();
	}
	
	public PScrollPanel(PComponent body) {
		this();
		setBody(body);
	}
	
	@Override
	protected PScrollPanelLayout getLayoutInternal() {
		return (PScrollPanelLayout) super.getLayout();
	}
	
	@Override
	public void setBorder(PBorder border) {
		super.setBorder(border);
	}
	
	public void setBody(PComponent comp) {
		getLayoutInternal().setBody(comp);
	}
	
	public PComponent getBody() {
		return getLayoutInternal().getBody();
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(getBoundsWithoutBorder());
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	@TemplateMethod
	protected void onScrollChanged(Axis axis) {
		fireReLayOutEvent();
	}
	
	@CallSuper
	@TemplateMethod
	protected void onBodyPrefSizeChanged() {
		PSize bodyPrefSize = getBody().getPreferredSize();
		getLayoutInternal().barX.setBodyPrefSize(bodyPrefSize.getWidth());
		getLayoutInternal().barY.setBodyPrefSize(bodyPrefSize.getHeight());
	}
	
	@CallSuper
	@TemplateMethod
	protected void onBodyLaidOut(int bodyW, int bodyH) {
		getLayoutInternal().barX.setBodyActualSize(bodyW);
		getLayoutInternal().barY.setBodyActualSize(bodyH);
	}
	
	@Override
	@TemplateMethod
	public void onScrollRequest(PComponent descendant, int x, int y, int fx, int fy) {
//		System.out.println("PScrollPanel.onChildRequestedScroll() child=" + child + "; x=" + offsetX + "; y="
//				+ offsetY);
		PScrollBar barX = getLayoutInternal().barX;
		PScrollBar barY = getLayoutInternal().barY;
		PBounds bounds = getBoundsWithoutBorder();
		int scrollToX = x;
		int scrollToY = y;
		int bodyActualX = bounds.getX();
		int bodyActualY = bounds.getY();
		int bodyActualFx = bodyActualX + barX.getBodyActualSize();
		int bodyActualFy = bodyActualY + barY.getBodyActualSize();
//		System.out.println("scrollTo=" + scrollToX + ", " + scrollToY);
//		System.out.println("vp=" + bodyActualX + ", " + bodyActualY + ", " + bodyActualFx + ", " + bodyActualFy);
		if (scrollToX < bodyActualX || scrollToX > bodyActualFx) {
			barX.setScroll(scrollToX);
		}
		if (scrollToY < bodyActualY || scrollToY > bodyActualFy) {
			barY.setScroll(scrollToY);
		}
	}
}