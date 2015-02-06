package edu.udo.piq.implementation.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.PScrollPanel;
import edu.udo.piq.components.PSplitPanel;
import edu.udo.piq.components.PTextArea;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.layouts.PBorderLayout;

public class SwingPTest_ScrollPanel {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_ScrollPanel window = new SwingPTest_ScrollPanel();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_ScrollPanel() {
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
		
		PSplitPanel split = new PSplitPanel();
		root.getLayout().addChild(split, PBorderLayout.Constraint.CENTER);
		
		PPicture pic = new PPicture();
		pic.getModel().setImagePath("Tex3.png");
		split.setSecondComponent(pic);
		
		PScrollPanel scrl = new PScrollPanel();
		split.setFirstComponent(scrl);
		
		PTextArea txtAr = new PTextArea();
		txtAr.setModel(new DefaultPTextModel("This\nis\na\ntest.\nWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na"));
		scrl.setView(txtAr);
	}
	
}