package edu.udo.piq.components.defaults;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.textbased.PLabel;

public class DefaultPCellComponent extends PLabel implements PCellComponent {
	
	public static final PColor DEFAULT_TEXT_SELECTED_COLOR			= PColor.WHITE;
	public static final PColor DEFAULT_BACKGROUND_SELECTED_COLOR	= PColor.BLUE;
	public static final PColor DEFAULT_DROP_HIGHLIGHT_COLOR			= PColor.RED;
	
	private boolean selected;
	private boolean highlighted;
	
	public void setSelected(boolean isSelected) {
		selected = isSelected;
		fireReRenderEvent();
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setDropHighlighted(boolean isHighlighted) {
		highlighted = isHighlighted;
		fireReRenderEvent();
	}
	
	public boolean isDropHighlighted() {
		return highlighted;
	}
	
	public void setElement(PModel model, PModelIndex index) {
		getModel().setValue(model.get(index));
	}
	
	public Object getElement() {
		return getModel().getValue();
	}
	
	public void defaultRender(PRenderer renderer) {
		if (isSelected()) {
			renderer.setColor(DEFAULT_BACKGROUND_SELECTED_COLOR);
			renderer.drawQuad(getBounds());
		}
		if (isDropHighlighted()) {
			PBounds bounds = getBounds();
			int x = bounds.getX();
			int y = bounds.getY();
			int fx = bounds.getFinalX();
			int fy = y + 2;
			
			renderer.setColor(DEFAULT_DROP_HIGHLIGHT_COLOR);
			renderer.drawQuad(x, y, fx, fy);
		}
		
		super.defaultRender(renderer);
	}
	
	protected PColor getDefaultTextColor() {
		if (isSelected()) {
			return DEFAULT_TEXT_SELECTED_COLOR;
		}
		return super.getDefaultTextColor();
	}
	
}