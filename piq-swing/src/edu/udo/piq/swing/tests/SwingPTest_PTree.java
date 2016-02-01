package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.collections.PTree;
import edu.udo.piq.components.collections.PTreeIndex;
import edu.udo.piq.components.containers.PSplitPanel;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPTreeModel;
import edu.udo.piq.swing.JCompPRoot;

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
	
	public SwingPTest_PTree() {
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
		
		PSplitPanel splitPnl = new PSplitPanel();
		root.setBody(splitPnl);
		
//		PPanel body = new PPanel();
//		body.setLayout(new PBorderLayout(body));
//		root.setBody(body);
		
		PTree tree = new PTree();
		splitPnl.setFirstComponent(tree);
//		body.addChild(tree, PBorderLayout.Constraint.CENTER);
		
		DefaultPTreeModel model = new DefaultPTreeModel();
		model.add(new PTreeIndex(), "Root");
		model.add(new PTreeIndex(0), "Directory A");
		model.add(new PTreeIndex(0, 0), "Directory a1");
		model.add(new PTreeIndex(0, 1), "Directory a2");
		model.add(new PTreeIndex(1), "Directory B");
		model.add(new PTreeIndex(1, 0), "Directory b1");
		model.add(new PTreeIndex(2), "Directory C");
		model.add(new PTreeIndex(2, 0), "Directory c1");
		model.add(new PTreeIndex(2, 1), "Directory c2");
		model.add(new PTreeIndex(2, 2), "Directory c3");
		model.add(new PTreeIndex(3), "Directory D");
		tree.setModel(model);
		
		PList list = new PList(new DefaultPListModel("T1", "T2", "T3"));
		splitPnl.setSecondComponent(list);
		
//		DefaultPTableModel tm = new DefaultPTableModel(3, 1);
//		tm.addObs(new PTableModelObs() {
//			public void rowRemoved(PTableModel model, int rowIndex) {
//				System.out.println("rowRemoved="+rowIndex);
//			}
//			public void rowAdded(PTableModel model, int rowIndex) {
//				System.out.println("rowAdded="+rowIndex);
//			}
//			public void columnRemoved(PTableModel model, int columnIndex) {
//				System.out.println("columnRemoved="+columnIndex);
//			}
//			public void columnAdded(PTableModel model, int columnIndex) {
//				System.out.println("columnAdded="+columnIndex);
//			}
//			public void cellChanged(PTableModel model, Object cell, int columnIndex, int rowIndex) {
//				System.out.println("cellChanged="+columnIndex+", "+rowIndex+" => "+cell);
//			}
//		});
//		System.out.println(tm.getDebugInfo());
//		tm.add("a1", 0, 0);
//		tm.add("b1", 1, 0);
//		tm.add("c1", 2, 0);
//		System.out.println(tm.getDebugInfo());
//		tm.addRow(1);
//		System.out.println(tm.getDebugInfo());
//		tm.add("a2", 0, 1);
//		tm.add("b2", 1, 1);
//		tm.add("c2", 2, 1);
//		System.out.println(tm.getDebugInfo());
//		tm.addRow(2);
//		System.out.println(tm.getDebugInfo());
//		tm.add("a3", 0, 2);
//		tm.add("b3", 1, 2);
//		tm.add("c3", 2, 2);
//		System.out.println(tm.getDebugInfo());
//		tm.removeRow(1);
//		System.out.println(tm.getDebugInfo());
//		tm.addColumn(3);
//		System.out.println(tm.getDebugInfo());
//		tm.add("d1", 3, 0);
//		tm.add("d3", 3, 1);
//		System.out.println(tm.getDebugInfo());
//		tm.removeColumn(1);
//		System.out.println(tm.getDebugInfo());
//		tm.removeColumn(2);
//		System.out.println(tm.getDebugInfo());
//		tm.removeColumn(0);
//		System.out.println(tm.getDebugInfo());
//		
//		PPanel bodyPnl = new PPanel();
//		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
//		root.setBody(bodyPnl);
//		
//		PSplitPanel splitPnl = new PSplitPanel();
//		bodyPnl.addChild(splitPnl, PBorderLayout.Constraint.CENTER);
//		
//		PTree tree = new PTree();
//		tree.getModel().setRoot("Root");
//		tree.getModel().addChild("Root", "A", 0);
//		tree.getModel().addChild("Root", "B", 1);
//		tree.getModel().addChild("Root", "C", 2);
//		splitPnl.setFirstComponent(tree);
//		
//		PList list = new PList();
//		list.getModel().addElement(0, "Anna");
//		list.getModel().addElement(1, "Ben");
//		list.getModel().addElement(2, "Chloe");
//		splitPnl.setSecondComponent(list);
//		
//		PPanel btnPnl = new PPanel();
//		btnPnl.setLayout(new PWrapLayout(btnPnl, ListAlignment.FROM_LEFT));
//		bodyPnl.addChild(btnPnl, PBorderLayout.Constraint.BOTTOM);
//		
//		PButton btnInsert = new PButton();
//		btnInsert.setContent(new PLabel(new DefaultPTextModel("Insert")));
//		btnPnl.addChild(btnInsert, null);
//		
//		PButton btnAdd = new PButton();
//		btnAdd.setContent(new PLabel(new DefaultPTextModel("Add")));
//		btnPnl.addChild(btnAdd, null);
//		
//		PButton btnRemove = new PButton();
//		btnRemove.setContent(new PLabel(new DefaultPTextModel("Remove")));
//		btnPnl.addChild(btnRemove, null);
//		
//		btnInsert.addObs(new PButtonObs() {
//			public void onClick(PButton button) {
//				if (tree.getModel().getRoot() == null) {
//					counter = 0;
//					tree.getModel().setRoot(Integer.valueOf(counter++));
//				} else {
//					PTreeSelection sel = tree.getSelection();
//					List<PTreePosition> posses = sel.getSelectedPositions();
//					for (PTreePosition pos : posses) {
//						Object node = Integer.valueOf(counter++);
//						if (pos.canBeAddedAsChild(node)) {
//							pos.addAsChild(node);
//						}
//					}
//				}
//			}
//		});
//		btnAdd.addObs(new PButtonObs() {
//			public void onClick(PButton button) {
//				if (tree.getModel().getRoot() == null) {
//					counter = 0;
//					tree.getModel().setRoot(Integer.valueOf(counter++));
//				} else {
//					PTreeSelection sel = tree.getSelection();
//					List<PTreePosition> posses = sel.getSelectedPositions();
//					for (PTreePosition pos : posses) {
//						Object node = Integer.valueOf(counter++);
//						if (pos.canBeAdded(node)) {
//							pos.add(node);
//						}
//					}
//				}
//			}
//		});
//		btnRemove.addObs(new PButtonObs() {
//			public void onClick(PButton button) {
//				PTreeSelection sel = tree.getSelection();
//				List<PTreePosition> posses = sel.getSelectedPositions();
//				for (PTreePosition pos : posses) {
//					if (pos.canBeRemoved()) {
//						pos.remove();
//					}
//				}
//			}
//		});
	}
	
}