package edu.udo.piq.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;

public class JCompPRoot extends SwingPRoot implements PRoot {
	
	protected Window wnd;
	protected final WindowListener wndListener = new WindowAdapter() {
		@Override
		public void windowIconified(WindowEvent e) {
			scheduleReRender(JCompPRoot.this);
		}
		@Override
		public void windowDeiconified(WindowEvent e) {
			scheduleReRender(JCompPRoot.this);
		}
	};
	protected final InnerJPanel panel;
	
	public JCompPRoot() {
		super(new InnerJPanel());
		panel = (InnerJPanel) super.getAwtComponent();
		panel.root = this;
	}
	
	public JPanel getJPanel() {
		return panel;
	}
	
	@Override
	public void scheduleReRender(PComponent component) {
		super.scheduleReRender(component);
		panel.repaint();
	}
	
	protected static class InnerJPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		JCompPRoot root;
		@Override
		public void addNotify() {
			super.addNotify();
			Component awtRoot = SwingUtilities.getRoot(this);
			if (awtRoot != null && awtRoot instanceof Window) {
				if (root.wnd != null) {
					root.wnd.removeWindowListener(root.wndListener);
					root.wnd = null;
				}
				root.wnd = (Window) awtRoot;
				root.wnd.addWindowListener(root.wndListener);
			}
		}
		@Override
		public void removeNotify() {
			super.removeNotify();
			if (root.wnd != null) {
				root.wnd.removeWindowListener(root.wndListener);
				root.wnd = null;
			}
		}
		@Override
		public void paintComponent(Graphics g) {
			root.render((Graphics2D) g);
		}
	}
	
}