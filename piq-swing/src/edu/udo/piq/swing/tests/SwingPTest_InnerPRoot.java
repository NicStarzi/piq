package edu.udo.piq.swing.tests;

import edu.udo.piq.PColor;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PClickable;
import edu.udo.piq.components.containers.PLineBorder;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PSizeTestArea;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.tools.InnerPRoot;

public class SwingPTest_InnerPRoot extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_InnerPRoot();
	}
	
	public SwingPTest_InnerPRoot() {
		super(640, 480);
	}
	
	protected void buildGUI() {
		PPanel body = new PPanel();
		body.setLayout(new PFreeLayout(body));
		root.setBody(body);
		
		body.addChild(new PLabel("Label 1"), new FreeConstraint(12, 14));
		body.addChild(new PLabel("Label 2"), new FreeConstraint(550, 128));
		body.addChild(new PLabel("Label 3"), new FreeConstraint(274, 395));
//		body.addChild(new PButton(), new FreeConstraint(15, 246, 100, 20));
		
		InnerPRoot innerRoot = new InnerPRoot();
		PLineBorder innerRootBorder = new PLineBorder(1);
		innerRootBorder.setContent(innerRoot);
		
		PSizeTestArea innerRootArea = new PSizeTestArea();
		innerRootArea.setBackgroundColor(PColor.WHITE);
		innerRootArea.setContent(innerRootBorder);
		innerRootArea.setContentX(25);
		innerRootArea.setContentY(25);
		innerRootArea.setContentFinalX(125);
		innerRootArea.setContentFinalY(125);
		body.addChild(innerRootArea, new FreeConstraint(120, 48, 400, 300));
		
//		PPanel innerBody = new PPanel();
//		innerBody.setLayout(new PBorderLayout(innerBody));
//		innerRoot.getDummyRoot().setBody(innerBody);
		
		PButton innerBtn = new PButton();//new PLabel("Test")
		innerBtn.addObs(new PClickObs() {
			public void onClick(PClickable clickable) {
				System.out.println("Click!");
			}
		});
//		innerBody.addChild(innerBtn, PBorderLayout.Constraint.BOTTOM);
		innerRoot.getDelegateRoot().setBody(innerBtn);
//		innerRoot.getDelegateRoot().setMouse(null);
		
//		root.setBody(body);
	}
	
}