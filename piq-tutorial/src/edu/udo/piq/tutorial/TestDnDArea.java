package edu.udo.piq.tutorial;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PClickable;
import edu.udo.piq.components.containers.PSplitPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.swing.tests.AbstractSwingPTest;

public class TestDnDArea extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new TestDnDArea();
	}
	
	public TestDnDArea() {
		super(640, 480);
	}
	
	public void buildGUI() {
		PSplitPanel splitPnl = new PSplitPanel();
		root.setBody(splitPnl);
		
		DnDArea left = new DnDArea();
		splitPnl.setFirstComponent(left);
		
		addLbl(left, 1, 17, 19);
		addLbl(left, 2, 184, 64);
		addBtn(left, 3, 9, 217);
		addLbl(left, 4, 261, 195);
		
		DnDArea right = new DnDArea();
		splitPnl.setSecondComponent(right);
		
		addBtn(right, 1, 64, 23);
		addBtn(right, 2, 117, 136);
		addLbl(right, 3, 215, 75);
		
		ColoredBox box = new ColoredBox();
		left.addChild(box, 76, 311);
	}
	
	private void addLbl(DnDArea area, int num, int x, int y) {
		PLabel lbl = new PLabel("Label #"+num);
		area.addChild(lbl, x, y);
	}
	
	private void addBtn(DnDArea area, int num, int x, int y) {
		PButton btn = new PButton();
		btn.setContent(new PLabel("Button #"+num));
		btn.addObs(new PClickObs() {
			public void onClick(PClickable clickable) {
				System.out.println("clicked #"+num);
			}
		});
		area.addChild(btn, x, y);
	}
	
}