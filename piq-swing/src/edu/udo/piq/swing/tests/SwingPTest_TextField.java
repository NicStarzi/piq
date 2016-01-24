package edu.udo.piq.swing.tests;

import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.components.defaults.PSpinnerModelList;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;

public class SwingPTest_TextField extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_TextField();
	}
	
	public SwingPTest_TextField() {
		super(480, 320);
	}
	
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PTextField txtField = new PTextField("Dies ist: EIN TEST!");
		bodyPnl.addChild(txtField, new FreeConstraint(24, 73));//, 200, 100
		
		PSpinner spinnerInt = new PSpinner(new PSpinnerModelInt(3, -16, 162, 4));
		bodyPnl.addChild(spinnerInt, new FreeConstraint(48, 128));
		
		PSpinner spinnerTxt = new PSpinner(new PSpinnerModelList("Berlin", new String[] {
				"Berlin", "Dortmund", "Köln", "Bremen", "Hannover", "München", "Dresden"
		}));
		bodyPnl.addChild(spinnerTxt, new FreeConstraint(162, 51, 200, 26));
	}
	
}