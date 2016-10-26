package edu.udo.piq.swing.tests;

import edu.udo.piq.borders.PLineBorder;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.containers.PAnchorPanel;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.PSpinnerModelList;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PGridLayout.Growth;

public class SwingPTest_PAnchorLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PAnchorLayout();
	}
	
	public SwingPTest_PAnchorLayout() {
		super(480, 320);
	}
	
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		PGridLayout gridLayout = new PGridLayout(bodyPnl, 2, 4);
		gridLayout.setColumnGrowth(0, Growth.MAXIMIZE);
		gridLayout.setRowGrowth(3, Growth.MAXIMIZE);
		bodyPnl.setLayout(gridLayout);
		root.setBody(bodyPnl);
		
		PAnchorPanel ancPnl = new PAnchorPanel();
		ancPnl.setContent(new PButton(new PLabel("Button")));
		bodyPnl.addChild(ancPnl, "0 0 1 6 alignX=F alignY=F");
		
//		PLineBorder border = new PLineBorder(1);
//		border.setElusive(true);
//		PGuiUtil.addBorderTo(ancPnl, border);
		ancPnl.setBorder(new PLineBorder(1));
		
		PSpinner selectAlignX = new PSpinner(
				new PSpinnerModelList(AlignmentX.ALL, ancPnl.getAlignmentX()));
		bodyPnl.addChild(selectAlignX, "1 0 alignX=F");
		
		PSpinner selectAlignY = new PSpinner(
				new PSpinnerModelList(AlignmentY.ALL, ancPnl.getAlignmentY()));
		bodyPnl.addChild(selectAlignY, "1 1 alignX=F");
		
		selectAlignX.getModel().addObs((mdl, old) ->
			ancPnl.getLayout().setAlignmentX((AlignmentX) mdl.getValue())
		);
		selectAlignY.getModel().addObs((mdl, old) ->
			ancPnl.getLayout().setAlignmentY((AlignmentY) mdl.getValue())
		);
	}
	
}