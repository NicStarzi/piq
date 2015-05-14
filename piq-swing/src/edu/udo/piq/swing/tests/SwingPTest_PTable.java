package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.comps.PPanel;
import edu.udo.piq.comps.PTable;
import edu.udo.piq.comps.defaults.DefaultPTableModel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PBorderLayout.Constraint;
import edu.udo.piq.swing.JCompPRoot;

public class SwingPTest_PTable {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_PTable window = new SwingPTest_PTable();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_PTable() {
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
		
		DefaultPTableModel tm = new DefaultPTableModel(3, 1);
		tm.add("Hello World", 0, 0);
		tm.add(Integer.valueOf(42), 1, 0);
		tm.add(Boolean.TRUE, 2, 0);
		
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PTable table = new PTable();
		table.setModel(tm);
		bodyPnl.addChild(table, Constraint.CENTER);
	}
	
}