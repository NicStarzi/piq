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
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPProgressBarModel;
import edu.udo.piq.components.defaults.PSpinnerModelList;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
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
		
		PPanel body = new PPanel();
		PGridLayout layout = new PGridLayout(body, 5, 6);
		body.setLayout(layout);
		
		DefaultPProgressBarModel progMdl = new DefaultPProgressBarModel();
		PClickObs progClickObs = btn -> progMdl.addValue(+1);
		
		PClickObs disableAll = self -> {
			self.getRoot().getAllDescendantsOfType(PInteractiveComponent.class)
				.stream().forEach(comp -> {
					if (comp != self && !(comp instanceof PCheckBox)) {
						comp.getEnableModel().toggleValue();
					}
				});
		};
		
		body.addChild(new PDropDownList("One", "Two", "Three", "Four", "Five"), "0 0 alignX=F");
		body.addChild(new PButton("Hello", clickObsChangeStyle), "1 0 alignX=R alignY=T");
		body.addChild(new PButton("World", disableAll), "2 0 alignX=L alignY=B");
		body.addChild(new PSpinner(new PSpinnerModelList("Red", "Blue", "Green", "Yellow", "Magenta")), "3 0 2 1 alignX=F");
		
		String[] btnLabels = {"How", "Are", "You?"};
		for (int i = 0; i < btnLabels.length; i++) {
			PPanel subPnl = new PPanel();
			subPnl.setLayout(new PListLayout(subPnl, ListAlignment.LEFT_TO_RIGHT));
			
			PButton btn = new PButton(btnLabels[i], progClickObs);
			PSlider sld = new PSlider(0, i * -25, btnLabels.length * 25 - i * 25);
			PCheckBoxTuple chkBx = new PCheckBoxTuple("Check "+i);
//			btn.setEnableModel(new DefaultPEnableModel() {
//				{
//					chkBx.addObs((model, oldVal, newVal) -> fireChangeEvent(oldVal));
//				}
//				@Override
//				public boolean isEnabled() {
//					return chkBx.isChecked() && super.isEnabled();
//				}
//			});
//			btn.addObs((PClickObs) self -> {
//				chkBx.getEnableModel().toggleValue();
//				sld.getEnableModel().toggleValue();
//			});
			PLabel lbl = new PLabel(new SpecialTextModel(sld, chkBx));
			lbl.getLayoutPreference().setAlignmentX(AlignmentX.RIGHT);
			
			subPnl.addChild(btn, null);
			subPnl.addChild(sld, null);
			subPnl.addChild(lbl, null);
			subPnl.addChild(chkBx, null);
			
			body.addChild(subPnl, "0 "+(i+1)+" 4 1 alignX=F alignY=F");
		}
		body.addChild(new PProgressBar(progMdl), "0 4 5 1 alignX=F");
//		body.addChild(new PSpacer(100, 0), "0 5");
//		body.addChild(new PSpacer(100, 0), "1 5");
//		body.addChild(new PSpacer(100, 0), "2 5");
//		body.addChild(new PSpacer(100, 0), "3 5");
//		body.addChild(new PSpacer(100, 0), "4 5");
		root.setBody(body);
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
			chkBx.addObs((PSingleValueModelObs) (model, ov, nv) -> {
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