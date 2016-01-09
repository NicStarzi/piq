package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PGlobalEventObs;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PCheckBoxObs;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PPopupButton;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.PProgressBar;
import edu.udo.piq.components.PProgressBarModel;
import edu.udo.piq.components.PProgressBarModelObs;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.PSliderModel;
import edu.udo.piq.components.PSliderModelObs;
import edu.udo.piq.components.PStraightLine;
import edu.udo.piq.components.PStraightLine.LineOrientation;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.containers.PDropDownList;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PSplitPanel;
import edu.udo.piq.components.containers.PToolTip;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextArea;
import edu.udo.piq.components.util.PPopup;
import edu.udo.piq.components.util.PPopupObs;
import edu.udo.piq.components.util.PPopupOptionsProvider;
import edu.udo.piq.designs.standard.PStandardDesignSheet;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PSplitLayout.Orientation;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.scroll.PScrollPanel;
import edu.udo.piq.swing.JCompPRoot;
import edu.udo.piq.tools.AbstractPTextModel;

public class SwingPTest {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest window = new SwingPTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	private final PStandardDesignSheet sDs = new PStandardDesignSheet();
	
	public SwingPTest() {
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		root = new JCompPRoot();
		root.setDesignSheet(sDs);
//		root.addObs(new PKeyboardObs() {
//			public void keyTriggered(PKeyboard keyboard, Key key) {
//				if (key == Key.TAB) {
//					root.getFocusTraversal().focusNext();
//					System.out.println("###############################################");
//					System.out.println("# FocusOwner="+root.getFocusOwner()+" #");
//					System.out.println("###############################################");
//				}
//			}
//		});
		frame.setContentPane(root.getPanel());
		root.addObs(new PGlobalEventObs() {
			public void onGlobalEvent(PComponent source, Object eventData) {
				if ("CreateNew".equals(eventData)) {
					System.out.println("Create New !!!");
				} else {
					System.out.println("UnknownGlobalEvent: "+eventData);
				}
			}
		});
		
		
		Timer updateTimer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root.update();
			}
		});
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
		updateTimer.start();
		
		PPanel bodyPnl = new PPanel();
		bodyPnl.setID("Body Panel");
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PSplitPanel splitV = new PSplitPanel();
		splitV.setID("SplitPanel Vertical");
		splitV.setOrientation(Orientation.VERTICAL);
		bodyPnl.addChild(splitV, PBorderLayout.Constraint.CENTER);
		
		PSplitPanel splitH = new PSplitPanel();
		splitH.setID("SplitPanel Horizontal");
		splitH.setOrientation(Orientation.HORIZONTAL);
		splitV.setFirstComponent(splitH);
		
		PPicture pic = new PPicture();
		pic.setID("Picture");
		pic.getModel().setImagePath("Tex.png");
		pic.setStretchToSize(true);
		splitH.setFirstComponent(pic);
		
		PList list = new PList(new DefaultPListModel(new Object[] {
				"A", "B", "C", "D",
				"E", "F", "G", "H",
			}));
		list.setID("List");
		PScrollPanel scrollList = new PScrollPanel();
		scrollList.setBody(list);
		splitH.setSecondComponent(scrollList);
//		splitH.setSecondComponent(list);
		
		PToolTip tipList = new PToolTip(new DefaultPTextModel("This is a nice list!"));
		tipList.setID("Tooltip - List");
		tipList.setTooltipComponent(list);
		
		PToolTip tipPic = new PToolTip();
		tipPic.setID("Tooltip - Picture");
		tipPic.setShowDelay(120);
		tipPic.setTooltipComponent(pic);
		PButton tipPicBtn = new PButton();
		tipPicBtn.setContent(new PLabel(new DefaultPTextModel("Tooltip Button")));
		tipPicBtn.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				System.out.println("You clicked a button inside a tooltip!");
			}
		});
		tipPic.setContent(tipPicBtn);
		
