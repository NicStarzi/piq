package edu.udo.piq.lwjgl3;

import org.lwjgl.glfw.GLFW;

import edu.udo.piq.PClipboard;
import edu.udo.piq.util.Throw;

public class GlfwPClipboard implements PClipboard {
	
	protected final long wndHandle;
	
	public GlfwPClipboard(long windowHandle) {
		wndHandle = windowHandle;
	}
	
	@Override
	public boolean put(Object obj) {
		Throw.ifNull(obj, "obj == null");
		try {
			GLFW.glfwSetClipboardString(wndHandle, obj.toString());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public Object get() {
		return GLFW.glfwGetClipboardString(wndHandle);
	}
	
}