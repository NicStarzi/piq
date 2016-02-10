package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PDockLayout;
import edu.udo.piq.swing.JCompPRoot;
import edu.udo.piq.tools.ImmutablePInsets;

public class SwingPTest_DockLayout {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_DockLayout window = new SwingPTest_DockLayout();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_DockLayout() {
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
		
		PPanel pnl = new PPanel();
		PDockLayout dockLayout = new PDockLayout(pnl);
		dockLayout.setInsets(new ImmutablePInsets(12));
		dockLayout.setGap(6);
		pnl.setLayout(dockLayout);
		root.setBody(pnl);
		
		PButton btn1 = new PButton();
		btn1.setContent(new PLabel(new DefaultPTextModel("Button")));
		pnl.addChild(btn1, new PDockLayout.Constraint(0, 0));
		
		PButton btn2 = new PButton();
		btn2.setContent(new PLabel(new DefaultPTextModel("Button")));
		pnl.addChild(btn2, new PDockLayout.Constraint(1, 0));
		
		PLabel lbl1 = new PLabel(new DefaultPTextModel("This is a very very very long label"));
		pnl.addChild(lbl1, new PDockLayout.Constraint(0, 1));
		
		PPicture pic1 = new PPicture();
		pic1.getModel().setImageID("Tex.png");
		pnl.addChild(pic1, new PDockLayout.Constraint(643, 0));
	}
	
}