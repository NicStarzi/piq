package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PTabPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.swing.JCompPRoot;

public class SwingPTest_TabPanel {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_TabPanel window = new SwingPTest_TabPanel();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_TabPanel() {
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
		
		PTabPanel bodyPnl = new PTabPanel();
		root.setBody(bodyPnl);
		
		PPanel tab1 = new PPanel();
		tab1.setLayout(new PFreeLayout(tab1));
		PButton btnTab1 = new PButton();
		btnTab1.setContent(new PLabel("Button 1"));
		btnTab1.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				System.out.println("Button Tab 1");
			}
		});
		tab1.addChild(btnTab1, new FreeConstraint(13, 47));
		
		PPanel tab2 = new PPanel();
		tab2.setLayout(new PFreeLayout(tab2));
		PButton btnTab2 = new PButton();
		btnTab2.setContent(new PLabel("Button B"));
		btnTab2.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				System.out.println("Button Tab 2");
			}
		});
		tab2.addChild(btnTab2, new FreeConstraint(68, 172));
		
		PLabel tabLbl1 = new PLabel("Tab 1");
		bodyPnl.addTab(tabLbl1, tab1);
		PLabel tabLbl2 = new PLabel("Tab 2");
		bodyPnl.addTab(tabLbl2, tab2);
	}
	
}