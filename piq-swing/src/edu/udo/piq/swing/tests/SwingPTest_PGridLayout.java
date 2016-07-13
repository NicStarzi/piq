package edu.udo.piq.swing.tests;

import edu.udo.piq.PColor;
import edu.udo.piq.components.PColoredShape;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PGridLayout;
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
		
		PColoredShape red = new PColoredShape(PColor.RED);
		red.setID("RED");
		PColoredShape green = new PColoredShape(PColor.GREEN);
		green.setID("GREEN");
		green.setSize(new ImmutablePSize(50, 125));
		PColoredShape teal = new PColoredShape(PColor.TEAL);
		teal.setID("TEAL");
		teal.setSize(new ImmutablePSize(125, 75));
		PColoredShape blue = new PColoredShape(PColor.BLUE);
		blue.setSize(new ImmutablePSize(150, 150));
		blue.setID("BLUE");
		PColoredShape magenta = new PColoredShape(PColor.MAGENTA);
		magenta.setID("MAGENTA");
		PColoredShape white = new PColoredShape(PColor.WHITE);
		white.setID("WHITE");
		PColoredShape yellow = new PColoredShape(PColor.YELLOW);
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
		body.addChild(blue,		new GridConstraint(1, 1, 2, 2).alignH(AlignmentX.F).alignV(AlignmentY.F));
		body.addChild(magenta,	new GridConstraint(0, 3, 1, 1).alignH(AlignmentX.C).alignV(AlignmentY.B));
		body.addChild(white,	new GridConstraint(1, 3, 1, 1).alignH(AlignmentX.R).alignV(AlignmentY.T));
		body.addChild(yellow,	new GridConstraint(2, 3, 1, 1).alignH(AlignmentX.R).alignV(AlignmentY.C));
		
		root.setBody(body);
	}
	
}