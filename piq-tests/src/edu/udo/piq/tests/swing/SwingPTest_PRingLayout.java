package edu.udo.piq.tests.swing;

import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PRingMenu;
import edu.udo.piq.layouts.PBorderLayout;

public class SwingPTest_PRingLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PRingLayout();
	}
	
	public SwingPTest_PRingLayout() {
		super(480, 320);
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setID("body");
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		String[] btnTxts = {"Button #1", "Button #2", "Button #3",
				"Button #4", "Button #5", "Button #6"};
		
		PRingMenu menu = new PRingMenu();
		for (int i = 0; i < btnTxts.length; i++) {
			String text = btnTxts[i];
			PButton btn = new PButton(text);
			btn.setID("Button #"+i);
			btn.addObs((PClickObs) (c) -> {
				System.out.println("You clicked: "+text);
			});
			menu.addChild(btn);
		}
		bodyPnl.addChild(menu, PBorderLayout.BorderLayoutConstraint.CENTER);
		
		menu.addObs(new PMouseObs() {
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				if (btn == MouseButton.RIGHT) {
					if (menu.isOpen()) {
						menu.close();
					} else {
						menu.open();
					}
				}
			}
		});
	}
	
}