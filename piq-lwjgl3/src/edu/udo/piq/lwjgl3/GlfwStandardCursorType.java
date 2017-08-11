package edu.udo.piq.lwjgl3;

import org.lwjgl.glfw.GLFW;

public enum GlfwStandardCursorType {
	
	ARROW		(GLFW.GLFW_ARROW_CURSOR),
	HAND		(GLFW.GLFW_HAND_CURSOR),
	CROSSHAIR	(GLFW.GLFW_CROSSHAIR_CURSOR),
	RESIZE_H	(GLFW.GLFW_HRESIZE_CURSOR),
	RESIZE_V	(GLFW.GLFW_VRESIZE_CURSOR),
	TEXT		(GLFW.GLFW_IBEAM_CURSOR),
	;
	
	public final int glfwShape;
	private GlfwPCursor cachedCrsObj;
	
	private GlfwStandardCursorType(int shape) {
		glfwShape = shape;
	}
	
	public GlfwPCursor getCursor() {
		if (cachedCrsObj == null) {
			cachedCrsObj = new GlfwPCursor(this);
		}
		return cachedCrsObj;
	}
	
}