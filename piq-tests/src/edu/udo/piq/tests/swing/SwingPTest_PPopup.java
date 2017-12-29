package edu.udo.piq.tests.swing;

import edu.udo.piq.actions.ActionCopyToClipboard;
import edu.udo.piq.actions.ActionCutToClipboard;
import edu.udo.piq.actions.ActionDelete;
import edu.udo.piq.actions.ActionPasteFromClipboard;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PSplitPanel;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.popup2.PMenuBar;
import edu.udo.piq.components.popup2.PMenuBarItem;
import edu.udo.piq.components.popup2.PMenuDivider;
import edu.udo.piq.components.popup2.PMenuItem;
import edu.udo.piq.components.popup2.PPopup;
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
		listGreek.addActionMapping(ActionCopyToClipboard.DEFAULT_KEY, ActionCopyToClipboard.INSTANCE);
		listGreek.addActionMapping(ActionPasteFromClipboard.DEFAULT_KEY, ActionPasteFromClipboard.INSTANCE);
		listGreek.addActionMapping(ActionCutToClipboard.DEFAULT_KEY, ActionCutToClipboard.INSTANCE);
		listGreek.addActionMapping(ActionDelete.DEFAULT_KEY, ActionDelete.INSTANCE);
		
		PList listNames = new PList(new DefaultPListModel("Angela", "Banjo", "Carla", "Diego", "Erik"));
		listNames.addActionMapping(ActionCopyToClipboard.DEFAULT_KEY, ActionCopyToClipboard.INSTANCE);
		listNames.addActionMapping(ActionPasteFromClipboard.DEFAULT_KEY, ActionPasteFromClipboard.INSTANCE);
		listNames.addActionMapping(ActionCutToClipboard.DEFAULT_KEY, ActionCutToClipboard.INSTANCE);
		
		PButton btnClick = new PButton("Click me!");
		PTextField txtField = new PTextField("...");
		txtField.addActionMapping(ActionCopyToClipboard.DEFAULT_KEY, ActionCopyToClipboard.INSTANCE);
		txtField.addActionMapping(ActionPasteFromClipboard.DEFAULT_KEY, ActionPasteFromClipboard.INSTANCE);
		txtField.addActionMapping(ActionCutToClipboard.DEFAULT_KEY, ActionCutToClipboard.INSTANCE);
		txtField.addActionMapping(ActionDelete.DEFAULT_KEY, ActionDelete.INSTANCE);
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
		itemFile.addMenuItem(ActionCopyToClipboard.DEFAULT_INDICATOR);
		itemFile.addMenuItem(ActionPasteFromClipboard.DEFAULT_INDICATOR);
		itemFile.addMenuItem(ActionCutToClipboard.DEFAULT_INDICATOR);
		itemFile.addMenuItem(new PMenuDivider());
		itemFile.addMenuItem(ActionDelete.DEFAULT_INDICATOR);
		itemFile.addMenuItem(ActionCopyToClipboard.DEFAULT_INDICATOR);
		menuBar.addMenuItem(itemFile);
		
		PMenuBarItem itemEdit = new PMenuBarItem("Edit");
		itemEdit.setID("edit");
		itemEdit.addMenuItem(ActionCopyToClipboard.DEFAULT_INDICATOR);
		itemEdit.addMenuItem(ActionPasteFromClipboard.DEFAULT_INDICATOR);
		itemEdit.addMenuItem(ActionCutToClipboard.DEFAULT_INDICATOR);
		itemEdit.addMenuItem(new PMenuDivider());
		itemEdit.addMenuItem(ActionDelete.DEFAULT_INDICATOR);
		itemEdit.addMenuItem(ActionCopyToClipboard.DEFAULT_INDICATOR);
		menuBar.addMenuItem(itemEdit);
		
		PMenuBarItem itemView = new PMenuBarItem("View");
		itemView.setID("view");
		itemView.addMenuItem(ActionCopyToClipboard.DEFAULT_INDICATOR);
		itemView.addMenuItem(ActionPasteFromClipboard.DEFAULT_INDICATOR);
		itemView.addMenuItem(ActionCutToClipboard.DEFAULT_INDICATOR);
		itemView.addMenuItem(new PMenuDivider());
		itemView.addMenuItem(ActionDelete.DEFAULT_INDICATOR);
		itemView.addMenuItem(ActionCopyToClipboard.DEFAULT_INDICATOR);
		menuBar.addMenuItem(itemView);
		
		root.setMenuBar(menuBar);
		root.setID("root");
		
		PPopup popupNames = new PPopup(listNames);
		popupNames.addItem(ActionCopyToClipboard.DEFAULT_INDICATOR);
		popupNames.addItem(ActionPasteFromClipboard.DEFAULT_INDICATOR);
		popupNames.addItem(ActionCutToClipboard.DEFAULT_INDICATOR);
		popupNames.addItem(new PMenuDivider());
		popupNames.addItem(ActionDelete.DEFAULT_INDICATOR);
		popupNames.addItem(new PMenuItem("Hello", null, root -> {
			System.out.println("Hello World!");
		}));
		
		PPopup popupGreek = new PPopup(listGreek);
		popupGreek.addItem(ActionCopyToClipboard.DEFAULT_INDICATOR);
		popupGreek.addItem(ActionPasteFromClipboard.DEFAULT_INDICATOR);
		popupGreek.addItem(ActionCutToClipboard.DEFAULT_INDICATOR);
		popupGreek.addItem(new PMenuDivider());
		popupGreek.addItem(ActionDelete.DEFAULT_INDICATOR);
		
		PPopup popupTxtField = new PPopup(txtField);
		popupTxtField.addItem(ActionCopyToClipboard.DEFAULT_INDICATOR);
		popupTxtField.addItem(ActionPasteFromClipboard.DEFAULT_INDICATOR);
		popupTxtField.addItem(ActionCutToClipboard.DEFAULT_INDICATOR);
		popupTxtField.addItem(new PMenuDivider());
		popupTxtField.addItem(ActionDelete.DEFAULT_INDICATOR);
	}
}