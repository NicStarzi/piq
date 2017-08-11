package edu.udo.piq.lwjgl3;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PDialog;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PImageMeta;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.borders.PLineBorder;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.collections.PListModel;
import edu.udo.piq.components.collections.PListSelection;
import edu.udo.piq.components.collections.PListSingleSelection;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPDnDSupport;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.util.DefaultPKeyInput;
import edu.udo.piq.components.util.PKeyInput.FocusPolicy;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.lwjgl3.StbImageResource.TexelFormat;
import edu.udo.piq.lwjgl3.renderer.LwjglPRendererBase;
import edu.udo.piq.lwjgl3.renderer.LwjglPRendererFbo;
import edu.udo.piq.scroll.PScrollPanel;
import edu.udo.piq.tools.AbstractPBounds;
import edu.udo.piq.tools.AbstractPRoot;
import edu.udo.piq.util.SoftReferenceCache;
import edu.udo.piq.util.ThrowException;

public class GlfwPRoot extends AbstractPRoot {
	
	public static ByteBuffer loadFileToByteBuffer(File file) throws IOException {
		ByteBuffer buffer;
		try (FileInputStream fis = new FileInputStream(file);
				FileChannel fc = fis.getChannel();)
		{
			buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		}
		return buffer;
	}
	
	public static void main(String[] args) {
		GlfwPRoot root = new GlfwPRoot("Test Window", 640, 480);
		
		PPanel body = new PPanel();
		PGridLayout layout = new PGridLayout(body, 1, 2);
		layout.setColumnGrowth(Growth.MAXIMIZE);
		layout.setRowGrowth(0, Growth.MAXIMIZE);
		body.setLayout(layout);
		
		PListModel listMdl = new DefaultPListModel("Alice", "Bob", "Clarice", "Daniel", "Erika", "Franz");
		PList list = new PList(listMdl);
		list.setDragAndDropSupport(new DefaultPDnDSupport());
		list.setSelection(new PListSingleSelection());
		PListSelection listSel = list.getSelection();
		
		PScrollPanel scroll = new PScrollPanel(list);
		
		PButton btnAdd = new PButton(new PLabel("Add"));
		btnAdd.defineInput("hotkey",
				new DefaultPKeyInput<PButton>(FocusPolicy.ALWAYS, Key.A, Modifier.CTRL),
				btn -> btn.simulateClick());
		PCursor cursor = root.createCustomCursor(root.fetchImageResource("Cursor.png"), 8, 8);
		btnAdd.setMouseOverCursor(cursor);
		btnAdd.getContent().setMouseOverCursor(cursor);
		
		PButton btnRemove = new PButton(new PLabel("Remove"));
		btnRemove.defineInput("hotkey",
				new DefaultPKeyInput<PButton>(FocusPolicy.ALWAYS, Key.R, Modifier.CTRL),
				btn -> btn.simulateClick());
		PPanel btnPnl = new PPanel();
		btnPnl.setBorder(new PLineBorder(2));
		btnPnl.setLayout(new PListLayout(btnPnl, ListAlignment.CENTERED_LEFT_TO_RIGHT));
		btnPnl.addChild(btnAdd, null);
		btnPnl.addChild(btnRemove, null);
		
		body.addChild(scroll, "0 0 alignX=F alignY=F");
		body.addChild(btnPnl, "0 1 alignX=F alignY=F");
		
		int[] idx = {0};
		btnAdd.addObs((PClickObs) clickable -> {
			String newElem = "New Element #"+idx[0]++;
			if (listSel.hasSelection()) {
				PModelIndex selectedIdx = listSel.getOneSelected();
				if (listMdl.canAdd(selectedIdx, newElem)) {
					listMdl.add(selectedIdx, newElem);
					listSel.clearSelection();
					listSel.addSelection(selectedIdx);
				}
			} else {
				listMdl.add(newElem);
			}
		});
		btnRemove.addObs((PClickObs) clickable -> {
			if (listSel.hasSelection()) {
				PModelIndex selectedIdx = listSel.getOneSelected();
				if (listMdl.canRemove(selectedIdx)) {
					listMdl.remove(selectedIdx);
					listSel.clearSelection();
					listSel.addSelection(selectedIdx);
				}
			}
		});
		root.setBody(body);
		
		root.startGlfwLoop();
	}
	
