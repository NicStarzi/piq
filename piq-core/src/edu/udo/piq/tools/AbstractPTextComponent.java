package edu.udo.piq.tools;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PRoot;
import edu.udo.piq.components.PSingleValueModel;
import edu.udo.piq.components.PSingleValueModelObs;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.collections.list.PListIndex;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.defaults.DefaultPTextSelection;
import edu.udo.piq.components.textbased.PCaretRenderTimer;
import edu.udo.piq.components.textbased.PTextComponent;
import edu.udo.piq.components.textbased.PTextInput;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.components.textbased.PTextSelection;
import edu.udo.piq.components.textbased.PTextSelector;
import edu.udo.piq.components.util.SymbolicFontKey;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;

public abstract class AbstractPTextComponent
	extends AbstractPComponent
	implements PTextComponent
{
	
	public static final Object FONT_ID = new SymbolicFontKey(AbstractPTextComponent.class);
	public static final PColor DEFAULT_TEXT_UNSELECTED_COLOR = PColor.BLACK;
	public static final PColor DEFAULT_TEXT_SELECTED_COLOR = PColor.WHITE;
	public static final PColor DEFAULT_SELECTION_BACKGROUND_COLOR = PColor.BLUE;
	
	{
		addActionMapping(PTextInput.KEY_COPY,		PTextInput.ACTION_COPY);
		addActionMapping(PTextInput.KEY_CUT,		PTextInput.ACTION_CUT);
		addActionMapping(PTextInput.KEY_PASTE,		PTextInput.ACTION_PASTE);
		addActionMapping(PTextInput.KEY_DELETE,		PTextInput.ACTION_DELETE);
		addActionMapping(PTextInput.KEY_BACKSPACE,	PTextInput.ACTION_BACKSPACE);
		addActionMapping(PTextInput.KEY_HOME,		PTextInput.ACTION_HOME);
		addActionMapping(PTextInput.KEY_END,		PTextInput.ACTION_END);
		addActionMapping(PTextInput.KEY_MOVE_LEFT,	PTextInput.ACTION_MOVE_LEFT);
		addActionMapping(PTextInput.KEY_MOVE_RIGHT,	PTextInput.ACTION_MOVE_RIGHT);
		addActionMapping(PTextInput.KEY_MOVE_DOWN,	PTextInput.ACTION_MOVE_DOWN);
		addActionMapping(PTextInput.KEY_MOVE_UP,	PTextInput.ACTION_MOVE_UP);
		addActionMapping(PTextInput.KEY_PAGE_DOWN,	PTextInput.ACTION_PAGE_DOWN);
		addActionMapping(PTextInput.KEY_PAGE_UP,	PTextInput.ACTION_PAGE_UP);
		addActionMapping(PTextInput.KEY_SELECT_ALL,	PTextInput.ACTION_SELECT_ALL);
	}
	
	protected final ObserverList<PSingleValueModelObs<Object>> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final ObserverList<PSelectionObs> selectionObsList
		= PiqUtil.createDefaultObserverList();
	protected final PSingleValueModelObs<Object> modelObs = this::onTextChanged;
	protected final PSelectionObs selectionObs = new PSelectionObs() {
		@Override
		public void onLastSelectedChanged(PSelection selection, PModelIndex prevLastSelected, PModelIndex newLastSelected) {
			AbstractPTextComponent.this.onLastSelectedChanged(newLastSelected);
		}
	};
	protected MutablePBounds scrollRequestBounds;
	protected PFontResource cachedFont;
	protected PTextInput txtInput;
	protected PTextSelector txtSel;
	protected PCaretRenderTimer caretTimer;
	protected PTextModel model;
	protected PTextSelection selection;
	protected boolean editable = true;
	protected boolean enabled = true;
	
	public AbstractPTextComponent() {
		setModel(PModelFactory.createModelFor(this, DefaultPTextModel::new, PTextModel.class));
		
		setSelection(new DefaultPTextSelection());
		setTextInput(new PTextInput(this));
		setTextSelector(new PTextSelector());
		setCaretRenderTimer(new PCaretRenderTimer(this));
	}
	
	protected void onTextChanged(PSingleValueModel<Object> model, Object oldValue, Object newValue) {
		if (getSelection() != null) {
			getSelection().clearSelection();
		}
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	protected void onLastSelectedChanged(PModelIndex lastSelected) {
		if (lastSelected == null) {
			return;
		}
		PBounds ownBounds = getBoundsWithoutBorder();
		if (ownBounds == null) {
			return;
		}
		PListIndex index = (PListIndex) lastSelected;
		if (scrollRequestBounds == null) {
			scrollRequestBounds = new MutablePBounds();
		}
		PBounds renderBounds = getRenderPositionForIndex(scrollRequestBounds, index);
		if (renderBounds == null) {
			return;
		}
		requestScrollToRect(renderBounds);
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
	
	public void setEnabled(boolean isEnabled) {
		if (enabled != isEnabled) {
			enabled = isEnabled;
			fireReRenderEvent();
		}
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public boolean isEditable() {
		return editable && isEnabled();
	}
	
	@Override
	public boolean isFocusable() {
		return true;
	}
	
	@Override
	public boolean isStrongFocusOwner() {
		return true;
	}
	
	protected void setSelection(PTextSelection selection) {
		if (getSelection() != null) {
			if (getCaretRenderTimer() != null) {
				getSelection().removeObs(getCaretRenderTimer().getSelectionObs());
			}
			getSelection().removeObs(selectionObs);
		}
		this.selection = selection;
		if (getSelection() != null) {
			if (getCaretRenderTimer() != null) {
				getSelection().addObs(getCaretRenderTimer().getSelectionObs());
			}
			getSelection().addObs(selectionObs);
		}
		fireReRenderEvent();
	}
	
	@Override
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
	
	@Override
	public PTextModel getModel() {
		return model;
	}
	
	public void setModelValue(Object value) {
		if (getModel() == null) {
			return;
		}
		getModel().setValue(value);
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
	
	public void addObs(PSingleValueModelObs<Object> obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PSingleValueModelObs<Object> obs) {
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
	
	protected Object getDefaultFontKey() {
		return FONT_ID;
	}
	
	protected PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		if (cachedFont != null && root.isFontSupported(cachedFont)) {
			return cachedFont;
		}
		cachedFont = root.fetchFontResource(getDefaultFontKey());
		return cachedFont;
	}
	
}