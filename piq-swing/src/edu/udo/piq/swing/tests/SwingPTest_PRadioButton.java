package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.components.PRadioButton;
import edu.udo.piq.components.PRadioButtonGroup;
import edu.udo.piq.components.PRadioButtonTuple;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.swing.JCompPRoot;

public class SwingPTest_PRadioButton {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_PRadioButton window = new SwingPTest_PRadioButton();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_PRadioButton() {
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		root = new JCompPRoot();
		frame.setContentPane(root.getPanel());
		
		final Timer updateTimer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root.update();
			}
		});
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
		updateTimer.start();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				updateTimer.stop();
			}
		});
		
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PRadioButton radBtn1 = new PRadioButton();
		bodyPnl.getLayout().addChild(radBtn1, new PFreeLayout.FreeConstraint(36, 53));
		
		PRadioButtonTuple radBtn2 = new PRadioButtonTuple();
		radBtn2.setSecondComponent(new PLabel(new DefaultPTextModel("Click me!")));
		bodyPnl.getLayout().addChild(radBtn2, new PFreeLayout.FreeConstraint(89, 12));
		
		new PRadioButtonGroup(radBtn1, radBtn2.getRadioButton());
		
		PRadioButton radBtn3 = new PRadioButton();
		bodyPnl.getLayout().addChild(radBtn3, new PFreeLayout.FreeConstraint(241, 41));
		
		PRadioButtonTuple radBtn4 = new PRadioButtonTuple();
		radBtn4.setSecondComponent(new PLabel(new DefaultPTextModel("Here!")));
		bodyPnl.getLayout().addChild(radBtn4, new PFreeLayout.FreeConstraint(173, 109));
		
		PRadioButtonTuple radBtn5 = new PRadioButtonTuple();
		radBtn5.setSecondComponent(new PLabel(new DefaultPTextModel("Come on!")));
		bodyPnl.getLayout().addChild(radBtn5, new PFreeLayout.FreeConstraint(201, 78));
		
		new PRadioButtonGroup(radBtn3, radBtn4.getRadioButton(), radBtn5.getRadioButton());
	}
	
}