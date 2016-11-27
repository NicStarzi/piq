package edu.udo.piq.tutorial;

import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.layouts.PAnchorLayout;
import edu.udo.piq.swing.tests.AbstractSwingPTest;

public class TestClickSpeedTester extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new TestClickSpeedTester();
	}
	
	public TestClickSpeedTester() {
		super(480, 320);
	}
	
	@Override
	public void buildGUI() {
		PPanel body = new PPanel();
		body.setLayout(new PAnchorLayout(body));
		root.setBody(body);
		
		ClickSpeedTester tester = new ClickSpeedTester();
		body.addChild(tester, null);
	}
	
}