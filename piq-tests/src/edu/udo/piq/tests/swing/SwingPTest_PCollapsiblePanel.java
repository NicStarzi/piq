package edu.udo.piq.tests.swing;

import edu.udo.piq.PColor;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PColoredShape;
import edu.udo.piq.components.PColoredShape.Shape;
import edu.udo.piq.components.containers.PCollapsiblePanel;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.ImmutablePInsets;

public class SwingPTest_PCollapsiblePanel extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PCollapsiblePanel();
	}
	
	public SwingPTest_PCollapsiblePanel() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel body = new PPanel();
		body.setLayout(new PListLayout(body, ListAlignment.TOP_TO_BOTTOM));
		
		PCollapsiblePanel red = new PCollapsiblePanel();
		PCollapsiblePanel blu = new PCollapsiblePanel();
		PCollapsiblePanel grn = new PCollapsiblePanel();
		
		red.getLayout().setHeaderAlignX(AlignmentX.LEFT);
		blu.getLayout().setHeaderAlignX(AlignmentX.CENTER);
		grn.getLayout().setHeaderAlignX(AlignmentX.R);
		
		red.getLayout().setHeaderAlignY(AlignmentY.TOP);
		blu.getLayout().setHeaderAlignY(AlignmentY.C);
		grn.getLayout().setHeaderAlignY(AlignmentY.BOTTOM);
		
		red.getLayout().setButtonLabelGap(4);
		blu.getLayout().setButtonLabelGap(16);
		grn.getLayout().setButtonLabelGap(48);
		
		red.getLayout().setHeaderBodyGap(4);
		blu.getLayout().setHeaderBodyGap(16);
		grn.getLayout().setHeaderBodyGap(48);
		
		red.getLayout().setInsets(new ImmutablePInsets(4));
		blu.getLayout().setInsets(new ImmutablePInsets(8, 16));
		grn.getLayout().setInsets(new ImmutablePInsets(8, 16, 32, 64));
		
		red.setHeader(new PLabel("Red Circle"));
		red.setBody(new PColoredShape(Shape.CIRCLE, PColor.RED));
		blu.setExpandButton(new PCheckBox());
		blu.setHeader(new PLabel("Blue Rectangle"));
		blu.setBody(new PColoredShape(Shape.RECTANGLE, PColor.BLUE));
		grn.setHeader(new PLabel("Green Triangle"));
		grn.setBody(new PColoredShape(Shape.TRIANGLE_DOWN, PColor.GREEN));
		
		body.addChild(red, null);
		body.addChild(blu, null);
		body.addChild(grn, null);
		
		root.setBody(body);
	}
	
}