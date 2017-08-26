package edu.udo.piq.components.defaults;

import java.util.function.Function;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.textbased.PLabel;

public class DefaultPCellComponent extends PLabel implements PCellComponent {
	
	public static final PColor DEFAULT_TEXT_SELECTED_COLOR			= PColor.WHITE;
	public static final PColor DEFAULT_BACKGROUND_SELECTED_COLOR	= PColor.DARK_BLUE;
	public static final PColor DEFAULT_DROP_HIGHLIGHT_COLOR			= PColor.RED;
	
	protected Function<Object, String> encoder;
	protected PModel cachedModel;
	protected PModelIndex cachedIndex;
	protected Object cachedElement;
	protected boolean selected;
	protected boolean highlighted;
	
	public DefaultPCellComponent() {
		this(null);
	}
	
	public DefaultPCellComponent(Function<Object, String> outputEncoder) {
		super();
		setOutputEncoder(outputEncoder);
	}
	
	public void setOutputEncoder(Function<Object, String> outputEncoder) {
		encoder = outputEncoder;
		refreshModelValue();
	}
	
	public Function<Object, String> getOutputEncoder() {
		return encoder;
	}
	
	@Override
	public void setSelected(boolean value) {
		if (selected != value) {
			selected = value;
			fireReRenderEvent();
		}
	}
	
	@Override
	public boolean isSelected() {
		return selected;
	}
	
	@Override
	public void setDropHighlighted(boolean value) {
		if (highlighted != value) {
			highlighted = value;
			fireReRenderEvent();
		}
	}
	
	@Override
	public boolean isDropHighlighted() {
		return highlighted;
	}
	
	@Override
	public void setElement(PModel model, PModelIndex index) {
		cachedModel = model;
		cachedIndex = index;
		cachedElement = model.get(index);
		refreshModelValue();
	}
	
	@Override
	public PModel getElementModel() {
		return cachedModel;
	}
	
	@Override
	public PModelIndex getElementIndex() {
		return cachedIndex;
	}
	
	@Override
	public Object getElement() {
		return cachedElement;
	}
	
	protected void refreshModelValue() {
		if (getOutputEncoder() == null) {
			getModel().setValue(cachedElement);
		} else {
			getModel().setValue(getOutputEncoder().apply(cachedElement));
		}
	}
	
	@Override
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
	
	@Override
	protected PColor getDefaultTextColor() {
		if (isSelected()) {
			return DEFAULT_TEXT_SELECTED_COLOR;
		}
		return super.getDefaultTextColor();
	}
	
}