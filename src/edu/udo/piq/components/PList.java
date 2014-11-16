package edu.udo.piq.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPListSelection;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PCompUtil;

public class PList extends AbstractPComponent {
	
	protected static final String DEFAULT_FONT_NAME = "Arial";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	protected static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	protected static final PColor DEFAULT_TEXT_SELECTED_COLOR = PColor.WHITE;
	protected static final PColor DEFAULT_SELECTION_HIGHLIGHT_COLOR = PColor.BLUE;
	
	private final PListModelObs modelObs = new PListModelObs() {
		public void elementRemoved(PListModel model, Object element, int index) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
		public void elementChanged(PListModel model, Object element, int index) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
		public void elementAdded(PListModel model, Object element, int index) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	private PListModel model;
	private final PListSelectionObs selectionObs = new PListSelectionObs() {
		public void selectionRemoved(PListSelection selection, int index) {
			fireReRenderEvent();
		}
		public void selectionAdded(PListSelection selection, int index) {
			fireReRenderEvent();
		}
	};
	private PListSelection selection;
	
	public PList() {
		setSelection(new DefaultPListSelection());
	}
	
	public void setSelection(PListSelection selection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
		}
		this.selection = selection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
		}
	}
	
	public PListSelection getSelection() {
		return selection;
	}
	
	public void setModel(PListModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public PListModel getModel() {
		return model;
	}
	
	public List<Object> getSelectedElements() {
		if (getSelection() == null || getModel() == null) {
			return Collections.emptyList();
		}
		Set<Integer> selectedIndices = getSelection().getSelection();
		List<Object> selectedElements = new ArrayList<>();
		PListModel model = getModel();
		for (Integer index : selectedIndices) {
			selectedElements.add(model.getElement(index));
		}
		return Collections.unmodifiableList(selectedElements);
	}
	
	protected void onUpdate() {
		PListSelection selection = getSelection();
		if (getModel() == null || selection == null) {
			return;
		}
		PMouse mouse = PCompUtil.getMouseOf(this);
		PKeyboard keyboard = PCompUtil.getKeyboardOf(this);
		if (mouse == null) {
			return;
		}
		PComponent mouseOwner = mouse.getOwner();
		if (mouseOwner != null && mouseOwner != this) {
			return;
		}
		
		PFontResource font = getDefaultFont();
		if (mouse.isTriggered(MouseButton.LEFT) 
				&& PCompUtil.isMouseContained(this, PCompUtil.getClippedBoundsOf(this))) {
			int my = mouse.getY();
			
			int index = -1;
			int y = getBounds().getY();
			for (int i = 0; i < getModel().getElementCount(); i++) {
				String text = getTextForElement(i);
				PSize size = font.getSize(text);
				if (my < y + size.getHeight()) {
					index = i;
					break;
				}
				y += size.getHeight();
			}
			if (index != -1) {
				if (keyboard != null && keyboard.isPressed(Key.CTRL)) {
					if (selection.isSelected(index)) {
						selection.removeSelection(index);
					} else {
						selection.addSelection(index);
					}
				} else if (keyboard != null && keyboard.isPressed(Key.SHIFT)) {
					selection.addSelection(index);
				} else {
					selection.clearSelection();
					selection.addSelection(index);
				}
			}
		}
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
		
		PListModel model = getModel();
		PListSelection selection = getSelection();
		PFontResource font = getDefaultFont();
		for (int i = 0; i < model.getElementCount(); i++) {
			String text = getTextForElement(i);
			PSize size = font.getSize(text);
			if (selection != null && selection.isSelected(i)) {
				renderer.setColor(DEFAULT_SELECTION_HIGHLIGHT_COLOR);
				renderer.drawQuad(x + 1, y, fx - 1, y + size.getHeight());
				renderer.setColor(DEFAULT_TEXT_SELECTED_COLOR);
			} else {
				renderer.setColor(getDefaultTextColor());
			}
			renderer.drawString(font, text, x + 1, y);
			y += size.getHeight();
		}
	}
	
	public PSize getDefaultPreferredSize() {
		int maxW = 0;
		int prefH = 0;
		PFontResource font = getDefaultFont();
		for (int i = 0; i < model.getElementCount(); i++) {
			String text = getTextForElement(i);
			PSize size = font.getSize(text);
			if (maxW < size.getWidth()) {
				maxW = size.getWidth();
			}
			prefH += size.getHeight();
		}
		if (maxW < 50) {
			maxW = 50;
		}
		return new ImmutablePSize(maxW, prefH);
	}
	
	protected String getTextForElement(int index) {
		Object element = getModel().getElement(index);
		if (element == null) {
			return "";
		}
		return element.toString();
	}
	
	protected PColor getDefaultTextColor() {
		return DEFAULT_TEXT_COLOR;
	}
	
	protected PFontResource getDefaultFont() {
		return getRoot().fetchFontResource(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
	}
	
}