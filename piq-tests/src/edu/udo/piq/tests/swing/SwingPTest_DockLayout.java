package edu.udo.piq.tests;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PDockLayoutOLD;
import edu.udo.piq.tools.ImmutablePInsets;

public class SwingPTest_DockLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_DockLayout();
	}
	
	public SwingPTest_DockLayout() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel pnl = new PPanel();
		PDockLayoutOLD dockLayout = new PDockLayoutOLD(pnl);
		dockLayout.setInsets(new ImmutablePInsets(12));
		dockLayout.setGap(6);
		pnl.setLayout(dockLayout);
		root.setBody(pnl);
		
		PButton btn1 = new PButton();
		btn1.setContent(new PLabel(new DefaultPTextModel("Button")));
		pnl.addChild(btn1, new PDockLayoutOLD.Constraint(0, 0));
		
		PButton btn2 = new PButton();
		btn2.setContent(new PLabel(new DefaultPTextModel("Button")));
		pnl.addChild(btn2, new PDockLayoutOLD.Constraint(1, 0));
		
		PLabel lbl1 = new PLabel(new DefaultPTextModel("This is a very very very long label"));
		pnl.addChild(lbl1, new PDockLayoutOLD.Constraint(0, 1));
		
		PPicture pic1 = new PPicture();
		pic1.getModel().setValue("Tex.png");
		pnl.addChild(pic1, new PDockLayoutOLD.Constraint(643, 0));
	}
	
}