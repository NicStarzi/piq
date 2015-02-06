package edu.udo.piq.implementation.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PList;
import edu.udo.piq.components.PListSelection;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.components.PSplitPanel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PWrapLayout;

public class SwingPTest_DragAndDrop {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_DragAndDrop window = new SwingPTest_DragAndDrop();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_DragAndDrop() {
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
		root.getBody().getLayout().addChild(split, PBorderLayout.Constraint.CENTER);
		
		final PList listLeft = new PList();
		listLeft.setID("LEFT");
//		listLeft.setModel(new DefaultPListModel() {
//			public boolean canAddElement(int index, Object element) {
//				return super.canAddElement(index, element) && element instanceof Integer;
//			}
//		});
		listLeft.getModel().addElement(0, Integer.valueOf(1));
		listLeft.getModel().addElement(1, Integer.valueOf(2));
		listLeft.getModel().addElement(2, Integer.valueOf(3));
		split.setFirstComponent(listLeft);
		
		PList listRight = new PList();
		listRight.setID("RIGHT");
		listRight.getModel().addElement(0, "Anna");
		listRight.getModel().addElement(1, "Brigitte");
		listRight.getModel().addElement(2, "Chloe");
		split.setSecondComponent(listRight);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PWrapLayout(btnPnl, PListLayout.ListAlignment.CENTERED_HORIZONTAL));
		root.getBody().getLayout().addChild(btnPnl, PBorderLayout.Constraint.BOTTOM);
		
		PButton btnAdd = new PButton();
		btnAdd.setContent(new PLabel(new DefaultPTextModel("Add")));
		btnPnl.getLayout().addChild(btnAdd, null);
		
		PButton btnRmv = new PButton();
		btnRmv.setContent(new PLabel(new DefaultPTextModel("Remove")));
		btnPnl.getLayout().addChild(btnRmv, null);
		
		btnAdd.addObs(new PButtonObs() {
			int num = 0;
			
			public void onClick(PButton button) {
				PListSelection selected = listLeft.getSelection();
				Integer maxIndex;
				if (selected.getSelection().isEmpty()) {
					maxIndex = Integer.valueOf(0);
				} else {
					maxIndex = Collections.max(selected.getSelection());
				}
				listLeft.getModel().addElement(maxIndex.intValue(), "Text "+(num++));
			}
		});
		btnRmv.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				PListSelection selected = listLeft.getSelection();
				for (Integer index : selected.getSelection()) {
					listLeft.getModel().removeElement(index.intValue());
				}
			}
		});
	}
	
}