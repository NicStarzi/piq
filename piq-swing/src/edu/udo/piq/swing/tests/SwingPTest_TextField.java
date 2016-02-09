package edu.udo.piq.swing.tests;

import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.PSpinnerModel;
import edu.udo.piq.components.PSpinnerModelObs;
import edu.udo.piq.components.containers.PComboBox;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.components.defaults.PSpinnerModelList;
import edu.udo.piq.components.textbased.PTextField;
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
	
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		String[] towns = new String[] {"Berlin", "Dortmund", 
				"Köln", "Bremen", "Hannover", "München", "Dresden"};
		
		PTextField txtField = new PTextField("Dies ist: EIN TEST!");
		bodyPnl.addChild(txtField, new FreeConstraint(24, 73));//, 200, 100
		
		PComboBox cmbBox = new PComboBox();
		cmbBox.setStringEncoder((obj) -> (String) obj);
		for (int i = 0; i < towns.length; i++) {
			cmbBox.getList().getModel().add(i, towns[i]);
		}
		bodyPnl.addChild(cmbBox, new FreeConstraint(136, 109, 150, 26));
		
		PSpinner spinnerInt = new PSpinner(new PSpinnerModelInt(3, -16, 162, 4));
		spinnerInt.setModel(new AbstractPSpinnerModel() {
			Farbe f = Farbe.ROT;
			public void setValue(Object obj) {
				if (obj instanceof String) {
					obj = Farbe.valueOf(Farbe.class, (String) obj);
				}
				
				Farbe o = f;
				f = (Farbe) obj;
				fireValueChangedEvent(o);
			}
			
			public Object getValue() {
				return f;
			}
			
			public Object getPrevious() {
				int o = f.ordinal() - 1;
				if (o < 0) {
					o = Farbe.values().length - 1;
				}
				return Farbe.values()[o];
			}
			
			public Object getNext() {
				int o = f.ordinal() + 1;
				if (o >= Farbe.values().length) {
					o = 0;
				}
				return Farbe.values()[o];
			}
			
			public boolean canSetValue(Object obj) {
				return obj != null && (obj instanceof Farbe || obj instanceof String);
			}
		});
		bodyPnl.addChild(spinnerInt, new FreeConstraint(48, 128));
		
		PSpinner spinnerTxt = new PSpinner(new PSpinnerModelList(towns[0], towns));
		bodyPnl.addChild(spinnerTxt, new FreeConstraint(162, 51, 200, 26));
	}
	
}