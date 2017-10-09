package edu.udo.piq.swing.tests;

import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PRingMenu;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PBorderLayout;

public class SwingPTest_PRingLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PRingLayout();
	}
	
	public SwingPTest_PRingLayout() {
		super(480, 320);
	}
	
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		String[] btnTxts = {"Button #1", "Button #2", "Button #3", 
				"Button #4", "Button #5", "Button #6"};
		
		/*
		 * Ring
		 */
		PRingMenu menu = new PRingMenu();
		for (int i = 0; i < btnTxts.length; i++) {
			String text = btnTxts[i];
			PButton btn = new PButton(new PLabel(text));
			btn.addObs((PClickObs) (c) -> {
				System.out.println("You clicked: "+text);
			});
			menu.addChild(btn);
		}
		bodyPnl.addChild(menu, PBorderLayout.Constraint.CENTER);
		menu.addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				if (btn == MouseButton.RIGHT) {
					if (menu.isOpen()) {
						menu.close();
					} else {
						menu.open();
					}
				}
			}
		});
		
//		PPanel ringPnl = new PPanel();
//		PRingLayout layout = new PRingLayout(ringPnl);
//		layout.setRadius(100);
//		ringPnl.setLayout(layout);
//		bodyPnl.addChild(ringPnl, PBorderLayout.Constraint.CENTER);
//		
//		int BTN_COUNT = 6;
//		
//		for (int i = 0; i < BTN_COUNT; i++) {
//			addButtonToPanel(ringPnl);
//		}
//		PTimer rotTimer = new PTimer(() -> {
//			double rot = layout.getRotationInDeg() + 1;
//			layout.setRotationInDeg(rot);
//		});
//		rotTimer.setDelay(1);
//		rotTimer.setRepeating(true);
//		rotTimer.setOwner(ringPnl);
//		rotTimer.start();
		
		/*
		 * Buttons
		 */
//		PPanel btnPnl = new PPanel();
//		btnPnl.setLayout(new PListLayout(btnPnl, ListAlignment.CENTERED_LEFT_TO_RIGHT));
//		bodyPnl.addChild(btnPnl, PBorderLayout.Constraint.BOTTOM);
//		
//		PSpinner inputRadius = new PSpinner();
//		inputRadius.setModel(new PSpinnerModelDouble(layout.getRadius(), 0, Double.MAX_VALUE, 1));
//		inputRadius.addObs((mdl, oldVal) -> layout.setRadius((Double) mdl.getValue()));
//		btnPnl.addChild(inputRadius, null);
//		
//		PCheckBoxTuple inputUsePref = new PCheckBoxTuple(new PLabel("Use Pref Radius"));
//		inputUsePref.addObs((PCheckBoxModelObs) (mdl) -> layout.setUsePreferredRadius(mdl.isChecked()));
//		btnPnl.addChild(inputUsePref, null);
//		
//		PSpinner inputRotation = new PSpinner();
//		inputRotation.setModel(new PSpinnerModelDouble(layout.getRotationInDeg()));
//		inputRotation.setEnabled(false);
//		inputRotation.addObs((mdl, oldVal) -> layout.setRotationInDeg((Double) mdl.getValue()));
//		inputRotation.setOutputEncoder((valObj) -> {
//			int valInt = (int) ((double) valObj) % 360;
//			return Integer.toString(valInt);
//		});
//		btnPnl.addChild(inputRotation, null);
//		
//		PButton btnAdd = new PButton(new PLabel("Add"));
//		btnAdd.addObs((PClickObs) (b) -> addButtonToPanel(ringPnl));
//		btnPnl.addChild(btnAdd, null);
//		
//		PButton btnRemove = new PButton(new PLabel("Remove"));
//		btnRemove.addObs((PClickObs) (b) -> removeButtonFromPanel(ringPnl));
//		btnPnl.addChild(btnRemove, null);
//		
//		layout.addObs(new PLayoutObs() {
//			public void onLayoutInvalidated(PReadOnlyLayout l) {
////				inputRadius.getModel().setValue(layout.getRadius());
//				inputUsePref.getCheckBox().getModel().setValue(layout.isUsePreferredRadius());
//				inputRotation.getModel().setValue(layout.getRotationInDeg());
//			}
//		});
	}
	
//	private void addButtonToPanel(PPanel ringPnl) {
//		String name = "Button #"+ringPnl.getChildCount();
//		PButton btn = new PButton(new PLabel(name));
//		btn.addObs((PClickObs) (b) -> System.out.println("clicked "+name));
//		ringPnl.addChild(btn, null);
//	}
//	
//	private void removeButtonFromPanel(PPanel ringPnl) {
//		ringPnl.removeChild(Integer.valueOf(0));
//	}
	
}