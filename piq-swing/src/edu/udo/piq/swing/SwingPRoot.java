package edu.udo.piq.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PDialog;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PImageMeta;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PStyleSheet;
import edu.udo.piq.tools.AbstractPRoot;
import edu.udo.piq.util.SoftReferenceCache;
import edu.udo.piq.util.ThrowException;

public class SwingPRoot extends AbstractPRoot implements PRoot {
	
	private static final boolean SMART_RE_RENDER = true;
	private final ReentrantLock renderLock = new ReentrantLock();
	
	protected final List<SwingPDialog> openedDialogs = new ArrayList<>();
	private final SoftReferenceCache<FontInfo, AwtPFontResource> fontMap = new SoftReferenceCache<>();
	private final SoftReferenceCache<String, AwtPImageResource> imgMap = new SoftReferenceCache<>();
	protected final SwingPRenderer renderer = new SwingPRenderer();
	protected final SwingPMouse mouse;
	protected final SwingPKeyboard keyboard;
	protected final SwingPClipboard clipboard = new SwingPClipboard();
	protected final PDnDManager dndManager = new PDnDManager(this);
	protected final JCompPBounds bounds;
	protected final Component awtComp;
	protected double deltaTime = 0;
	
	public SwingPRoot(Component awtComp) {
		super();
		this.awtComp = awtComp;
		mouse = new SwingPMouse(this, awtComp);
		keyboard = new SwingPKeyboard(awtComp);
		bounds = new JCompPBounds(awtComp);
		
		awtComp.setFocusable(true);
		awtComp.requestFocus();
		awtComp.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				reRender(SwingPRoot.this);
				fireSizeChanged();
			}
			@Override
			public void componentResized(ComponentEvent e) {
				reRender(SwingPRoot.this);
				fireSizeChanged();
			}
		});
		super.mouse = mouse;
		super.keyboard = keyboard;
		super.clipboard = clipboard;
		super.dndManager = dndManager;
	}
	
	public Component getAwtComponent() {
		return awtComp;
	}
	
	/*
	 * Overwrites the protected super method
	 */
	@Override
	public void setStyleSheet(PStyleSheet styleSheet) {
		super.setStyleSheet(styleSheet);
	}
	
	@Override
	public void onMouseOverCursorChanged(PComponent component) {
		mouse.mouseOverCursorChanged(component);
	}
	
	@Override
	public double getDeltaTime() {
		return deltaTime;
	}
	
	@Override
	public void update(double deltaTime) {
		this.deltaTime = deltaTime;
		while (!openedDialogs.isEmpty()) {
			PDialog dlg = openedDialogs.get(openedDialogs.size() - 1);
			if (dlg.isDisposed()) {
				openedDialogs.remove(dlg);
			} else {
//				dlg.update();
//				mouse.update();
				return;
			}
		}
		super.update(deltaTime);
		
		mouse.update();
	}
	
	@Override
	public PDialog createDialog() {
		Component awtComp = getAwtComponent();
		
		JDialog jDlg = new JDialog(SwingUtilities.getWindowAncestor(awtComp));
		jDlg.setSize(320, 240);
		jDlg.setLocationRelativeTo(awtComp);
		SwingPDialog pDlg = new SwingPDialog(this, jDlg);
		jDlg.setContentPane(pDlg.getPanel());
		
		openedDialogs.add(pDlg);
		
		return pDlg;
	}
	
	@Override
	public boolean isFontSupported(PFontResource font) {
		return font instanceof AwtPFontResource;
	}
	
	@Override
	public PFontResource fetchFontResource(String fontName, int pixelSize, Style style)
			throws NullPointerException, IllegalArgumentException
	{
		FontInfo info = new FontInfo(fontName, pixelSize, style);
		AwtPFontResource fontRes = fontMap.get(info);
		if (fontRes == null) {
			int awtStyle = AwtPFontResource.getAwtStyle(info.getStyle());
			fontRes = new AwtPFontResource(new Font(fontName, awtStyle, pixelSize));
			fontMap.put(info, fontRes);
		}
		return fontRes;
	}
	
	@Override
	public boolean isImageSupported(PImageResource imageResource) {
		return imageResource instanceof AwtPImageResource;
	}
	
	@Override
	public PImageResource fetchImageResource(Object imgID)
			throws NullPointerException
	{
		ThrowException.ifNull(imgID, "imgID == null");
		String imgPath;
		if (imgID instanceof String) {
			imgPath = (String) imgID;
		} else {
			imgPath = imgID.toString();
		}
		AwtPImageResource imgRes = imgMap.get(imgPath);
		if (imgRes == null) {
			BufferedImage bufImg = null;
			try {
				bufImg = ImageIO.read(new File(imgPath));
			} catch (IOException e) {
				System.err.println(e.getMessage());
//				e.printStackTrace();
			}
			imgRes = new AwtPImageResource(bufImg);
			imgMap.put(imgPath, imgRes);
		}
		return imgRes;
	}
	
	@Override
	public PImageResource createImageResource(int width, int height,
			PImageMeta metaInfo) throws IllegalArgumentException
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		AwtPImageResource res = new AwtPImageResource(img);
		return res;
	}
	
	@Override
	public PCursor createCustomCursor(PImageResource image, int offsetX,
			int offsetY) throws IllegalArgumentException
	{
		return null;
	}
	
	@Override
	public PBounds getBounds() {
		return bounds;
	}
	
	@Override
	public void reRender(PComponent component) {
		Component awtComp = getAwtComponent();
		if (awtComp != null) {
//			System.out.println("SwingPRoot.reRender="+component);
			renderLock.lock();
			try {
				if (SMART_RE_RENDER) {
					reRenderSet.add(component);
				} else {
					reRenderSet.add(this);
				}
			} finally {
				renderLock.unlock();
			}
		}
	}
	
	protected void render(Graphics2D g) {
//		System.out.println("### RENDER ALL ###");
		renderer.setAwtGraphics(g);
		PBounds bnds = getBounds();
		int rootFx = bnds.getWidth();
		int rootFy = bnds.getHeight();
		
		// A JCompPRoot always has its origin at (0,0) as per Swing convention
		defaultRootRender(renderer, 0, 0, rootFx, rootFy);
	}
	
	protected void fullRootRender() {
		int w = bounds.getWidth();
		int h = bounds.getHeight();
		Deque<RenderStackInfo> renderStack;
		renderLock.lock();
		try {
			renderStack = AbstractPRoot.createRenderStack(this, reRenderSet, 0, 0, w, h);
			reRenderSet.clear();
		} finally {
			renderLock.unlock();
		}
		AbstractPRoot.defaultRootRender(this, renderer, renderStack);
	}
	
	@Override
	protected void defaultRootRender(PRenderer renderer,
			int rootClipX, int rootClipY, int rootClipFx, int rootClipFy)
	{
		Deque<RenderStackInfo> renderStack;
		renderLock.lock();
		try {
			renderStack = AbstractPRoot.createRenderStack(this,
					reRenderSet, rootClipX, rootClipY, rootClipFx, rootClipFy);
			reRenderSet.clear();
		} finally {
			renderLock.unlock();
		}
//		System.out.println("### defaultRootRender ###");
		AbstractPRoot.defaultRootRender(this, renderer, renderStack);
//		System.out.println("#######");
//		System.out.println();
	}
	
}