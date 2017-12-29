package edu.udo.piq.tests.swing;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PTabPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;

public class SwingPTest_TabPanel extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_TabPanel();
	}
	
	public SwingPTest_TabPanel() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PTabPanel bodyPnl = new PTabPanel();
		root.setBody(bodyPnl);
		
		PPanel tab1 = new PPanel();
		tab1.setID("Tab 1");
		tab1.setLayout(new PFreeLayout(tab1));
		
		PButton btnTab1 = new PButton();
		btnTab1.setContent(new PLabel("Button 1"));
		btnTab1.addObs((PClickObs) (cmp) -> System.out.println("Button Tab 1"));
		tab1.addChild(btnTab1, new FreeConstraint(13, 47));
		
		PPanel tab2 = new PPanel();
		tab2.setID("Tab 2");
		tab2.setLayout(new PFreeLayout(tab2));
		
		PButton btnTab2 = new PButton();
		btnTab2.setContent(new PLabel("Button B"));
		btnTab2.addObs((PClickObs) (cmp) -> System.out.println("Button Tab 2"));
		tab2.addChild(btnTab2, new FreeConstraint(68, 172));
		
		PLabel tabLbl1 = new PLabel("Tab 1");
		bodyPnl.addTab(tabLbl1, tab1);
		PLabel tabLbl2 = new PLabel("Tab 2");
		bodyPnl.addTab(tabLbl2, tab2);
	}
	
}