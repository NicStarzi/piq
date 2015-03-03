package edu.udo.piq.swing;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JComponent;

import edu.udo.piq.PCursor;

public class AwtPCursor implements PCursor {
	
	private static final int[] piqToAwtMap = new int[PCursorType.values().length];
	static {
		piqToAwtMap[PCursorType.NORMAL.ordinal()] = Cursor.DEFAULT_CURSOR;
		piqToAwtMap[PCursorType.BUSY.ordinal()] = Cursor.WAIT_CURSOR;
		piqToAwtMap[PCursorType.CUSTOM.ordinal()] = Cursor.CUSTOM_CURSOR;
		piqToAwtMap[PCursorType.HAND.ordinal()] = Cursor.HAND_CURSOR;
		piqToAwtMap[PCursorType.SCROLL.ordinal()] = Cursor.MOVE_CURSOR;
		piqToAwtMap[PCursorType.TEXT.ordinal()] = Cursor.TEXT_CURSOR;
	}
	
	private final int awtType;
	private final PCursorType type;
	private final Cursor cursor;
	
	public AwtPCursor(Image image, int offsetX, int offsetY) {
		if (image == null) {
			throw new IllegalArgumentException("image="+image);
		}
		type = PCursorType.CUSTOM;
		awtType = Cursor.CUSTOM_CURSOR;
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension bestSize = tk.getBestCursorSize(image.getWidth(null), image.getHeight(null));
		
		offsetX = Math.max(0, Math.min(offsetX, bestSize.width));
		offsetY = Math.max(0, Math.min(offsetY, bestSize.height));
		
		cursor = tk.createCustomCursor(image, new Point(offsetX, offsetY), "CustomCursor");
	}
	
	public AwtPCursor(PCursorType cursorType) {
		if (cursorType == null || cursorType == PCursorType.CUSTOM) {
			throw new IllegalArgumentException("cursorType="+cursorType);
		}
		type = cursorType;
		awtType = piqToAwtMap[type.ordinal()];
		cursor = Cursor.getPredefinedCursor(awtType);
	}
	
	public PCursorType getCursorType() {
		return type;
	}
	
	public void applyTo(JComponent component) {
		component.setCursor(cursor);
	}
	
}