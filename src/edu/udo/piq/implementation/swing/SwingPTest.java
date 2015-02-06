package edu.udo.piq.implementation.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.PDialog;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PCheckBoxObs;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PList;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.PProgressBar;
import edu.udo.piq.components.PProgressBarModel;
import edu.udo.piq.components.PProgressBarModelObs;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.PSliderModel;
import edu.udo.piq.components.PSliderModelObs;
import edu.udo.piq.components.PSplitPanel;
import edu.udo.piq.components.PTextArea;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PSplitLayout.Orientation;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.tools.AbstractPTextModel;

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
	private final JCompPRoot root;
	
	public SwingPTest() {
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
		
		PSplitPanel splitV = new PSplitPanel();
		splitV.getLayout().setOrientation(Orientation.VERTICAL);
		root.getLayout().addChild(splitV, PBorderLayout.Constraint.CENTER);
		
		PSplitPanel splitH = new PSplitPanel();
		splitH.getLayout().setOrientation(Orientation.HORIZONTAL);
		splitV.setFirstComponent(splitH);
		
		PPicture pic = new PPicture();
		pic.getModel().setImagePath("Tex.png");
		pic.setStretchToSize(true);
		splitH.setFirstComponent(pic);
		
		PList list = new PList();
		String[] items = new String[] {
			"A",
			"B",
			"C",
			"D",
			"E",
			"F",
			"G",
			"H",
		};
		for (int i = 0; i < items.length; i++) {
			list.getModel().addElement(i, items[i]);
		}
		splitH.setSecondComponent(list);
		
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
		txtAr.setEditable(false);
		splitV.setSecondComponent(txtAr);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PWrapLayout(btnPnl, ListAlignment.FROM_LEFT));
//		btnPnl.setLayout(new PListLayout(btnPnl, ListAlignment.FROM_LEFT));
		root.getLayout().addChild(btnPnl, PBorderLayout.Constraint.BOTTOM);
		
		final PButton btnChange = new PButton();
		btnChange.setContent(new PSlider());
//		btnChange.setContent(new PLabel(new DefaultPTextModel("Change")));
		btnPnl.addChild(btnChange, null);
		
		final PProgressBar prgBar = new PProgressBar();
		prgBar.getModel().setMaximum(17);
		btnPnl.addChild(prgBar, null);
		
		final PCheckBox chkBx = new PCheckBox();
		btnPnl.addChild(chkBx, null);
		
		final PLabel lblChkBx = new PLabel();
		btnPnl.addChild(lblChkBx, null);
		
		final PSlider sld = new PSlider();
		sld.getModel().setMinValue(13);
		sld.getModel().setMaxValue(77);
		btnPnl.addChild(sld, null);
		
		final PLabel lblSld = new PLabel();
		btnPnl.addChild(lblSld, null);
		
		btnChange.addObs(new PButtonObs() {
			boolean increment = true;
			public void onClick(PButton button) {
				int val = prgBar.getModel().getValue();
				val = increment ? val + 1 : val - 1;
				prgBar.getModel().setValue(val);
				if (prgBar.getModel().getValue() == prgBar.getModel().getMaxValue() || prgBar.getModel().getValue() == 0) {
					increment = !increment;
				}
			}
		});
		chkBx.addObs(new PCheckBoxObs() {
			public void clicked(PCheckBox checkBox) {
				lblChkBx.getModel().setValue(null);
				lblSld.getModel().setValue(null);
			}
		});
		sld.getModel().addObs(new PSliderModelObs() {
			public void valueChanged(PSliderModel model) {
				lblSld.getModel().setValue(null);
			}
			public void rangeChanged(PSliderModel model) {
				lblSld.getModel().setValue(null);
			}
		});
		lblChkBx.setModel(new AbstractPTextModel() {
			public void setValue(Object text) {
				fireTextChangeEvent();
			}
			public Object getValue() {
				return null;
			}
			public String getText() {
				if (chkBx.isChecked()) {
					return "Relative";
				}
				return "Absolute";
			}
		});
		lblSld.setModel(new AbstractPTextModel() {
			public void setValue(Object text) {
				fireTextChangeEvent();
			}
			public Object getValue() {
				return null;
			}
			public String getText() {
				if (chkBx.isChecked()) {
					double percent = sld.getModel().getValuePercent();
					double val = ((int) (10000 * percent)) / 100.0;
					return val+"%";
				}
				return Integer.toString(sld.getModel().getValue());
			}
		});
		prgBar.getModel().addObs(new PProgressBarModelObs() {
			public void valueChanged(PProgressBarModel model) {
				if (model.getValue() == model.getMaxValue()) {
					PDialog dlg = prgBar.getRoot().createDialog();
					new MyLittleDialog(dlg);
				}
			}
		});
	}
	
	public static class MyLittleDialog {
		
		public MyLittleDialog(final PDialog dlg) {
			dlg.setLayout(new PBorderLayout(dlg));
			
			PPanel pnlBody = new PPanel();
			pnlBody.setLayout(new PBorderLayout(pnlBody));
			dlg.getLayout().addChild(pnlBody, PBorderLayout.Constraint.CENTER);
			
			PLabel lblBodyContent = new PLabel();
			lblBodyContent.getModel().setValue("This is a dialog body!");
			pnlBody.addChild(lblBodyContent, PBorderLayout.Constraint.CENTER);
			
			PPanel pnlButtons = new PPanel();
			pnlButtons.setLayout(new PListLayout(pnlButtons, ListAlignment.CENTERED_HORIZONTAL));
			dlg.getLayout().addChild(pnlButtons, PBorderLayout.Constraint.BOTTOM);
			
			PLabel lblOkayBtn = new PLabel();
			lblOkayBtn.getModel().setValue("OK");
			
			PButton btnOkay = new PButton();
			btnOkay.setContent(lblOkayBtn);
			btnOkay.addObs(new PButtonObs() {
				public void onClick(PButton button) {
					System.out.println("OK!");
					dlg.dispose();
				}
			});
			pnlButtons.addChild(btnOkay, null);
			
			dlg.show();
		}
		
	}
	
}