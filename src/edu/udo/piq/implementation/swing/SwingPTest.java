package edu.udo.piq.implementation.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDialog;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.components.PBarChart;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PCheckBoxModel;
import edu.udo.piq.components.PCheckBoxModelObs;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PLineBorder;
import edu.udo.piq.components.PList;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.PScrollPanel;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.PSliderModel;
import edu.udo.piq.components.PSliderModelObs;
import edu.udo.piq.components.PTuple;
import edu.udo.piq.components.defaults.DefaultPBarChartModel;
import edu.udo.piq.components.defaults.DefaultPLabelModel;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPSliderModel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PMatrixLayout;
import edu.udo.piq.layouts.PListLayout.Orientation;
import edu.udo.piq.tools.PLabelDesign;

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
	private PPanel north;
	private PPanel east;
	private PPanel west;
	private PPanel south;
//	private PPanel center;
	private PLabelDesign testDesign;
	private int colorID = 0;
	private int colorTimer = 0;
	
	public SwingPTest() {
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		root = new SwingPRoot();
		frame.setContentPane(root.getPanel());
		
		final PColor[] colors = new PColor[] {PColor.RED, PColor.BLUE, PColor.GREEN, PColor.MAGENTA, PColor.YELLOW};
		Timer updateTimer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root.update();
				colorTimer++;
				if (colorTimer % 100 == 0) {
					colorTimer = 0;
					testDesign.setTextColor(colors[(++colorID) % colors.length]);
					root.reRender(root);
				}
			}
		});
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
		updateTimer.start();
		
		north = new PPanel();
		north.setID("north");
		north.setLayout(new PCentricLayout(north));
		root.getLayout().addChild(north, PBorderLayout.Constraint.TOP);
		
		final DefaultPLabelModel sldLblModel = new DefaultPLabelModel("50");
		
		PSlider sld1 = new PSlider();
		sld1.setModel(new DefaultPSliderModel());
		sld1.getModel().setMinValue(0);
		sld1.getModel().setMaxValue(100);
		sld1.getModel().setValue(50);
		sld1.getModel().addObs(new PSliderModelObs() {
			public void valueChanged(PSliderModel model) {
				sldLblModel.setText(Integer.toString(model.getValue()));
			}
			public void boundsChanged(PSliderModel model) {
			}
		});
		
		PLabel lbl1 = new PLabel();
		lbl1.setModel(sldLblModel);
		north.addChild(new PTuple(sld1, lbl1), null);
		
		east = new PPanel();
		east.setID("east");
		east.setLayout(new PCentricLayout(east));
		root.getLayout().addChild(east, PBorderLayout.Constraint.RIGHT);
		
		PPicture pic = new PPicture();
		pic.getModel().setImagePath("Tex.png");
		east.addChild(pic, null);
		
		PList list = new PList();
		DefaultPListModel listModel = new DefaultPListModel();
		list.setModel(listModel);
		listModel.addElement("Dies");
		listModel.addElement("ist");
		listModel.addElement("Test");
		listModel.addElement("ein", 2);
		for (int i = 0; i < 15; i++) {
			listModel.addElement("Item ("+i+")");
		}
		listModel.addElement("A really really really long item to try out the scroll feature");
//		east.addChild(list, PBorderLayout.Constraint.CENTER);
		
		west = new PPanel();
		west.setID("west");
		west.setLayout(new PMatrixLayout(west, 2, 2));
		root.getLayout().addChild(west, PBorderLayout.Constraint.LEFT);
		
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				final DefaultPLabelModel lblModel = new DefaultPLabelModel();
				PLabel lbl = new PLabel();
				lbl.setModel(lblModel);
				
				PCheckBox chkBx = new PCheckBox();
				chkBx.getModel().addObs(new PCheckBoxModelObs() {
					public void onClick(PCheckBoxModel model) {
					}
					public void onChange(PCheckBoxModel model) {
						String text = model.isChecked() ? "Checked" : "Unchecked";
						lblModel.setText(text);
					}
				});
				lblModel.setText(chkBx.isChecked() ? "Checked" : "Unchecked");
				
				PComponent cell = new PLineBorder(new PTuple(chkBx, lbl));
				cell.setID("("+x+", "+y+")");
				west.addChild(cell, new PMatrixLayout.GridConstraint(x, y));
			}
		}
		
		south = new PPanel();
		south.setID("south");
		south.setLayout(new PListLayout(south, Orientation.LEFT_TO_RIGHT));
		root.getLayout().addChild(south, PBorderLayout.Constraint.BOTTOM);
		
		PLabel lblConfirm = new PLabel();
		lblConfirm.setModel(new DefaultPLabelModel("Confirm"));
		
		PButton btnConfirm = new PButton();
		btnConfirm.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				System.out.println("Confirm!");
				new MyLittleDialog(root.createDialog());
			}
		});
		btnConfirm.setContent(lblConfirm);
		
		PLabel lblCancel = new PLabel();
		lblCancel.setModel(new DefaultPLabelModel("Cancel"));
		
		PLabelDesign lblCancelDesign = new PLabelDesign();
		lblCancelDesign.setFont(root.fetchFontResource("Sylfaen", 24, Style.ITALIC));
		lblCancelDesign.setTextColor(PColor.RED);
		lblCancel.setDesign(lblCancelDesign);
		testDesign = lblCancelDesign;
		
		PButton btnCancel = new PButton();
		btnCancel.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				System.out.println("Cancel!");
			}
		});
		btnCancel.setContent(lblCancel);
		
		south.addChild(btnConfirm, null);
		south.addChild(btnCancel, null);
		
		PScrollPanel center = new PScrollPanel();
		center.setID("center");
//		center = new PPanel("center");
//		center.setLayout(new PBorderLayout(center));
		root.getLayout().addChild(center, PBorderLayout.Constraint.CENTER);
		
		DefaultPBarChartModel chartModel = new DefaultPBarChartModel(5);
		chartModel.setBarValue(0, 64);
		chartModel.setBarValue(1, 127);
		chartModel.setBarValue(2, 19);
		chartModel.setBarValue(3, 275);
		chartModel.setBarValue(4, 97);
		
		PBarChart barChart = new PBarChart();
		barChart.setModel(chartModel);
		
		PPanel centerPnl = new PPanel();
		centerPnl.setLayout(new PCentricLayout(centerPnl));
		centerPnl.addChild(barChart, null);
		
		center.setView(list);
//		center.setContent(centerPnl);
//		center.addChild(barChart, PBorderLayout.Constraint.CENTER);
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
			pnlButtons.setLayout(new PListLayout(pnlButtons, Orientation.RIGHT_TO_LEFT));
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