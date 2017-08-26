package edu.udo.piq.swing.tests;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PRadioButtonGroup;
import edu.udo.piq.components.PRadioButtonTuple;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextArea;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.tools.SpatialArrowKeyPFocusTraversal;

public class SwingPTest_FocusTraversal extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_FocusTraversal().toString();
	}
	
	public SwingPTest_FocusTraversal() {
		super(480, 320);
	}
	
	@SuppressWarnings("unused")
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		root.setFocusTraversal(new SpatialArrowKeyPFocusTraversal());
		
		PSpinner spinner = new PSpinner(new PSpinnerModelInt(5, 0, 12));
		spinner.setID("spinner");
		bodyPnl.addChild(spinner, new FreeConstraint(32, 32, 256, -1));
		
		PTextField textField = new PTextField("Text Field...");
		textField.setID("textField");
		bodyPnl.addChild(textField, new FreeConstraint(64, 96, 192, 20));
		
		PTextArea textArea = new PTextArea("Text Area...");
		textArea.setID("textArea");
		bodyPnl.addChild(textArea, new FreeConstraint(16, 128, 160, 160));
		
		PButton btn1 = new PButton(new PLabel("Button 1"));
		btn1.setID("button");
		bodyPnl.addChild(btn1, new FreeConstraint(320, 48, 96, 24));
		
		PCheckBoxTuple chk1 = new PCheckBoxTuple(new PLabel("Checkbox 1"));
		chk1.setID("checkbox");
		bodyPnl.addChild(chk1, new FreeConstraint(320, 96, 96, 24));
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PGridLayout(btnPnl, 2, 2));
		btnPnl.setFocusTraversal(new SpatialArrowKeyPFocusTraversal());
		bodyPnl.addChild(btnPnl, new FreeConstraint(192, 128, 320, 256));
		
		PRadioButtonTuple rad1 = new PRadioButtonTuple(new PLabel("Radio 1"));
		PRadioButtonTuple rad2 = new PRadioButtonTuple(new PLabel("Radio 2"));
		PRadioButtonTuple rad3 = new PRadioButtonTuple(new PLabel("Radio 3"));
		PRadioButtonTuple rad4 = new PRadioButtonTuple(new PLabel("Radio 4"));
		rad1.setID("radio NW");
		rad2.setID("radio NE");
		rad3.setID("radio SW");
		rad4.setID("radio SE");
		new PRadioButtonGroup(rad1, rad2, rad3, rad4);
		btnPnl.addChild(rad1, "0 0");
		btnPnl.addChild(rad2, "1 0");
		btnPnl.addChild(rad3, "0 1");
		btnPnl.addChild(rad4, "1 1");
	}
	
}