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
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
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
			reRenderSet.add(component);
//			System.out.println("reRender="+component);
			panel.repaint();
		}
	}
	
	private void render(Graphics2D g) {
//		System.out.println("RENDER ALL");
//		System.out.println();
		
		renderer.setGraphics(g);
		
//		PComponent[] compsToRender = reRenderSet.toArray();
//		reRenderSet.clear();
//		Arrays.sort(compsToRender, componentReRenderComparator);
		
		class StackInfo {
			final PComponent child;
			final int clipX;
			final int clipY;
			final int clipFx;
			final int clipFy;
			
			public StackInfo(PComponent child, int clipX, int clipY, int clipFx, int clipFy) {
				this.child = child;
				this.clipX = clipX;
				this.clipY = clipY;
				this.clipFx = clipFx;
				this.clipFy = clipFy;
			}
		}
		
		Deque<StackInfo> stack = new LinkedList<>();
		Iterable<PComponent> compsToRender = reRenderSet.containsRoot() ? getLayout().getChildren() : reRenderSet;
//		for (PComponent child : getLayout().getChildren()) {
		for (PComponent child : compsToRender) {
			stack.add(new StackInfo(child, 0, 0, getBounds().getWidth(), getBounds().getHeight()));
		}
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent comp = info.child;
			if (comp.getRoot() == null) {
				continue;
			}
			PBounds compBounds = comp.getBounds();
			int clipX = Math.max(compBounds.getX(), info.clipX);
			int clipY = Math.max(compBounds.getY(), info.clipY);
			int clipFx = Math.min(compBounds.getFinalX(), info.clipFx);
			int clipFy = Math.min(compBounds.getFinalY(), info.clipFy);
			int clipW = clipFx - clipX;
			int clipH = clipFy - clipY;
			if (clipW < 0 || clipH < 0) {
				continue;
			}
			renderer.setClipBounds(clipX, clipY, clipW, clipH);
			
			PDesign design = comp.getDesign();
			design.render(renderer, comp);
//			System.out.println("render="+comp);
			
			PReadOnlyLayout layout = comp.getLayout();
			if (layout != null) {
				for (PComponent child : layout.getChildren()) {
					stack.addFirst(new StackInfo(child, clipX, clipY, clipFx, clipFy));
				}
			}
		}
		reRenderSet.clear();
//		System.out.println("###############################");
//		System.out.println();
	}
	
}