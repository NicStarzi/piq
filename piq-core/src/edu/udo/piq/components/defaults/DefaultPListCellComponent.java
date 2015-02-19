package edu.udo.piq.components.defaults;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PListCellComponent;
import edu.udo.piq.components.PListModel;

public class DefaultPListCellComponent extends PLabel implements PListCellComponent {
	
	protected static final PColor DEFAULT_TEXT_SELECTED_COLOR = PColor.WHITE;
	protected static final PColor DEFAULT_BACKGROUND_SELECTED_COLOR = PColor.BLUE;
	protected static final PColor DEFAULT_DROP_HIGHLIGHT_COLOR = PColor.BLUE;
	
	protected boolean selected;
	protected boolean dropHighlight;
	
	public void setSelected(boolean isSelected) {
		selected = isSelected;
		fireReRenderEvent();
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setDropHighlighted(boolean isHighlighted) {
		dropHighlight = isHighlighted;
		fireReRenderEvent();
	}
	
	public boolean isDropHighlighted() {
		return dropHighlight;
	}
	
	public void setElement(PListModel model, int index) {
		getModel().setValue(model.getElement(index));
	}
	
	public Object getElement() {
		return getModel().getValue();
	}
	
	protected PColor getDefaultTextColor() {
		if (isSelected()) {
			return DEFAULT_TEXT_SELECTED_COLOR;
		}
		return super.getDefaultTextColor();
	}
	
	public void defaultRender(PRenderer renderer) {
		if (isSelected()) {
			PBounds bounds = getBounds();
			int x = bounds.getX();
			int y = bounds.getY();
			int fx = bounds.getFinalX();
			int fy = bounds.getFinalY();
			
			renderer.setColor(DEFAULT_BACKGROUND_SELECTED_COLOR);
			renderer.drawQuad(x, y, fx, fy);
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
	
}