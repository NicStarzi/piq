package edu.udo.piq.swing;

import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class JFramePRoot {
	
	protected final JFrame frame;
	protected final JCompPRoot root;
	
	public JFramePRoot() {
		this(new JFrame());
	}
	
	public JFramePRoot(JFrame jFrame) {
		frame = jFrame;
		
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		root = new JCompPRoot();
		frame.setContentPane(root.getJPanel());
//		frame.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowOpened(WindowEvent e) {
//				onOpen();
//			}
//			@Override
//			public void windowClosed(WindowEvent e) {
//				onClose();
//			}
//		});
	}
	
	public void setDecorated(boolean value) {
		frame.setUndecorated(!value);
	}
	
	public void setPositionOnScreen(int x, int y) {
		frame.setLocation(x, y);
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
	
	public JCompPRoot getPRoot() {
		return root;
	}
	
	public void dispose() {
		frame.dispose();
	}
	
	public void setVisible(boolean value) {
		getJFrame().setVisible(value);
	}
	
//	protected void onOpen() {
//		prevTime = System.nanoTime() / MILLISECOND_FACTOR;
//		updateTimer.start();
//	}
//	
//	protected void onClose() {
//		updateTimer.stop();
//	}
//	
//	protected void onTimerTick(ActionEvent e) {
//		double nanoTime = System.nanoTime() / MILLISECOND_FACTOR;
//		double delta = nanoTime - prevTime;
//		prevTime = nanoTime;
//		root.update(delta);
//	}
	
}