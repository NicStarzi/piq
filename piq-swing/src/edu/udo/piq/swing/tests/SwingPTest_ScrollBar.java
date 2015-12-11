package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PButtonObs;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.collections.PListModel;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PBorderLayout.Constraint;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.scroll.PScrollPanel;
import edu.udo.piq.swing.JCompPRoot;

public class SwingPTest_ScrollBar {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_ScrollBar window = new SwingPTest_ScrollBar();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	private final PList list;
	
	public SwingPTest_ScrollBar() {
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
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PWrapLayout(btnPnl, ListAlignment.CENTERED_HORIZONTAL));
		bodyPnl.addChild(btnPnl, Constraint.BOTTOM);
		
		PButton btnAdd = new PButton();
		btnAdd.setContent(new PLabel(new DefaultPTextModel("Add")));
		btnPnl.addChild(btnAdd, null);
		
		PButton btnRemove = new PButton();
		btnRemove.setContent(new PLabel(new DefaultPTextModel("Remove")));
		btnPnl.addChild(btnRemove, null);
		
		PScrollPanel scrlPnl = new PScrollPanel();
		bodyPnl.addChild(scrlPnl, Constraint.CENTER);
		
		list = new PList();
		scrlPnl.setBody(list);
		
		btnAdd.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				addElement();
			}
		});
		btnRemove.addObs(new PButtonObs() {
			public void onClick(PButton button) {
				removeElement();
			}
		});
		
		for (int i = 0; i < 25 + 1; i++) {
			addElement();
		}
		
		PPanel bodyPnl2 = new PPanel();
		bodyPnl2.setLayout(new PFreeLayout(bodyPnl2));
		root.setBody(bodyPnl2);
		
		PScrollPanel scrlPnl2 = new PScrollPanel();
		bodyPnl2.addChild(scrlPnl2, new PFreeLayout.FreeConstraint(31, 46, 256, 256));
		
		PPicture content = new PPicture();
		content.getModel().setImagePath("Tex3.png");
		content.setStretchToSize(false);
		scrlPnl2.setBody(content);
	}
	
	private int counter = 0;
	
	private void addElement() {
		String name = "Ein langer Element Name Nummer "+(counter++);
		PListModel model = list.getModel();
		model.add(model.getSize(), name);
	}
	
	private void removeElement() {
		if (list.getModel().canRemove(0)) {
			list.getModel().remove(0);
		}
	}
	
}