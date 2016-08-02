package edu.udo.piq.components.collections;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.defaults.DefaultPCellComponent;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PCellComponentWrapper extends AbstractPLayoutOwner implements PCellComponent {
	
	public static final PColor DEFAULT_BACKGROUND_COLOR				= 
			PColor.WHITE;
	public static final PColor DEFAULT_BACKGROUND_SELECTED_COLOR	= 
			DefaultPCellComponent.DEFAULT_BACKGROUND_SELECTED_COLOR;
	public static final PColor DEFAULT_DROP_HIGHLIGHT_COLOR			= 
			DefaultPCellComponent.DEFAULT_DROP_HIGHLIGHT_COLOR;
	
	private WrapperContentDelegator contentDel;
	private PModel model;
	private PModelIndex index;
	private Object element;
	private boolean selected;
	private boolean dropHL;
	
	public PCellComponentWrapper() {
		this(null);
	}
	
	public PCellComponentWrapper(PComponent content) {
		super();
		setLayout(new PCentricLayout(this));
		getLayout().setGrowContent(true);
		setContent(content);
	}
	
	public PCentricLayout getLayout() {
		return (PCentricLayout) super.getLayout();
	}
	
	public void setContent(PComponent content) {
		if (!getLayout().isEmpty()) {
			getLayout().removeChild(null);
		}
		if (content != null) {
			getLayout().addChild(content, null);
		}
	}
	
	public PComponent getContent() {
		return getLayout().getContent();
	}
	
	public void setContentDelegator(WrapperContentDelegator value) {
		contentDel = value;
	}
	
	public WrapperContentDelegator getContentDelegator() {
		return contentDel;
	}
	
	public void setSelected(boolean isSelected) {
		if (selected != isSelected) {
			selected = isSelected;
			fireReRenderEvent();
		}
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setDropHighlighted(boolean isHighlighted) {
		if (dropHL != isHighlighted) {
			dropHL = isHighlighted;
			fireReRenderEvent();
		}
	}
	
	public boolean isDropHighlighted() {
		return dropHL;
	}
	
	public void setElement(PModel model, PModelIndex index) {
		this.model = model;
		this.index = index;
		element = getElementModel().get(index);
		WrapperContentDelegator contentDel = getContentDelegator();
		if (contentDel != null) {
			PComponent content = getContent();
			contentDel.setElement(content, element, model, index);
		}
	}
	
	public PModel getElementModel() {
		return model;
	}
	
	public PModelIndex getElementIndex() {
		return index;
	}
	
	public Object getElement() {
		return element;
	}
	
	public void defaultRender(PRenderer renderer) {
		PColor backgroundColor = DEFAULT_BACKGROUND_COLOR;
		if (isSelected()) {
			backgroundColor = DEFAULT_BACKGROUND_SELECTED_COLOR;
		}
		renderer.setColor(backgroundColor);
		renderer.drawQuad(getBounds());
		if (isDropHighlighted()) {
			PBounds bounds = getBounds();
			int x = bounds.getX();
			int y = bounds.getY();
			int fx = bounds.getFinalX();
			int fy = y + 2;
			
			renderer.setColor(DEFAULT_DROP_HIGHLIGHT_COLOR);
			renderer.drawQuad(x, y, fx, fy);
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public static interface WrapperContentDelegator {
		public void setElement(PComponent content, Object element, PModel model, PModelIndex index);
	}
	
}