package edu.udo.piq.tests.swing;

import edu.udo.piq.components.PRadioButton;
import edu.udo.piq.components.PRadioButtonGroup;
import edu.udo.piq.components.PRadioButtonTuple;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PFreeLayout;

public class SwingPTest_PRadioButton extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PRadioButton();
	}
	
	public SwingPTest_PRadioButton() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PRadioButton radBtn1 = new PRadioButton();
		bodyPnl.getLayout().addChild(radBtn1, new PFreeLayout.FreeConstraint(36, 53));

		PRadioButtonTuple radBtn2 = new PRadioButtonTuple();
		radBtn2.setSecondComponent(new PLabel(new DefaultPTextModel("Click me!")));
		bodyPnl.getLayout().addChild(radBtn2, new PFreeLayout.FreeConstraint(89, 12));

		new PRadioButtonGroup(radBtn1, radBtn2.getRadioButton());

		PRadioButton radBtn3 = new PRadioButton();
		bodyPnl.getLayout().addChild(radBtn3, new PFreeLayout.FreeConstraint(241, 41));

		PRadioButtonTuple radBtn4 = new PRadioButtonTuple();
		radBtn4.setSecondComponent(new PLabel(new DefaultPTextModel("Here!")));
		bodyPnl.getLayout().addChild(radBtn4, new PFreeLayout.FreeConstraint(173, 109));

		PRadioButtonTuple radBtn5 = new PRadioButtonTuple();
		radBtn5.setSecondComponent(new PLabel(new DefaultPTextModel("Come on!")));
		bodyPnl.getLayout().addChild(radBtn5, new PFreeLayout.FreeConstraint(201, 78));

		new PRadioButtonGroup(radBtn3, radBtn4.getRadioButton(), radBtn5.getRadioButton());
	}
	
}