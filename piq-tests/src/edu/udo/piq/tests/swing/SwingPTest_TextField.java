package edu.udo.piq.tests.swing;

import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.containers.PComboBox;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.PSpinnerModelDouble;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.components.defaults.PSpinnerModelList;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.components.textbased.PTextFieldObs;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.tools.AbstractPSpinnerModel;

public class SwingPTest_TextField extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_TextField();
	}
	
	public SwingPTest_TextField() {
		super(480, 320);
	}
	
	public static enum Farbe {
		ROT,
		GELB,
		GRÜN,
		BLAU,
		WEIS,
		;
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		String[] towns = new String[] {"Berlin", "Dortmund",
				"Köln", "Bremen", "Hannover", "München", "Dresden"};
		
		PTextField txtField = new PTextField("Dies ist: EIN TEST!");
		txtField.setColumnCount(10);
		bodyPnl.addChild(txtField, new FreeConstraint(24, 73));//, 200, 100
		
		PComboBox cmbBox = new PComboBox();
//		cmbBox.setTextField(new PPassWordField());
		cmbBox.setInputDecoder((str) -> {
			str = str.toLowerCase();
			for (String townName : towns) {
				if (townName.toLowerCase().contains(str)) {
					return townName;
				}
			}
			return null;
		});
		cmbBox.setOutputEncoder((obj) -> {
			if (obj == null) {
				return "<Nothing>";
			}
			return new StringBuilder(obj.toString()).reverse().toString();
		});
		cmbBox.getList().setOutputEncoder(cmbBox.getOutputEncoder());
		for (int i = 0; i < towns.length; i++) {
			cmbBox.getList().getModel().add(i, towns[i]);
		}
		bodyPnl.addChild(cmbBox, new FreeConstraint(136, 109, 150, 26));
		
		txtField.addObs((PTextFieldObs) (c) -> cmbBox.setOutputEncoder(null));
		txtField.addObs((PTextFieldObs) (c) -> cmbBox.getList().setOutputEncoder(null));
		
		PSpinner spinnerColor = new PSpinner(new PSpinnerModelInt(3, -16, 162, 4));
		spinnerColor.setModel(new AbstractPSpinnerModel() {
			Farbe f = Farbe.ROT;
			@Override
			public void setValue(Object obj) {
				if (obj instanceof String) {
					String name = ((String) obj).toUpperCase();
					obj = null;
					for (Farbe tmpF : Farbe.values()) {
						if (tmpF.name().contains(name)) {
							obj = tmpF;
							break;
						}
					}
					if (obj == null) {
						return;
					}
				}
				
				Farbe o = f;
				f = (Farbe) obj;
				fireValueChangedEvent(o);
			}
			
			@Override
			public Object getValue() {
				return f.name().toLowerCase();
			}
			
			@Override
			public Object getPrevious() {
				int o = f.ordinal() - 1;
				if (o < 0) {
					o = Farbe.values().length - 1;
				}
				return Farbe.values()[o];
			}
			
			@Override
			public Object getNext() {
				int o = f.ordinal() + 1;
				if (o >= Farbe.values().length) {
					o = 0;
				}
				return Farbe.values()[o];
			}
			
			@Override
			public boolean canSetValue(Object obj) {
				return obj != null && (obj instanceof Farbe || obj instanceof String);
			}
		});
		bodyPnl.addChild(spinnerColor, new FreeConstraint(48, 128));
		
		PSpinnerModelList modelTxt = new PSpinnerModelList(towns, towns[0]);
		PSpinner spinnerTxt = new PSpinner(modelTxt);
		modelTxt.setInputDecoder((str) -> {
			str = str.toLowerCase();
			for (String townName : towns) {
				if (townName.toLowerCase().contains(str)) {
					return townName;
				}
			}
			return null;
		});
		spinnerTxt.setOutputEncoder((obj) -> obj.toString().toUpperCase());
		bodyPnl.addChild(spinnerTxt, new FreeConstraint(162, 51, 200, 26));
		
		PSpinnerModelDouble modelDbl = new PSpinnerModelDouble(1.0);
		PSpinner spinnerDbl = new PSpinner(modelDbl);
		modelDbl.setInputDecoder((str) -> {
			str = str.replace(',', '.');
			if (str.charAt(str.length() - 1) == '%') {
				str = str.substring(0, str.length() - 1);
			}
			try {
				return Double.parseDouble(str);
			} catch (Exception e) {
				return null;
			}
		});
		spinnerDbl.setOutputEncoder((obj) -> obj.toString()+"%");
		bodyPnl.addChild(spinnerDbl, new FreeConstraint(190, 180, 150, 26));
	}
	
}