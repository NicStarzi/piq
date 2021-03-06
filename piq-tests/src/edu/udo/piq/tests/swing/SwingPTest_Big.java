package edu.udo.piq.tests.swing;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PClickable;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.PProgressBar;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.PSliderModel;
import edu.udo.piq.components.PSliderModelObs;
import edu.udo.piq.components.collections.list.PList;
import edu.udo.piq.components.collections.list.PListIndex;
import edu.udo.piq.components.containers.PDropDownList;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PSplitPanel;
import edu.udo.piq.components.containers.PToolTip;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextArea;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PSplitLayout.Orientation;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.scroll2.PScrollPanel;
import edu.udo.piq.tools.AbstractPTextModel;

public class SwingPTest_Big extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_Big();
	}
	
	public SwingPTest_Big() {
		super(480, 320);
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setID("Body Panel");
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PSplitPanel splitV = new PSplitPanel();
		splitV.setID("SplitPanel Vertical");
		splitV.setOrientation(Orientation.VERTICAL);
		bodyPnl.addChild(splitV, PBorderLayout.BorderLayoutConstraint.CENTER);
		
		PSplitPanel splitH = new PSplitPanel();
		splitH.setID("SplitPanel Horizontal");
		splitH.setOrientation(Orientation.HORIZONTAL);
		splitV.setFirstComponent(splitH);
		
		PPicture pic = new PPicture();
		pic.setID("Picture");
		pic.getModel().setValue("Tex.png");
		pic.setAlignmentX(AlignmentX.FILL);
		pic.setAlignmentY(AlignmentY.FILL);
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
		tipList.setShowDelay(1500);
		tipList.setTooltipTarget(list);
		
		PToolTip tipPic = new PToolTip();
		tipPic.setID("Tooltip - Picture");
		tipPic.setShowDelay(120);
		tipPic.setTooltipTarget(pic);
		PButton tipPicBtn = new PButton();
		tipPicBtn.setContent(new PLabel(new DefaultPTextModel("Tooltip Button")));
		tipPicBtn.addObs((PClickObs) button -> System.out.println("You clicked a button inside a tooltip!"));
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
		txtAr.setID("TextArea");
		txtAr.setEditable(true);
		txtAr.getSelection().addSelection(new PListIndex(6));
		PScrollPanel scrollTxtAr = new PScrollPanel();
		scrollTxtAr.setBody(txtAr);
		splitV.setSecondComponent(scrollTxtAr);
		
//		PPopup popupTxtAr = new PPopup(txtAr);
//		popupTxtAr.addObs(new PPopupObs() {
//			@Override
//			public void onPopupShown(PPopup popup, PComponent popupComp) {
//				System.out.println("onPopupShown");
//			}
//			@Override
//			public void onPopupHidden(PPopup popup, PComponent popupComp) {
//				System.out.println("onPopupHidden");
//			}
//		});
////		popupTxtAr.setBodyProvider((comp) -> new PGlassPanel());
////		popupTxtAr.setBorderProvider((comp) -> null);
//		popupTxtAr.setOptionsProvider(new PPopupOptionsProvider() {
//			boolean chkBxVal = false;
//			@Override
//			public List<PComponent> apply(PComponent component) {
//				List<PComponent> result = new ArrayList<>();
//				
//				PPopupButton btnNew = new PPopupButton("New");
//				btnNew.setGlobalEventProvider((btn) -> "CreateNew");
////				btnNew.addObs((PButton btn) -> System.out.println("New!"));
//				result.add(btnNew);
//				
//				PStraightLine sep1 = new PStraightLine(LineOrientation.HORIZONTAL);
//				result.add(sep1);
//				
//				PPopupButton btnEdit = new PPopupButton("Edit");
////				btnEdit.addObs((PButton btn) -> System.out.println("Edit!"));
//				result.add(btnEdit);
//				
//				PPopupCheckBox chkRmb = new PPopupCheckBox("Remember");
//				chkRmb.getCheckBox().getModel().setValue(chkBxVal);
//				chkRmb.addObs((PClickObs) (chkBx) -> chkBxVal = !chkBxVal);
//				result.add(chkRmb);
//				
//				PPopupSubMenu subMn = new PPopupSubMenu("Sub-Menu");
//				result.add(subMn);
//				
//				PStraightLine sep2 = new PStraightLine(LineOrientation.HORIZONTAL);
//				result.add(sep2);
//				
//				PPopupButton btnDelete = new PPopupButton("Delete");
//				btnDelete.setThisIsEnabled(false);
////				btnDelete.addObs((PButton btn) -> System.out.println("Delete!"));
//				result.add(btnDelete);
//				
//				return result;
//			}
//		});
//		popupTxtAr.setEnabled(true);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setID("Button Panel");
		btnPnl.setLayout(new PWrapLayout(btnPnl, ListAlignment.LEFT_TO_RIGHT));
//		btnPnl.setLayout(new PListLayout(btnPnl, ListAlignment.FROM_LEFT));
		bodyPnl.addChild(btnPnl, PBorderLayout.BorderLayoutConstraint.BOTTOM);
		
		final PButton btnChange = new PButton();
//		btnChange.setGlobalEventProvider((btn) -> "CreateNew");
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
			ddl.getList().getModel().add(s);
		}
		btnPnl.addChild(ddl, null);
		
		btnChange.addObs(new PClickObs() {
			boolean increment = true;

//			PColor[] fontColors = new PColor[] {
//					PColor.BLACK, PColor.RED, PColor.GREY25,
//					PColor.BLUE, PColor.MAGENTA, PColor.YELLOW,
//					PColor.GREEN, PColor.TEAL, PColor.GREY50,
//					PColor.GREY75, };
//			int currentStyle = 0;
//			int currentColor = 0;

			@Override
			public void onClick(PClickable button) {
				int val = prgBar.getModel().getValue();
				val = increment ? val + 1 : val - 1;
				prgBar.getModel().setValue(val);
				if (prgBar.getModel().getValue() == prgBar.getModel().getMaxValue() || prgBar.getModel().getValue() == 0) {
					increment = !increment;
				}
//				Style style = Style.values()[(++currentStyle) % Style.values().length];
//				int size = sDs.getLabelDesign().getFontSize() + 1;
//				int colorID = (++currentColor) % fontColors.length;
//				PColor fontColor = fontColors[colorID];
//				PColor backColor = fontColors[fontColors.length - 1 - colorID];
//				sDs.getLabelDesign().setFontColor(fontColor);
//				sDs.getLabelDesign().setBackgroundColor(backColor);
//				sDs.getLabelDesign().setFontSize(size);
//				sDs.getLabelDesign().setFontStyle(style);
//				root.reRender(root);
			}
		});
		chkBxTpl.addObs((PClickObs) checkBox -> {
			lblChkBx.getModel().setValue(null);
			lblSld.getModel().setValue(null);
		});
		sld.getModel().addObs(new PSliderModelObs() {
			@Override
			public void onValueChanged(PSliderModel model) {
				lblSld.getModel().setValue(null);
			}
			@Override
			public void onRangeChanged(PSliderModel model) {
				lblSld.getModel().setValue(null);
			}
		});
		lblChkBx.setModel(new AbstractPTextModel() {
			@Override
			public void setValue(Object value) {
				fireChangeEvent(null);
			}
			@Override
			public void setValueInternal(Object text) {
			}
			@Override
			public Object getValue() {
				return null;
			}
			@Override
			public String getText() {
				if (chkBxTpl.isChecked()) {
					return "Relative";
				}
				return "Absolute";
			}
		});
		lblSld.setModel(new AbstractPTextModel() {
			@Override
			public void setValue(Object value) {
				fireChangeEvent(null);
			}
			@Override
			public void setValueInternal(Object text) {}
			@Override
			public Object getValue() {
				return null;
			}
			@Override
			public String getText() {
				if (chkBxTpl.isChecked()) {
					double percent = sld.getModel().getValuePercent();
					double val = ((int) (10000 * percent)) / 100.0;
					return val+"%";
				}
				return Integer.toString(sld.getModel().getValue());
			}
		});
		prgBar.getModel().addObs(model -> {
			if (model.getValue() == model.getMaxValue()) {
//					PDialog dlg = prgBar.getRoot().createDialog();
//					new MyLittleDialog(dlg);
			}
		});
		
//		System.out.println(PGuiUtil.guiTreeToString(root));
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