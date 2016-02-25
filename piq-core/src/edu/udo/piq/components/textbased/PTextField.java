package edu.udo.piq.components.textbased;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PInsets;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.tools.AbstractPTextComponent;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.MutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PTextField extends AbstractPTextComponent {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	protected static final PColor DEFAULT_BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor DISABLED_BACKGROUND_COLOR = PColor.GREY75;
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(200, 24);
	
	protected final ObserverList<PTextFieldObs> obsList = 
			PCompUtil.createDefaultObserverList();
	protected final MutablePSize prefSize = new MutablePSize(200, 22);
	protected PTextIndexTableSingleLine idxTab = new PTextIndexTableSingleLine();
	protected PInsets insets;
	private boolean contentsWereChanged = false;
	
	public PTextField(PTextModel model) {
		this();
		setModel(model);
	}
	
	public PTextField(Object defaultModelValue) {
		this();
		getModel().setValue(defaultModelValue);
	}
	
	public PTextField() {
		super();
		setInsets(DEFAULT_INSETS);
		
		setTextInput(new PTextInput(this) {
			protected void keyNewLine(PKeyboard kb) {
				fireConfirmEvent();
			}
		});
		addObs(new PFocusObs() {
			public void onFocusLost(PComponent oldOwner) {
				fireConfirmEvent();
			}
		});
	}
	
	public void setModel(PTextModel model) {
		super.setModel(model);
		if (idxTab != null) {
			idxTab.setLastIndexInRow(getText().length());
		}
	}
	
	public void setSelection(PTextSelection textSelection) {
		super.setSelection(textSelection);
	}
	
	public void setEditable(boolean value) {
		super.setEditable(value);
	}
	
	public PTextIndexTable getIndexTable() {
		return idxTab;
	}
	
	protected void onTextChanged() {
		contentsWereChanged = true;
		idxTab.setLastIndexInRow(getText().length());
	}
	
	public void setInsets(PInsets value) {
		insets = value;
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public PInsets getInsets() {
		return insets;
	}
	
	public PListIndex getTextIndexAt(int x, int y) {
		PFontResource font = getDefaultFont();
		// No font => no selection
		if (font == null) {
			return null;
		}
		String text = getText();
		// Empty text => always select first index
		if (text.isEmpty()) {
			return new PListIndex(0);
		}
		PBounds bounds = getBounds();
		PInsets insets = getInsets();
		x = (x - bounds.getX()) - insets.getFromBottom();
		y = (y - bounds.getY()) - insets.getFromTop();
		// Out of bounds => select first or last index
		if (x < 0 || y < 0) {
			return new PListIndex(0);
		}
		if (x > bounds.getFinalX() - insets.getFromRight() 
				|| y > bounds.getFinalY() - insets.getFromBottom()) 
		{
			return new PListIndex(text.length());
		}
		int letterX = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			PSize size = font.getSize(Character.toString(c));
			int letterW = size.getWidth();
			if (x < letterX + letterW) {
				return new PListIndex(i);
			}
			letterX += letterW;
		}
		return new PListIndex(text.length());
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		// Render border
		renderer.setRenderMode(renderer.getRenderModeFill());
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
		renderer.setColor(getDefaultBackgroundColor());
		renderer.drawQuad(x + 2, y + 2, fx - 2, fy - 2);
		
		PFontResource font = getDefaultFont();
		
		String text = getText();
		if (text == null) {
			return;
		}
		PInsets insets = getInsets();
		int txtX = x + insets.getFromLeft();
		int txtY = y + insets.getFromTop();
		int txtH = bnds.getHeight() - insets.getVertical();
		if (text.isEmpty()) {
			if (hasFocus() && getCaretRenderTimer().isFocusRender()) {
				int minH = font.getSize(" ").getHeight();
				int drawY = (txtY + txtH / 2) - minH / 2;
				renderer.setColor(getDefaultSelectionBackgroundColor());
				renderer.drawQuad(txtX, drawY, txtX + 2, drawY + minH);
			}
			return;
		}
		
		PTextSelection selection = getSelection();
		PColor textColor = getDefaultTextUnselectedColor();
		if (selection.hasSelection()) {
			PListIndex fromIndex = selection.getLowestSelectedIndex();
			PListIndex toIndex = selection.getHighestSelectedIndex();
			int selectedFrom = fromIndex.getIndexValue();
			int selectedTo = toIndex.getIndexValue();
			
			String beforeSelected = text.substring(0, selectedFrom);
			String afterSelected = text.substring(selectedTo);
			
			txtX = renderText(renderer, font, txtX, txtY, txtH, beforeSelected, textColor, null);
			if (selectedFrom == selectedTo) {
				if (isEditable() && hasFocus() && getCaretRenderTimer().isFocusRender()) {
					renderer.setColor(getDefaultSelectionBackgroundColor());
					char c;
					if (selectedFrom == text.length()) {
						c = text.charAt(selectedFrom - 1);
					} else {
						c = text.charAt(selectedFrom);
					}
					PSize charSize = font.getSize(Character.toString(c));
					int minH = charSize.getHeight();
					int drawY = (txtY + txtH / 2) - minH / 2;
					renderer.drawQuad(txtX - 1, drawY, txtX + 1, drawY + minH);
				}
				renderText(renderer, font, txtX, txtY, txtH, afterSelected, textColor, null);
			} else {
				String selected = text.substring(selectedFrom, selectedTo);
				txtX = renderText(renderer, font, txtX, txtY, txtH, selected, 
						getDefaultTextSelectedColor(), getDefaultSelectionBackgroundColor());
				
				renderText(renderer, font, txtX, txtY, txtH, afterSelected, textColor, null);
			}
		} else {
			renderText(renderer, font, txtX, txtY, txtH, text, textColor, null);
		}
	}
	
	private int renderText(PRenderer renderer, PFontResource font, 
			int drawX, int drawY, int h, String text, 
			PColor txtColor, PColor bgColor) 
	{
		renderer.setColor(txtColor);
		PSize lineSize = font.getSize(text);
		int txtW = lineSize.getWidth();
		int txtH = lineSize.getHeight();
		drawY = (drawY + h / 2) - txtH / 2;
		if (bgColor != null) {
			renderer.setColor(bgColor);
			renderer.drawQuad(drawX, drawY, drawX + txtW, drawY + txtH);
			renderer.setColor(txtColor);
		}
		renderer.drawString(font, text, drawX, drawY);
		return drawX + txtW;
	}
	
	public PSize getDefaultPreferredSize() {
		PFontResource font = getDefaultFont();
		if (font == null) {
			return DEFAULT_PREFERRED_SIZE;
		}
		String text = getText();
		if (text == null || text.isEmpty()) {
			return DEFAULT_PREFERRED_SIZE;
		}
		PInsets insets = getInsets();
		PSize textSize = font.getSize(text);
		prefSize.setWidth(textSize.getWidth() + insets.getHorizontal());
		prefSize.setHeight(textSize.getHeight() + insets.getVertical());
		return prefSize;
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public PCursor getMouseOverCursor(PMouse mouse) {
		return mouse.getCursorText();
	}
	
	protected PColor getDefaultBackgroundColor() {
		if (isEditable()) {
			return DEFAULT_BACKGROUND_COLOR;
		}
		return DISABLED_BACKGROUND_COLOR;
	}
	
	public void addObs(PTextFieldObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTextFieldObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireConfirmEvent() {
		if (contentsWereChanged) {
			obsList.fireEvent((obs) -> obs.onConfirm(this));
			contentsWereChanged = false;
		}
	}
}