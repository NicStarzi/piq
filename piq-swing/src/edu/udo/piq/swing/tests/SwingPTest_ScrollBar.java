package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PPanel;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.scroll.PScrollPanel;
import edu.udo.piq.swing.JCompPRoot;

public class SwingPTest_ScrollBar {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_ScrollBar window = new SwingPTest_ScrollBar();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_ScrollBar() {
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
		
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PScrollPanel scrlPnl = new PScrollPanel();
		bodyPnl.addChild(scrlPnl, new PFreeLayout.FreeConstraint(31, 46, 256, 256));
	}
	
}