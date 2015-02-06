package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.PRenderUtil;

public class PToolTip2 extends AbstractPLayoutOwner {
	
	private final PLabel label;
	private PComponent target;
	
	public PToolTip2() {
		super();
		setLayout(new PCentricLayout(this));
		label = new PLabel();
		getLayout().setContent(label);
	}
	
	public void setTooltipComponent(PComponent component) {
		target = component;
	}
	
	public PComponent getTooltipComponent() {
		return target;
	}
	
	public void setModel(PTextModel model) {
		label.setModel(model);
	}
	
	public PTextModel getModel() {
		return label.getModel();
	}
	
	public PCentricLayout getLayout() {
		return (PCentricLayout) super.getLayout();
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		renderer.setColor(PColor.BLACK);
		PRenderUtil.strokeQuad(renderer, x, y, fx, fy);
	}
	
	public PSize getDefaultPreferredSize() {
		if (getLayout() != null) {
			return getLayout().getPreferredSize();
		}
		return PSize.NULL_SIZE;
	}
	
}