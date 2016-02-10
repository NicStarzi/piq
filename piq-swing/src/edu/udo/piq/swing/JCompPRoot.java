package edu.udo.piq.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PDialog;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PImageMeta;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRoot;
import edu.udo.piq.tools.AbstractPRoot;
import edu.udo.piq.tools.MutablePBounds;
import edu.udo.piq.tools.ReRenderSet;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public class JCompPRoot extends AbstractPRoot implements PRoot {
	
	private static final boolean SMART_RE_RENDER = true;
	
	private Window wnd;
	private final WindowListener wndListener = new WindowAdapter() {
		public void windowIconified(WindowEvent e) {
			reRender(JCompPRoot.this);
		}
		public void windowDeiconified(WindowEvent e) {
			reRender(JCompPRoot.this);
		}
	};
	private final JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;
		public void addNotify() {
			super.addNotify();
			Component awtRoot = SwingUtilities.getRoot(panel);
			if (awtRoot != null && awtRoot instanceof Window) {
				wnd = (Window) awtRoot;
				wnd.addWindowListener(wndListener);
			}
		}
		public void removeNotify() {
			super.removeNotify();
			if (wnd != null) {
				wnd.removeWindowListener(wndListener);
			}
		}
		public void paintComponent(Graphics g) {
			render((Graphics2D) g);
		}
	};
	private final List<SwingPDialog> openedDialogs = new ArrayList<>();
	private final Map<FontInfo, AwtPFontResource> fontMap = new HashMap<>();
	private final Map<String, BufferedPImageResource> imgMap = new HashMap<>();
	private final SwingPRenderer renderer = new SwingPRenderer();
	private final SwingPMouse mouse = new SwingPMouse(this, panel);
	private final SwingPKeyboard keyboard = new SwingPKeyboard(panel);
	private final SwingPClipboard clipboard = new SwingPClipboard();
	private final PDnDManager dndManager = new PDnDManager(this);
	private final JPanelPBounds bounds = new JPanelPBounds(panel);
	private final ReRenderSet reRenderSet = new ReRenderSet(this);
	
	public JCompPRoot() {
		super();
		panel.setFocusable(true);
		panel.requestFocus();
		panel.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				reRender(JCompPRoot.this);
				fireSizeChanged();
			}
			public void componentResized(ComponentEvent e) {
				reRender(JCompPRoot.this);
				fireSizeChanged();
			}
		});
		super.mouse = mouse;
		super.keyboard = keyboard;
		super.clipboard = clipboard;
		super.dndManager = dndManager;
	}
	
	/*
	 * Overwrites the protected super method
	 */
	public void setDesignSheet(PDesignSheet designSheet) {
		super.setDesignSheet(designSheet);
	}
	
	public JPanel getJPanel() {
		return panel;
	}
	
	public void mouseOverCursorChanged(PComponent component) {
		mouse.mouseOverCursorChanged(component);
	}
	
	public void update() {
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
		super.update(1);
		
		mouse.update();
	}
	
	public PDialog createDialog() {
		JDialog jDlg = new JDialog(SwingUtilities.getWindowAncestor(panel));
		jDlg.setSize(320, 240);
		jDlg.setLocationRelativeTo(panel);
		SwingPDialog pDlg = new SwingPDialog(this, jDlg);
		jDlg.setContentPane(pDlg.getPanel());
		
		openedDialogs.add(pDlg);
		
		return pDlg;
	}
	
	public PFontResource fetchFontResource(String fontName, double pointSize, Style style) 
			throws NullPointerException, IllegalArgumentException 
	{
		FontInfo info = new FontInfo(fontName, pointSize, style);
		AwtPFontResource fontRes = fontMap.get(info);
		if (fontRes == null) {
			fontRes = new AwtPFontResource(new Font(fontName, getAwtStyle(info), (int) pointSize));
			fontMap.put(info, fontRes);
		}
		return fontRes;
	}
	
	private int getAwtStyle(FontInfo info) {
		Style style = info.getStyle();
		int awtStyle;
		switch (style) {
		case BOLD:
			awtStyle = Font.BOLD;
			break;
		case BOLD_ITALIC:
			awtStyle = Font.ITALIC | Font.BOLD;
			break;
		case ITALIC:
			awtStyle = Font.ITALIC;
			break;
		case PLAIN:
		default:
			awtStyle = Font.PLAIN;
		}
		return awtStyle;
	}
	
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
		BufferedPImageResource imgRes = imgMap.get(imgPath);
		if (imgRes == null) {
			BufferedImage bufImg = null;
			try {
				bufImg = ImageIO.read(new File(imgPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			imgRes = new BufferedPImageResource(bufImg);
			imgMap.put(imgPath, imgRes);
		}
		return imgRes;
	}
	
	public PImageResource createImageResource(int width, int height,
			PImageMeta metaInfo) throws IllegalArgumentException 
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		BufferedPImageResource res = new BufferedPImageResource(img);
		return res;
	}
	
	public PCursor createCustomCursor(PImageResource image, int offsetX,
			int offsetY) throws IllegalArgumentException {
		return null;
	}
	
	public PBounds getBounds() {
		return bounds;
	}
	
	public void reRender(PComponent component) {
		if (panel != null) {
			if (SMART_RE_RENDER) {
				reRenderSet.add(component);
			} else {
				reRenderSet.add(this);
			}
//			System.out.println("reRender="+component);
			panel.repaint();
		}
	}
	
	private void render(Graphics2D g) {
//		System.out.println("RENDER ALL");
//		System.out.println();
		
		renderer.setGraphics(g);
		PBounds bnds = getBounds();
		int rootFx = bnds.getWidth();
		int rootFy = bnds.getHeight();
		
		Deque<StackInfo> stack = new ArrayDeque<>();
		/*
		 * If the root is to be rendered we will re-render everything.
		 */
		PRootOverlay overlay = getOverlay();
		if (reRenderSet.containsRoot()) {
			stack.addLast(new StackInfo(getBody(), true, 0, 0, rootFx, rootFy));
		} else {
			// these are filled by PCompUtil.fillClippedBounds(...)
			// This is used to cut down on the number of objects created
			MutablePBounds tmpBnds = new MutablePBounds();
			
			for (PComponent child : reRenderSet) {
				// We check to see whether the component is still part of this GUI tree
				if (child.getRoot() == this) {
					PBounds clipBnds = PCompUtil.fillClippedBounds(tmpBnds, child);
//					System.out.println("child="+child+", clipBnds="+clipBnds);
					// If the clipped bounds are null the component is completely 
					// concealed and does not need to be rendered
					if (clipBnds == null) {
//						System.out.println("JCompPRoot.render()::ComponentClipped");
						continue;
					}
					int clipX = clipBnds.getX();
					int clipY = clipBnds.getY();
					int clipFx = clipBnds.getFinalX();
					int clipFy = clipBnds.getFinalY();
					// We do addFirst here for consistency with the while-loop
					stack.addFirst(new StackInfo(child, true, clipX, clipY, clipFx, clipFy));
				}
			}
		}
//		System.out.println("STACK1="+stack);
		// The overlay must be rendered last whenever the rest of the GUI is rendered
		if (overlay != null) {
			for (PComponent overlayComp : overlay.getChildren()) {
				stack.addFirst(new StackInfo(overlayComp, true, 0, 0, rootFx, rootFy));
			}
		}
//		System.out.println("STACK2="+stack);
		while (!stack.isEmpty()) {
			StackInfo info = stack.pollLast();
//			StackInfo info = stack.pop();
			PComponent comp = info.child;
			PBounds compBounds = comp.getBounds();
			int clipX = Math.max(compBounds.getX(), info.clipX);
			int clipY = Math.max(compBounds.getY(), info.clipY);
			int clipFx = Math.min(compBounds.getFinalX(), info.clipFx);
			int clipFy = Math.min(compBounds.getFinalY(), info.clipFy);
			int clipW = clipFx - clipX;
			int clipH = clipFy - clipY;
//			System.out.println("comp="+comp+", clipX="+clipX+", clipY="+clipY+", clipW="+clipW+", clipH="+clipH);
			if (clipW < 0 || clipH < 0) {
//				System.out.println("ignore="+comp);
				continue;
			}
			/*
			 * We might not need to render this component.
			 * Only render this component if this component fired a reRenderEvent 
			 * or if an ancestor of this component was rendered.
			 */
			boolean render = info.needRender || reRenderSet.contains(comp);
			if (render) {
				renderer.setClipBounds(clipX, clipY, clipW, clipH);
//				System.out.println("clip="+clipX+", "+clipY+", "+clipW+", "+clipH);
				
				renderer.setRenderMode(renderer.getRenderModeFill());
				renderer.setColor1(1, 1, 1, 1);
				PDesign design = comp.getDesign();
				design.render(renderer, comp);
//				System.out.println("render="+comp);
			}
			
			PReadOnlyLayout layout = comp.getLayout();
			if (layout != null) {
				for (PComponent child : layout.getChildren()) {
					/*
					 * We need to addFirst to make sure children are rendered after 
					 * their parents and before any siblings of the parent will be rendered.
					 * Do NOT change to addLast!
					 */
//					stack.addFirst(new StackInfo(child, render, clipX, clipY, clipFx, clipFy));
					stack.addLast(new StackInfo(child, render, clipX, clipY, clipFx, clipFy));
				}
			}
		}
		reRenderSet.clear();
//		System.out.println("###############################");
//		System.out.println();
	}
	
	protected static class StackInfo {
		public final PComponent child;
		public final boolean needRender;
		public final int clipX;
		public final int clipY;
		public final int clipFx;
		public final int clipFy;
		
		public StackInfo(PComponent child, boolean needRender, 
				int clipX, int clipY, int clipFx, int clipFy) {
			this.child = child;
			this.needRender = needRender;
			this.clipX = clipX;
			this.clipY = clipY;
			this.clipFx = clipFx;
			this.clipFy = clipFy;
		}
		
		public String toString() {
			return child.toString();
		}
	}
	
}