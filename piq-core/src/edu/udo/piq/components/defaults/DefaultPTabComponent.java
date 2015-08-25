package edu.udo.piq.components.defaults;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PTabComponent;
import edu.udo.piq.layouts.PCentricLayout;

public class DefaultPTabComponent extends PPanel implements PTabComponent {
	
	private PComponent preview = null;
	private boolean selected = false;
	private int index = -1;
	
	public DefaultPTabComponent() {
		setLayout(new PCentricLayout(this));
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		fireReRenderEvent();
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setPreview(PComponent component) {
		if (preview != null) {
			getLayout().removeChild(preview);
		}
		preview = component;
		if (preview != null) {
			getLayout().addChild(preview, null);
		}
		fireReRenderEvent();
	}
	
	public PComponent getPreview() {
		return preview;
	}
	
	public void setIndex(int index) {
		this.index = index;
		fireReRenderEvent();
	}
	
	public int getIndex() {
		return index;
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		if (isSelected()) {
			renderer.setColor(PColor.GREY875);
		} else {
			renderer.setColor(PColor.GREY75);
		}
		renderer.drawQuad(x, y, fx, fy);
		renderer.setColor(PColor.GREY50);
		if (isSelected()) {
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
		} else {
			renderer.strokeQuad(x, y, fx, fy);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		if (getLayout() != null) {
			return getLayout().getPreferredSize();
		}
		return DEFAULT_PREFERRED_SIZE;
	}
	
}