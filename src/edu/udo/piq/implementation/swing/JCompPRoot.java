package edu.udo.piq.implementation.swing;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.Timer;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PDialog;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PLayout;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRoot;
import edu.udo.piq.tools.AbstractPRoot;

public class JCompPRoot extends AbstractPRoot implements PRoot {
	
	private final JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;
		public void addNotify() {
			super.addNotify();
			timerUpdate.start();
		}
		public void removeNotify() {
			super.removeNotify();
			timerUpdate.stop();
		}
		public void paintComponent(Graphics g) {
//			g.setColor(Color.BLACK);
//			g.fillRect(0, 0, getWidth(), getHeight());
			render((Graphics2D) g);
		}
	};
	private final List<SwingPDialog> openedDialogs = new ArrayList<>();
	private final Map<FontInfo, AwtPFontResource> fontMap = new HashMap<>();
	private final Map<String, BufferedPImageResource> imgMap = new HashMap<>();
	private final SwingPRenderer renderer = new SwingPRenderer();
	private final SwingPMouse mouse = new SwingPMouse(panel);
	private final SwingPKeyboard keyboard = new SwingPKeyboard(panel);
	private final PDnDManager dndManager = new PDnDManager(this);
	private final JPanelPBounds bounds = new JPanelPBounds(panel);
	private final Timer timerUpdate = new Timer(1, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			timersNeedUpdate = true;
		}
	});
//	private Set<PComponent> reRenderSet = new HashSet<>();
	private volatile boolean timersNeedUpdate = false;
	
	public JCompPRoot() {
		super();
		panel.setFocusable(true);
		panel.requestFocus();
		panel.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				fireSizeChanged();
				reRender(JCompPRoot.this);
			}
			public void componentResized(ComponentEvent e) {
				fireSizeChanged();
				reRender(JCompPRoot.this);
			}
		});
		super.mouse = mouse;
		super.keyboard = keyboard;
		super.dndManager = dndManager;
		
		timerUpdate.setRepeats(true);
		timerUpdate.setCoalesce(true);
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
				dlg.update();
				mouse.update();
				return;
			}
		}
		if (timersNeedUpdate) {
			tickAllTimers();
			timersNeedUpdate = false;
		}
		updateRootLayout();
		updateComponents();
		
		mouse.update();
	}
	
	public void reRender(PComponent component) {
		if (panel != null) {
//			PComponent current = component;
//			while (!PCompUtil.isOpaque(current)) {
//				current = current.getParent();
//			}
//			if (current == this) {
//				reRenderSet.addAll(getLayout().getChildren());
//			} else {
//				reRenderSet.add(current);
//			}
			panel.repaint();
		}
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
	
	private void render(Graphics2D g) {
//		Set<PComponent> toBeRendered = reRenderSet;
//		reRenderSet = new HashSet<>();
		
		renderer.setGraphics(g);
		
		class StackInfo {
			PComponent child;
			int clipX;
			int clipY;
			int clipFx;
			int clipFy;
			
			public StackInfo(PComponent child, int clipX, int clipY, int clipFx, int clipFy) {
				this.child = child;
				this.clipX = clipX;
				this.clipY = clipY;
				this.clipFx = clipFx;
				this.clipFy = clipFy;
			}
		}
		
		Deque<StackInfo> stack = new LinkedList<>();
		for (PComponent child : getLayout().getChildren()) {
//		for (PComponent child : toBeRendered) {
			stack.add(new StackInfo(child, 0, 0, getBounds().getWidth(), getBounds().getHeight()));
		}
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent comp = info.child;
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
			
			PLayout layout = comp.getLayout();
			if (layout != null) {
				for (PComponent child : layout.getChildren()) {
					stack.addFirst(new StackInfo(child, clipX, clipY, clipFx, clipFy));
				}
			}
		}
	}
	
}