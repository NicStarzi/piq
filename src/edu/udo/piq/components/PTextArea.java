package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.PRenderUtil;

public class PTextArea extends AbstractPComponent {
	
	protected static final String DEFAULT_FONT_NAME = "Arial";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	protected static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	
	private final PTextModelObs modelObs = new PTextModelObs() {
		public void textChanged(PTextModel model) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	private final PTextSelectionObs selectionObs = new PTextSelectionObs() {
		public void selectionRemoved(PTextSelection selection, int index) {
			fireReRenderEvent();
		}
		public void selectionAdded(PTextSelection selection, int index) {
			fireReRenderEvent();
		}
	};
	private PTextModel model;
	private PTextSelection selection;
	private boolean pressed = false;
	
	public PTextArea() {
		this(new DefaultPTextModel());
	}
	
	public PTextArea(PTextModel model) {
		setModel(model);
		setSelection(null);
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
		if (getModel() == null || getSelection() == null) {
			pressed = false;
			return;
		}
		PMouse mouse = PCompUtil.getMouseOf(this);
		if (mouse == null) {
			pressed = false;
			return;
		}
		PComponent mouseOwner = mouse.getOwner();
		if (mouseOwner != null && mouseOwner != this) {
			pressed = false;
			return;
		}
		
		int mx = mouse.getX();
		int my = mouse.getY();
		if (pressed) {
			pressed = mouse.isPressed(MouseButton.LEFT)
					&& PCompUtil.isWithinClippedBounds(this, mx, my);
		} else if (mouse.isTriggered(MouseButton.LEFT)) {
			pressed = PCompUtil.isWithinClippedBounds(this, mx, my);
		} else {
			pressed = false;
		}
	}
	
	protected int getTextIndex(int x, int y) {
		PTextModel model = getModel();
		if (model == null) {
			return -1;
		}
		String text = model.getText().toString();
		
		return -1;
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
		
		renderer.setColor(PColor.BLACK);
		PRenderUtil.strokeQuad(renderer, x, y, fx, fy);
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		
		x += 1;
		y += 1;
		
		renderer.setColor(getDefaultTextColor());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				String line = sb.toString();
				renderer.drawString(font, line, x, y);
				
				PSize lineSize = font.getSize(line);
				y += lineSize.getHeight();
				
				sb.delete(0, sb.length());
			} else {
				sb.append(c);
			}
		}
		if (sb.length() > 0) {
			String line = sb.toString();
			renderer.drawString(font, line, x, y);
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
	
}