package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PTimerCallback;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PTimer;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.defaults.DefaultPTextSelection;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.PTextIndex;
import edu.udo.piq.tools.PTextIndexTable;

public class PTextArea extends AbstractPComponent {
	
	public static final int INDEX_NO_SELECTION = PTextSelection.INDEX_NO_SELECTION;
	protected static final String DEFAULT_FONT_NAME = "Arial";//"Monospaced";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	protected static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	protected static final PColor DEFAULT_TEXT_HIGHLIGHT_COLOR = PColor.WHITE;
	protected static final PColor DEFAULT_TEXT_SELECTION_COLOR = PColor.BLUE;
	protected static final PColor DEFAULT_BACKGROUND_COLOR = PColor.WHITE;
	protected static final int DEFAULT_FOCUS_RENDER_TOGGLE_TIMER_DELAY = 6;
	
	private final PTimer focusToggleTimer = new PTimer(this, new PTimerCallback() {
		public void action() {
			PTextSelection selection = getSelection();
			int selectionFrom = selection.getFrom();
			int selectionTo = selection.getTo();
			if (hasFocus() && selectionFrom != INDEX_NO_SELECTION && selectionFrom == selectionTo) 
			{
				focusRenderToggleTimer += 1;
				if (focusRenderToggleTimer >= DEFAULT_FOCUS_RENDER_TOGGLE_TIMER_DELAY) {
					focusRenderToggleTimer = 0;
					focusRenderToggle = !focusRenderToggle;
					fireReRenderEvent();
				}
			}
		}
	});
	private final PKeyboardObs keyObs = new PTextComponentKeyboardObs() {
		public void stringTyped(PKeyboard keyboard, String typedString) {
			if (!isEditable() || skipInput(keyboard, null)) {
				return;
			}
			PTextSelection selection = getSelection();
			int from = selection.getFrom();
			int to = selection.getTo();
			
			int newFrom = from + typedString.length();
			
			String oldText = getText();
			String newText = oldText.substring(0, from) + typedString + oldText.substring(to);
			selection.setSelection(newFrom, newFrom);
			getModel().setValue(newText);
		}
		public void controlInput(PKeyboard keyboard, Key key) {
			PTextSelection selection = getSelection();
			int from = selection.getFrom();
			int to = selection.getTo();
			int first = selection.getFirst();
			int second = selection.getSecond();
			int newFirst = first;
			int newSecond = second;
			
			String oldText = getText();
			String newText = oldText;
			
			boolean shift = keyboard.isPressed(Key.SHIFT);
			
			switch (key) {
			case BACKSPACE:
				if (from != to) {
					newText = oldText.substring(0, from) + oldText.substring(to);
					newFirst = from;
					newSecond = from;
				} else if (from > 0) {
					newText = oldText.substring(0, from - 1) + oldText.substring(to);
					newFirst = from - 1;
					newSecond = newFirst;
				}
				break;
			case DEL:
				if (from != to) {
					newText = oldText.substring(0, from) + oldText.substring(to);
					newFirst = from;
					newSecond = from;
				} else if (from < oldText.length()) {
					newText = oldText.substring(0, from) + oldText.substring(to + 1);
					newFirst = from;
					newSecond = from;
				}
				break;
			case UP:
				int rowFirstUp = posTable.getRowOf(newFirst) - 1;
				int colFirstUp = posTable.getColumnOf(newFirst);
				newFirst = posTable.getIndex(rowFirstUp, colFirstUp);
				if (!shift) {
					newSecond = newFirst;
				}
				break;
			case DOWN:
				int rowFirstDwn = posTable.getRowOf(newFirst) + 1;
				int colFirstDwn = posTable.getColumnOf(newFirst);
				newFirst = posTable.getIndex(rowFirstDwn, colFirstDwn);
				if (!shift) {
					newSecond = newFirst;
				}
				break;
			case HOME:
				newFirst = 0;
				if (!shift) {
					newSecond = newFirst;
				}
				break;
			case END:
				newFirst = newText.length();
				if (!shift) {
					newSecond = newFirst;
				}
				break;
			case LEFT:
				if (!shift && from != to) {
					newFirst = from;
					newSecond = from;
				} else {
					if (newFirst > 0) {
						newFirst = newFirst - 1;
					}
					if (!shift) {
						newSecond = newFirst;
					}
				}
				break;
			case RIGHT:
				if (!shift && from != to) {
					newFirst = to;
					newSecond = to;
				} else {
					if (newFirst < newText.length()) {
						newFirst = newFirst + 1;
					}
					if (!shift) {
						newSecond = newFirst;
					}
				}
				break;
			case PAGE_DOWN:
				break;
			case PAGE_UP:
				break;
				//$CASES-OMITTED$
			default:
				break;
			}
			
			if (isEditable() && oldText != newText) {
				getModel().setValue(newText);
			}
			if (newFirst != first || newSecond != second) {
				selection.setSelection(newFirst, newSecond);
			}
		}
		public boolean skipInput(PKeyboard keyboard, Key key) {
			return !hasFocus() 
					|| getSelection() == null 
					|| getModel() == null;
		}
	};
	private final PMouseObs mouseObs = new PMouseObs() {
		public void mouseMoved(PMouse mouse) {
			if (mouse.isPressed(MouseButton.LEFT) && pressedIndex != INDEX_NO_SELECTION) {
				int mx = mouse.getX();
				int my = mouse.getY();
				selection.setSelection(getTextIndexAt(mx, my), pressedIndex);
				takeFocus();
				fireReRenderEvent();
			}
		}
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT) {
				int mx = mouse.getX();
				int my = mouse.getY();
				if (getClippedBounds().contains(mx, my)) {
					pressedIndex = getTextIndexAt(mx, my);
					selection.setSelection(pressedIndex, pressedIndex);
					takeFocus();
					fireReRenderEvent();
				}
			}
		}
		public void buttonReleased(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && pressedIndex != INDEX_NO_SELECTION) {
				pressedIndex = INDEX_NO_SELECTION;
				fireReRenderEvent();
			}
		}
	};
	private final PTextModelObs modelObs = new PTextModelObs() {
		public void textChanged(PTextModel model) {
			posTable.invalidate();
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	private final PTextSelectionObs selectionObs = new PTextSelectionObs() {
		public void selectionChanged(PTextSelection selection) {
			focusRenderToggle = true;
			focusRenderToggleTimer = 0;
			fireReRenderEvent();
		}
	};
	private PTextModel model;
	private PTextSelection selection;
	private PTextIndexTable posTable = new PTextIndexTable();
	private int pressedIndex = INDEX_NO_SELECTION;
	private int focusRenderToggleTimer;
	private boolean focusRenderToggle;
	private boolean editable;
	
	public PTextArea() {
		this(new DefaultPTextModel());
	}
	
	public PTextArea(PTextModel model) {
		setModel(model);
		setSelection(new DefaultPTextSelection());
		
		addObs(new PFocusObs() {
			public void focusLost(PComponent oldOwner) {
				focusToggleTimer.stop();
				getSelection().clearSelection();
			}
			public void focusGained(PComponent oldOwner, PComponent newOwner) {
				focusRenderToggle = true;
				focusRenderToggleTimer = 0;
				focusToggleTimer.setRepeating(true);
				focusToggleTimer.setDelay(DEFAULT_FOCUS_RENDER_TOGGLE_TIMER_DELAY);
				focusToggleTimer.start();
			}
		});
		addObs(keyObs);
		addObs(mouseObs);
	}
	
	public void setSelection(PTextSelection selection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
			getSelection().setModel(null);
		}
		this.selection = selection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
			getSelection().setModel(getModel());
		}
		fireReRenderEvent();
	}
	
	public PTextSelection getSelection() {
		return selection;
	}
	
	public void setModel(PTextModel model) {
		if (getModel() != null) {
			if (getSelection() != null) {
				getSelection().setModel(null);
			}
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			if (getSelection() != null) {
				getSelection().setModel(getModel());
			}
			getModel().addObs(modelObs);
		}
		posTable.invalidate();
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public PTextModel getModel() {
		return model;
	}
	
	public void setEditable(boolean isEditable) {
		editable = isEditable;
	}
	
	public boolean isEditable() {
		return editable;
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
	
	public void defaultRender(PRenderer renderer) {
		PFontResource font = getDefaultFont();
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(getDefaultBackgroundColor());
		renderer.drawQuad(x, y, fx, fy);
		
		String text = getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		
		renderer.setColor(getDefaultTextColor());
		StringBuilder sb = new StringBuilder();
		int lineY = y;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				String line = sb.toString();
				renderer.drawString(font, line, x, lineY);
				
				PSize lineSize = font.getSize(line);
				lineY += lineSize.getHeight();
				
				sb.delete(0, sb.length());
			} else {
				sb.append(c);
			}
		}
		if (sb.length() > 0) {
			String line = sb.toString();
			renderer.drawString(font, line, x, lineY);
		}
		
		PTextSelection selection = getSelection();
		int from = selection.getFrom();
		int to = selection.getTo();
		
		if (from != INDEX_NO_SELECTION) {
			if (from == to && hasFocus()) {
				if (focusRenderToggle) {
					PBounds letterBounds = getBoundsForLetter(from);
					int letterX = letterBounds.getX() + x - 1;
					int letterY = letterBounds.getY() + y;
					int letterFx = letterX + 2;
					int letterFy = letterBounds.getFinalY() + y;
					renderer.setColor(getDefaultSelectionColor());
					renderer.drawQuad(letterX, letterY, letterFx, letterFy);
				}
			} else {
				for (int i = from; i < to; i++) {
					PBounds letterBounds = getBoundsForLetter(i);
					int letterX = letterBounds.getX() + x;
					int letterY = letterBounds.getY() + y;
					int letterFx = letterBounds.getFinalX() + x;
					int letterFy = letterBounds.getFinalY() + y;
					renderer.setColor(getDefaultSelectionColor());
					renderer.drawQuad(letterX, letterY, letterFx, letterFy);
					
					renderer.setColor(getDefaultTextHighlightColor());
					renderer.drawString(font, text.substring(i, i+1), letterX, letterY);
				}
			}
		}
	}
	
	public PSize getDefaultPreferredSize() {
		String text = getText();
		if (text == null || text.isEmpty()) {
			return PSize.NULL_SIZE;
		}
		PFontResource font = getDefaultFont();
		if (font == null) {
			return PSize.NULL_SIZE;
		}
		int prefW = 0;
		int prefH = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				String line = sb.toString();
				PSize lineSize = font.getSize(line);
				int lineW = lineSize.getWidth();
				int lineH = lineSize.getHeight();
				if (lineW > prefW) {
					prefW = lineW;
				}
				prefH += lineH;
				
				sb.delete(0, sb.length());
			} else {
				sb.append(c);
			}
		}
		if (sb.length() > 0) {
			String line = sb.toString();
			PSize lineSize = font.getSize(line);
			int lineW = lineSize.getWidth();
			int lineH = lineSize.getHeight();
			if (lineW > prefW) {
				prefW = lineW;
			}
			prefH += lineH;
		}
		return new ImmutablePSize(prefW, prefH);
	}
	
	protected int getTextIndex(int row, int column) {
		PFontResource font = getDefaultFont();
		if (font == null) {
			return INDEX_NO_SELECTION;
		}
		String text = getText();
		if (text.isEmpty()) {
			return 0;
		}
		if (posTable.isInvalid()) {
			buildTextPositionTable();
		}
		return posTable.getIndex(row, column);
	}
	
	protected int getTextIndexAt(int x, int y) {
		PFontResource font = getDefaultFont();
		if (font == null) {
			return INDEX_NO_SELECTION;
		}
		String text = getText();
		if (text.isEmpty()) {
			return 0;
		}
		if (posTable.isInvalid()) {
			buildTextPositionTable();
		}
		PBounds bounds = getBounds();
		x -= bounds.getX();
		y -= bounds.getY();
		return posTable.getIndexAt(x, y);
	}
	
	protected PBounds getBoundsForLetter(int index) {
		PDesign design = getDesign();
		if (design instanceof PTextAreaDesign) {
			return ((PTextAreaDesign) design).getBoundsForLetter(this, index);
		}
		String text = getText();
		PFontResource font = getDefaultFont();
		if (font == null || text.isEmpty()) {
			return null;
		}
		if (posTable.isInvalid()) {
			buildTextPositionTable();
		}
		return posTable.getBounds(index);
	}
	
	private void buildTextPositionTable() {
		String text = getText();
		posTable.setToSize(text.length());
		
		PFontResource font = getDefaultFont();
		
		int x = 0;
		int y = 0;
		int lineH = 0;
		
		int rowID = 0;
		int colID = 0;
		
		for (int i = 0; i < text.length(); i++) {
			char letter = text.charAt(i);
			PSize letterSize = font.getSize(Character.toString(letter));
			int w = letterSize.getWidth();
			int h = letterSize.getHeight();
			
			posTable.setBounds(i, new PTextIndex(x, y, w, h, rowID, colID));
			
			x += w;
			if (lineH < h) {
				lineH = h;
			}
			colID += 1;
			if (letter == '\n') {
				y += lineH;
				x = 0;
				colID = 0;
				rowID += 1;
			}
		}
	}
	
	protected PColor getDefaultTextColor() {
		return DEFAULT_TEXT_COLOR;
	}
	
	protected PColor getDefaultTextHighlightColor() {
		return DEFAULT_TEXT_HIGHLIGHT_COLOR;
	}
	
	protected PColor getDefaultSelectionColor() {
		return DEFAULT_TEXT_SELECTION_COLOR;
	}
	
	protected PColor getDefaultBackgroundColor() {
		return DEFAULT_BACKGROUND_COLOR;
	}
	
	protected PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.fetchFontResource(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
	}
	
}