package edu.udo.piq.components.textbased;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.defaults.DefaultPTextSelection;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PTextField extends AbstractPComponent {
	
	protected static final String DEFAULT_FONT_NAME = "Arial";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	protected static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	protected static final PColor DEFAULT_TEXT_HIGHLIGHT_COLOR = PColor.WHITE;
	protected static final PColor DEFAULT_TEXT_SELECTION_COLOR = PColor.BLUE;
	protected static final PColor DEFAULT_BACKGROUND_COLOR = PColor.WHITE;
	protected static final int DEFAULT_FOCUS_RENDER_TOGGLE_TIMER_DELAY = 6;
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, 18);
	
	protected final ObserverList<PTextModelObs> modelObsList
		= PCompUtil.createDefaultObserverList();
	private final PTextModelObs modelObs = new PTextModelObs() {
		public void textChanged(PTextModel model) {
			fireReRenderEvent();
		}
	};
	private final PSelectionObs selectionObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			fireReRenderEvent();
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			fireReRenderEvent();
		}
		public void onLastSelectedChanged(PSelection selection, 
				PModelIndex prevLastSelected, PModelIndex newLastSelected) 
		{
			fireReRenderEvent();
		}
	};
	private PTextModel model;
	private PTextSelection selection;
	
	public PTextField() {
		super();
		setSelection(new DefaultPTextSelection());
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PTextModel defaultModel = new DefaultPTextModel();
		if (modelFac != null) {
			defaultModel = (PTextModel) modelFac.getModelFor(this, defaultModel);
		}
		setModel(defaultModel);
		
		addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				PTextField.this.onMouseButtonTriggred(mouse, btn);
			}
		});
		addObs(new PFocusObs() {
			public void focusGained(PComponent oldOwner, PComponent newOwner) {
				if (newOwner == PTextField.this && getSelection() != null) {
					fireReRenderEvent();
				}
			}
			public void focusLost(PComponent oldOwner) {
				if (oldOwner == PTextField.this && getSelection() != null) {
					fireReRenderEvent();
				}
			}
		});
	}
	
	public PTextField(PTextModel model) {
		super();
		setModel(model);
	}
	
	public PTextField(Object defaultModelValue) {
		this();
		getModel().setValue(defaultModelValue);
	}
	
	public void setModel(PTextModel model) {
		PTextModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			for (PTextModelObs obs : modelObsList) {
				oldModel.removeObs(obs);
			}
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			for (PTextModelObs obs : modelObsList) {
				model.addObs(obs);
			}
		}
		fireReRenderEvent();
	}
	
	public PTextModel getModel() {
		return model;
	}
	
	public void setSelection(PTextSelection textSelection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
//			for (PSelectionObs obs : selectionObsList) {
//				getSelection().removeObs(obs);
//			}
		}
		selection = textSelection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
//			for (PSelectionObs obs : selectionObsList) {
//				getSelection().addObs(obs);
//			}
		}
	}
	
	public PTextSelection getSelection() {
		return selection;
	}
	
	public String getText() {
		if (getModel() == null) {
			return "";
		}
		Object text = getModel().getText();
		if (text == null) {
			return "";
		}
		return text.toString();
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		// Render border
		renderer.setColor(PColor.BLACK);
		renderer.strokeTop(x + 1, y + 1, fx - 1, fy - 1);
		renderer.strokeLeft(x + 1, y + 1, fx - 1, fy - 1);
		renderer.setColor(PColor.GREY50);
		renderer.strokeTop(x, y, fx, fy);
		renderer.strokeLeft(x, y, fx, fy);
		
		renderer.setColor(PColor.GREY50);
		renderer.strokeBottom(x + 1, y + 1, fx - 1, fy - 1);
		renderer.strokeRight(x + 1, y + 1, fx - 1, fy - 1);
		renderer.setColor(PColor.GREY875);
		renderer.strokeBottom(x, y, fx, fy);
		renderer.strokeRight(x, y, fx, fy);
		
		// Render background
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 2, y + 2, fx - 2, fy - 2);
		
		// Render text
		String text = getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		PFontResource font = getDefaultFont();
		int textX = x + 2;
		
		PTextSelection selection = getSelection();
		if (selection.hasSelection()) {
			int selectionFrom = selection.getLowestSelectedIndex().getIndexValue();
			int selectionTo = selection.getHighestSelectedIndex().getIndexValue();
			
			String textPreSelection = text.substring(0, selectionFrom);
			String textSelection = text.substring(selectionFrom, selectionTo);
			String textPostSelection = text.substring(selectionTo);
			
			PSize sizePreSelection = font.getSize(textPreSelection);
			PSize sizeSelection = font.getSize(textSelection);
//			PSize sizePostSelection = font.getSize(textPostSelection);
			
			int textY = y + (fy - y) / 2 - sizeSelection.getHeight() / 2;
			
			renderer.setColor(getDefaultTextColor());
			renderer.drawString(font, textPreSelection, textX, textY);
			textX += sizePreSelection.getWidth();
			
			renderer.setColor(DEFAULT_TEXT_HIGHLIGHT_COLOR);
			renderer.drawString(font, textSelection, textX, textY);
			textX += sizeSelection.getWidth();
			
			renderer.setColor(getDefaultTextColor());
			renderer.drawString(font, textPostSelection, textX, textY);
		} else {
			PSize textSize = font.getSize(text);
			
			int textY = y + (fy - y) / 2 - textSize.getHeight() / 2;
			
			renderer.setColor(getDefaultTextColor());
			renderer.drawString(font, text, textX, textY);
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	protected PColor getDefaultTextColor() {
		return DEFAULT_TEXT_COLOR;
	}
	
	protected PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.fetchFontResource(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
	}
	
	protected PListIndex getIndexAt(int x, int y) {
		return new PListIndex(0);
//		PFontResource font = getDefaultFont();
//		if (font == null) {
//			return INDEX_NO_SELECTION;
//		}
//		String text = getText();
//		if (text.isEmpty()) {
//			return 0;
//		}
////		if (posTable.isInvalid()) {
////			buildTextPositionTable();
////		}
//		PBounds bounds = getBounds();
//		x -= bounds.getX();
//		y -= bounds.getY();
//		return posTable.getIndexAt(x, y);
	}
	
	protected void onMouseButtonTriggred(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOver()) {
			PListIndex index = getIndexAt(mouse.getX(), mouse.getY());
			if (index != null) {
				getSelection().clearSelection();
				getSelection().addSelection(index);
				takeFocus();
			}
		}
	}
	
}