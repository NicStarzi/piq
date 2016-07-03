package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.collections.DefaultPTableModel;
import edu.udo.piq.components.collections.PColumnIndex;
import edu.udo.piq.components.collections.PRowIndex;
import edu.udo.piq.components.collections.PTable;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PBorderLayout.Constraint;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PGridLayout.AlignH;
import edu.udo.piq.layouts.PGridLayout.AlignV;
import edu.udo.piq.layouts.PGridLayout.GridConstraint;
import edu.udo.piq.swing.JCompPRoot;

public class SwingPTest_PTable {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_PTable window = new SwingPTest_PTable();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_PTable() {
		frame = new JFrame();
		frame.setSize(320, 240);
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
		
		DefaultPTableModel tm = new DefaultPTableModel(3, 3);
		tm.set(1, 0, "Aaaa");
		tm.set(2, 1, "Bbbb");
		tm.set(0, 2, "Cccc");
		
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PTable table = new PTable();
		table.setOutputEncoder((element) -> Objects.toString(element, ""));
		table.setModel(tm);
		bodyPnl.addChild(table, Constraint.CENTER);
		
		PPanel ctrlPnl = new PPanel();
		bodyPnl.addChild(ctrlPnl, Constraint.BOTTOM);
		PGridLayout layout = new PGridLayout(ctrlPnl, 3, 2);
//		layout.setColumnGrowth(Growth.MAXIMIZE);
//		layout.setRowGrowth(Growth.MAXIMIZE);
		ctrlPnl.setLayout(layout);
		
		PSpinner inputCol = new PSpinner(new PSpinnerModelInt(0, 0, 100));
		ctrlPnl.addChild(inputCol, new GridConstraint(0, 0, AlignH.FILL, AlignV.CENTER));
		
		PSpinner inputRow = new PSpinner(new PSpinnerModelInt(0, 0, 100));
		ctrlPnl.addChild(inputRow, new GridConstraint(0, 1, AlignH.FILL, AlignV.CENTER));
		
		PButton btnAddColumn = new PButton(new PLabel("Add Column"));
		ctrlPnl.addChild(btnAddColumn, new GridConstraint(1, 0, AlignH.FILL, AlignV.FILL));
		
		PButton btnRemoveColumn = new PButton(new PLabel("Remove Column"));
		ctrlPnl.addChild(btnRemoveColumn, new GridConstraint(1, 1, AlignH.FILL, AlignV.FILL));
		
		PButton btnAddRow = new PButton(new PLabel("Add Row"));
		ctrlPnl.addChild(btnAddRow, new GridConstraint(2, 0, AlignH.FILL, AlignV.FILL));
		
		PButton btnRemoveRow = new PButton(new PLabel("Remove Row"));
		ctrlPnl.addChild(btnRemoveRow, new GridConstraint(2, 1, AlignH.FILL, AlignV.FILL));
		
		btnAddColumn.addObs((PClickObs) (c) -> {
			int col = (int) inputCol.getModel().getValue();
			tm.add(new PColumnIndex(col), null);
		});
		btnRemoveColumn.addObs((PClickObs) (c) -> {
			int col = (int) inputCol.getModel().getValue();
			tm.remove(new PColumnIndex(col));
		});
		btnAddRow.addObs((PClickObs) (c) -> {
			int row = (int) inputRow.getModel().getValue();
			tm.add(new PRowIndex(row), null);
		});
		btnRemoveRow.addObs((PClickObs) (c) -> {
			int row = (int) inputRow.getModel().getValue();
			tm.remove(new PRowIndex(row));
		});
	}
	
}