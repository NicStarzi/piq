package edu.udo.piq.swing;

import java.awt.Cursor;

import javax.swing.JComponent;

import edu.udo.piq.PCursor;

public enum AwtPCursor implements PCursor {
	
	DEFAULT		(Cursor.DEFAULT_CURSOR),
	BUSY		(Cursor.WAIT_CURSOR),
	HAND		(Cursor.HAND_CURSOR),
	SCROLL		(Cursor.MOVE_CURSOR),
	TEXT		(Cursor.TEXT_CURSOR),
	;
	
	public final int awtEnum;
	public final Cursor awtCursor;
	
	private AwtPCursor(int value) {
		awtEnum = value;
		awtCursor = Cursor.getPredefinedCursor(awtEnum);
	}
	
	public void applyTo(JComponent component) {
		component.setCursor(awtCursor);
	}
	
}