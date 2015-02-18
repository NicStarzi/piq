package edu.udo.piq.implementation.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.components.PTree;
import edu.udo.piq.components.PTreePosition;
import edu.udo.piq.components.PTreeSelection;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;

public class SwingPTest_PTree {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_PTree window = new SwingPTest_PTree();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	private int counter = 0;
	
	public SwingPTest_PTree() {
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
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PTree tree = new PTree();
		tree.getModel().setRoot("Root");
		tree.getModel().addChild("Root", "A", 0);
		tree.getModel().addChild("Root", "B", 1);
		tree.getModel().addChild("Root", "C", 2);
		bodyPnl.addChild(tree, PBorderLayout.Constraint.CENTER);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PWrapLayout(btnPnl, ListAlignment.FROM_LEFT));
		bodyPnl.addChild(btnPnl, PBorderLayout.Constraint.BOTTOM);
		
		PButton btnInsert = new PButton();
		btnInsert.setContent(new PLabel(new DefaultPTextModel("Insert")));
		btnPnl.addChild(btnInsert, null);
		
		PButton btnAdd = new PButton();
		btnAdd.setContent(new PLabel(new DefaultPTextModel("Add")));
		btnPnl.addChild(btnAdd, null);
		
		PButton btnRemove = new PButton();
		btnRemove.setContent(new PLabel(new DefaultPTextModel("Remove")));
		btnPnl.addChild(btnRemove, null);
		
		btnInsert.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				PTreeSelection sel = tree.getSelection();
				List<PTreePosition> posses = sel.getSelectedPositions();
				for (PTreePosition pos : posses) {
					pos.addAsChild(Integer.valueOf(counter++));
				}
			}
		});
		btnAdd.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				PTreeSelection sel = tree.getSelection();
				List<PTreePosition> posses = sel.getSelectedPositions();
				for (PTreePosition pos : posses) {
					pos.add(Integer.valueOf(counter++));
				}
			}
		});
		btnRemove.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				PTreeSelection sel = tree.getSelection();
				List<PTreePosition> posses = sel.getSelectedPositions();
				for (PTreePosition pos : posses) {
					pos.remove();
				}
			}
		});
	}
	
}