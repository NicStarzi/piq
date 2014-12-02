package edu.udo.piq.implementation.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PDialog;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.tools.AbstractPBounds;
import edu.udo.piq.tools.AbstractPDesignSheet;
import edu.udo.piq.util.PCompUtil;

public class SwingPRoot implements PRoot {
	
	private final JPanel panel;
	private final List<SwingPDialog> openedDialogs;
	private final Map<FontInfo, AwtPFontResource> fontMap;
	private final Map<String, BufferedPImageResource> imgMap;
	private final SwingPRenderer renderer;
	private final SwingPMouse mouse;
	private final SwingPKeyboard keyboard;
	private final JPanelPBounds bounds;
	private final PBorderLayout layout;
	private final PComponentObs childObs = new PComponentObs() {
		public void wasRemoved(PComponent component) {
		}
		public void wasAdded(PComponent component) {
		}
		public void preferredSizeChanged(PComponent component) {
			needReLayout = true;
		}
	};
	private boolean needReLayout;
	private PDesignSheet designSheet = new AbstractPDesignSheet();
	
	public SwingPRoot() {
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				render((Graphics2D) g);
			}
		};
		panel.setFocusable(true);
		panel.requestFocus();
		panel.addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {
				needReLayout = true;
			}
			public void componentResized(ComponentEvent e) {
				needReLayout = true;
			}
			public void componentMoved(ComponentEvent e) {
			}
			public void componentHidden(ComponentEvent e) {
			}
		});
		openedDialogs = new ArrayList<>();
		fontMap = new HashMap<>();
		imgMap = new HashMap<>();
		renderer = new SwingPRenderer();
		mouse = new SwingPMouse(panel);
		keyboard = new SwingPKeyboard(panel);
		bounds = new JPanelPBounds(panel);
		layout = new PBorderLayout(this);
		layout.addObs(new PLayoutObs() {
			public void layoutInvalidated(PLayout layout) {
				needReLayout = true;
			}
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				child.addObs(childObs);
				needReLayout = true;
			}
			public void childRemoved(PLayout layout, PComponent child, Object constraint) {
				child.removeObs(childObs);
				needReLayout = true;
			}
			public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
			}
		});
		needReLayout = true;
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public PBounds getBounds() {
		return bounds;
	}
	
	public void setDesignSheet(PDesignSheet sheet) {
		designSheet = sheet;
		needReLayout = true;
		panel.repaint();
	}
	
	public PDesignSheet getDesignSheet() {
		return designSheet;
	}
	
	public void update() {
		while (!openedDialogs.isEmpty()) {
			PDialog dlg = openedDialogs.get(openedDialogs.size() - 1);
			if (dlg.isDisposed()) {
				openedDialogs.remove(dlg);
			} else {
				dlg.update();
				mouse.update();
				keyboard.update();
				return;
			}
		}
		if (needReLayout) {
			getLayout().layOut();
			needReLayout = false;
		}
		
		Deque<PComponent> stack = new LinkedList<>();
		stack.addAll(getLayout().getChildren());
		while (!stack.isEmpty()) {
			PComponent comp = stack.pop();
			comp.update();
			
			PLayout layout = comp.getLayout();
			if (layout != null) {
				for (PComponent child : layout.getChildren()) {
					stack.addFirst(child);
				}
			}
		}
		mouse.update();
		keyboard.update();
	}
	
	private void render(Graphics2D g) {
//		System.out.println(PGuiUtil.guiTreeToString(this));
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
			stack.add(new StackInfo(child, 0, 0, getBounds().getWidth(), getBounds().getHeight()));
		}
//		System.out.println("####################################");
//		System.out.println("       START RENDERING CYCLE        ");
//		System.out.println("####################################");
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent comp = info.child;
			PBounds compBounds = PCompUtil.getBoundsOf(comp);
//			System.out.println("parentX="+info.clipX+", parentY="+info.clipY+", parentFx="+info.clipFx+", parentFy="+info.clipFy);
			int clipX = Math.max(compBounds.getX(), info.clipX);
			int clipY = Math.max(compBounds.getY(), info.clipY);
			int clipFx = Math.min(compBounds.getFinalX(), info.clipFx);
			int clipFy = Math.min(compBounds.getFinalY(), info.clipFy);
			int clipW = clipFx - clipX;
			int clipH = clipFy - clipY;
//			System.out.println("ownX="+clipX+", ownY="+clipY+", ownFx="+clipFx+", ownFy="+clipFy);
//			System.out.println();
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
	
	public void reRender(PComponent component) {
		panel.repaint();
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
	
	public PFontResource fetchFontResource(String fontName, int pointSize, Style style) {
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
		FontInfo info = new FontInfo(fontName, pointSize, awtStyle);
		AwtPFontResource fontRes = fontMap.get(info);
		if (fontRes == null) {
			fontRes = new AwtPFontResource(new Font(fontName, awtStyle, pointSize));
			fontMap.put(info, fontRes);
		}
		return fontRes;
	}
	
	public PImageResource fetchImageResource(String imgPath) {
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
	
	public PMouse getMouse() {
		return mouse;
	}
	
	public PKeyboard getKeyboard() {
		return keyboard;
	}
	
	/*
	 * PComponent methods
	 */
	
	public void addObs(PComponentObs obs) 
			throws NullPointerException {
	}
	
	public void removeObs(PComponentObs obs)
			throws NullPointerException {
	}
	
	public PRoot getRoot() {
		return this;
	}
	
	public void setParent(PComponent parent) 
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	public PComponent getParent() {
		return null;
	}
	
	public PLayout getLayout() {
		return layout;
	}
	
	public void defaultRender(PRenderer renderer)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	public boolean isDefaultOpaque() {
		return true;
	}
	
	public PSize getDefaultPreferredSize() {
		return getBounds();
	}
	
	public void setID(String value) {
	}
	
	public String getID() {
		return null;
	}
	
	public void setDesign(PDesign design) {
		throw new UnsupportedOperationException();
	}
	
	public PDesign getDesign() {
		throw new UnsupportedOperationException();
	}
	
	public String toString() {
		return getClass().getSimpleName()+" ["+getBounds()+"]";
	}
	
	public static class JPanelPBounds extends AbstractPBounds implements PBounds {
		
		protected final JPanel panel;
		
		public JPanelPBounds(JPanel panel) {
			this.panel = panel;
		}
		
		public int getX() {
			return 0;//panel.getX();
		}
		
		public int getY() {
			return 0;//panel.getY();
		}
		
		public int getWidth() {
			return panel.getWidth();
		}
		
		public int getHeight() {
			return panel.getHeight();
		}
	}
	
	protected static class FontInfo {
		protected final String name;
		protected final int size;
		protected final int style;
		
		protected FontInfo(String fontName, int pointSize, int awtStyle) {
			if (fontName == null) {
				throw new NullPointerException();
			}
			name = fontName;
			size = pointSize;
			style = awtStyle;
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + style;
			result = prime * result + name.hashCode();
			result = prime * result + size;
			return result;
		}
		
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || !(obj instanceof FontInfo)) {
				return false;
			}
			FontInfo other = (FontInfo) obj;
			return name.equals(other.name) 
					&& size == other.size 
					&& style == other.style;
		}
	}
	
}