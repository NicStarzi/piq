package edu.udo.piq.tests.swing;

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
import edu.udo.piq.style.standard.StandardStyleSheet;

public class SwingPTest_PAnchorLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PAnchorLayout();
	}
	
	public SwingPTest_PAnchorLayout() {
		super(480, 320);
	}
	
	@Override
	public void buildGUI() {
		root.setStyleSheet(new StandardStyleSheet());
		
		PPanel bodyPnl = new PPanel();
		PGridLayout gridLayout = new PGridLayout(bodyPnl, 2, 4);
		gridLayout.setColumnGrowth(0, Growth.MAXIMIZE);
		gridLayout.setRowGrowth(3, Growth.MAXIMIZE);
		bodyPnl.setLayout(gridLayout);
		root.setBody(bodyPnl);
		
		PAnchorPanel ancPnl = new PAnchorPanel();
		ancPnl.setContent(new PButton(new PLabel("Button")));
		bodyPnl.addChild(ancPnl, "0 0 1 4 alignX=F alignY=F");
		
		ancPnl.setBorder(new PLineBorder(1));
//		ancPnl.setBorder(new PBevelBorder());
		
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