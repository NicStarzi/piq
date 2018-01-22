package edu.udo.piq.scroll2;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.Axis;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PScrollPanel extends AbstractPLayoutOwner implements PScrollComponent {
	
	public PScrollPanel() {
		super();
		setLayout(new PScrollPanelLayout(this));
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
	
	@Override
	public PSize getDefaultPreferredSize() {
		PComponent body = getBody();
		if (body != null) {
			return body.getDefaultPreferredSize();
		}
		prefSize.set(50, 50);
		return prefSize;
	}
	
	protected void onScrollChanged(Axis axis) {
		fireReLayOutEvent();
	}
	
	protected void onBodyPrefSizeChanged() {
		PSize bodyPrefSize = getBody().getPreferredSize();
		getLayoutInternal().barX.setBodyPrefSize(bodyPrefSize.getWidth());
		getLayoutInternal().barY.setBodyPrefSize(bodyPrefSize.getHeight());
	}
	
	protected void onBodyLaidOut(int bodyW, int bodyH) {
		getLayoutInternal().barX.setBodyActualSize(bodyW);
		getLayoutInternal().barY.setBodyActualSize(bodyH);
	}
	
	@Override
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