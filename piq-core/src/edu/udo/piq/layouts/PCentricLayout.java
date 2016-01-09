package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PLayoutDesign;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractArrayPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.MutablePSize;

public class PCentricLayout extends AbstractArrayPLayout {
	
	/**
	 * To save memory the preferred size of the layout 
	 * is an instance of MutablePSize which is updated 
	 * and returned by the {@link #getPreferredSize()} 
	 * method.<br>
	 */
	protected final MutablePSize prefSize;
	protected PInsets insets = new ImmutablePInsets(4);
	
	public PCentricLayout(PComponent component) {
		super(component, 1);
		prefSize = new MutablePSize();
	}
	
	public void setInsets(PInsets insets) {
		this.insets = insets;
		fireInvalidateEvent();
	}
	
	public PInsets getInsets() {
		PLayoutDesign design = getDesign();
		if (design == null) {
			return insets;
		}
		Object maybeInsets = getDesign().getAttribute(ATTRIBUTE_KEY_INSETS);
		if (maybeInsets != null && maybeInsets instanceof PInsets) {
			return (PInsets) maybeInsets;
		}
		return insets;
	}
	
	public void setContent(PComponent component) {
		if (getContent() != null) {
			removeChild(component);
		}
		addChild(component, null);
	}
	
	public PComponent getContent() {
		return getCompAt(0);
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint == null;
	}
	
	public void layOut() {
		PComponent content = getContent();
		if (content != null) {
			PBounds ob = getOwner().getBounds();
			PInsets insets = getInsets();
			
			int x = ob.getX() + insets.getFromLeft();
			int y = ob.getY() + insets.getFromTop();
			int w = (ob.getFinalX() - insets.getFromRight()) - x;
			int h = (ob.getFinalY() - insets.getFromBottom()) - y;
			
			PSize prefSize = getPreferredSizeOf(content);
			int prefW = prefSize.getWidth();
			int prefH = prefSize.getHeight();
			
			int compX;
			int compY;
			int compW;
			int compH;
			if (prefW > w) {
				compX = x;
				compW = w;
			} else {
				compX = x + w / 2 - prefW / 2;
				compW = prefW;
			}
			if (prefH > h) {
				compY = y;
				compH = h;
			} else {
				compY = y + h / 2 - prefH / 2;
				compH = prefH;
			}
			
			setChildBounds(content, compX, compY, compW, compH);
		}
	}
	
	public PSize getPreferredSize() {
		int prefW = getInsets().getHorizontal();
		int prefH = getInsets().getVertical();
		PComponent content = getContent();
		if (content != null) {
			PSize contentSize = getPreferredSizeOf(content);
			prefW += contentSize.getWidth();
			prefH += contentSize.getHeight();
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	protected int getIndexFor(Object constr) {
		if (constr == null) {
			return 0;
		}
		return -1;
	}
	
}