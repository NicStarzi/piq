package edu.udo.piq.tests.swing;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.PSliderModel;
import edu.udo.piq.components.PSliderModelObs;
import edu.udo.piq.components.containers.PDropDown;
import edu.udo.piq.components.containers.PDropDownList;
import edu.udo.piq.components.containers.PDropDownObs;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.util.PiqUtil;

public class SwingPTest_FreeLayout extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_FreeLayout();
	}
	
	public SwingPTest_FreeLayout() {
		super(640, 480);
	}
	
	static class Circle extends AbstractPComponent {
		public int angle1 = 0;
		public int angle2 = 360;
		@Override
		public PSize getDefaultPreferredSize() {
			prefSize.set(128, 128);
			return prefSize;
		}
		@Override
		public void defaultRender(PRenderer renderer) {
			PBounds bnds = getBoundsWithoutBorder();
			int x = bnds.getX();
			int y = bnds.getY();
			int w = bnds.getWidth();
			int h = bnds.getHeight();
			int fx = x + w;
			int fy = y + h;
			
			renderer.setColor(PColor.DARK_BLUE);
			renderer.drawRoundedRect(x, y, fx, fy, 24, 24);
			renderer.setColor(PColor.LIGHT_BLUE);
			renderer.drawArc(x, y, w, h, angle1, angle2);
		}
	}
	
	@Override
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		Circle circle = new Circle();
		
		PSlider sld1 = new PSlider(circle.angle1, 0, 360);
		bodyPnl.addChild(sld1, new PFreeLayout.FreeConstraint(256, 128));
		PSlider sld2 = new PSlider(circle.angle2, 0, 360);
		bodyPnl.addChild(sld2, new PFreeLayout.FreeConstraint(256, 160));
		
		PSliderModelObs sldObs = new PSliderModelObs() {
			@Override
			public void onValueChanged(PSliderModel model) {
				circle.angle1 = sld1.getModelValue();
				circle.angle2 = sld2.getModelValue();
				PiqUtil.fireReRenderEventFor(circle);
			}
		};
		sld1.addObs(sldObs);
		sld2.addObs(sldObs);
		
		bodyPnl.addChild(circle, new PFreeLayout.FreeConstraint(256, 256));
		
		Person p = new Person("Max", "Mustermann");
		
		PButton btn = new PButton();
		btn.setContent(new PLabel(new DefaultPTextModel(p) {
			@Override
			public String getText() {
				Person p = (Person) getValue();
				return p.firstName + ": " + p.lastName;
			}
		}));
		bodyPnl.addChild(btn, new PFreeLayout.FreeConstraint(36, 53));
		
		p.firstName = "Frederick";
		
		PPanel pnlDd = new PPanel();
		pnlDd.setID("Test");
		pnlDd.setLayout(new PWrapLayout(pnlDd));
		
		PButton pnlDdBtn1 = new PButton();
		pnlDdBtn1.setContent(new PLabel(new DefaultPTextModel("Button 1")));
		pnlDd.addChild(pnlDdBtn1, null);
		
		PSlider pnlDdSld1 = new PSlider();
		pnlDdSld1.getModel().setMaxValue(123);
		pnlDdSld1.getModel().setMinValue(31);
		pnlDd.addChild(pnlDdSld1, null);
		
		PCheckBoxTuple pnlDdChkBx1 = new PCheckBoxTuple();
		pnlDdChkBx1.setSecondComponent(new PLabel(new DefaultPTextModel("Check this!")));
		pnlDd.addChild(pnlDdChkBx1, null);
		
		PDropDown dd = new PDropDown();
		dd.setPreview(new PLabel(new DefaultPTextModel("Click Me!")));
		dd.setBody(pnlDd);
		dd.addObs(new PDropDownObs() {
			@Override
			public void onBodyShown(PDropDown dropDown) {
//				System.out.println();
//				System.out.println("bodyShown");
//				System.out.println(dropDown.getBody().getDebugInfo());
//				System.out.println(((PComponent) root.getOverlay()).getDebugInfo());
//				System.out.println(root.getDebugInfo());
//				for (PComponent child : dropDown.getBody().getChildren()) {
//					System.out.println(child.getDebugInfo());
//				}
			}
			@Override
			public void onBodyHidden(PDropDown dropDown) {
//				System.out.println();
//				System.out.println("bodyHidden");
//				System.out.println(dropDown.getBody().getDebugInfo());
//				System.out.println(((PComponent) root.getOverlay()).getDebugInfo());
//				System.out.println(root.getDebugInfo());
			}
		});
		bodyPnl.addChild(dd, new PFreeLayout.FreeConstraint(285, 64));
		
		PDropDownList ddl = new PDropDownList();
		String[] elems = new String[] {"eins", "zwei", "drei", "vier"};
		for (String s : elems) {
			ddl.getList().getModel().add(ddl.getList().getModel().getSize(), s);
		}
//		pnlDd.addChild(ddl, null);
		bodyPnl.addChild(ddl, new PFreeLayout.FreeConstraint(122, 175));
	}
	
	public static class Person {
		String firstName;
		String lastName;
		
		Person(String a, String b) {
			firstName = a;
			lastName = b;
		}
		
	}
	
}