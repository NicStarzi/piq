package edu.udo.piq.tests.swing;

import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.AbstractPComponentAction;
import edu.udo.piq.actions.ImmutableAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.collections.PSelectionComponent;
import edu.udo.piq.components.collections.list.PList;
import edu.udo.piq.components.collections.list.PListLike;
import edu.udo.piq.components.collections.list.PListModel;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PSplitPanel;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.popup.ImmutablePActionIndicator;
import edu.udo.piq.components.popup.PComponentActionIndicator;
import edu.udo.piq.components.popup.PMenuBar;
import edu.udo.piq.components.popup.PMenuBarItem;
import edu.udo.piq.components.popup.PMenuDivider;
import edu.udo.piq.components.popup.PPopup;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PSplitLayout.Orientation;
import edu.udo.piq.layouts.PWrapLayout;

public class SwingPTest_PPopup extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PPopup();
	}
	
	public SwingPTest_PPopup() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PSplitPanel splitX = new PSplitPanel(Orientation.HORIZONTAL);
		PSplitPanel splitY = new PSplitPanel(Orientation.VERTICAL);
		PPanel pnlComps = new PPanel();
		pnlComps.setLayout(new PWrapLayout(pnlComps, ListAlignment.CENTERED_TOP_TO_BOTTOM));
		
		PList listGreek = new PList(new DefaultPListModel("Alpha", "Beta", "Gamma", "Delta", "Omega"));
		PComponentAction actTest = new AbstractPComponentAction(
				new PAccelerator(ActualKey.F, Modifier.ALT_GRAPH, FocusPolicy.ALWAYS))
		{
			@Override
			public boolean isEnabled(PRoot root) {
				return listGreek.isEnabled()
						&& listGreek.getModel() != null
						&& listGreek.getModel().getSize() > 0;
			}
			@Override
			public void tryToPerform(PRoot root) {
				PListModel model = listGreek.getModel();
				Object content = model.get(0);
				for (int i = 0; i < 5; i++) {
					model.add(content);
				}
			}
			@Override
			public AbstractPComponentAction clone() {
				throw new UnsupportedOperationException();
			}
		};
		PComponentActionIndicator indTest = new ImmutablePActionIndicator(new PActionKey("Test1"), "Copy first element 5 times");
		listGreek.clearActionMap();
		listGreek.addActionMapping(indTest.getActionKey(), actTest);
		
		PList listNames = new PList(new DefaultPListModel("Angela", "Banjo", "Carla", "Diego", "Erik"));
		PButton btnClick = new PButton("Click me!");
		PTextField txtField = new PTextField("Hello World! how nice it is to see you again.");
		txtField.addActionMapping(new PActionKey("Test2"), new ImmutableAction(
				new PAccelerator(ActualKey.ESCAPE, FocusPolicy.ALWAYS),
				root -> txtField.isEditable()
						&& txtField.getSelection() != null
						&& txtField.getSelection().hasSelection()
						&& txtField.getModel() != null,
				root -> {
					int low = txtField.getSelection().getLowestSelectedIndex().getIndexValue();
					int high = txtField.getSelection().getHighestSelectedIndex().getIndexValue();
					
					String oldText = txtField.getModel().getText();
					String newText = oldText.substring(0, low) + "<Bla>" + oldText.substring(high);
					txtField.getModel().setValue(newText);
					
					int newSelIdx = low + "<Bla>".length();
					txtField.getSelection().setSelectionToRange(newSelIdx, newSelIdx);
				})
		);
