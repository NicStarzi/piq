package edu.udo.piq.tutorial;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PTabPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.swing.JCompPRoot;

public class TestMovingPLayout {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestMovingPLayout window = new TestMovingPLayout();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public TestMovingPLayout() {
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
//		JPanel cp = new JPanel();
//		frame.setContentPane(cp);
//		cp.setLayout(new BorderLayout(0, 0));
//		
//		JTree tree = new JTree();
//		JScrollPane scrollPane = new JScrollPane(tree);
//		cp.add(scrollPane, BorderLayout.WEST);
//		
//		JPanel panel = new JPanel();
//		cp.add(panel, BorderLayout.NORTH);
//		
//		JButton btnNewButton = new JButton("New button");
//		panel.add(btnNewButton);
//		
//		JTextField textField = new JTextField();
//		panel.add(textField);
//		textField.setColumns(10);
//		
//		JSpinner spinner = new JSpinner();
//		panel.add(spinner);
//		
//		JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
//		panel.add(chckbxNewCheckBox);
//		
//		JButton btnNewButton_1 = new JButton("New button");
//		panel.add(btnNewButton_1);
//		
//		JList list = new JList();
//		list.setModel(new AbstractListModel() {
//			String[] values = new String[] {"Hallo", "Welt", "Wie", "Geht", "Es", "Dir"};
//			public int getSize() {
//				return values.length;
//			}
//			public Object getElementAt(int index) {
//				return values[index];
//			}
//		});
//		cp.add(list, BorderLayout.EAST);
		
		root = new JCompPRoot();
//		cp.add(root.getJPanel(), BorderLayout.CENTER);
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
		bodyPnl.setLayout(new MovingPLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PTabPanel tab = new PTabPanel();
		bodyPnl.addChild(tab, null);
		
		PPanel tab1 = new PPanel();
		tab1.setLayout(new PCentricLayout(tab1));
		tab.addTab(new PLabel("Hallo"), tab1);
		
		PPanel tab2 = new PPanel();
		tab2.setLayout(new PCentricLayout(tab2));
		tab.addTab(new PLabel("Welt"), tab2);
		
		PButton btn = new PButton();
		btn.setContent(new PLabel("Click me!"));
		btn.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				System.out.println("You clicked it!");
			}
		});
		tab1.addChild(btn, null);
		
		PSlider s = new PSlider();
		s.getModel().setMaxValue(100);
		tab2.addChild(s, null);
	}
	
}