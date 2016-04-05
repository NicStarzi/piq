package edu.udo.piq.tutorial;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PClickable;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.swing.JCompPRoot;
import edu.udo.piq.tutorial.TrianglePLayout.Pos;

public class TestTrianglePLayout {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestTrianglePLayout window = new TestTrianglePLayout();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public TestTrianglePLayout() {
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		root = new JCompPRoot();
		frame.setContentPane(root.getJPanel());
		
		Timer updateTimer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root.update();
			}
		});
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
		updateTimer.start();
		
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new TrianglePLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PButton btnTop = new PButton();
		btnTop.setContent(new PLabel("Top"));
		btnTop.addObs(new PClickObs() {
			public void onClick(PClickable clickable) {
				System.out.println("You clicked top!");
			}
		});
		bodyPnl.addChild(btnTop, Pos.TOP);
		
		PButton btnLft = new PButton();
		btnLft.setContent(new PLabel("Left"));
		btnLft.addObs(new PClickObs() {
			public void onClick(PClickable clickable) {
				System.out.println("You clicked left!");
			}
		});
		bodyPnl.addChild(btnLft, Pos.BOTTOM_LEFT);
		
		PButton btnRgt = new PButton();
		btnRgt.setContent(new PLabel("Right"));
		btnRgt.addObs(new PClickObs() {
			public void onClick(PClickable clickable) {
				System.out.println("You clicked right!");
			}
		});
		bodyPnl.addChild(btnRgt, Pos.BOTTOM_RIGHT);
	}
	
}