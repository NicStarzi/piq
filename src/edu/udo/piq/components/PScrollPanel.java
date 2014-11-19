package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPScrollBarModel;
import edu.udo.piq.layouts.PScrollLayout;
import edu.udo.piq.tools.AbstractPBounds;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.PCompUtil;

public class PScrollPanel extends AbstractPLayoutOwner {
	
//	private boolean isArmed;
	private final DefaultPScrollBarModel scrollModelH;
	
	public PScrollPanel() {
		super();
		setLayout(new PScrollLayout(this));
		getLayout().setParentBounds(new RelativePBounds(this, new ImmutablePInsets(1)));
		
		PScrollBarHorizontal scrollH = new PScrollBarHorizontal();
		scrollModelH = new DefaultPScrollBarModel();
		scrollModelH.setAlignment(true);
		scrollH.setModel(scrollModelH);
		
		getLayout().addChild(scrollH, PScrollLayout.Constraint.HORIZONTAL_SCROLL_BAR);
	}
	
	public PScrollLayout getLayout() {
		return (PScrollLayout) super.getLayout();
	}
	
	public void setView(PComponent component) {
		getLayout().setView(component);
		scrollModelH.setView(component);
	}
	
	public PComponent getView() {
		return getLayout().getView();
	}
	
	protected void onUpdate() {
//		PMouse mouse = PCompUtil.getMouseOf(this);
//		if (mouse == null) {
//			return;
//		}
//		PComponent mouseOwner = mouse.getOwner();
//		if (mouseOwner != null && mouseOwner != this) {
//			return;
//		}
//		if (isArmed) {
//			if (mouse.isPressed(MouseButton.LEFT)) {
//				int scrollX = getLayout().getScrollOffsetX();
//				int scrollY = getLayout().getScrollOffsetY();
//				int dx = mouse.getDeltaX();
//				int dy = mouse.getDeltaY();
//				scrollX += dx;
//				scrollY += dy;
//				getLayout().setScrollOffset(scrollX, scrollY);
//			} else {
//				isArmed = false;
//			}
//		} else {
//			int mx = mouse.getX();
//			int my = mouse.getY();
//			boolean within = getBounds().contains(mx, my);
//			
//			isArmed = within && mouse.isTriggered(MouseButton.LEFT);
//		}
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
	
	public PSize getDefaultPreferredSize() {
		return getLayout().getPreferredSize();
	}
	
//	public int getDefaultPreferredWidth() {
//		if (getLayout() != null) {
//			return getLayout().getPreferredWidth();
//		}
//		return 20;
//	}
//	
//	public int getDefaultPreferredHeight() {
//		if (getLayout() != null) {
//			return getLayout().getPreferredHeight();
//		}
//		return 20;
//	}
	
	protected static class RelativePBounds extends AbstractPBounds implements PBounds {
		
		protected final PComponent owner;
		protected PInsets insets;
		
		public RelativePBounds(PComponent owner, PInsets insets) {
			this.owner = owner;
			this.insets = insets;
		}
		
		public int getX() {
			return PCompUtil.getBoundsOf(owner).getX() + insets.getFromLeft();
		}
		
		public int getY() {
			return PCompUtil.getBoundsOf(owner).getY() + insets.getFromTop();
		}
		
		public int getWidth() {
			return PCompUtil.getBoundsOf(owner).getWidth() - insets.getHorizontal();
		}
		
		public int getHeight() {
			return PCompUtil.getBoundsOf(owner).getHeight() - insets.getVertical();
		}
		
	}
	
}