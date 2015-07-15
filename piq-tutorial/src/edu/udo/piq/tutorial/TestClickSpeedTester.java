package edu.udo.piq.tutorial;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PPanel;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.swing.JCompPRoot;

public class TestClickSpeedTester {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestClickSpeedTester window = new TestClickSpeedTester();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public TestClickSpeedTester() {
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		root = new JCompPRoot();
		frame.setContentPane(root.getPanel());
		
		Timer updateTimer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root.update();
			}
		});
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
		updateTimer.start();
		
		PPanel body = new PPanel();
		body.setLayout(new PCentricLayout(body));
		root.setBody(body);
		
		ClickSpeedTester tester = new ClickSpeedTester();
		body.addChild(tester, null);
	}
	
}