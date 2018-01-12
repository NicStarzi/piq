package edu.udo.piq.tests.swing;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PProgressBar;
import edu.udo.piq.components.PSingleValueModelObs;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.PSliderModel;
import edu.udo.piq.components.PSliderModelObs;
import edu.udo.piq.components.PSpacer;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.containers.PDropDownList;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPProgressBarModel;
import edu.udo.piq.components.defaults.PSpinnerModelList;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PGridLayout;
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
//		layout.setColumnGrowth(2, Growth.MAXIMIZE);
//		layout.setRowGrowth(3, Growth.MAXIMIZE);
		body.setLayout(layout);
		
		DefaultPProgressBarModel progMdl = new DefaultPProgressBarModel();
		PClickObs progClickObs = btn -> progMdl.addValue(+1);
		
		body.addChild(new PDropDownList("One", "Two", "Three", "Four", "Five"), "0 0 alignX=F");
		body.addChild(new PButton("Hello", clickObsChangeStyle), "1 0 alignX=R alignY=T");
		body.addChild(new PButton("World"), "2 0 alignX=L alignY=B");
		body.addChild(new PSpinner(new PSpinnerModelList("Red", "Blue", "Green", "Yellow", "Magenta")), "3 0 2 1 alignX=F");
		body.addChild(new PButton("How", progClickObs), "0 1 alignX=F");
		body.addChild(new PButton("Are", progClickObs), "0 2 alignX=F");
		body.addChild(new PButton("You?", progClickObs), "0 3 alignX=F");
		PSlider sld1 = new PSlider(0, 0, 50);
		PSlider sld2 = new PSlider(0, -25, 25);
		PSlider sld3 = new PSlider(0, -50, 0);
		PCheckBoxTuple chkBx1 = new PCheckBoxTuple("Check 1");
		PCheckBoxTuple chkBx2 = new PCheckBoxTuple("Check 2");
		PCheckBoxTuple chkBx3 = new PCheckBoxTuple("Check 3");
		body.addChild(sld1, "1 1 2 1 alignX=F");
		body.addChild(sld2, "1 2 2 1 alignX=F");
		body.addChild(sld3, "1 3 2 1 alignX=F");
		body.addChild(new PLabel(new SpecialTextModel(sld1, chkBx1)), "3 1 alignX=L");
		body.addChild(new PLabel(new SpecialTextModel(sld2, chkBx2)), "3 2 alignX=L");
		body.addChild(new PLabel(new SpecialTextModel(sld3, chkBx3)), "3 3 alignX=L");
		body.addChild(chkBx1, "4 1");
		body.addChild(chkBx2, "4 2");
		body.addChild(chkBx3, "4 3");
		body.addChild(new PProgressBar(progMdl), "0 4 5 1 alignX=F");
		body.addChild(new PSpacer(100, 0), "0 5");
		body.addChild(new PSpacer(100, 0), "1 5");
		body.addChild(new PSpacer(100, 0), "2 5");
		body.addChild(new PSpacer(100, 0), "3 5");
		body.addChild(new PSpacer(100, 0), "4 5");
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
			return getValue().toString();
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