	protected final PBounds wndBounds = new AbstractPBounds() {
		@Override
		public int getX() {
			return 0;
		}
		@Override
		public int getY() {
			return 0;
		}
		@Override
		public int getWidth() {
			return wndW;
		}
		@Override
		public int getHeight() {
			return wndH;
		}
	};
	protected final GLFWWindowSizeCallback wndSizeCB = new GLFWWindowSizeCallback() {
		@Override
		public void invoke(long window, int width, int height) {
			GlfwPRoot.this.onWindowSizeChanged(width, height);
		}
	};
	protected final GLFWWindowPosCallback wndPosCB = new GLFWWindowPosCallback() {
		@Override
		public void invoke(long window, int xpos, int ypos) {
			GlfwPRoot.this.onWindowPositionChanged(xpos, ypos);
		}
	};
	protected final GLFWFramebufferSizeCallback frameBufSizeCB = new GLFWFramebufferSizeCallback() {
		@Override
		public void invoke(long window, int width, int height) {
			GlfwPRoot.this.onFrameBufferSizeChanged(width, height);
		}
	};
	protected final GLFWErrorCallback errorCB = GLFWErrorCallback.createPrint(System.out);
//	protected final GLFWErrorCallback errorCB = new GLFWErrorCallback() {
//		@Override
//		public void invoke(int error, long description) {
//			System.err.println("error="+error+"; description="+description);
//		}
//	};
	protected final LwjglPRendererBase renderer = new LwjglPRendererFbo();
	protected final GlfwPKeyboard keyboard = new GlfwPKeyboard(this);
	protected final GlfwPMouse mouse = new GlfwPMouse(this);
	protected final SoftReferenceCache<FontInfo, StbTtFontResource> fontMap = new SoftReferenceCache<>();
	protected final SoftReferenceCache<String, StbImageResource> imgMap = new SoftReferenceCache<>();
	protected StbTtFontResource defaultFontRes;
	
	protected int wndX, wndY, wndW, wndH;
	protected long wndHandle;
	protected double deltaTimeMs;
	protected String wndTitle;
	protected GLCapabilities caps;
	protected Callback debugProc;
	protected boolean needReRender;
	
	public GlfwPRoot(String title, int initialWindowWidth, int initialWindowHeight) {
		super();
		reRenderSet = null;
		
		GLFW.glfwInit();
//		GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//		GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		GLFW.glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		
		wndTitle = title;
		wndHandle = GLFW.glfwCreateWindow(initialWindowWidth, initialWindowHeight, wndTitle, NULL, NULL);
		
		GLFW.glfwMakeContextCurrent(wndHandle);
		GLFW.glfwSwapInterval(1);
		GLFW.glfwShowWindow(wndHandle);
		
		caps = GL.createCapabilities();
		debugProc = GLUtil.setupDebugMessageCallback(System.out);
		GLFW.glfwSetErrorCallback(errorCB);
		GLFW.glfwSetWindowSizeCallback(wndHandle, wndSizeCB);
		GLFW.glfwSetWindowPosCallback(wndHandle, wndPosCB);
		GLFW.glfwSetFramebufferSizeCallback(wndHandle, frameBufSizeCB);
		
		IntBuffer bufFrameBufSize = BufferUtils.createIntBuffer(2);
		GLFW.nglfwGetFramebufferSize(wndHandle, memAddress(bufFrameBufSize), memAddress(bufFrameBufSize) + 4);
		onFrameBufferSizeChanged(bufFrameBufSize.get(0), bufFrameBufSize.get(1));
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		
		super.keyboard = keyboard;
		keyboard.install();
		super.mouse = mouse;
		mouse.install();
		dndManager = new PDnDManager(this);
	}
	
