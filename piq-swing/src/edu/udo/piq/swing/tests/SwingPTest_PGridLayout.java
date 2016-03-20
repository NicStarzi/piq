package edu.udo.piq.swing.tests;

import edu.udo.piq.PColor;
import edu.udo.piq.components.PColoredRectangle;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PGridLayout.AlignH;
import edu.udo.piq.layouts.PGridLayout.AlignV;
import edu.udo.piq.layouts.PGridLayout.GridConstraint;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.ImmutablePSize;

public class SwingPTest_PGridLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PGridLayout();
	}
	
	public SwingPTest_PGridLayout() {
		super(640, 480);
	}
	
	public void buildGUI() {
		PPanel body = new PPanel();
//		body.setLayout(new PGridLayout(body, 1, 1));
		body.setLayout(new PGridLayout(body, 3, 4));
		((PGridLayout) body.getLayout()).setColumnGrowth(2, Growth.MAXIMIZE);
		((PGridLayout) body.getLayout()).setRowGrowth(3, Growth.MAXIMIZE);
		((PGridLayout) body.getLayout()).setInsets(new ImmutablePInsets(1));
		
		PColoredRectangle red = new PColoredRectangle(PColor.RED);
		red.setID("RED");
		PColoredRectangle green = new PColoredRectangle(PColor.GREEN);
		green.setID("GREEN");
		green.setSize(new ImmutablePSize(50, 125));
		PColoredRectangle teal = new PColoredRectangle(PColor.TEAL);
		teal.setID("TEAL");
		teal.setSize(new ImmutablePSize(125, 75));
		PColoredRectangle blue = new PColoredRectangle(PColor.BLUE);
		blue.setSize(new ImmutablePSize(150, 150));
		blue.setID("BLUE");
		PColoredRectangle magenta = new PColoredRectangle(PColor.MAGENTA);
		magenta.setID("MAGENTA");
		PColoredRectangle white = new PColoredRectangle(PColor.WHITE);
		white.setID("WHITE");
		PColoredRectangle yellow = new PColoredRectangle(PColor.YELLOW);
		yellow.setID("YELLOW");
		
		/*
		AAA B BBB
		--C D DDD
		... D DDD
		-E- F G--
		
		A = (0, 0, 1, 1)
		B = (2, 0, 2, 1)
		C = (0, 1, 1, 1 | RIGHT)
		D = (1, 1, 2, 2)
		E = (0, 3, 1, 1 | CENTER)
		F = (1, 3, 1, 1)
		G = (2, 3, 1, 1 | LEFT)
		
		[][][grow]
		 */
		
		body.addChild(red,		"0 0 1 1 alignX=r alignV=bOtToM");
		body.addChild(green,	new GridConstraint(1, 0, 2, 1));
		body.addChild(teal,		new GridConstraint(0, 1, 1, 1));//.alignH(AlignH.RIGHT)
		body.addChild(blue,		new GridConstraint(1, 1, 2, 2).alignH(AlignH.F).alignV(AlignV.F));
		body.addChild(magenta,	new GridConstraint(0, 3, 1, 1).alignH(AlignH.C).alignV(AlignV.B));
		body.addChild(white,	new GridConstraint(1, 3, 1, 1).alignH(AlignH.R).alignV(AlignV.T));
		body.addChild(yellow,	new GridConstraint(2, 3, 1, 1).alignH(AlignH.R).alignV(AlignV.C));
		
		root.setBody(body);
	}
	
}