//		txtField.removeActionMapping(PTextInput.KEY_BACKSPACE);
//		txtField.removeActionMapping(PTextInput.KEY_DELETE);
		PCheckBoxTuple chkBx = new PCheckBoxTuple(new PLabel("Check it!"));
		
		pnlComps.addChild(btnClick, null);
		pnlComps.addChild(txtField, null);
		pnlComps.addChild(chkBx, null);
		
		splitY.setFirstComponent(pnlComps);
		splitY.setSecondComponent(listGreek);
		
		splitX.setFirstComponent(listNames);
		splitX.setSecondComponent(splitY);
		
		root.setBody(splitX);
		
		PMenuBar menuBar = new PMenuBar();
		menuBar.setID("menuBar");
		
		PMenuBarItem itemFile = new PMenuBarItem("File");
		itemFile.setID("file");
		itemFile.addMenuItem(PSelectionComponent.INDICATOR_COPY);
		itemFile.addMenuItem(PSelectionComponent.INDICATOR_PASTE);
		itemFile.addMenuItem(PSelectionComponent.INDICATOR_CUT);
		itemFile.addMenuItem(new PMenuDivider());
		itemFile.addMenuItem(PSelectionComponent.INDICATOR_DELETE);
		menuBar.addMenuItem(itemFile);
		
		PMenuBarItem itemEdit = new PMenuBarItem("Edit");
		itemEdit.setID("edit");
		itemEdit.addMenuItem(new ImmutablePActionIndicator(PListLike.KEY_PREV, "Move Selection Up"));
		itemEdit.addMenuItem(new ImmutablePActionIndicator(PListLike.KEY_NEXT, "Move Selection Down"));
		itemEdit.addMenuItem(new PMenuDivider());
		itemEdit.addMenuItem(PSelectionComponent.INDICATOR_COPY);
		itemEdit.addMenuItem(PSelectionComponent.INDICATOR_PASTE);
		itemEdit.addMenuItem(PSelectionComponent.INDICATOR_CUT);
		itemEdit.addMenuItem(new PMenuDivider());
		itemEdit.addMenuItem(PSelectionComponent.INDICATOR_DELETE);
		itemEdit.addMenuItem(indTest);
		menuBar.addMenuItem(itemEdit);
		
		PMenuBarItem itemView = new PMenuBarItem("View");
		itemView.setID("view");
		itemView.addMenuItem(PSelectionComponent.INDICATOR_COPY);
		itemView.addMenuItem(PSelectionComponent.INDICATOR_PASTE);
		itemView.addMenuItem(PSelectionComponent.INDICATOR_CUT);
		itemView.addMenuItem(new PMenuDivider());
		itemView.addMenuItem(PSelectionComponent.INDICATOR_DELETE);
		menuBar.addMenuItem(itemView);
		
		root.setMenuBar(menuBar);
		root.setID("root");
		
		PPopup popupNames = new PPopup(listNames);
		popupNames.addItem(PSelectionComponent.INDICATOR_COPY);
		popupNames.addItem(PSelectionComponent.INDICATOR_PASTE);
		popupNames.addItem(PSelectionComponent.INDICATOR_CUT);
		popupNames.addDivider();
		popupNames.addItem(PSelectionComponent.INDICATOR_DELETE);
		popupNames.addItem("Hello", null, root -> {
			System.out.println("Hello World!");
		});
		
		PPopup popupGreek = new PPopup(listGreek);
		popupGreek.addItem(PSelectionComponent.INDICATOR_COPY);
		popupGreek.addItem(PSelectionComponent.INDICATOR_PASTE);
		popupGreek.addItem(PSelectionComponent.INDICATOR_CUT);
		popupGreek.addDivider();
		popupGreek.addItem(PSelectionComponent.INDICATOR_DELETE);
		popupGreek.addItem(indTest);
		
		PPopup popupTxtField = new PPopup(txtField);
		popupTxtField.addItem(PSelectionComponent.INDICATOR_COPY);
		popupTxtField.addItem(PSelectionComponent.INDICATOR_PASTE);
		popupTxtField.addItem(PSelectionComponent.INDICATOR_CUT);
		popupTxtField.addDivider();
		popupTxtField.addItem(PSelectionComponent.INDICATOR_DELETE);
	}
}