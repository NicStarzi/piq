package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.swing.JCompPRoot;

public abstract class AbstractSwingPTest {
	
	protected static final double MILLISECOND_FACTOR = 1000 * 1000;
	
	protected final JCompPRoot root;
	protected JFrame frame;
	protected int timerDelay = 12;
	private double prevTime;
	
	public AbstractSwingPTest() {
		root = new JCompPRoot();
	}
	
	public AbstractSwingPTest(int w, int h) {
		this();
		buildSwing(w, h);
	}
	
	protected final void buildSwing(int w, int h) {
		prevTime = System.nanoTime() / MILLISECOND_FACTOR;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new JFrame();
					frame.setSize(w, h);
					frame.setLocationRelativeTo(null);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setContentPane(root.getJPanel());
					
					Timer updateTimer = new Timer(timerDelay, AbstractSwingPTest.this::onTimerTick);
					updateTimer.setCoalesce(true);
					updateTimer.setRepeats(true);
					updateTimer.start();
					frame.addWindowListener(new WindowAdapter() {
						public void windowClosed(WindowEvent e) {
							updateTimer.stop();
						}
					});
					
					try {
						buildGUI();
						frame.setVisible(true);
					} catch (Exception e) {
						updateTimer.stop();
						frame.dispose();
						
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final void onTimerTick(ActionEvent e) {
		double nanoTime = System.nanoTime() / MILLISECOND_FACTOR;
		double delta = nanoTime - prevTime;
		prevTime = nanoTime;
		root.update(delta);
	}
	
	protected abstract void buildGUI();
	
}