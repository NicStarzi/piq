package edu.udo.piq.swing.tests;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBoxModelObs;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.containers.PLineBorder;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PTuple;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.defaults.PSpinnerModelList;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.layouts.PTupleLayout.Distribution;
import edu.udo.piq.layouts.PTupleLayout.Orientation;

public class SwingPTest_Tuple extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_Tuple();
	}
	
	public SwingPTest_Tuple() {
		super(480, 320);
	}
	
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PButton p1 = new PButton();
		PButton p2 = new PButton();
		p2.setContent(new PTextField("Langer Text"));
		p2.setIgnoreClickOnChildren(true);
		PTuple t1 = new PTuple(p1, p2);
		bodyPnl.addChild(t1, new FreeConstraint(24, 73, 200, 200));
		
		PLineBorder b = new PLineBorder(1);
		b.setElusive(true);
		bodyPnl.addChild(b, bodyPnl.getLayout().getChildConstraint(t1));
		
		PSpinner s1 = new PSpinner(new PSpinnerModelList(
				t1.getLayout().getDistribution(), Distribution.values()));
		bodyPnl.addChild(s1, new FreeConstraint(250, 50, 200, 24));
		
		PSpinner s2 = new PSpinner(new PSpinnerModelList(
				t1.getLayout().getSecondaryDistribution(), Distribution.values()));
		bodyPnl.addChild(s2, new FreeConstraint(250, 100, 200, 24));
		
		PCheckBoxTuple s3 = new PCheckBoxTuple(new PLabel(new DefaultPTextModel() {
			public String getText() {
				return t1.getLayout().getOrientation().toString();
			}
		}));
		bodyPnl.addChild(s3, new FreeConstraint(250, 150, 200, 24));
		
		s1.getModel().addObs((mdl, old) -> 
			t1.getLayout().setDistribution((Distribution) mdl.getValue())
		);
		s2.getModel().addObs((mdl, old) -> 
			t1.getLayout().setSecondaryDistribution((Distribution) mdl.getValue())
		);
		s3.addObs((PCheckBoxModelObs) (mdl) -> {
			if (mdl.isChecked()) {
				t1.getLayout().setOrientation(Orientation.TOP_TO_BOTTOM);
			} else {
				t1.getLayout().setOrientation(Orientation.LEFT_TO_RIGHT);
			}
		});
		p1.addObs((PClickObs) (btn) -> System.out.println("BTN 1"));
		p2.addObs((PClickObs) (btn) -> System.out.println("BTN 2"));
	}
	
}