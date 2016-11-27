package edu.udo.piq.tutorial;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PTabPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PAnchorLayout;
import edu.udo.piq.swing.tests.AbstractSwingPTest;

public class TestMovingPLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new TestMovingPLayout();
	}
	
	public TestMovingPLayout() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new MovingPLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PTabPanel tab = new PTabPanel();
		bodyPnl.addChild(tab, null);
		
		PPanel tab1 = new PPanel();
		tab1.setLayout(new PAnchorLayout(tab1));
		tab.addTab(new PLabel("Hallo"), tab1);
		
		PPanel tab2 = new PPanel();
		tab2.setLayout(new PAnchorLayout(tab2));
		tab.addTab(new PLabel("Welt"), tab2);
		
		PButton btn = new PButton();
		btn.setContent(new PLabel("Click me!"));
		btn.addObs((PClickObs) clickable -> System.out.println("You clicked it!"));
		tab1.addChild(btn, null);
		
		PSlider s = new PSlider();
		s.getModel().setMaxValue(100);
		tab2.addChild(s, null);
	}
	
}