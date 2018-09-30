package edu.udo.piq.tests.swing;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PInteractiveComponent;
import edu.udo.piq.components.PProgressBar;
import edu.udo.piq.components.PSingleValueModelObs;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.PSliderModel;
import edu.udo.piq.components.PSliderModelObs;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.containers.PDropDownList;
import edu.udo.piq.components.containers.PGridPanel;
import edu.udo.piq.components.containers.PListPanel;
import edu.udo.piq.components.defaults.DefaultPProgressBarModel;
import edu.udo.piq.components.defaults.PSpinnerModelList;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.DefaultPLayoutPreference;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.scroll2.PScrollPanel;
import edu.udo.piq.tests.styles.SwingStyleSheet;
import edu.udo.piq.tools.AbstractPTextModel;

public class SwingPTest_Style extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_Style();
	}
	
	public SwingPTest_Style() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		SwingStyleSheet sheet = new SwingStyleSheet();
//		root.setStyleSheet(sheet);
		
		PClickObs clickObsChangeStyle = btn -> {
			if (root.getStyleSheet() == sheet) {
				root.setStyleSheet(null);
			} else {
				root.setStyleSheet(sheet);
			}
		};
		
		PGridPanel body = new PGridPanel(6, 6);
		
		DefaultPProgressBarModel progMdl = new DefaultPProgressBarModel();
		PClickObs progClickObs = btn -> progMdl.addValue(+1);
		
		PClickObs disableAll = self -> {
			self.getRoot().getDescendants().ofType(PInteractiveComponent.class)
				.forEach(comp -> {
					if (comp != self && !(comp instanceof PCheckBox)) {
						comp.getEnableModel().toggleValue();
					}
				});
		};
		
		body.addChild(new PDropDownList("One", "Two", "Three", "Four", "Five"), "0 0 alignX=F");
		body.addChild(new PButton("Change Style", clickObsChangeStyle), "1 0");
		body.addChild(new PButton("Toggle Enabled", disableAll), "2 0");
		body.addChild(new PSpinner(new PSpinnerModelList("Red", "Blue", "Green", "Yellow", "Magenta")), "3 0 2 1 alignX=F");
		
		String[] btnLabels = {"How", "Are", "You?"};
		for (int i = 0; i < btnLabels.length; i++) {
			PListPanel subPnl = new PListPanel(ListAlignment.LEFT_TO_RIGHT);
			
			PButton btn = new PButton(btnLabels[i], progClickObs);
			PSlider sld = new PSlider(0, i * -25, btnLabels.length * 25 - i * 25);
			PCheckBoxTuple chkBx = new PCheckBoxTuple("Check "+i);
			PLabel lbl = new PLabel(new SpecialTextModel(sld, chkBx));
			DefaultPLayoutPreference layoutPref = (DefaultPLayoutPreference) lbl.getLayoutPreference();
			layoutPref.setAlignmentX(AlignmentX.RIGHT);
			
			subPnl.addChild(btn, null);
			subPnl.addChild(sld, null);
			subPnl.addChild(lbl, null);
			subPnl.addChild(chkBx, null);
			
			body.addChild(subPnl, "0 "+(i+1)+" 4 1 alignX=F alignY=F");
		}
		body.addChild(new PProgressBar(progMdl), "0 4 5 1 alignX=F");
		
//		DefaultPListModel nameModel = new DefaultPListModel();
//		char[] chars = new char[1];
//		for (char a = 'a'; a <= 'z'; a++) {
////			for (char b = 'a'; b <= 'z'; b++) {
////				for (char c = 'a'; c <= 'z'; c++) {
////					chars[0] = a;
////					chars[1] = b;
////					chars[2] = c;
////					nameModel.add(new String(chars));
////					System.out.println(new String(chars));
////				}
////			}
//			chars[0] = a;
//			nameModel.add(new String(chars));
//		}
//		PList nameList = new PList(nameModel);
//		body.addChild(new PScrollPanel(nameList), "5 0 1 6 alignX=F alignY=F");
		
		root.setBody(new PScrollPanel(body));
	}
	
	public static class SpecialTextModel extends AbstractPTextModel {
		final PSlider sld;
		final PCheckBox chkBx;
		public SpecialTextModel(PSlider slider, PCheckBoxTuple checkBox) {
			sld = slider;
			sld.addObs(new PSliderModelObs() {
				@Override
				public void onValueChanged(PSliderModel model) {
					fireChangeEvent(null);
				}
			});
			chkBx = checkBox.getCheckBox();
			chkBx.addObs((PSingleValueModelObs<Boolean>) (model, ov, nv) -> {
				Object oldValue = getValue();
				fireChangeEvent(oldValue);
			});
		}
		@Override
		public String getText() {
			String valStr = getValue().toString();
			if (valStr.length() > 5) {
				return valStr.substring(0, 5);
			}
			return valStr;
		}
		@Override
		public Object getValue() {
			if (chkBx.isChecked()) {
				return sld.getModel().getValuePercent();
			}
			return sld.getModel().getValue();
		}
		@Override
		protected void setValueInternal(Object newValue) {
			throw new IllegalArgumentException();
		}
	}
}