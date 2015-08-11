package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.swing.JCompPRoot;

public abstract class AbstractSwingPTest {
	
	protected final JCompPRoot root;
	
	public AbstractSwingPTest() {
		this(640, 480);
	}
	
	public AbstractSwingPTest(int w, int h) {
		root = new JCompPRoot();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					buildSwing(w, h);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void buildSwing(int w, int h) {
		JFrame frame = new JFrame();
		frame.setSize(w, h);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(root.getPanel());
		
		final Timer updateTimer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root.update();
			}
		});
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
		updateTimer.start();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				updateTimer.stop();
			}
		});
		
		buildGUI();
		
		frame.setVisible(true);
	}
	
	protected abstract void buildGUI();
	
}