	public void requestClose() {
		GLFW.glfwSetWindowShouldClose(wndHandle, true);
	}
	
	public void dispose() {
		try {
			if (debugProc != null) {
				debugProc.free();
			}
			keyboard.uninstall();
			mouse.uninstall();
//			Callbacks.glfwFreeCallbacks(wndHandle);
			GLFW.glfwDestroyWindow(wndHandle);
		} finally {
			GLFW.glfwTerminate();
		}
	}
	
	public void startGlfwLoop() {
		long timeStamp = System.nanoTime();
		long lastTimeStamp = timeStamp;
		while (!GLFW.glfwWindowShouldClose(wndHandle)) {
			checkGlError("glfwLoop");
			
			timeStamp = System.nanoTime();
			long timeSince = timeStamp - lastTimeStamp;
			lastTimeStamp = timeStamp;
			deltaTimeMs = timeSince / 1_000_000.0;
			super.update(deltaTimeMs);
			
			GLFW.glfwPollEvents();
			if (wndW > 0 && wndH > 0) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				if (needReRender) {
					defaultRootRender(renderer, 0, 0, wndW, wndH);
				}
				renderer.renderAll();
				GLFW.glfwSwapBuffers(wndHandle);
			}
		}
		dispose();
	}
	
	public static void checkGlError(String info) {
		int errCode = GL11.glGetError();
		if (errCode != GL11.GL_NO_ERROR) {
			String errMsg;
			switch (errCode) {
			case GL11.GL_INVALID_ENUM: errMsg = "Invalid Enum"; break;
			case GL11.GL_INVALID_OPERATION: errMsg = "Invalid Operation"; break;
			case GL11.GL_INVALID_VALUE: errMsg = "Invalid Value"; break;
			case GL11.GL_STACK_OVERFLOW: errMsg = "Stack Overflow"; break;
			case GL11.GL_STACK_UNDERFLOW: errMsg = "Stack Underflow"; break;
			case GL11.GL_OUT_OF_MEMORY: errMsg = "Out of Memory"; break;
			case GL30.GL_INVALID_FRAMEBUFFER_OPERATION: errMsg = "Invalid Fbo Operation"; break;
			default: errMsg = "Unknown Error";
			}
			System.err.print("Gl Error: '");
			System.err.print(errMsg);
			System.err.print("' at: ");
			System.err.println(info);
		}
	}
	
	@Override
	public void reRender(PComponent component) {
//		System.out.println("GlfwPRoot.reRender() c="+component);
		needReRender = true;
	}
	
	@Override
	protected void defaultRootRender(PRenderer renderer,
			int rootClipX, int rootClipY, int rootClipFx, int rootClipFy)
	{
		this.renderer.beginReRender();
//		System.out.println("GlfwPRoot.defaultRootRender()");
		
		Deque<RenderStackInfo> renderStack = new ArrayDeque<>();
		renderStack.push(new RenderStackInfo(getBody(), rootClipX, rootClipY, rootClipFx, rootClipFy));
		PRootOverlay overlay = getOverlay();
		if (overlay != null) {
			for (PComponent overlayComp : overlay.getChildren()) {
				renderStack.addFirst(new RenderStackInfo(overlayComp, rootClipX, rootClipY, rootClipFx, rootClipFy));
			}
		}
		AbstractPRoot.defaultRootRender(this, this.renderer, renderStack);
		needReRender = false;
		this.renderer.endReRender();
	}
	
	@Override
	public GlfwPKeyboard getKeyboard() {
		return (GlfwPKeyboard) super.getKeyboard();
	}
	
	@Override
	public GlfwPMouse getMouse() {
		return (GlfwPMouse) super.getMouse();
	}
	
	@Override
	public PBounds getBounds() {
		return wndBounds;
	}
	
	@Override
	public void onMouseOverCursorChanged(PComponent component) {
		mouse.mouseOverCursorChanged(component);
	}
	
	@Override
	public PDialog createDialog() {
		return null;
	}
	
	@Override
	public StbTtFontResource fetchFontResource(String fontName, int pixelSize, Style style) {
		if (style != Style.PLAIN) {
			System.err.println("GlfwPRoot.fetchFontResource() style="+style);
			style = Style.PLAIN;
		}
		FontInfo info = new FontInfo(fontName, pixelSize, style);
		StbTtFontResource fontRes = fontMap.get(info);
		if (fontRes == null) {
			String fileName;
			if (fontName.toLowerCase().endsWith(".ttf")) {
				fileName = fontName;
			} else {
				fileName = fontName.concat(".ttf");
			}
//			System.out.println("ttfFile="+fileName);
			File ttfFile = new File(fileName);
//			System.out.println("exists?="+ttfFile.exists()+"; isFile?="+ttfFile.isFile());
			if (ttfFile.exists() && ttfFile.isFile()) {
				fontRes = new StbTtFontResource(ttfFile, pixelSize);
				fontMap.put(info, fontRes);
			} else {
				if (defaultFontRes == null) {
					defaultFontRes = new StbTtFontResource(new File("defaultFont.ttf"), 16);
				}
				return defaultFontRes;
			}
		}
		return fontRes;
	}
	
	@Override
	public boolean isFontSupported(PFontResource font) {
		return font instanceof StbTtFontResource;
	}
	
	@Override
	public StbImageResource fetchImageResource(Object imgID) {
		ThrowException.ifNull(imgID, "imgID == null");
		String imgPath;
		if (imgID instanceof String) {
			imgPath = (String) imgID;
		} else {
			imgPath = imgID.toString();
		}
		StbImageResource imgRes = imgMap.get(imgPath);
		if (imgRes == null) {
			try {
				imgRes = new StbImageResource(new File(imgPath));
				imgMap.put(imgPath, imgRes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return imgRes;
	}
	
	@Override
	public boolean isImageSupported(PImageResource imageResource) {
		return imageResource instanceof StbImageResource;
	}
	
	@Override
	public StbImageResource createImageResource(int width, int height, PImageMeta metaInfo) {
		return new StbImageResource(width, height);
	}
	
	@Override
	public GlfwPCursor createCustomCursor(PImageResource image, int offsetX, int offsetY) {
		StbImageResource imgRes = ThrowException.ifTypeCastFails(image,
				StbImageResource.class, "!(image instanceof StbIPImageResource)");
		
		int imgW = imgRes.getWidth();
		int imgH = imgRes.getHeight();
		ByteBuffer texelBuffer = imgRes.getTexels();
		ByteBuffer imagePixelBuffer = imgRes.getTexelFormat().transform(TexelFormat.RGBA, texelBuffer);
		
		GLFWImage glfwImage = GLFWImage.create();
		glfwImage.set(imgW, imgH, imagePixelBuffer);
		return new GlfwPCursor(glfwImage, offsetX, offsetY);
	}
	
	@Override
	public double getDeltaTime() {
		return deltaTimeMs;
	}
	
	protected void onWindowPositionChanged(int x, int y) {
		wndX = x;
		wndY = y;
	}
	
	protected void onWindowSizeChanged(int width, int height) {
		onFrameBufferSizeChanged(width, height);
	}
	
	protected void onFrameBufferSizeChanged(int width, int height) {
		if (wndW == width && wndH == height) {
			return;
		}
		wndW = width;
		wndH = height;
		renderer.setViewportSize(wndW, wndH);
		if (wndW > 0 && wndH > 0) {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, wndW, wndH, 0, 1, -1);
			GL11.glViewport(0, 0, wndW, wndH);
		}
		fireSizeChanged();
	}
	
}