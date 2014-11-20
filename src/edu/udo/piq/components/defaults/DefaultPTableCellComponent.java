package edu.udo.piq.components.defaults;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PTableCell;
import edu.udo.piq.components.PTableCellComponent;
import edu.udo.piq.components.PTableModel;

public class DefaultPTableCellComponent extends PLabel implements PTableCellComponent {
	
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
	
	public void cellChanged(PTableModel model, PTableCell cell) {
		Object obj = model.getCell(cell.getColumnIndex(), cell.getRowIndex());
		getModel().setText(obj.toString());
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