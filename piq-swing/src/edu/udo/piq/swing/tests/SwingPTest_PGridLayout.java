package edu.udo.piq.swing.tests;

import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.PSpinnerModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.containers.PDropDownList;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.PSpinnerModelEnum;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PGridLayout.GridConstraint;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Distribution;
import edu.udo.piq.layouts.PTupleLayout.Orientation;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.ImmutablePSize;

public class SwingPTest_PGridLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PGridLayout();
	}
	
	private final PPanel body = new PPanel();
	
	public SwingPTest_PGridLayout() {
		super(800, 600);
	}
	
	public void buildGUI() {
		PGridLayout layout = new PGridLayout(body, 5, 6);
		layout.setInsets(new ImmutablePInsets(1));
		body.setLayout(layout);
		
		for (int x = 2; x < layout.getColumnCount(); x++) {
			final int col = x;
			Growth gr = layout.getColumnGrowth(x);
			PSpinnerModel model = new PSpinnerModelEnum<>(Growth.class, gr);
			PSpinner selectGrowth = new PSpinner(model);
			selectGrowth.addObs((mdl, oldVal) -> {
				Growth value = (Growth) model.getValue();
				layout.setColumnGrowth(col, value);
			});
			body.addChild(selectGrowth, new GridConstraint(x, 1, AlignmentX.L, AlignmentY.T));
			
			PSpinnerModel modelGap = new PSpinnerModelInt(layout.getGapAfterColumn(x), 0, 100);
			PSpinner selectGap = new PSpinner(modelGap);
			selectGap.addObs((mdl, oldVal) -> {
				int gap = (int) modelGap.getValue();
				layout.setGapAfterColumn(col, gap);
			});
			body.addChild(selectGap, new GridConstraint(x, 0, AlignmentX.L, AlignmentY.T));
		}
		for (int y = 2; y < layout.getRowCount(); y++) {
			final int row = y;
			Growth gr = layout.getRowGrowth(y);
			PSpinnerModel model = new PSpinnerModelEnum<>(Growth.class, gr);
			PSpinner selectGrowth = new PSpinner(model);
			selectGrowth.addObs((mdl, oldVal) -> {
				Growth value = (Growth) model.getValue();
				layout.setRowGrowth(row, value);
			});
			body.addChild(selectGrowth, new GridConstraint(1, y, AlignmentX.L, AlignmentY.T));
			
			PSpinnerModel modelGap = new PSpinnerModelInt(layout.getGapAfterRow(y), 0, 100);
			PSpinner selectGap = new PSpinner(modelGap);
			selectGap.addObs((mdl, oldVal) -> {
				int gap = (int) modelGap.getValue();
				layout.setGapAfterRow(row, gap);
			});
			body.addChild(selectGap, new GridConstraint(0, y, AlignmentX.L, AlignmentY.T));
		}
		
		GridPanel red = new GridPanel(PColor.RED, new ImmutablePSize(100, 100));
		red.setID("RED");
		GridPanel green = new GridPanel(PColor.GREEN, new ImmutablePSize(110, 70));
		green.setID("GREEN");
		GridPanel teal = new GridPanel(PColor.TEAL, new ImmutablePSize(90, 90));
		teal.setID("TEAL");
		GridPanel blue = new GridPanel(PColor.BLUE, new ImmutablePSize(180, 100));
		blue.setID("BLUE");
		GridPanel magenta = new GridPanel(PColor.MAGENTA, new ImmutablePSize(120, 90));
		magenta.setID("MAGENTA");
		GridPanel white = new GridPanel(PColor.WHITE, new ImmutablePSize(100, 90));
		white.setID("WHITE");
		GridPanel yellow = new GridPanel(PColor.YELLOW, new ImmutablePSize(90, 120));
		yellow.setID("YELLOW");
		
		body.addChild(red,		"2 2 1 1 alignX=F alignV=F");
		body.addChild(green,	"3 2 2 1 alignX=F alignV=F");
		body.addChild(teal,		"2 3 1 1 alignX=F alignV=F");
		body.addChild(blue,		"3 3 1 2 alignX=F alignV=F");
		body.addChild(magenta,	"2 4 1 2 alignX=F alignV=F");
		body.addChild(white,	"3 5 1 1 alignX=F alignV=F");
		body.addChild(yellow,	"4 3 1 3 alignX=F alignV=F");
		
		root.setBody(body);
	}
	
	private void updateGridPanelConstraint(GridPanel pnl, GridConstraint constraint) {
		body.removeChild(pnl);
		body.addChild(pnl, constraint);
	}
	
	public class GridPanel extends PPanel {
		
		final PDropDownList selectAlignX = new PDropDownList();
		final PDropDownList selectAlignY = new PDropDownList();
		final PColor color;
		final PSize prefSize;
		
		public GridPanel(PColor color, PSize size) {
			this.color = color;
			prefSize = size;
			selectAlignX.getList().setModel(new DefaultPListModel(AlignmentX.ALL));
			selectAlignY.getList().setModel(new DefaultPListModel(AlignmentY.ALL));
			
			PSelectionObs alignObs = new PSelectionObs() {
				public void onLastSelectedChanged(PSelection selection, 
						PModelIndex prevLastSelected, PModelIndex newLastSelected) 
				{
					updateAlignment();
				}
			};
			
			selectAlignX.getList().addObs(alignObs);
			selectAlignY.getList().addObs(alignObs);
			
			PTupleLayout layout = new PTupleLayout(this);
			layout.setOrientation(Orientation.TOP_TO_BOTTOM);
			layout.setDistribution(Distribution.RESPECT_BOTH);
			layout.setSecondaryDistribution(Distribution.RESPECT_BOTH);
			setLayout(layout);
			layout.addChild(selectAlignX, PTupleLayout.Constraint.FIRST);
			layout.addChild(selectAlignY, PTupleLayout.Constraint.SECOND);
		}
		
		public void defaultRender(PRenderer renderer) {
			renderer.setColor(color);
			renderer.drawQuad(getBounds());
		}
		
		public PSize getDefaultPreferredSize() {
			if (prefSize == null) {
				return super.getDefaultPreferredSize();
			}
			return prefSize;
		}
		
		private void updateAlignment() {
			if (getParent() == null) {
				return;
			}
			AlignmentX alignX = (AlignmentX) selectAlignX.getList().getSelectedValue();
			AlignmentY alignY = (AlignmentY) selectAlignY.getList().getSelectedValue();
			GridConstraint oldConstraint = (GridConstraint) getConstraintsAtParent();
			if (alignX == oldConstraint.getAlignX() 
					&& alignY == oldConstraint.getAlignY()) 
			{
				return;
			}
			
			GridConstraint newConstraint = new GridConstraint();
			newConstraint.x(oldConstraint.getX());
			newConstraint.y(oldConstraint.getY());
			newConstraint.w(oldConstraint.getW());
			newConstraint.h(oldConstraint.getH());
			newConstraint.alignX(alignX);
			newConstraint.alignY(alignY);
			updateGridPanelConstraint(this, newConstraint);
		}
		
		protected void onThisLaidOut(Object constraint) {
			GridConstraint c = (GridConstraint) constraint;
			selectAlignX.getList().setSelected(c.getAlignX());
			selectAlignY.getList().setSelected(c.getAlignY());
		}
		
	}
	
}