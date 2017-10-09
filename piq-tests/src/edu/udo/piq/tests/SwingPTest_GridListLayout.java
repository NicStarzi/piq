package edu.udo.piq.tests;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PColoredShape;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.layouts.PGridListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.ImmutablePSize;

public class SwingPTest_GridListLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_GridListLayout().toString();
	}
	
	public SwingPTest_GridListLayout() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		int numCols = 3;
		int numRows = 5;
		
		PPanel pnl = new PPanel();
		root.setBody(pnl);
		PGridListLayout layout = new PGridListLayout(pnl, numCols);
		layout.setListAlignment(ListAlignment.CENTERED_LEFT_TO_RIGHT);
		layout.setColumnAlignmentY(0, AlignmentY.BOTTOM);
		layout.setColumnAlignmentY(1, AlignmentY.CENTER);
		layout.setColumnAlignmentY(2, AlignmentY.TOP);
		layout.setColumnAlignmentX(0, AlignmentX.RIGHT);
		layout.setColumnAlignmentX(1, AlignmentX.CENTER);
		layout.setColumnAlignmentX(2, AlignmentX.LEFT);
		layout.setColumnGrowth(1, Growth.MAXIMIZE);
		layout.setGapBetweenColumns(0);
		layout.setGapBetweenRows(0);
		layout.setInsets(new ImmutablePInsets(0));
		pnl.setLayout(layout);
		
		PColor[] colors = {PColor.RED, PColor.GREEN, PColor.BLUE};
		int[] widths = {64, 48, 32};
		int[] heights = {32, 48, 64};
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
//				PTableCellIndex idx = new PTableCellIndex(c, r);
				PSize size = new ImmutablePSize(widths[c] + r * 8, heights[c] + r * 8);
				PComponent comp = new ColoredLabelDot(size, colors[c], "#"+c+"x"+r);
//				pnl.addChild(comp, idx);
				pnl.addChild(comp, null);
			}
		}
	}
	
	public static class ColoredLabelDot extends PColoredShape {
		
		private String text = "";
		
		public ColoredLabelDot(PSize size, PColor color, String text) {
			super(Shape.CIRCLE, color);
			setSize(size);
			setText(text);
		}
		
		public void setText(String value) {
			if (!text.equals(value)) {
				text = value;
				fireReRenderEvent();
			}
		}
		
		public String getText() {
			return text;
		}
		
		@Override
		public void defaultRender(PRenderer renderer) {
			super.defaultRender(renderer);
			
			PFontResource font = getRoot().fetchFontResource(PLabel.FONT_ID);
			
			PBounds bnds = getBoundsWithoutBorder();
			String txt = getText();
			PSize txtSize = font.getSize(txt, null);
			int txtX = bnds.getX() + bnds.getWidth() / 2 - txtSize.getWidth() / 2;
			int txtY = bnds.getY() + bnds.getHeight() / 2 - txtSize.getHeight() / 2;
			renderer.setColor(PColor.WHITE);
			renderer.drawString(font, txt, txtX, txtY);
		}
		
	}
	
}