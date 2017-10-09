package edu.udo.piq.swing;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

public class JFramePRoot {
	
	protected static final double MILLISECOND_FACTOR = 1000 * 1000;
	
	protected final JFrame frame;
	protected final JCompPRoot root;
	protected Timer updateTimer;
	protected int timerDelay = 12;
	protected double prevTime;
	
	public JFramePRoot() {
		this(new JFrame());
	}
	
	public JFramePRoot(JFrame jFrame) {
		root = new JCompPRoot();
		frame = jFrame;
		
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setContentPane(root.getJPanel());
		
		updateTimer = new Timer(timerDelay, JFramePRoot.this::onTimerTick);
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				onOpen();
			}
			@Override
			public void windowClosed(WindowEvent e) {
				onClose();
				
			}
		});
	}
	
	public void setSizeUndecorated(int width, int height) {
		frame.setSize(width, height);
		frame.pack();
		Insets borders = frame.getInsets();
		int offsetW = borders.left + borders.right;
		int offsetH = borders.top + borders.bottom;
		frame.setSize(width + offsetW, height + offsetH);
	}
	
	public JFrame getJFrame() {
		return frame;
	}
	
	public Timer getUpdateTimer() {
		return updateTimer;
	}
	
	public JCompPRoot getPRoot() {
		return root;
	}
	
	public void dispose() {
		frame.dispose();
	}
	
	public void setVisible(boolean value) {
		getJFrame().setVisible(value);
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
		root.update(delta);
	}
	
}