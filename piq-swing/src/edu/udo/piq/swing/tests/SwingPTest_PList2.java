package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.comps.selectcomps.DefaultPListModel;
import edu.udo.piq.comps.selectcomps.PList;
import edu.udo.piq.comps.selectcomps.PListIndex;
import edu.udo.piq.comps.selectcomps.PListSingleSelection;
import edu.udo.piq.comps.selectcomps.PModel;
import edu.udo.piq.comps.selectcomps.PModelIndex;
import edu.udo.piq.comps.selectcomps.PSelection;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.swing.JCompPRoot;

public class SwingPTest_PList2 {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_PList2 window = new SwingPTest_PList2();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_PList2() {
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
		
		final PList list = new PList(new DefaultPListModel(new Object[] {"Hallo", "Welt"}));
		list.setSelection(new PListSingleSelection());
		bodyPnl.addChild(list, PBorderLayout.Constraint.CENTER);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PWrapLayout(btnPnl, ListAlignment.CENTERED_HORIZONTAL));
		bodyPnl.addChild(btnPnl, PBorderLayout.Constraint.BOTTOM);
		
		PButton btnAdd = new PButton();
		btnAdd.setContent(new PLabel("Add"));
		btnPnl.addChild(btnAdd, null);
		
		PButton btnRemove = new PButton();
		btnRemove.setContent(new PLabel("Remove"));
		btnPnl.addChild(btnRemove, null);
		
		final List<String> names = new ArrayList<String>(Arrays.asList(new String[] {
				"A", "B", "C", "D", "E", "F", "G", "H", "I", 
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", 
				"S", "T", "U", "V", "W", "X", "Y", "Z",
		}));
		
		btnAdd.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				int index = (int) (Math.random() * names.size());
				list.getModel().add(new PListIndex(0), names.remove(index));
			}
		});
		btnRemove.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				PModel model = list.getModel();
				PSelection select = list.getSelection();
				List<PModelIndex> indices = select.getAllSelected();
				while (!indices.isEmpty()) {
					PModelIndex index = indices.get(0);
					if (model.canRemove(index)) {
						model.remove(index);
					}
				}
			}
		});
	}
	
}