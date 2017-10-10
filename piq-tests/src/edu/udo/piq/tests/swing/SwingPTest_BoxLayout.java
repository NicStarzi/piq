package edu.udo.piq.tests.swing;

import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.layouts.PBoxLayout;
import edu.udo.piq.layouts.PBoxLayout.Box;

public class SwingPTest_BoxLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_BoxLayout();
	}
	
	public SwingPTest_BoxLayout() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel pnl = new PPanel();
		PBoxLayout boxLayout = new PBoxLayout(pnl);
		pnl.setLayout(boxLayout);
		root.setBody(pnl);
		
		Box b1 = boxLayout.getRootBox();
		b1.splitVertical(0.4, 5);
		Box b2 = b1.getLeft();
		Box b3 = b1.getRight();
		
		b2.splitHorizontal(0.3, 10);
		Box c1 = b2.getTop();
		Box c2 = b2.getBottom();
		
		b3.splitHorizontal(0.6, 20);
		Box c3 = b3.getTop();
		Box c4 = b3.getBottom();
		
		PPicture pic1 = new PPicture();
		pic1.getModel().setValue("Tex.png");
		pic1.setStretchToSize(true);
		pnl.getLayout().addChild(pic1, c1);
		
		PPicture pic2 = new PPicture();
		pic2.getModel().setValue("Tex.png");
		pic2.setStretchToSize(true);
		pnl.getLayout().addChild(pic2, c2);
		
		PPicture pic3 = new PPicture();
		pic3.getModel().setValue("Tex.png");
		pic3.setStretchToSize(true);
		pnl.getLayout().addChild(pic3, c3);
		
		PPicture pic4 = new PPicture();
		pic4.getModel().setValue("Tex.png");
		pic4.setStretchToSize(true);
		pnl.getLayout().addChild(pic4, c4);
	}
	
}