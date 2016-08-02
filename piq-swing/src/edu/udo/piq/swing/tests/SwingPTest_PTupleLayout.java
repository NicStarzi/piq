package edu.udo.piq.swing.tests;

import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBoxModelObs;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.PSliderModel;
import edu.udo.piq.components.PSliderModelObs;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.containers.PLineBorder;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PTuple;
import edu.udo.piq.components.defaults.DefaultPSliderModel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.defaults.PSpinnerModelList;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.layouts.PTupleLayout.Distribution;
import edu.udo.piq.layouts.PTupleLayout.Orientation;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PGuiUtil;

public class SwingPTest_PTupleLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PTupleLayout();
	}
	
	public SwingPTest_PTupleLayout() {
		super(480, 320);
	}
	
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		PGridLayout gridLayout = new PGridLayout(bodyPnl, 2, 5);
		gridLayout.setColumnGrowth(0, Growth.MAXIMIZE);
		gridLayout.setRowGrowth(4, Growth.MAXIMIZE);
		bodyPnl.setLayout(gridLayout);
		root.setBody(bodyPnl);
		
		PComponent p1 = new PButton(new PLabel("First")) {
			// This component gets a fixed preferred size to showcase the different distributions
			final PSize SIZE = new ImmutablePSize(50, 50);
			public PSize getDefaultPreferredSize() {
				if (SIZE == null) {
					return new ImmutablePSize(1, 1);
				}
				return SIZE;
			}
		};
		PComponent p2 = new PButton(new PLabel("Second"));
		PTuple tuple = new PTuple(p1, p2);
		bodyPnl.addChild(tuple, "0 0 1 5 alignX=F alignY=F");
		
		PLineBorder border = new PLineBorder(1);
		border.setElusive(true);
		PGuiUtil.addBorderTo(tuple, border);
		
		Distribution distPrim = tuple.getLayout().getDistribution();
		PSpinner selectDistr = new PSpinner(
				new PSpinnerModelList(Distribution.ALL, distPrim));
		bodyPnl.addChild(selectDistr, "1 0 alignX=F");
		
		Distribution distScnd = tuple.getLayout().getSecondaryDistribution();
		PSpinner selectScndDistr = new PSpinner(
				new PSpinnerModelList(Distribution.ALL, distScnd));
		bodyPnl.addChild(selectScndDistr, "1 1 alignX=F");
		
		PSlider selectGap = new PSlider(
				new DefaultPSliderModel(tuple.getLayout().getGap(), 0, 100));
		bodyPnl.addChild(selectGap, "1 2 alignX=F");
		
		PCheckBoxTuple selectOri = new PCheckBoxTuple(
				new PLabel(new DefaultPTextModel() {
			public String getText() {
				return tuple.getLayout().getOrientation().toString();
			}
		}));
		bodyPnl.addChild(selectOri, "1 3 alignX=F");
		
		selectDistr.getModel().addObs((mdl, old) -> 
			tuple.getLayout().setDistribution((Distribution) mdl.getValue())
		);
		selectScndDistr.getModel().addObs((mdl, old) -> 
			tuple.getLayout().setSecondaryDistribution((Distribution) mdl.getValue())
		);
		selectGap.addObs(new PSliderModelObs() {
			public void onValueChanged(PSliderModel model) {
				tuple.getLayout().setGap(model.getValue());
			}
		});
		selectOri.addObs((PCheckBoxModelObs) (mdl) -> {
			if (mdl.isChecked()) {
				tuple.getLayout().setOrientation(Orientation.TOP_TO_BOTTOM);
			} else {
				tuple.getLayout().setOrientation(Orientation.LEFT_TO_RIGHT);
			}
		});
	}
	
}