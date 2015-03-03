package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.MutablePSize;

public class PCentricLayout extends AbstractPLayout {
	
	/**
	 * To save memory the preferred size of the layout 
	 * is an instance of MutablePSize which is updated 
	 * and returned by the {@link #getPreferredSize()} 
	 * method.<br>
	 */
	protected final MutablePSize prefSize;
	protected PInsets insets = new ImmutablePInsets(4);
	protected PComponent content;
	
	public PCentricLayout(PComponent component) {
		super(component);
		prefSize = new MutablePSize();
		
		addObs(new PLayoutObs() {
			public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
				content = child;
			}
			public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
				content = null;
			}
		});
	}
	
	public void setInsets(PInsets insets) {
		this.insets = insets;
		fireInvalidateEvent();
	}
	
	public PInsets getInsets() {
		return insets;
	}
	
	public void setContent(PComponent component) {
		if (getContent() != null) {
			removeChild(component);
		}
		addChild(component, null);
	}
	
	public PComponent getContent() {
		return content;
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint == null;
	}
	
	public void layOut() {
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
		if (content != null) {
			PSize contentSize = getPreferredSizeOf(content);
			prefW += contentSize.getWidth();
			prefH += contentSize.getHeight();
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
}