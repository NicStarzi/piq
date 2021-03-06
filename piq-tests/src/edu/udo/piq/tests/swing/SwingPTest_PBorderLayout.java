package edu.udo.piq.tests.swing;

import java.util.EnumMap;
import java.util.Map;

import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.PSpinnerModelObs;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PBorderLayout.BorderLayoutConstraint;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PGridLayout.GridConstraint;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.tools.MutablePSize;

public class SwingPTest_PBorderLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PBorderLayout();
	}
	
	public SwingPTest_PBorderLayout() {
		super(480, 320);
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		
		PBorderLayout borderLayout = new PBorderLayout(bodyPnl);
		bodyPnl.setLayout(borderLayout);
		root.setBody(bodyPnl);
		
		Map<BorderLayoutConstraint, PColor> colorMap = new EnumMap<>(BorderLayoutConstraint.class);
		colorMap.put(BorderLayoutConstraint.TOP,	PColor.RED);
		colorMap.put(BorderLayoutConstraint.LEFT,	PColor.BLUE);
		colorMap.put(BorderLayoutConstraint.RIGHT,	PColor.GREEN);
		colorMap.put(BorderLayoutConstraint.BOTTOM,	PColor.MAGENTA);
		colorMap.put(BorderLayoutConstraint.CENTER,	PColor.YELLOW);
		
		for (BorderLayoutConstraint c : BorderLayoutConstraint.ALL) {
			TestPanel cell = new TestPanel(colorMap.get(c));
			bodyPnl.addChild(cell, c);
		}
	}
	
	private static class TestPanel extends PPanel {
		
		final MutablePSize prefSize = new MutablePSize(100, 100);
		final PSpinner setWidth = new PSpinner();
		final PSpinner setHeight = new PSpinner();
		final PColor color;
		
		public TestPanel(PColor color) {
			this.color = color;
			int min = 60;
			int max = 400;
			int initialVal = 100;
			
			setWidth.setModel(new PSpinnerModelInt(initialVal, min, max));
			setHeight.setModel(new PSpinnerModelInt(initialVal, min, max));
			
			PSpinnerModelObs sizeObs = (mdl, oldVal) -> refreshSize();
			setWidth.addObs(sizeObs);
			setHeight.addObs(sizeObs);
			
			PGridLayout layout = new PGridLayout(this, 3, 4);
			layout.setColumnGrowth(0, Growth.MAXIMIZE);
			layout.setColumnGrowth(2, Growth.MAXIMIZE);
			layout.setRowGrowth(0, Growth.MAXIMIZE);
			layout.setRowGrowth(3, Growth.MAXIMIZE);
			setLayout(layout);
			addChild(setWidth, new GridConstraint(1, 1));
			addChild(setHeight, new GridConstraint(1, 2));
		}
		
		private void refreshSize() {
			int width = (int) setWidth.getModel().getValue();
			prefSize.setWidth(width);
			int height = (int) setHeight.getModel().getValue();
			prefSize.setHeight(height);
			firePreferredSizeChangedEvent();
		}
		
		@Override
		public void defaultRender(PRenderer renderer) {
			renderer.setColor(color);
			renderer.drawQuad(getBounds());
		}
		
		@Override
		public PSize getDefaultPreferredSize() {
			return prefSize;
		}
		
	}
	
}