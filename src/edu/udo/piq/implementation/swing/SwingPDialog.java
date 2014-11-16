package edu.udo.piq.implementation.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PDialog;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.implementation.swing.SwingPRoot.JPanelPBounds;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PLayout;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;

public class SwingPDialog implements PDialog {
	
	private final SwingPRoot root;
	private final JDialog jDialog;
	private final JPanel panel;
	private final SwingPRenderer renderer;
	private final JPanelPBounds bounds;
	private final PComponentObs childObs = new PComponentObs() {
		public void wasRemoved(PComponent component) {
		}
		public void wasAdded(PComponent component) {
		}
		public void preferredSizeChanged(PComponent component) {
			needReLayout = true;
		}
	};
	private final PLayoutObs layoutObs = new PLayoutObs() {
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
	};
	private PLayout layout;
	private boolean needReLayout;
	private boolean disposed;
	
	public SwingPDialog(SwingPRoot root, JDialog jDialog) {
		this.root = root;
		this.jDialog = jDialog;
		jDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (!isDisposed()) {
					dispose();
				}
			}
			public void windowClosed(WindowEvent e) {
				if (!isDisposed()) {
					dispose();
				}
			}
		});
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
		renderer = new SwingPRenderer();
		bounds = new JPanelPBounds(panel);
		setLayout(new PBorderLayout(this));
		needReLayout = true;
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public JDialog getJDialog() {
		return jDialog;
	}
	
	public void show() throws IllegalStateException {
		if (getJDialog().isVisible()) {
			throw new IllegalStateException("PDialog is already shown.");
		}
		if (disposed) {
			throw new IllegalStateException("PDialog is disposed.");
		}
		getJDialog().setVisible(true);
	}
	
	public void dispose() throws IllegalStateException {
		if (isDisposed()) {
			throw new IllegalStateException("PDialog is disposed.");
		}
		getJDialog().dispose();
		disposed = true;
	}
	
	public boolean isDisposed() {
		return disposed;
	}
	
	public void setLayout(PLayout layout) {
		if (getLayout() != null) {
			layout.removeObs(layoutObs);
		}
		this.layout = layout;
		if (getLayout() != null) {
			layout.addObs(layoutObs);
		}
		needReLayout = true;
		reRender(this);
	}
	
	public PLayout getLayout() {
		return layout;
	}
	
	public PBounds getBounds() {
		return bounds;
	}
	
	public PSize getDefaultPreferredSize() {
		return getBounds();
	}
	
	public void update() {
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
	}
	
	private void render(Graphics2D g) {
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
		while (!stack.isEmpty()) {
			StackInfo info = stack.pop();
			PComponent comp = info.child;
			PBounds compBounds = PCompUtil.getBoundsOf(comp);
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
	
	public void reRender(PComponent component) {
		panel.repaint();
	}
	
	public void addObs(PComponentObs obs) throws NullPointerException {
	}
	
	public void removeObs(PComponentObs obs) throws NullPointerException {
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
	
	public void defaultRender(PRenderer renderer)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
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
	
	public PDesignSheet getDesignSheet() {
		return root.getDesignSheet();
	}
	
	public PDialog createDialog() {
		return root.createDialog();
	}
	
	public PFontResource fetchFontResource(String fontName, int pointSize, Style style) 
			throws NullPointerException {
		return root.fetchFontResource(fontName, pointSize, style);
	}
	
	public PImageResource fetchImageResource(String imgPath)
			throws NullPointerException {
		return root.fetchImageResource(imgPath);
	}
	
	public PMouse getMouse() {
		return root.getMouse();
	}
	
	public PKeyboard getKeyboard() {
		return root.getKeyboard();
	}
}