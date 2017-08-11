package edu.udo.piq.lwjgl3;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import edu.udo.piq.PCursor;

public class GlfwPCursor implements PCursor {
	
	protected final String name;
	protected final GLFWImage glfwImage;
	protected final long glfwHandle;
	protected boolean disposed;
	
	GlfwPCursor(GlfwStandardCursorType standardCursorType) {
		glfwHandle = GLFW.glfwCreateStandardCursor(standardCursorType.glfwShape);
		glfwImage = null;
		name = standardCursorType.name();
	}
	
	public GlfwPCursor(long glfwCursorHandle) {
		glfwHandle = glfwCursorHandle;
		glfwImage = null;
		name = "Custom (from handle)";
	}
	
	public GlfwPCursor(GLFWImage image, int hotspotX, int hotspotY) {
		glfwHandle = GLFW.glfwCreateCursor(image, hotspotX, hotspotY);
		glfwImage = image;
		name = "Custom (from image)";
	}
	
	public long getGlfwHandle() {
		return glfwHandle;
	}
	
	public boolean isDisposed() {
		return disposed;
	}
	
	@Override
	public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		GLFW.glfwDestroyCursor(glfwHandle);
		if (glfwImage != null) {
			glfwImage.free();
		}
	}
	
	@Override
	protected void finalize() {
		dispose();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}