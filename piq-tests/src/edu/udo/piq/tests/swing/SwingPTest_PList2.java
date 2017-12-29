package edu.udo.piq.tests.swing;

import edu.udo.piq.PInsets;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.PSliderModel;
import edu.udo.piq.components.PSliderModelObs;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.PSpinnerModelObs;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.containers.PDropDownList;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPDnDSupport;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPSliderModel;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PGridLayout.GridConstraint;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.ImmutablePInsets;

public class SwingPTest_PList2 extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PList2();
	}
	
	public SwingPTest_PList2() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		Object[] elements = new Object[] {
			"A",
			"B",
			"C",
			"D",
			"E",
			"F",
		};
		
		PList list = new PList(new DefaultPListModel(elements));
		list.setDragAndDropSupport(new DefaultPDnDSupport());
		
//		PScrollPanel scroll = new PScrollPanel(list);
//		bodyPnl.addChild(scroll, PBorderLayout.Constraint.CENTER);
		bodyPnl.addChild(list, PBorderLayout.BorderLayoutConstraint.CENTER);
		
		PPanel ctrlPnl = new PPanel();
		ctrlPnl.setLayout(new PGridLayout(ctrlPnl, 2, 8));
		bodyPnl.addChild(ctrlPnl, PBorderLayout.BorderLayoutConstraint.RIGHT);
		
		ctrlPnl.addChild(new PLabel("List Alignment"), new GridConstraint("0 0 alignY=C"));
		PDropDownList selectAlign = new PDropDownList();
		Object[] alignments = PListLayout.ListAlignment.values();
		selectAlign.getList().setModel(new DefaultPListModel(alignments));
		selectAlign.getList().setSelected(list.getAlignment());
		selectAlign.getList().addObs(new PSelectionObs() {
			@Override
			public void onLastSelectedChanged(PSelection selection,
					PModelIndex prevLastSelected, PModelIndex newLastSelected)
			{
				if (newLastSelected != null) {
					PModel model = selectAlign.getList().getModel();
					ListAlignment alignment = (ListAlignment) model.get(newLastSelected);
					list.setAlignment(alignment);
				}
			}
		});
		ctrlPnl.addChild(selectAlign, new GridConstraint("1 0 alignX=F alignY=C"));
		
		ctrlPnl.addChild(new PLabel("Gap"), new GridConstraint("0 1 alignY=C"));
		PSlider selectGap = new PSlider(new DefaultPSliderModel(list.getGap(), 0, 50));
		selectGap.addObs(new PSliderModelObs() {
			@Override
			public void onValueChanged(PSliderModel model) {
				list.setGap(model.getValue());
			}
		});
		ctrlPnl.addChild(selectGap, new GridConstraint("1 1 alignX=F alignY=C"));
		
		ctrlPnl.addChild(new PLabel("From Top"), new GridConstraint("0 2 alignY=C"));
		PSpinner selectFT = new PSpinner(new PSpinnerModelInt(list.getInsets().getFromTop(), 0, 100));
		ctrlPnl.addChild(selectFT, new GridConstraint("1 2 alignX=F alignY=C"));
		
		ctrlPnl.addChild(new PLabel("From Left"), new GridConstraint("0 3 alignY=C"));
		PSpinner selectFL = new PSpinner(new PSpinnerModelInt(list.getInsets().getFromLeft(), 0, 100));
		ctrlPnl.addChild(selectFL, new GridConstraint("1 3 alignX=F alignY=C"));
		
		ctrlPnl.addChild(new PLabel("From Right"), new GridConstraint("0 4 alignY=C"));
		PSpinner selectFR = new PSpinner(new PSpinnerModelInt(list.getInsets().getFromRight(), 0, 100));
		ctrlPnl.addChild(selectFR, new GridConstraint("1 4 alignX=F alignY=C"));
		
		ctrlPnl.addChild(new PLabel("From Bottom"), new GridConstraint("0 5 alignY=C"));
		PSpinner selectFB = new PSpinner(new PSpinnerModelInt(list.getInsets().getFromBottom(), 0, 100));
		ctrlPnl.addChild(selectFB, new GridConstraint("1 5 alignX=F alignY=C"));
		
		PSpinnerModelObs selectInsetsObs = (model, oldValue) -> {
			int ft = (int) selectFT.getModel().getValue();
			int fl = (int) selectFL.getModel().getValue();
			int fr = (int) selectFR.getModel().getValue();
			int fb = (int) selectFB.getModel().getValue();
			PInsets insets = new ImmutablePInsets(ft, fb, fl, fr);
			list.setInsets(insets);
		};
		selectFT.addObs(selectInsetsObs);
		selectFL.addObs(selectInsetsObs);
		selectFR.addObs(selectInsetsObs);
		selectFB.addObs(selectInsetsObs);
		
		PTextField inputAdd = new PTextField();
		ctrlPnl.addChild(inputAdd, new GridConstraint("1 6 alignX=F"));
		
		PButton btnAdd = new PButton(new PLabel("Add"));
		ctrlPnl.addChild(btnAdd, new GridConstraint("0 6 alignX=F"));
		btnAdd.addObs((PClickObs) clickable -> {
			String str = inputAdd.getText();
			if (str == null || str.trim().isEmpty()) {
				return;
			}
			PModelIndex index = list.getLastSelectedIndex();
			if (index == null) {
				index = new PListIndex(list.getModel().getSize());
			}
			list.getModel().add(index, str);
		});
		
		PButton btnRemove = new PButton(new PLabel("Remove"));
		ctrlPnl.addChild(btnRemove, new GridConstraint("0 7 alignX=F"));
		btnRemove.addObs((PClickObs) clickable -> {
			PModelIndex index = list.getLastSelectedIndex();
			if (index == null) {
				return;
			}
			list.getModel().remove(index);
		});
	}
	
}