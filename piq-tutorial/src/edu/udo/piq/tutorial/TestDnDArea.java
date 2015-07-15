package edu.udo.piq.tutorial;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PSplitPanel;
import edu.udo.piq.swing.JCompPRoot;

public class TestDnDArea {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestDnDArea window = new TestDnDArea();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public TestDnDArea() {
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
		
		PSplitPanel splitPnl = new PSplitPanel();
		root.setBody(splitPnl);
		
		DnDArea left = new DnDArea();
		splitPnl.setFirstComponent(left);
		
		addLbl(left, 1, 17, 19);
		addLbl(left, 2, 184, 64);
		addBtn(left, 3, 9, 217);
		addLbl(left, 4, 261, 195);
		
		DnDArea right = new DnDArea();
		splitPnl.setSecondComponent(right);
		
		addBtn(right, 1, 64, 23);
		addBtn(right, 2, 117, 136);
		addLbl(right, 3, 215, 75);
		
		ColoredBox box = new ColoredBox();
		left.addChild(box, 76, 311);
	}
	
	private void addLbl(DnDArea area, int num, int x, int y) {
		PLabel lbl = new PLabel("Label #"+num);
		area.addChild(lbl, x, y);
	}
	
	private void addBtn(DnDArea area, int num, int x, int y) {
		PButton btn = new PButton();
		btn.setContent(new PLabel("Button #"+num));
		btn.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				System.out.println("clicked #"+num);
			}
		});
		area.addChild(btn, x, y);
	}
	
}