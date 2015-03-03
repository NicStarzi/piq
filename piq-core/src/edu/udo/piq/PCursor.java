package edu.udo.piq;

public interface PCursor {
	
	public PCursorType getCursorType();
	
	public static enum PCursorType {
		NORMAL, 
		HAND, 
		TEXT, 
		SCROLL, 
		BUSY, 
		CUSTOM, 
		;
	}
	
}