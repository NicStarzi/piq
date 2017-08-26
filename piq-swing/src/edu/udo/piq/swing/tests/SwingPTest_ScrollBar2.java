package edu.udo.piq.swing.tests;

import edu.udo.piq.borders.PLineBorder;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PTextArea;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.scroll2.PScrollPanel;

public class SwingPTest_ScrollBar2 extends AbstractSwingPTest {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new SwingPTest_ScrollBar2();
	}
	
	public SwingPTest_ScrollBar2() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PScrollPanel scroll = new PScrollPanel();
		scroll.setBorder(new PLineBorder(3));
		bodyPnl.addChild(scroll, new FreeConstraint(32, 32, 256, 256));
		
		PTextArea txtArea = new PTextArea("Dies ist ein großer langer Text, welcher nur zu Testzwecken gedacht ist.");
		scroll.setBody(txtArea);
	}
	
}