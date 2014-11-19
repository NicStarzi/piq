package edu.udo.piq.components.defaults;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PListCellComponent;
import edu.udo.piq.components.PListCellFactory;
import edu.udo.piq.components.PListModel;

public class DefaultPListCellFactory implements PListCellFactory {
	
	public PListCellComponent getCellComponentFor(PListModel listModel, int index) {
		Object text = listModel.getElement(index).toString();
		
		PListCellLabel label = new PListCellLabel();
		label.getModel().setText(text);
		
		return label;
	}
	
	protected static class PListCellLabel extends PLabel implements PListCellComponent {
		
		protected static final PColor DEFAULT_TEXT_SELECTED_COLOR = PColor.WHITE;
		protected static final PColor DEFAULT_BACKGROUND_SELECTED_COLOR = PColor.BLUE;
		
		protected boolean selected;
		
		public void setSelected(boolean isSelected) {
			selected = isSelected;
			fireReRenderEvent();
		}
		
		public boolean isSelected() {
			return selected;
		}
		
		public void elementChanged(PListModel model, Integer index) {
			getModel().setText(model.getElement(index).toString());
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
			
			super.defaultRender(renderer);
		}
		
	}
	
}