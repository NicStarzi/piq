package edu.udo.piq.implementation.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PPanel;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PBoxLayout;
import edu.udo.piq.layouts.PBoxLayout.Box;

public class SwingPTest_BoxLayout {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_BoxLayout window = new SwingPTest_BoxLayout();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_BoxLayout() {
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
		
		PPanel pnl = new PPanel();
		PBoxLayout boxLayout = new PBoxLayout(pnl);
		pnl.setLayout(boxLayout);
		root.getBody().getLayout().addChild(pnl, PBorderLayout.Constraint.CENTER);
		
		Box b1 = boxLayout.getRootBox();
		b1.splitVertical(0.4);
		Box b2 = b1.getLeft();
		Box b3 = b1.getRight();
		
		b2.splitHorizontal(0.3);
		Box c1 = b2.getTop();
		Box c2 = b2.getBottom();
		
		b3.splitHorizontal(0.6);
		Box c3 = b3.getTop();
		Box c4 = b3.getBottom();
		
		PPicture pic1 = new PPicture();
		pic1.getModel().setImagePath("Tex.png");
		pic1.setStretchToSize(true);
		pnl.getLayout().addChild(pic1, c1);
		
		PPicture pic2 = new PPicture();
		pic2.getModel().setImagePath("Tex.png");
		pic2.setStretchToSize(true);
		pnl.getLayout().addChild(pic2, c2);
		
		PPicture pic3 = new PPicture();
		pic3.getModel().setImagePath("Tex.png");
		pic3.setStretchToSize(true);
		pnl.getLayout().addChild(pic3, c3);
		
		PPicture pic4 = new PPicture();
		pic4.getModel().setImagePath("Tex.png");
		pic4.setStretchToSize(true);
		pnl.getLayout().addChild(pic4, c4);
	}
	
}