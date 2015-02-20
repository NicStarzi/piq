package edu.udo.piq.components.defaults;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PTreeCellComponent;
import edu.udo.piq.components.PTreeModel;

public class DefaultPTreeCellComponent extends PLabel implements PTreeCellComponent {
	
	protected static final PColor DEFAULT_TEXT_SELECTED_COLOR = PColor.WHITE;
	protected static final PColor DEFAULT_BACKGROUND_SELECTED_COLOR = PColor.BLUE;
	protected static final PColor DEFAULT_DROP_HIGHLIGHT_COLOR = PColor.RED;
	
	protected boolean selected;
	protected DropHighlightType dropHighlight;
	
	public void setSelected(boolean isSelected) {
		selected = isSelected;
		fireReRenderEvent();
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setDropHighlightType(DropHighlightType type) {
		dropHighlight = type;
		fireReRenderEvent();
	}
	
	public DropHighlightType getDropHighlightType() {
		return dropHighlight;
	}
	
	public void setNode(PTreeModel model, Object parent, int index) {
		if (parent == null && index == -1) {
			getModel().setValue(model.getRoot());
		} else {
			getModel().setValue(model.getChild(parent, index));
		}
	}
	
	public Object getNode() {
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
		DropHighlightType dhType = getDropHighlightType();
		if (dhType != null) {
			renderer.setColor(PColor.YELLOW);
			renderer.drawQuad(getBounds());
//			PBounds bounds = getBounds();
//			int x = 0;
//			int y = 0;
//			int fx = 0;
//			int fy = 0;
//			
//			switch (dhType) {
//			case BEFORE:
//				System.out.println("before");
//				x = bounds.getX();
//				y = bounds.getY();
//				fx = bounds.getFinalX();
//				fy = y + 2;
//				break;
//			case BEHIND:
//				System.out.println("behind");
//				x = bounds.getX();
//				y = bounds.getFinalY();
//				fx = bounds.getFinalX();
//				fy = y + 2;
//				break;
//			case INSIDE:
//				System.out.println("inside");
//				x = bounds.getFinalX();
//				y = bounds.getY();
//				fx = x + 2;
//				fy = bounds.getFinalY();
//				break;
//			default:
//				break;
//			}
//			renderer.setColor(DEFAULT_DROP_HIGHLIGHT_COLOR);
//			System.out.println("x="+x+", y="+y+", fx="+fx+", fy="+fy);
//			renderer.drawQuad(x, y, fx, fy);
		}
		
		super.defaultRender(renderer);
	}
	
}