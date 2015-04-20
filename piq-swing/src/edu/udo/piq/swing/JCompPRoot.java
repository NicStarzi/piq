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
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PDialog;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRoot;
import edu.udo.piq.tools.AbstractPRoot;
import edu.udo.piq.tools.ReRenderSet;

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
//			timerUpdate.start();
		}
		public void removeNotify() {
			super.removeNotify();
			if (wnd != null) {
				wnd.removeWindowListener(wndListener);
			}
//			timerUpdate.stop();
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
//	private final Timer timerUpdate = new Timer(1, new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//			timersNeedUpdate = true;
//		}
//	});
//	private Set<PComponent> reRenderSet = new HashSet<>();
//	private volatile boolean timersNeedUpdate = false;
//	private final Set<PComponent> reRenderSet = new HashSet<>();
	private final ReRenderSet reRenderSet = new ReRenderSet(this);
//	private final Comparator<PComponent> componentReRenderComparator = new Comparator<PComponent>() {
//		public int compare(PComponent o1, PComponent o2) {
//			int depth1 = o1.getDepth();
//			int depth2 = o2.getDepth();
//			return depth1 - depth2;
//		}
//	};
//	private boolean reRenderAll = false;
	
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
		
//		timerUpdate.setRepeats(true);
//		timerUpdate.setCoalesce(true);
	}
	
	public JPanel getPanel() {
		return panel;
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
		super.update();
//		if (timersNeedUpdate) {
//			tickAllTimers();
//			timersNeedUpdate = false;
//		}
		
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
	
	public PFontResource fetchFontResource(String fontName, int pointSize, Style style) 
			throws NullPointerException, IllegalArgumentException 
	{
		FontInfo info = new FontInfo(fontName, pointSize, style);
		AwtPFontResource fontRes = fontMap.get(info);
		if (fontRes == null) {
			fontRes = new AwtPFontResource(new Font(fontName, getAwtStyle(info), pointSize));
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
	
	public PImageResource fetchImageResource(String imgPath) 
			throws NullPointerException 
	{
		if (imgPath == null) {
			throw new NullPointerException("imgPath="+imgPath);
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
	
	public void setDesignSheet(PDesignSheet designSheet) {
		super.setDesignSheet(designSheet);
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
		int w = bnds.getWidth();
		int h = bnds.getHeight();
		
		Deque<StackInfo> stack = new ArrayDeque<>();
//		Iterable<PComponent> compsToRender = reRenderSet.containsRoot() ? getLayout().getChildren() : reRenderSet;
//		for (PComponent child : compsToRender) {
		for (PComponent child : getLayout().getChildren()) {
			// We check to see whether the component is still part of this GUI tree
			if (child.getRoot() == this) {
				// We do addFirst here for consistency with the while-loop
				stack.addFirst(new StackInfo(child, true, 0, 0, w, h));
//				stack.addLast(new StackInfo(child, true, 0, 0, w, h));
			}
		}
//		if (!reRenderSet.containsRoot() && getLayout().getOverlay() != null) {
//			System.out.println("add overlay to render set");
//			stack.addLast(new StackInfo((PComponent) getLayout().getOverlay(), true, 0, 0, w, h));
//		}
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
			if (clipW < 0 || clipH < 0) {
//				System.out.println("ignore="+comp);
				continue;
			}
			/*
			 * We might not need to render this component.
			 * Only render component if component fired a reRenderEvent or if an 
			 * ancestor of component was rendered.
			 */
			boolean render = info.needRender || reRenderSet.contains(comp);
			if (render) {
				renderer.setClipBounds(clipX, clipY, clipW, clipH);
//				System.out.println("clip="+clipX+", "+clipY+", "+clipW+", "+clipH);
				
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
	}
	
}