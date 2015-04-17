package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PDropDown;
import edu.udo.piq.components.PDropDownList;
import edu.udo.piq.components.PDropDownObs;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.swing.JCompPRoot;
import edu.udo.piq.tools.AbstractPDesignSheet;

public class SwingPTest_FreeLayout {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_FreeLayout window = new SwingPTest_FreeLayout();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_FreeLayout() {
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		root = new JCompPRoot();
		frame.setContentPane(root.getPanel());
		
		Timer updateTimer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				root.update();
			}
		});
		updateTimer.setCoalesce(true);
		updateTimer.setRepeats(true);
		updateTimer.start();
		
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PFreeLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		Person p = new Person("Max", "Mustermann");
		
		PDesignSheet sheet = new AbstractPDesignSheet() {
			protected PDesign getDesignInternally(PComponent component) {
				if (component.getClass() == PButton.class) {
					return new PDesign() {
						public void render(PRenderer renderer, PComponent component) {
							PBounds bnds = component.getBounds();
							PButton btn = (PButton) component;
							
							if (btn.isPressed()) {
								renderer.setColor(PColor.RED);
							} else {
								renderer.setColor(PColor.BLUE);
							}
							renderer.drawQuad(bnds);
						}
					};
				}
				return super.getDesignInternally(component);
			}
		};
		root.setDesignSheet(sheet);
		
		PButton btn = new PButton();
		btn.setContent(new PLabel(new DefaultPTextModel(p) {
			public String getText() {
				Person p = (Person) getValue();
				return p.firstName + ": " + p.lastName;
			}
		}));
//		btn.setDesign();
		bodyPnl.getLayout().addChild(btn, new PFreeLayout.FreeConstraint(36, 53));
		
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
			public void bodyShown(PDropDown dropDown) {
				System.out.println();
				System.out.println(dropDown.getBody().getDebugInfo());
				for (PComponent child : dropDown.getBody().getChildren()) {
					System.out.println(child.getDebugInfo());
				}
			}
			public void bodyHidden(PDropDown dropDown) {
			}
		});
		bodyPnl.getLayout().addChild(dd, new PFreeLayout.FreeConstraint(285, 64));
		
		PDropDownList ddl = new PDropDownList();
		String[] elems = new String[] {"eins", "zwei", "drei", "vier"};
		for (String s : elems) {
			ddl.getList().getModel().addElement(ddl.getList().getModel().getElementCount(), s);
		}
//		pnlDd.addChild(ddl, null);
		bodyPnl.getLayout().addChild(ddl, new PFreeLayout.FreeConstraint(122, 175));
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