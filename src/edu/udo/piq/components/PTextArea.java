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
import edu.udo.piq.tools.AbstractPMouseObs;
import edu.udo.piq.tools.AbstractPTextSelectionObs;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.PTextPositionTable;
import edu.udo.piq.util.PCompUtil;

public class PTextArea extends AbstractPComponent {
	
	protected static final String DEFAULT_FONT_NAME = "Arial";
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
			if (PCompUtil.hasFocus(PTextArea.this) 
					&& selectionFrom != -1 && selectionFrom == selectionTo) 
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
		public void textTyped(PKeyboard keyboard, String typedString) {
			PTextSelection selection = getSelection();
			int from = selection.getFrom();
			int to = selection.getTo();
			
			int newFrom = from + typedString.length();
			
			String oldText = getText();
			String newText = oldText.substring(0, from) + typedString + oldText.substring(to);
			selection.setSelection(newFrom, newFrom);
			getModel().setText(newText);
		}
		public void controlInput(PKeyboard keyboard, Key key) {
			PTextSelection selection = getSelection();
			int from = selection.getFrom();
			int to = selection.getTo();
			int newFrom = from;
			int newTo = to;
			
			String oldText = getText();
			String newText = oldText;
			
			switch (key) {
			case BACKSPACE:
				if (from != to) {
					newText = oldText.substring(0, from) + oldText.substring(to);
					newFrom = from;
					newTo = from;
				} else if (from > 0) {
					newText = oldText.substring(0, from - 1) + oldText.substring(to);
					newFrom = from - 1;
					newTo = newFrom;
				}
				break;
			case DEL:
				if (from != to) {
					newText = oldText.substring(0, from) + oldText.substring(to);
					newFrom = from;
					newTo = from;
				} else if (from < oldText.length()) {
					newText = oldText.substring(0, from) + oldText.substring(to + 1);
					newFrom = from;
					newTo = from;
				}
				break;
			case DOWN:
				break;
			case END:
				break;
			case HOME:
				break;
			case LEFT:
				break;
			case PAGE_DOWN:
				break;
			case PAGE_UP:
				break;
			case RIGHT:
				break;
			case UP:
				break;
			default:
				break;
			}
			
			if (oldText != newText) {
				getModel().setText(newText);
			}
			if (newFrom != from || newTo != to) {
				selection.setSelection(newFrom, newTo);
			}
		}
		public boolean skipInput(PKeyboard keyboard, Key key) {
			return !PCompUtil.hasFocus(PTextArea.this) 
					|| getSelection() == null 
					|| getModel() == null;
		}
	};
	private final PMouseObs mouseObs = new AbstractPMouseObs() {
		public void mouseMoved(PMouse mouse) {
			if (mouse.isPressed(MouseButton.LEFT) && pressedIndex != -1) {
				int mx = mouse.getX();
				int my = mouse.getY();
				selection.setSelection(pressedIndex, getTextIndexAt(mx, my));
				if (!PCompUtil.hasFocus(PTextArea.this)) {
					PCompUtil.takeFocus(PTextArea.this);
				}
				fireReRenderEvent();
			}
		}
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT) {
				int mx = mouse.getX();
				int my = mouse.getY();
				if (PCompUtil.isWithinClippedBounds(PTextArea.this, mx, my)) {
					pressedIndex = getTextIndexAt(mx, my);
					selection.setSelection(pressedIndex, pressedIndex);
					if (!PCompUtil.hasFocus(PTextArea.this)) {
						PCompUtil.takeFocus(PTextArea.this);
					}
					fireReRenderEvent();
				}
			}
		}
		public void buttonReleased(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && pressedIndex != -1) {
				pressedIndex = -1;
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
	private final PTextSelectionObs selectionObs = new AbstractPTextSelectionObs() {
		public void selectionChanged(PTextSelection selection) {
			focusRenderToggle = true;
			focusRenderToggleTimer = 0;
			fireReRenderEvent();
		}
	};
	private PTextModel model;
	private PTextSelection selection;
	private PTextPositionTable posTable = new PTextPositionTable();
	private int pressedIndex;
	private int focusRenderToggleTimer;
	private boolean focusRenderToggle;
	
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
		String text = getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		PFontResource font = getDefaultFont();
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(getDefaultBackgroundColor());
		renderer.drawQuad(x, y, fx, fy);
		
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
		
		if (from != -1) {
			if (from == to && PCompUtil.hasFocus(this)) {
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
	
	protected PKeyboardObs getKeyboardObs() {
		return keyObs;
	}
	
	protected PMouseObs getMouseObs() {
		return mouseObs;
	}
	
	protected int getTextIndexAt(int x, int y) {
		String text = getText();
		if (text.isEmpty()) {
			return -1;
		}
		PFontResource font = getDefaultFont();
		if (font == null) {
			return -1;
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
		
		for (int i = 0; i < text.length(); i++) {
			char letter = text.charAt(i);
			PSize letterSize = font.getSize(Character.toString(letter));
			int w = letterSize.getWidth();
			int h = letterSize.getHeight();
			
			posTable.setBounds(i, new ImmutablePBounds(x, y, w, h));
			
			x += w;
			if (lineH < h) {
				lineH = h;
			}
			if (letter == '\n') {
				y += lineH;
				x = 0;
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