package edu.udo.piq.tools;

import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PRoot;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.defaults.DefaultPTextSelection;
import edu.udo.piq.components.textbased.PCaretRenderTimer;
import edu.udo.piq.components.textbased.PTextComponent;
import edu.udo.piq.components.textbased.PTextInput;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.components.textbased.PTextModelObs;
import edu.udo.piq.components.textbased.PTextSelection;
import edu.udo.piq.components.textbased.PTextSelector;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPTextComponent 
	extends AbstractPInputComponent 
	implements PTextComponent 
{
	
	protected static final String DEFAULT_FONT_NAME = "Arial";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	protected static final PColor DEFAULT_TEXT_UNSELECTED_COLOR = PColor.BLACK;
	protected static final PColor DEFAULT_TEXT_SELECTED_COLOR = PColor.WHITE;
	protected static final PColor DEFAULT_SELECTION_BACKGROUND_COLOR = PColor.BLUE;
	
	protected final ObserverList<PTextModelObs> modelObsList
		= PCompUtil.createDefaultObserverList();
	protected final ObserverList<PSelectionObs> selectionObsList
		= PCompUtil.createDefaultObserverList();
	protected final PTextModelObs modelObs = (mdl) -> AbstractPTextComponent.this.onTextChanged();
	protected PFontResource cachedFont;
	protected PTextInput txtInput;
	protected PTextSelector txtSel;
	protected PCaretRenderTimer caretTimer;
	protected PTextModel model;
	protected PTextSelection selection;
	protected boolean editable = true;
	
	public AbstractPTextComponent() {
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PTextModel defaultModel = new DefaultPTextModel();
		if (modelFac != null) {
			defaultModel = (PTextModel) modelFac.getModelFor(this, defaultModel);
		}
		
		setModel(defaultModel);
		
		setSelection(new DefaultPTextSelection());
		setTextInput(new PTextInput(this));
		setTextSelector(new PTextSelector());
		setCaretRenderTimer(new PCaretRenderTimer(this));
	}
	
	protected void onTextChanged() {
		if (getSelection() != null) {
			getSelection().clearSelection();
		}
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	protected void setTextInput(PTextInput textInput) {
		if (getTextInput() != null) {
			removeObs(getTextInput().getKeyObs());
		}
		txtInput = textInput;
		if (getTextInput() != null) {
			addObs(getTextInput().getKeyObs());
		}
	}
	
	protected PTextInput getTextInput() {
		return txtInput;
	}
	
	protected void setTextSelector(PTextSelector textSelector) {
		if (getTextSelector() != null) {
			getTextSelector().setOwner(null);
		}
		txtSel = textSelector;
		if (getTextSelector() != null) {
			getTextSelector().setOwner(this);
		}
	}
	
	protected PTextSelector getTextSelector() {
		return txtSel;
	}
	
	protected void setCaretRenderTimer(PCaretRenderTimer caretToggleTimer) {
		if (getSelection() != null && getCaretRenderTimer() != null) {
			getSelection().removeObs(getCaretRenderTimer().getSelectionObs());
		}
		if (getCaretRenderTimer() != null) {
			removeObs(getCaretRenderTimer().getFocusObs());
		}
		caretTimer = caretToggleTimer;
		if (getCaretRenderTimer() != null) {
			addObs(getCaretRenderTimer().getFocusObs());
		}
		if (getSelection() != null && getCaretRenderTimer() != null) {
			getSelection().addObs(getCaretRenderTimer().getSelectionObs());
		}
	}
	
	protected PCaretRenderTimer getCaretRenderTimer() {
		return caretTimer;
	}
	
	protected void setEditable(boolean isEditable) {
		editable = isEditable;
	}
	
	public boolean isEditable() {
		return editable && isEnabled();
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	protected void setSelection(PTextSelection selection) {
		if (getSelection() != null && getCaretRenderTimer() != null) {
			getSelection().removeObs(getCaretRenderTimer().getSelectionObs());
		}
		this.selection = selection;
		if (getSelection() != null && getCaretRenderTimer() != null) {
			getSelection().addObs(getCaretRenderTimer().getSelectionObs());
		}
		fireReRenderEvent();
	}
	
	public PTextSelection getSelection() {
		return selection;
	}
	
	protected void setModel(PTextModel model) {
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
	
	public PTextModel getModel() {
		return model;
	}
	
	public String getText() {
		if (getModel() == null) {
			return "";
		}
		Object text = model.getText();
		if (text == null) {
			return "";
		}
		return text.toString();
	}
	
	public void addObs(PTextModelObs obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PTextModelObs obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	public void addObs(PSelectionObs obs) {
		selectionObsList.add(obs);
		if (getSelection() != null) {
			getSelection().addObs(obs);
		}
	}
	
	public void removeObs(PSelectionObs obs) {
		selectionObsList.remove(obs);
		if (getSelection() != null) {
			getSelection().removeObs(obs);
		}
	}
	
	protected PColor getDefaultTextUnselectedColor() {
		return DEFAULT_TEXT_UNSELECTED_COLOR;
	}
	
	protected PColor getDefaultTextSelectedColor() {
		return DEFAULT_TEXT_SELECTED_COLOR;
	}
	
	protected PColor getDefaultSelectionBackgroundColor() {
		return DEFAULT_SELECTION_BACKGROUND_COLOR;
	}
	
	protected PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		if (cachedFont != null && root.isFontSupported(cachedFont)) {
			return cachedFont;
		}
		cachedFont = root.fetchFontResource(DEFAULT_FONT_NAME, 
				DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
		return cachedFont;
	}
	
}