package edu.udo.piq.tests.swing;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.collections.PListModel;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PBorderLayout.BorderLayoutConstraint;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.scroll2.PScrollPanel;

public class SwingPTest_ScrollBar extends AbstractSwingPTest {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new SwingPTest_ScrollBar();
	}
	
	private PList list;
	
	public SwingPTest_ScrollBar() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PWrapLayout(btnPnl, ListAlignment.CENTERED_LEFT_TO_RIGHT));
		bodyPnl.addChild(btnPnl, BorderLayoutConstraint.BOTTOM);
		
		PButton btnAdd = new PButton();
		btnAdd.setContent(new PLabel(new DefaultPTextModel("Add")));
		btnPnl.addChild(btnAdd, null);
		
		PButton btnRemove = new PButton();
		btnRemove.setContent(new PLabel(new DefaultPTextModel("Remove")));
		btnPnl.addChild(btnRemove, null);
		
		PScrollPanel scrlPnl = new PScrollPanel();
		bodyPnl.addChild(scrlPnl, BorderLayoutConstraint.CENTER);
		
		list = new PList();
		scrlPnl.setBody(list);
		
		btnAdd.addObs((PClickObs) (cmp) -> addElement());
		btnRemove.addObs((PClickObs) (cmp) -> removeElement());
		
		for (int i = 0; i < 25 + 1; i++) {
			addElement();
		}
		
//		PPanel bodyPnl2 = new PPanel();
//		bodyPnl2.setLayout(new PFreeLayout(bodyPnl2));
//		root.setBody(bodyPnl2);
//
//		PScrollPanel scrlPnl2 = new PScrollPanel();
//		bodyPnl2.addChild(scrlPnl2, new PFreeLayout.FreeConstraint(31, 46, 256, 256));
//
//		PPicture content = new PPicture();
//		content.getModel().setImageID("Tex3.png");
//		content.setStretchToSize(false);
//		scrlPnl2.setBody(content);
	}
	
	private int counter = 0;
	
	private void addElement() {
		String name = "Ein langer Element Name Nummer " + (counter++);
		PListModel model = list.getModel();
		model.add(model.getSize(), name);
	}
	
	private void removeElement() {
		if (list.getModel().canRemove(0)) {
			list.getModel().remove(0);
		}
	}
	
}