//		final DefaultPTableModel tableModel = new DefaultPTableModel(new Object[][] {
//				{"John", "Smith", "001"},
//				{"Marry", "Sue", "003"},
//				{"Jane", "Doe", "005"},
//				{"Joe", "Schmo", "007"},
//		});
//		
//		PTable table = new PTable();
//		table.setModel(tableModel);
//		table.getSelection().setSelectionMode(SelectionMode.SINGLE_CELL);
//		splitV.setSecondComponent(table);
		
		PTextArea txtAr = new PTextArea("This is \n a simple test "
				+ "\nto see whether the PTextArea class \nworks as intended.");
		PScrollPanel scrollTxtAr = new PScrollPanel();
		txtAr.setID("TextArea");
		txtAr.setEditable(true);
		scrollTxtAr.setBody(txtAr);
		splitV.setSecondComponent(scrollTxtAr);
		
		PPopup popupTxtAr = new PPopup(txtAr);
		popupTxtAr.addObs(new PPopupObs() {
			public void onPopupShown(PPopup popup, PComponent popupComp) {
				System.out.println("onPopupShown");
			}
			public void onPopupHidden(PPopup popup, PComponent popupComp) {
				System.out.println("onPopupHidden");
			}
		});
		popupTxtAr.setOptionsProvider(new PPopupOptionsProvider() {
			public List<PComponent> createOptions(PComponent component) {
				List<PComponent> result = new ArrayList<>();
				
				PPopupButton btnNew = new PPopupButton();
				btnNew.setGlobalEventProvider((btn) -> "CreateNew");
				btnNew.setContent(new PLabel("New"));
				btnNew.addObs((PButton btn) -> System.out.println("New!"));
				result.add(btnNew);
				
				PStraightLine sep1 = new PStraightLine(LineOrientation.HORIZONTAL);
				result.add(sep1);
				
				PPopupButton btnEdit = new PPopupButton();
				btnEdit.setContent(new PLabel("Edit"));
				btnEdit.addObs((PButton btn) -> System.out.println("Edit!"));
				result.add(btnEdit);
				
				PCheckBoxTuple chkRmb = new PCheckBoxTuple();
				chkRmb.setSecondComponent(new PLabel("Remember"));
				result.add(chkRmb);
				
				PStraightLine sep2 = new PStraightLine(LineOrientation.HORIZONTAL);
				result.add(sep2);
				
				PPopupButton btnDelete = new PPopupButton();
				btnDelete.setContent(new PLabel("Delete"));
				btnDelete.addObs((PButton btn) -> System.out.println("Delete!"));
				result.add(btnDelete);
				
				return result;
			}
		});
		popupTxtAr.setEnabled(true);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setID("Button Panel");
		btnPnl.setLayout(new PWrapLayout(btnPnl, ListAlignment.FROM_LEFT));
//		btnPnl.setLayout(new PListLayout(btnPnl, ListAlignment.FROM_LEFT));
		bodyPnl.addChild(btnPnl, PBorderLayout.Constraint.BOTTOM);
		
		final PButton btnChange = new PButton();
		btnChange.setGlobalEventProvider((btn) -> "CreateNew");
		btnChange.setID("Change Button");
		btnChange.setContent(new PSlider());
