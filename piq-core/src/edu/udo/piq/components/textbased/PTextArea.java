package edu.udo.piq.components.textbased;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PCursor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PSingleValueModel;
import edu.udo.piq.components.collections.list.PListIndex;
import edu.udo.piq.components.util.SymbolicFontKey;
import edu.udo.piq.tools.AbstractPTextComponent;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.MutablePBounds;
import edu.udo.piq.tools.MutablePSize;
import edu.udo.piq.util.ThrowException;

public class PTextArea extends AbstractPTextComponent {
	
	public static final Object FONT_ID = new SymbolicFontKey(PTextArea.class);
	protected static final PColor DEFAULT_BACKGROUND_COLOR = PColor.WHITE;
	
	protected PTextIndexTableMultiLine  idxTab = new PTextIndexTableMultiLine();
	protected boolean idxTableIsDirty = true;
	protected MutablePSize cachedResultSize = new MutablePSize();
	protected int columns = -1;
	
	public PTextArea(Object initialModelValue) {
		this();
		getModel().setValue(initialModelValue);
	}
	
	public PTextArea(PTextModel model) {
		super();
		setModel(model);
	}
	
	public PTextArea() {
		super();
	}
	
	public void setColumnCount(int value) {
		if (columns != value) {
			columns = value;
			firePreferredSizeChangedEvent();
		}
	}
	
	public int getColumnCount() {
		return columns;
	}
	
	@Override
	public void setModel(PTextModel model) {
		super.setModel(model);
		idxTableIsDirty = true;
	}
	
	@Override
	public void setSelection(PTextSelection selection) {
		super.setSelection(selection);
	}
	
	@Override
	public void setEditable(boolean value) {
		super.setEditable(value);
	}
	
	@Override
	public PTextIndexTable getIndexTable() {
		if (idxTableIsDirty) {
			buildIndexTable();
			idxTableIsDirty = false;
		}
		return idxTab;
	}
	
