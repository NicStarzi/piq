package edu.udo.piq.implementation.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDialog;
import edu.udo.piq.PLayout;
import edu.udo.piq.PRoot;
import edu.udo.piq.tools.AbstractPDialog;
import edu.udo.piq.util.PCompUtil;

public class SwingPDialog extends AbstractPDialog implements PDialog {
	
	private final JCompPRoot root;
	private final JDialog jDialog;
	private final JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;
		public void paintComponent(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			render((Graphics2D) g);
		}
	};
	private final SwingPRenderer renderer = new SwingPRenderer();
	private final JPanelPBounds bounds = new JPanelPBounds(panel);
	
	public SwingPDialog(JCompPRoot root, JDialog jDialog) {
		super();
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
		panel.setFocusable(true);
		panel.requestFocus();
		panel.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				fireSizeChanged();
			}
			public void componentResized(ComponentEvent e) {
				fireSizeChanged();
			}
		});
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public JDialog getJDialog() {
		return jDialog;
	}
	
	public void update() {
		updateTimers();
		updateLayout();
		updateComponents();
	}
	
	public void show() throws IllegalStateException {
		if (getJDialog().isVisible()) {
			throw new IllegalStateException("PDialog is already shown.");
		}
		throwExceptionIfDisposed();
		getJDialog().setVisible(true);
	}
	
	public void dispose() {
		super.dispose();
		getJDialog().dispose();
	}
	
	public PBounds getBounds() {
		return bounds;
	}
	
	public void reRender(PComponent component) {
		panel.repaint();
	}
	
	protected PRoot getSuperRoot() {
		return root;
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
	
}