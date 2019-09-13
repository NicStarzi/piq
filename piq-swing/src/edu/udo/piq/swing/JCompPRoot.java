package edu.udo.piq.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;

public class JCompPRoot extends SwingPRoot implements PRoot {
	
	protected static final double MILLISECOND_FACTOR = 1000 * 1000;
	
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
	protected Timer updateTimer;
	protected int timerDelay = 12;
	protected double prevTime;
	
	public JCompPRoot() {
		super(new InnerJPanel());
		panel = (InnerJPanel) super.getAwtComponent();
		panel.setRoot(this);
		
		updateTimer = new Timer(timerDelay, JCompPRoot.this::onTimerTick);
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
	}
	
	public JPanel getJPanel() {
		return panel;
	}
	
	public Timer getUpdateTimer() {
		return updateTimer;
	}
	
	@Override
	public void scheduleReRender(PComponent component) {
//		System.out.println("JCompPRoot.scheduleReRender()="+component);
		super.scheduleReRender(component);
		panel.repaint();
	}
	
	protected void onOpen() {
		prevTime = System.nanoTime() / MILLISECOND_FACTOR;
		updateTimer.start();
	}
	
	protected void onClose() {
		updateTimer.stop();
	}
	
	protected void onTimerTick(ActionEvent e) {
		double nanoTime = System.nanoTime() / MILLISECOND_FACTOR;
		double delta = nanoTime - prevTime;
		prevTime = nanoTime;
		update(delta);
	}
	
	protected static class InnerJPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private final PComponentObs rootObs = new PComponentObs() {
			@Override
			public void onPreferredSizeChanged(PComponent component) {
				revalidateParent();
			}
		};
		private final Dimension prefSizeDim = new Dimension();
		private JCompPRoot root;
		
		{
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					requestFocusInWindow();
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					requestFocusInWindow();
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					requestFocusInWindow();
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					requestFocusInWindow();
				}
			});
		}
		
		void setRoot(JCompPRoot root) {
			if (this.root != null) {
				this.root.removeObs(rootObs);
			}
			this.root = root;
			if (this.root != null) {
				this.root.addObs(rootObs);
			}
		}
		
		void revalidateParent() {
			PSize prefSize = root.getPreferredSize();
			prefSizeDim.setSize(prefSize.getWidth(), prefSize.getHeight());
			setPreferredSize(prefSizeDim);
			revalidate();
		}
		
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
			root.onOpen();
		}
		
		@Override
		public void removeNotify() {
			super.removeNotify();
			if (root.wnd != null) {
				root.wnd.removeWindowListener(root.wndListener);
				root.wnd = null;
			}
			root.onClose();
		}
		
		@Override
		public void paintComponent(Graphics g) {
//			super.paintComponent(g);
			root.render((Graphics2D) g);
		}
	}
	
}