package edu.udo.piq.tests.swing;

import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.collections.PColumnIndex;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.popup2.DelegatedPLayout;
import edu.udo.piq.components.popup2.DelegationPGridListLayout;
import edu.udo.piq.components.popup2.DelegationPGridListLayout.PDelegateRowIndex;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.layouts.PGridLayout.Growth;

public class SwingPTest_PDelegatedLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PDelegatedLayout();
	}
	
	public SwingPTest_PDelegatedLayout() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PPanel base = new PPanel();
		DelegationPGridListLayout gridLayout = new DelegationPGridListLayout(base, 3);
//		gridLayout.setColumnAlignmentX(AlignmentX.FILL);
//		gridLayout.setColumnAlignmentY(AlignmentY.FILL);
		gridLayout.setColumnGrowth(Growth.MAXIMIZE);
		base.setLayout(gridLayout);
		bodyPnl.addChild(base, new FreeConstraint(8, 8, 480, 320));
		
		base.addChild(new TestPanel(PColor.LIGHT_RED, "Short", "Loooooooong", "Short"), new PDelegateRowIndex(0));
		base.addChild(new TestPanel(PColor.LIGHT_BLUE, "Loooooooong", "Short", "Short"), new PDelegateRowIndex(1));
		base.addChild(new TestPanel(PColor.LIGHT_GREEN, "Short", "Short", "Loooooooong"), new PDelegateRowIndex(2));
	}
	
	private static class TestPanel extends PPanel {
		final PColor bgColor;
		public TestPanel(PColor color, String s1, String s2, String s3) {
			setLayout(new DelegatedPLayout(this));
			addChild(new PButton(new PLabel(s1)), new PColumnIndex(0));
			addChild(new PLabel(s2), new PColumnIndex(1));
			addChild(new PCheckBoxTuple(new PLabel(s3)), new PColumnIndex(2));
			bgColor = color;
		}
		@Override
		public void defaultRender(PRenderer renderer) {
			renderer.setColor(bgColor);
			renderer.drawQuad(getBoundsWithoutBorder());
		}
	}
}