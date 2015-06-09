package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PLabel;
import edu.udo.piq.components.PList;
import edu.udo.piq.components.PPanel;
import edu.udo.piq.components.PSplitPanel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.swing.JCompPRoot;
import edu.udo.piq.tools.AbstractPListModel;

public class SwingPTest_DragAndDrop {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_DragAndDrop window = new SwingPTest_DragAndDrop();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_DragAndDrop() {
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
		
		PSplitPanel split = new PSplitPanel();
		bodyPnl.addChild(split, PBorderLayout.Constraint.CENTER);
		
		final PList listLeft = new PList();
		for (int i = 0; i < 4; i++) {
			listLeft.getModel().addElement(i, Integer.valueOf(i));
		}
		split.setFirstComponent(listLeft);
		
		PList listRight = new PList();
		listRight.setModel(new AbstractPListModel() {
			
			List<Person> list = new ArrayList<>();
			
			public void removeElement(int index) throws IllegalArgumentException {
				Object element = getElement(index);
				list.remove(index);
				fireRemovedEvent(element, index);
			}
			
			public int getElementIndex(Object element) {
				return list.indexOf(element);
			}
			
			public int getElementCount() {
				return list.size();
			}
			
			public Object getElement(int index) throws IndexOutOfBoundsException {
				return list.get(index);
			}
			
			public boolean canRemoveElement(int index) {
				return index >= 0 && index < list.size();
			}
			
			public boolean canAddElement(int index, Object element) {
				if (element instanceof Person && index >= 0 && index <= list.size()) {
					return true;
				}
				return false;
			}
			
			public void addElement(int index, Object element) throws IllegalArgumentException {
				list.add(index, (Person) element);
				fireAddedEvent(element, index);
			}
		});
		listRight.getModel().addElement(0, new Person("Anna", "A"));
		listRight.getModel().addElement(1, new Person("Brigitte", "B"));
		listRight.getModel().addElement(2, new Person("Chloe", "C"));
		split.setSecondComponent(listRight);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PWrapLayout(btnPnl, PListLayout.ListAlignment.CENTERED_HORIZONTAL));
		bodyPnl.addChild(btnPnl, PBorderLayout.Constraint.BOTTOM);
		
		PButton btnAdd = new PButton();
		btnAdd.setContent(new PLabel(new DefaultPTextModel("Add")));
		btnPnl.getLayout().addChild(btnAdd, null);
		
		PButton btnRmv = new PButton();
		btnRmv.setContent(new PLabel(new DefaultPTextModel("Remove")));
		btnPnl.getLayout().addChild(btnRmv, null);
		
//		btnAdd.addObs(new PButtonObs() {
//			int num = 0;
//			
//			public void onClick(PButton button) {
//				PListSelection selected = listLeft.getSelection();
//				Integer maxIndex;
//				if (selected.getSelection().isEmpty()) {
//					maxIndex = Integer.valueOf(0);
//				} else {
//					maxIndex = Collections.max(selected.getSelection());
//				}
//				listLeft.getModel().addElement(maxIndex.intValue(), "Text "+(num++));
//			}
//		});
//		btnRmv.addObs(new PButtonObs() {
//			public void onClick(PButton button) {
//				PListSelection selected = listLeft.getSelection();
//				for (Integer index : selected.getSelection()) {
//					listLeft.getModel().removeElement(index.intValue());
//				}
//			}
//		});
	}
	
	public static class Person {
		
		String firstName;
		String lastName;
		
		public Person(String a, String b) {
			firstName = a;
			lastName = b;
		}
		
		public String toString() {
			return firstName + " " + lastName + ".";
		}
		
	}
	
}