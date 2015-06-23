package edu.udo.piq.lwjgl3;

import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PDialog;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRoot;
import edu.udo.piq.tools.AbstractPBounds;
import edu.udo.piq.tools.AbstractPRoot;
import edu.udo.piq.util.PGuiTreeIterator;

public class Lwjgl3PRoot extends AbstractPRoot implements PRoot {
	
	private final GLFWErrorCallback errorCB = Callbacks.errorCallbackPrint(System.err);
	private final GLFWWindowSizeCallback sizeCB = new GLFWWindowSizeCallback() {
		public void invoke(long window, int width, int height) {
			wndWidth = width;
			wndHeight = height;
		}
	};
	private final GLFWWindowPosCallback posCB = new GLFWWindowPosCallback() {
		public void invoke(long window, int xpos, int ypos) {
			wndX = xpos;
			wndY = ypos;
		}
	};
	private final AbstractPBounds bnds = new AbstractPBounds() {
		public int getY() {
			return wndY;
		}
		public int getX() {
			return wndX;
		}
		public int getWidth() {
			return wndWidth;
		}
		public int getHeight() {
			return wndHeight;
		}
	};
	private final Lwjgl3PRenderer renderer = new Lwjgl3PRenderer();
	private int wndX, wndY, wndWidth, wndHeight;
	private long wndHnd;
	private boolean needReRender = true;
	
	public Lwjgl3PRoot(int x, int y, int w, int h) {
		glfwSetErrorCallback(errorCB);
		if (glfwInit() != GL11.GL_TRUE) {
			throw new IllegalStateException("glfwInit == false");
		}
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		wndHnd = glfwCreateWindow(w, h, "Window", NULL, NULL);
		if (wndHnd == NULL) {
			throw new RuntimeException("glfwCreateWindow == NULL");
		}
		glfwSetWindowPosCallback(wndHnd, posCB);
		glfwSetWindowSizeCallback(wndHnd, sizeCB);
		glfwSetCharCallback(wndHnd, getKeyboard().charCB);
		glfwSetKeyCallback(wndHnd, getKeyboard().keyCB);
		glfwSetMouseButtonCallback(wndHnd, getMouse().mouseBtnCB);
		glfwSetCursorPosCallback(wndHnd, getMouse().mousePosCB);
		
		glfwSetWindowPos(wndHnd, x, y);
		glfwMakeContextCurrent(wndHnd);
		glfwSwapInterval(1);
		glfwShowWindow(wndHnd);
		GLContext.createFromCurrent();
		glClearColor(0f, 0f, 0f, 1.0f);
	}
	
	public long getWindowHandle() {
		return wndHnd;
	}
	
	public void dispose() {
		try {
			posCB.release();
			sizeCB.release();
			getKeyboard().charCB.release();
			getKeyboard().keyCB.release();
			getMouse().mouseBtnCB.release();
			getMouse().mousePosCB.release();
		} catch (Exception e) {
			// ignore exception
		}
		try {
			glfwDestroyWindow(wndHnd);
		} finally {
			glfwTerminate();
			errorCB.release();
		}
	}
	
	/*
	 * Overwrites the protected super method
	 */
	public void setDesignSheet(PDesignSheet designSheet) {
		super.setDesignSheet(designSheet);
	}
	
	public PBounds getBounds() {
		return bnds;
	}
	
	public void update() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		if (needReRender) {
			renderer.startRendering();
			for (PComponent comp : new PGuiTreeIterator(this)) {
				PDesign design = comp.getDesign();
				if (design == null) {
					comp.defaultRender(renderer);
				} else {
					design.render(renderer, comp);
				}
			}
			renderer.endRendering();
		}
		renderer.renderAll();
		
		glfwSwapBuffers(wndHnd);
		glfwPollEvents();
		super.update();
	}
	
	public void reRender(PComponent component) {
	}
	
	public PDialog createDialog() {
		return null;
	}
	
	public PFontResource fetchFontResource(String fontName, double pointSize, Style style) {
		Lwjgl3PFont font = new Lwjgl3PFont();
		font.create(fontName, pointSize, style);
		return font;
	}
	
	public PImageResource fetchImageResource(String imgPath) {
		Lwjgl3PImage img = new Lwjgl3PImage();
		try {
			img.loadFromStream(new FileInputStream(imgPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public Lwjgl3PKeyboard getKeyboard() {
		return (Lwjgl3PKeyboard) super.getKeyboard();
	}
	
	public Lwjgl3PMouse getMouse() {
		return (Lwjgl3PMouse) super.getMouse();
	}
}