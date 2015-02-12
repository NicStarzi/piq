package edu.udo.piq.implementation.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PFreeLayout;

public class SwingPTest_FreeLayout {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_FreeLayout window = new SwingPTest_FreeLayout();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_FreeLayout() {
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
		root.getBody().getLayout().addChild(bodyPnl, PBorderLayout.Constraint.CENTER);
		
		Person p = new Person("Max", "Mustermann");
		
		PButton btn = new PButton();
		btn.setContent(new PLabel(new DefaultPTextModel(p) {
			public String getText() {
				Person p = (Person) getValue();
				return p.firstName + ": " + p.lastName;
			}
		}));
		bodyPnl.getLayout().addChild(btn, new PFreeLayout.FreeConstraint(36, 53));
		
		p.firstName = "Frederick";
	}
	
	public static class Person {
		String firstName;
		String lastName;
		
		Person(String a, String b) {
			firstName = a;
			lastName = b;
		}
		
	}
	
}