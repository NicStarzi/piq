package edu.udo.piq.swing;

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
import edu.udo.piq.PDialog;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PMouse;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRoot;
import edu.udo.piq.tools.AbstractPDialog;

public class SwingPDialog extends AbstractPDialog implements PDialog {
	
	protected final PRoot root;
	protected final JDialog jDialog;
	protected final JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;
		@Override
		public void paintComponent(Graphics g) {
//			g.setColor(Color.BLACK);
//			g.fillRect(0, 0, getWidth(), getHeight());
			render((Graphics2D) g);
		}
	};
	protected final SwingPRenderer renderer = new SwingPRenderer();
	protected final SwingPMouse mouse = new SwingPMouse(this, panel);
	protected final SwingPKeyboard keyboard = new SwingPKeyboard(panel);
	protected final JCompPBounds bounds = new JCompPBounds(panel);
	
	public SwingPDialog(PRoot root, JDialog jDialog) {
		super();
		this.root = root;
		this.jDialog = jDialog;
		jDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!isDisposed()) {
					dispose();
				}
			}
			@Override
			public void windowClosed(WindowEvent e) {
				if (!isDisposed()) {
					dispose();
				}
			}
		});
		panel.setFocusable(true);
		panel.requestFocus();
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				fireSizeChanged();
			}
			@Override
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
		super.update(1);
//		tickAllTimers();
//		updateRootLayout();
//		updateComponents();
	}
	
	@Override
	public void show() throws IllegalStateException {
		if (getJDialog().isVisible()) {
			throw new IllegalStateException("PDialog is already shown.");
		}
		throwExceptionIfDisposed();
		getJDialog().setVisible(true);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		getJDialog().dispose();
	}
	
	@Override
	public PBounds getBounds() {
		return bounds;
	}
	
	@Override
	public void reRender(PComponent component) {
		if (panel != null) {
			panel.repaint();
		}
	}
	
	@Override
	public double getDeltaTime() {
		return getSuperRoot().getDeltaTime();
	}
	
	@Override
	protected PRoot getSuperRoot() {
		return root;
	}
	
	@Override
	public PMouse getMouse() {
		return mouse;
	}
	
	@Override
	public PKeyboard getKeyboard() {
		return keyboard;
	}
	
	@Override
	public void onMouseOverCursorChanged(PComponent component) {
		mouse.mouseOverCursorChanged(component);
	}
	
	private void render(Graphics2D g) {
		renderer.setAwtGraphics(g);
		
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
			
			comp.render(renderer);
			
			PReadOnlyLayout layout = comp.getLayout();
			if (layout != null) {
				for (PComponent child : layout.getChildren()) {
					stack.addFirst(new StackInfo(child, clipX, clipY, clipFx, clipFy));
				}
			}
		}
	}
}