package edu.udo.piq.components;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PMouse;
import edu.udo.piq.PTimerCallback;
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
import edu.udo.piq.tools.PTextPositionTable;
import edu.udo.piq.util.PCompUtil;

public class PTextArea extends AbstractPComponent {
	
	protected static final String DEFAULT_FONT_NAME = "Arial";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	protected static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	protected static final int DEFAULT_FOCUS_RENDER_TOGGLE_TIMER_DELAY = 4;
	
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
	private final PTextModelObs modelObs = new PTextModelObs() {
		public void textChanged(PTextModel model) {
			posTable.setTextLines(null);
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	private final PTextSelectionObs selectionObs = new PTextSelectionObs() {
		public void selectionRemoved(PTextSelection selection, int index) {
		}
		public void selectionAdded(PTextSelection selection, int index) {
		}
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
		posTable.setTextLines(null);
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
	
	protected void onUpdate() {
		PTextSelection selection = getSelection();
		if (getModel() == null || selection == null) {
			pressedIndex = -1;
			return;
		}
		PMouse mouse = PCompUtil.getMouseOf(this);
		if (mouse == null) {
			pressedIndex = -1;
			return;
		}
		
		boolean isClicked = false;
		int mx = mouse.getX();
		int my = mouse.getY();
		if (mouse.isTriggered(MouseButton.LEFT)) {
			if (PCompUtil.isWithinClippedBounds(this, mx, my)) {
				pressedIndex = getTextIndexAt(mx, my);
				selection.setSelection(pressedIndex, pressedIndex);
				isClicked = true;
			}
		} else if (mouse.isPressed(MouseButton.LEFT)) {
			if (pressedIndex != -1) {
				selection.setSelection(pressedIndex, getTextIndexAt(mx, my)); // + 1
				isClicked = true;
			}
		} else {
			pressedIndex = -1;
		}
		
		boolean hasFocus = PCompUtil.hasFocus(this);
		if (isClicked && !hasFocus) {
			PCompUtil.takeFocus(this);
			fireReRenderEvent();
		}
		if (hasFocus) {
			
		}
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
		
		renderer.setColor(PColor.WHITE);
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
					PBounds letterBounds = getBoundsForText(from);
					int letterX = letterBounds.getX() + x - 1;
					int letterY = letterBounds.getY() + y;
					int letterFx = letterX + 2;
					int letterFy = letterBounds.getFinalY() + y;
					renderer.setColor(PColor.BLUE);
					renderer.drawQuad(letterX, letterY, letterFx, letterFy);
				}
			} else {
				for (int i = from; i < to; i++) {
					PBounds letterBounds = getBoundsForText(i);
					int letterX = letterBounds.getX() + x;
					int letterY = letterBounds.getY() + y;
					int letterFx = letterBounds.getFinalX() + x;
					int letterFy = letterBounds.getFinalY() + y;
					renderer.setColor(PColor.BLUE);
					renderer.drawQuad(letterX, letterY, letterFx, letterFy);
					
					renderer.setColor(PColor.WHITE);
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
	
	protected int getTextIndexAt(int x, int y) {
		String text = getText();
		if (text.isEmpty()) {
			return -1;
		}
		PDesign design = getDesign();
		if (design instanceof PTextAreaDesign) {
			return ((PTextAreaDesign) design).getTextIndexAt(this, x, y);
		}
		PFontResource font = getDefaultFont();
		if (font == null) {
			return -1;
		}
		if (!posTable.isValid()) {
			buildLinesArray();
		}
		x -= getBounds().getX();
		y -= getBounds().getY();
		return posTable.getIndex(x, y);
	}
	
	protected PBounds getBoundsForText(int index) {
		PDesign design = getDesign();
		if (design instanceof PTextAreaDesign) {
			return ((PTextAreaDesign) design).getBoundsForText(this, index);
		}
		String text = getText();
		PFontResource font = getDefaultFont();
		if (font == null || text.isEmpty()) {
			return null;
		}
		if (!posTable.isValid()) {
			buildLinesArray();
		}
		return posTable.getBoundsOf(index);
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
	
	protected void buildLinesArray() {
		List<String> lines = new ArrayList<>();
		PFontResource font = getDefaultFont();
		String text = getText();
		if (font == null || text == null || getParent() == null) {
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				sb.append('\n');
				String line = sb.toString();
				lines.add(line);
				sb.delete(0, sb.length());
			} else {
				sb.append(c);
			}
		}
		if (sb.length() > 0) {
			String line = sb.toString();
			lines.add(line);
		}
		
		posTable.setFont(font);
		posTable.setTextLines(lines);
	}
	
}