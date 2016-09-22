package edu.udo.piq.tutorial;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PClickable;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.swing.tests.AbstractSwingPTest;
import edu.udo.piq.tutorial.TrianglePLayout.Pos;

public class TestTrianglePLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new TestTrianglePLayout();
	}
	
	public TestTrianglePLayout() {
		super(240, 240);
	}
	
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new TrianglePLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PButton btnTop = new PButton();
		btnTop.setContent(new PLabel("Top"));
		btnTop.addObs(new PClickObs() {
			public void onClick(PClickable clickable) {
				System.out.println("You clicked top!");
			}
		});
		bodyPnl.addChild(btnTop, Pos.TOP);
		
		PButton btnLft = new PButton();
		btnLft.setContent(new PLabel("Left"));
		btnLft.addObs(new PClickObs() {
			public void onClick(PClickable clickable) {
				System.out.println("You clicked left!");
			}
		});
		bodyPnl.addChild(btnLft, Pos.BOTTOM_LEFT);
		
		PButton btnRgt = new PButton();
		btnRgt.setContent(new PLabel("Right"));
		btnRgt.addObs(new PClickObs() {
			public void onClick(PClickable clickable) {
				System.out.println("You clicked right!");
			}
		});
		bodyPnl.addChild(btnRgt, Pos.BOTTOM_RIGHT);
	}
	
}