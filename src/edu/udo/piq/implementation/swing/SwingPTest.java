package edu.udo.piq.implementation.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.PDialog;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.PProgressBar;
import edu.udo.piq.components.PSplitPanel;
import edu.udo.piq.components.PTable;
import edu.udo.piq.components.PTableSelection.SelectionMode;
import edu.udo.piq.components.PTextArea;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.defaults.DefaultPTableModel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PSplitLayout.Orientation;
import edu.udo.piq.layouts.PWrapLayout;

public class SwingPTest {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest window = new SwingPTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final SwingPRoot root;
	
	public SwingPTest() {
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		root = new SwingPRoot();
		frame.setContentPane(root.getPanel());
		
		Timer updateTimer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root.update();
			}
		});
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
		updateTimer.start();
		
		PSplitPanel splitV = new PSplitPanel();
		splitV.getLayout().setOrientation(Orientation.VERTICAL);
		root.getLayout().addChild(splitV, PBorderLayout.Constraint.CENTER);
		
		PSplitPanel splitH = new PSplitPanel();
		splitH.getLayout().setOrientation(Orientation.HORIZONTAL);
		splitV.setFirstComponent(splitH);
		
		PPicture left = new PPicture();
		left.getModel().setImagePath("Tex.png");
		splitH.setFirstComponent(left);
		
		PPicture right = new PPicture();
		right.getModel().setImagePath("Tex2.png");
		splitH.setSecondComponent(right);
		
//		DefaultPListModel listModel = new DefaultPListModel();
//		
//		PList list = new PList();
//		list.setModel(listModel);
//		splitV.setSecondComponent(list);
//		
//		String[] items = new String[] {
//			"A",
//			"B",
//			"C",
//			"D",
//			"E",
//			"F",
//			"G",
//			"H",
//		};
//		for (int i = 0; i < items.length; i++) {
//			listModel.addElement(items[i]);
//		}
		
//		final DefaultPTableModel tableModel = new DefaultPTableModel(new Object[][] {
//				{"John", "Smith", "001"},
//				{"Marry", "Sue", "003"},
//				{"Jane", "Doe", "005"},
//				{"Joe", "Schmo", "007"},
//		});
//		
//		PTable table = new PTable();
//		table.setModel(tableModel);
//		table.getSelection().setSelectionMode(SelectionMode.SINGLE_CELL);
//		splitV.setSecondComponent(table);
		
		PTextArea txtAr = new PTextArea(new DefaultPTextModel(
			"This is \n a simple test \nto see whether the PTextArea class \nworks as intended."
		));
		splitV.setSecondComponent(txtAr);
		
//		PPanel barPnl = new PPanel();
//		barPnl.setLayout(new PCentricLayout(barPnl));
//		root.getLayout().addChild(barPnl, PBorderLayout.Constraint.CENTER);
//		
//		final PProgressBar bar = new PProgressBar();
//		bar.getModel().setMaximum(20);
//		barPnl.addChild(bar, null);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PListLayout(btnPnl));
		root.getLayout().addChild(btnPnl, PBorderLayout.Constraint.BOTTOM);
		
		PButton change = new PButton();
		change.setContent(new PLabel(new DefaultPTextModel("Change")));
		change.addObs(new PButtonObs() {
			public void onClick(PButton button) {
//				bar.getModel().setValue(bar.getModel().getValue() + 1);
//				try {
//					String content = tableModel.getCell(2, 2).toString();
//					int asInt = Integer.parseInt(content);
//					tableModel.setCell(Integer.valueOf(asInt + 1), 2, 2);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
		});
		btnPnl.addChild(change, null);
		
//		PScrollPanel scrollPanel = new PScrollPanel();
//		root.getLayout().addChild(scrollPanel, PBorderLayout.Constraint.CENTER);
//		
//		PPanel lblPnl = new PPanel();
//		lblPnl.setLayout(new PBorderLayout(lblPnl));
//		
//		PLabel longLbl = new PLabel();
//		longLbl.getModel().setText("This is a really fucking long text. I wonder what it is good for...");
//		lblPnl.getLayout().addChild(longLbl, PBorderLayout.Constraint.CENTER);
//		
//		scrollPanel.setView(lblPnl);
	}
	
	public static class MyLittleDialog {
		
		public MyLittleDialog(PDialog dlg) {
			PBorderLayout layout = new PBorderLayout(dlg);
			dlg.setLayout(layout);
			
			PPanel pnlBody = new PPanel();
			pnlBody.setLayout(new PBorderLayout(pnlBody));
			layout.addChild(pnlBody, PBorderLayout.Constraint.CENTER);
			
			PLabel lblBodyContent = new PLabel();
			lblBodyContent.getModel().setText("This is a dialog body!");
			pnlBody.addChild(lblBodyContent, PBorderLayout.Constraint.CENTER);
			
			PPanel pnlButtons = new PPanel();
			pnlButtons.setLayout(new PListLayout(pnlButtons, ListAlignment.CENTERED_HORIZONTAL));
			layout.addChild(pnlButtons, PBorderLayout.Constraint.BOTTOM);
			
			PLabel lblOkayBtn = new PLabel();
			lblOkayBtn.getModel().setText("OK");
			
			PButton btnOkay = new PButton();
			btnOkay.setContent(lblOkayBtn);
			btnOkay.addObs(new PButtonObs() {
				public void onClick(PButton button) {
					System.out.println("OK!");
				}
			});
			pnlButtons.addChild(btnOkay, null);
			
			dlg.show();
		}
		
	}
	
}