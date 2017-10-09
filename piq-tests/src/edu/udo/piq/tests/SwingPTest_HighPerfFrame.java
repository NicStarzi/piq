package edu.udo.piq.tests;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.swing.JFramePRootHighPerformance;

public class SwingPTest_HighPerfFrame {
	
	public static void main(String[] args) {
		new SwingPTest_HighPerfFrame();
	}
	
	private JFramePRootHighPerformance frame;
	
	public SwingPTest_HighPerfFrame() {
		frame = new JFramePRootHighPerformance();
		
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		
		PButton btn = new PButton(new PLabel("Test"));
		bodyPnl.addChild(btn, new PFreeLayout.FreeConstraint(0, 0, 120, 20));
		
		PButton btn2 = new PButton(new PLabel("Click me!"));
		bodyPnl.addChild(btn2, new PFreeLayout.FreeConstraint(55, 64));
		
		frame.setBody(bodyPnl);
//		frame.show();
		frame.setVisible(true);
		frame.renderLoop();
	}
	
//	@Override
//	public void buildGUI() {
//		PPanel bodyPnl = new PPanel();
//		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
//		root.setBody(bodyPnl);
//
//		Person p = new Person("Max", "Mustermann");
//
////		PDesignSheet sheet = new AbstractPDesignSheet() {
////			@Override
////			protected PDesign getDesignInternally(PComponent component) {
////				if (component.getClass() == PButton.class) {
////					return new PDesign() {
////						@Override
////						public void render(PRenderer renderer, PComponent component) {
////							PBounds bnds = component.getBounds();
////							PButton btn = (PButton) component;
////
////							if (btn.isPressed()) {
////								renderer.setColor(PColor.RED);
////							} else {
////								renderer.setColor(PColor.BLUE);
////							}
////							renderer.drawQuad(bnds);
////						}
////					};
////				}
////				return super.getDesignInternally(component);
////			}
////		};
////		root.setDesignSheet(sheet);
//
//		PButton btn = new PButton();
//		btn.setContent(new PLabel(new DefaultPTextModel(p) {
//			@Override
//			public String getText() {
//				Person p = (Person) getValue();
//				return p.firstName + ": " + p.lastName;
//			}
//		}));
////		btn.setDesign(new PDesign() {
////			public void render(PRenderer renderer, PComponent component) {
////				PBounds bnds = component.getBounds();
////				renderer.setColor(PColor.YELLOW);
////				renderer.drawQuad(bnds);
////			}
////		});
////		btn.setDesign();
//		bodyPnl.getLayout().addChild(btn, new PFreeLayout.FreeConstraint(36, 53));
//
//		p.firstName = "Frederick";
//
//		PPanel pnlDd = new PPanel();
//		pnlDd.setID("Test");
//		pnlDd.setLayout(new PWrapLayout(pnlDd));
//
//		PButton pnlDdBtn1 = new PButton();
//		pnlDdBtn1.setContent(new PLabel(new DefaultPTextModel("Button 1")));
//		pnlDd.addChild(pnlDdBtn1, null);
//
//		PSlider pnlDdSld1 = new PSlider();
//		pnlDdSld1.getModel().setMaxValue(123);
//		pnlDdSld1.getModel().setMinValue(31);
//		pnlDd.addChild(pnlDdSld1, null);
//
//		PCheckBoxTuple pnlDdChkBx1 = new PCheckBoxTuple();
//		pnlDdChkBx1.setSecondComponent(new PLabel(new DefaultPTextModel("Check this!")));
//		pnlDd.addChild(pnlDdChkBx1, null);
//
//		PDropDown dd = new PDropDown();
//		dd.setPreview(new PLabel(new DefaultPTextModel("Click Me!")));
//		dd.setBody(pnlDd);
//		dd.addObs(new PDropDownObs() {
//			@Override
//			public void onBodyShown(PDropDown dropDown) {
////				System.out.println();
////				System.out.println("bodyShown");
////				System.out.println(dropDown.getBody().getDebugInfo());
////				System.out.println(((PComponent) root.getOverlay()).getDebugInfo());
////				System.out.println(root.getDebugInfo());
////				for (PComponent child : dropDown.getBody().getChildren()) {
////					System.out.println(child.getDebugInfo());
////				}
//			}
//			@Override
//			public void onBodyHidden(PDropDown dropDown) {
////				System.out.println();
////				System.out.println("bodyHidden");
////				System.out.println(dropDown.getBody().getDebugInfo());
////				System.out.println(((PComponent) root.getOverlay()).getDebugInfo());
////				System.out.println(root.getDebugInfo());
//			}
//		});
//		bodyPnl.getLayout().addChild(dd, new PFreeLayout.FreeConstraint(285, 64));
//
//		PDropDownList ddl = new PDropDownList();
//		String[] elems = new String[] {"eins", "zwei", "drei", "vier"};
//		for (String s : elems) {
//			ddl.getList().getModel().add(ddl.getList().getModel().getSize(), s);
//		}
////		pnlDd.addChild(ddl, null);
//		bodyPnl.getLayout().addChild(ddl, new PFreeLayout.FreeConstraint(122, 175));
//	}
//
//	public static class Person {
//		String firstName;
//		String lastName;
//
//		Person(String a, String b) {
//			firstName = a;
//			lastName = b;
//		}
//
//	}
	
}