//		btnChange.setContent(new PLabel(new DefaultPTextModel("Change")));
		btnPnl.addChild(btnChange, null);
		
		final PProgressBar prgBar = new PProgressBar();
		prgBar.setID("ProgressBar");
		prgBar.getModel().setMaximum(17);
		btnPnl.addChild(prgBar, null);
		
		final PLabel lblChkBx = new PLabel();
		lblChkBx.setID("CheckBox Label");
		final PCheckBoxTuple chkBxTpl = new PCheckBoxTuple(lblChkBx);
		chkBxTpl.setID("CheckBoxTuple");
		btnPnl.addChild(chkBxTpl, null);
		
		final PSlider sld = new PSlider();
		sld.setID("Absolute/Relative Slider");
		sld.getModel().setMinValue(13);
		sld.getModel().setMaxValue(77);
		btnPnl.addChild(sld, null);
		
		final PLabel lblSld = new PLabel();
		lblSld.setID("Slider Label");
		btnPnl.addChild(lblSld, null);
		
		final PLabel lblDd = new PLabel();
		lblDd.setID("DropDownList - Preview Label");
		lblDd.getModel().setValue("Drop Down");
		
		PDropDownList ddl = new PDropDownList();
		ddl.setID("DropDownList");
		String[] elems = new String[] {"one", "two", "three", "four"};
		for (String s : elems) {
			ddl.getList().getModel().add(ddl.getList().getModel().getSize(), s);
		}
		btnPnl.addChild(ddl, null);
		
		btnChange.addObs(new PButtonObs() {
			boolean increment = true;
			
			PColor[] fontColors = new PColor[] {
					PColor.BLACK, PColor.RED, PColor.GREY25, 
					PColor.BLUE, PColor.MAGENTA, PColor.YELLOW, 
					PColor.GREEN, PColor.TEAL, PColor.GREY50, 
					PColor.GREY75, };
			int currentStyle = 0;
			int currentColor = 0;
			
			public void onClick(PButton button) {
				int val = prgBar.getModel().getValue();
				val = increment ? val + 1 : val - 1;
				prgBar.getModel().setValue(val);
				if (prgBar.getModel().getValue() == prgBar.getModel().getMaxValue() || prgBar.getModel().getValue() == 0) {
					increment = !increment;
				}
				
				Style style = Style.values()[(++currentStyle) % Style.values().length];
				int size = sDs.getLabelDesign().getFontSize() + 1;
				int colorID = (++currentColor) % fontColors.length;
				PColor fontColor = fontColors[colorID];
				PColor backColor = fontColors[fontColors.length - 1 - colorID];
				sDs.getLabelDesign().setFontColor(fontColor);
				sDs.getLabelDesign().setBackgroundColor(backColor);
				sDs.getLabelDesign().setFontSize(size);
				sDs.getLabelDesign().setFontStyle(style);
				root.reRender(root);
			}
		});
		chkBxTpl.getCheckBox().addObs(new PCheckBoxObs() {
			public void onClick(PCheckBox checkBox) {
				lblChkBx.getModel().setValue(null);
				lblSld.getModel().setValue(null);
			}
		});
		sld.getModel().addObs(new PSliderModelObs() {
			public void valueChanged(PSliderModel model) {
				lblSld.getModel().setValue(null);
			}
			public void rangeChanged(PSliderModel model) {
				lblSld.getModel().setValue(null);
			}
		});
		lblChkBx.setModel(new AbstractPTextModel() {
			public void setValue(Object text) {
				fireTextChangeEvent();
			}
			public Object getValue() {
				return null;
			}
			public String getText() {
				if (chkBxTpl.isChecked()) {
					return "Relative";
				}
				return "Absolute";
			}
		});
		lblSld.setModel(new AbstractPTextModel() {
			public void setValue(Object text) {
				fireTextChangeEvent();
			}
			public Object getValue() {
				return null;
			}
			public String getText() {
				if (chkBxTpl.isChecked()) {
					double percent = sld.getModel().getValuePercent();
					double val = ((int) (10000 * percent)) / 100.0;
					return val+"%";
				}
				return Integer.toString(sld.getModel().getValue());
			}
		});
		prgBar.getModel().addObs(new PProgressBarModelObs() {
			public void valueChanged(PProgressBarModel model) {
				if (model.getValue() == model.getMaxValue()) {
//					PDialog dlg = prgBar.getRoot().createDialog();
//					new MyLittleDialog(dlg);
				}
			}
		});
	}
//	public static class MyLittleDialog {
//		
//		public MyLittleDialog(final PDialog dlg) {
//			dlg.setLayout(new PBorderLayout(dlg));
//			
//			PPanel pnlBody = new PPanel();
//			pnlBody.setLayout(new PBorderLayout(pnlBody));
//			dlg.getLayout().addChild(pnlBody, PBorderLayout.Constraint.CENTER);
//			
//			PLabel lblBodyContent = new PLabel();
//			lblBodyContent.getModel().setText("This is a dialog body!");
//			pnlBody.addChild(lblBodyContent, PBorderLayout.Constraint.CENTER);
//			
//			PPanel pnlButtons = new PPanel();
//			pnlButtons.setLayout(new PListLayout(pnlButtons, ListAlignment.CENTERED_HORIZONTAL));
//			dlg.getLayout().addChild(pnlButtons, PBorderLayout.Constraint.BOTTOM);
//			
//			PLabel lblOkayBtn = new PLabel();
//			lblOkayBtn.getModel().setText("OK");
//			
//			PButton btnOkay = new PButton();
//			btnOkay.setContent(lblOkayBtn);
//			btnOkay.addObs(new PButtonObs() {
//				public void onClick(PButton button) {
//					System.out.println("OK!");
//					dlg.dispose();
//				}
//			});
//			pnlButtons.addChild(btnOkay, null);
//			
//			dlg.show();
//		}
//		
//	}
	
}