	protected void buildIndexTable() {
		String text = getText();
		int rowID = 0;
		idxTab.clear();
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '\n') {
				idxTab.setLastIndexInRow(rowID++, i);
			}
		}
		idxTab.setLastIndexInRow(rowID, text.length());
	}
	
	@Override
	protected void onTextChanged(PSingleValueModel<Object> model, Object oldValue, Object newValue) {
		idxTableIsDirty = true;
		super.onTextChanged(model, oldValue, newValue);
	}
	
	@Override
	public PBounds getRenderPositionForIndex(MutablePBounds result, PListIndex index) {
		ThrowException.ifNull(index, "index == null");
		int idx = index.getIndexValue();
		PFontResource font = getDefaultFont();
		if (font == null) {
			return null;
		}
		PBounds bounds = getBounds();
		if (bounds == null) {
			return null;
		}
		int x = bounds.getX();
		int y = bounds.getY();
		String text = getText();
		if (text == null) {
			return null;
		}
		ThrowException.ifNotWithin(0, text.length(), idx, "index < 0 || index >= getText().length()");
		StringBuilder sb = new StringBuilder();
		int drawX = x;
		int drawY = y;
		int lineH = 0;
		for (int i = 0; i < idx; i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				String line = sb.toString();
				PSize lineSize = font.getSize(line, cachedResultSize);
				lineH = lineSize.getHeight();
				
				drawX = x;
				drawY += lineH;
				sb.delete(0, sb.length());
			} else {
				sb.append(c);
			}
		}
		int drawW = 0;
		int drawH = lineH;
		if (sb.length() > 0) {
			String line = sb.toString();
			PSize lineSize = font.getSize(line, cachedResultSize);
			drawW = lineSize.getWidth();
			drawH = lineSize.getHeight();
		}
		if (result == null) {
			return new ImmutablePBounds(drawX, drawY, drawW, drawH);
		}
		result.set(drawX, drawY, drawW, drawH);
		return result;
	}
	
	@Override
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
		x -= bounds.getX();
		y -= bounds.getY();
		// Out of bounds => select first or last index
		if (x < 0 || y < 0) {
			return new PListIndex(0);
		}
		if (y > bounds.getFinalY()) {
			return new PListIndex(text.length());
		}
		int letterX = 0;
		int letterY = 0;
		int lineH = 0;
		// We store the last index on the correct line as a best guess return value
		int lastGoodIndex = 0;
		// Go through text, letter by letter, and calculate the letter bounds
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			PSize size = font.getSize(Character.toString(c), cachedResultSize);
			int letterW = size.getWidth();
			int letterH = size.getHeight();
			if (lineH < letterH) {
				lineH = letterH;
			}
			// Bounds is part of a line above point
			if (letterY + lineH >= y) {
				// No bounds on correct line fit, last bounds in line
				if (letterY > y) {
					return new PListIndex(lastGoodIndex);
				}
				// Bounds fit in both x and y axis
				if (letterX <= x && letterX + letterW >= x) {
					return new PListIndex(i);
				}
				// Advance last known good bounds
				lastGoodIndex = i;
			}
			letterX += letterW;
			if (c == '\n') {
				letterX = 0;
				letterY += lineH;
				lineH = 0;
			}
		}
		// In case we want the end of the string
		return new PListIndex(text.length());
	}
	
	protected void renderBackground(PRenderer renderer, int x, int y, int w, int h) {
		renderer.setColor(getDefaultBackgroundColor());
		renderer.drawQuad(x, y, x + w, y + h);
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
//		System.out.println("PTextArea.defaultRender()");
		PFontResource font = getDefaultFont();
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		
		renderBackground(renderer, x, y, bounds.getWidth(), bounds.getHeight());
		
		String text = getText();
		if (text == null) {
			return;
		}
		if (text.isEmpty()) {
			if (hasFocus() && getCaretRenderTimer().isFocusRender()) {
				renderer.setColor(getDefaultSelectionBackgroundColor());
				renderer.drawQuad(x, y, x + 2, y + font.getSize(" ", cachedResultSize).getHeight());
			}
			return;
		}
		
		PTextSelection selection = getSelection();
		PColor textColor = getDefaultTextUnselectedColor();
		if (selection != null && selection.hasSelection()) {
			PListIndex fromIndex = selection.getLowestSelectedIndex();
			PListIndex toIndex = selection.getHighestSelectedIndex();
			int selectedFrom = fromIndex.getIndexValue();
			int selectedTo = toIndex.getIndexValue();
			
			String beforeSelected = text.substring(0, selectedFrom);
			String afterSelected = text.substring(selectedTo);
			
			DrawPos pos = new DrawPos(x, y);
			renderText(renderer, font, x, y, pos, beforeSelected, textColor, null);
			if (selectedFrom == selectedTo) {
				if (hasFocus() && getCaretRenderTimer().isFocusRender()) {
					renderer.setColor(getDefaultSelectionBackgroundColor());
					char c;
					if (selectedFrom == text.length()) {
						c = text.charAt(selectedFrom - 1);
					} else {
						c = text.charAt(selectedFrom);
					}
					PSize charSize = font.getSize(Character.toString(c), cachedResultSize);
					int h = charSize.getHeight();
					renderer.drawQuad(pos.x, pos.y, pos.x + 2, pos.y + h);
				}
				renderText(renderer, font, x, y, pos, afterSelected, textColor, null);
			} else {
				String selected = text.substring(selectedFrom, selectedTo);
				renderText(renderer, font, x, y, pos, selected,
						getDefaultTextSelectedColor(), getDefaultSelectionBackgroundColor());
				
				renderText(renderer, font, x, y, pos, afterSelected, textColor, null);
			}
		} else {
			renderText(renderer, font, x, y, null, text, textColor, null);
		}
	}
	
	protected void renderText(PRenderer renderer, PFontResource font,
			int x, int y, DrawPos pos, String text,
			PColor txtColor, PColor bgColor)
	{
		StringBuilder sb = new StringBuilder();
		int drawX;
		int drawY;
		if (pos != null) {
			drawX = pos.x;
			drawY = pos.y;
		} else {
			drawX = x;
			drawY = y;
		}
		int lineH = 0;
		renderer.setColor(txtColor);
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				String line = sb.toString();
				PSize lineSize = font.getSize(line, cachedResultSize);
				
				lineH = lineSize.getHeight();
				if (bgColor != null) {
					int txtW = lineSize.getWidth();
					renderer.setColor(bgColor);
					renderer.drawQuad(drawX, drawY, drawX + txtW, drawY + lineH);
					renderer.setColor(txtColor);
				}
				renderer.drawString(font, line, drawX, drawY);
				
				drawX = x;
				drawY += lineH;
				sb.delete(0, sb.length());
			} else {
				sb.append(c);
			}
		}
		if (sb.length() > 0) {
			String line = sb.toString();
			PSize lineSize = font.getSize(line, cachedResultSize);
			int txtW = lineSize.getWidth();
			lineH = lineSize.getHeight();
			if (bgColor != null) {
				renderer.setColor(bgColor);
				renderer.drawQuad(drawX, drawY, drawX + txtW, drawY + lineH);
				renderer.setColor(txtColor);
			}
			renderer.drawString(font, line, drawX, drawY);
			drawX += txtW;
		}
		if (pos != null) {
			pos.x = drawX;
			pos.y = drawY;
		}
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		PFontResource font = getDefaultFont();
		if (font == null) {
			return PSize.ZERO_SIZE;
		}
		String text = getText();
		if (text == null || text.isEmpty()) {
			int colCount = getColumnCount();
			if (colCount > 0) {
				PSize letterSize = font.getSize("W", cachedResultSize);
				prefSize.setWidth(letterSize.getWidth() * colCount + 2);
				prefSize.setHeight(letterSize.getHeight());
				return prefSize;
			}
			return PSize.ZERO_SIZE;
		}
		int prefW = 0;
		int prefH = 0;
		StringBuilder sb = new StringBuilder();
		int lineH = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				if (sb.length() == 0) {
					prefH += lineH;
					continue;
				}
				String line = sb.toString();
				PSize lineSize = font.getSize(line, cachedResultSize);
				int lineW = lineSize.getWidth();
				lineH = lineSize.getHeight();
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
			PSize lineSize = font.getSize(line, cachedResultSize);
			int lineW = lineSize.getWidth();
			lineH = lineSize.getHeight();
			if (lineW > prefW) {
				prefW = lineW;
			}
			prefH += lineH;
		}
		int colCount = getColumnCount();
		if (colCount > 0) {
			PSize letterSize = font.getSize("W", cachedResultSize);
			prefW = letterSize.getWidth() * colCount;
		}
		prefSize.set(prefW + 2, prefH);
		return prefSize;
	}
	
	@Override
	public PCursor getMouseOverCursor(PMouse mouse) {
		return mouse.getCursorText();
	}
	
	protected PColor getDefaultBackgroundColor() {
		return DEFAULT_BACKGROUND_COLOR;
	}
	
	@Override
	protected PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.fetchFontResource(FONT_ID);
	}
	
	protected static class DrawPos {
		int x, y;
		public DrawPos(int x, int y) {
			this.x = x;
			this.y = y;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("(");
			builder.append(x);
			builder.append(", ");
			builder.append(y);
			builder.append(")");
			return builder.toString();
		}
	}
	
}