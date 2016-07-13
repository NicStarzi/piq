package edu.udo.piq.swing.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PCellFactory;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PSplitPanel;
import edu.udo.piq.components.defaults.DefaultPCellComponent;
import edu.udo.piq.components.defaults.DefaultPDnDSupport;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.swing.JCompPRoot;

public class SwingPTest_PList2 {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingPTest_PList2 window = new SwingPTest_PList2();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final JFrame frame;
	private final JCompPRoot root;
	
	public SwingPTest_PList2() {
		frame = new JFrame();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		root = new JCompPRoot();
		frame.setContentPane(root.getJPanel());
		
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
		
		PSplitPanel splitPnl = new PSplitPanel();
		bodyPnl.addChild(splitPnl, PBorderLayout.Constraint.CENTER);
		
		Person[] males = new Person[] {
			new Person("Anton"),
			new Person("Benjamin"),
			new Person("Charles"),
			new Person("David"),
			new Person("Erich"),
			new Person("Frank"),
		};
		
		Person[] females = new Person[] {
			new Person("Antje"),
			new Person("Bridget"),
			new Person("Clara"),
			new Person("Daphne"),
			new Person("Elisabeth"),
			new Person("Felicia"),
		};
		
		final PList list1 = new PList(new DefaultPListModel((Object[]) males) {
			public boolean canAdd(int index, Object content) {
				return super.canAdd(index, content) && content instanceof Person;
			}
		});
		list1.setCellFactory(new PCellFactory() {
			public PCellComponent makeCellComponent(PModel model, PModelIndex index) {
				DefaultPCellComponent cellComp = new DefaultPCellComponent();
				cellComp.setModel(new DefaultPTextModel() {
					public String getText() {
						Person p = (Person) getValue();
						return p.getName();
					}
				});
				cellComp.setElement(model, index);
				return cellComp;
			}
		});
		list1.setID("LEFT");
		list1.setDragAndDropSupport(new DefaultPDnDSupport());
		splitPnl.setFirstComponent(list1);
		
		final PList list2 = new PList(new DefaultPListModel((Object[]) females) {
			public boolean canAdd(int index, Object content) {
				return super.canAdd(index, content) && content instanceof Person;
			}
		});
		list2.setCellFactory(new PCellFactory() {
			public PCellComponent makeCellComponent(PModel model, PModelIndex index) {
				final Person p = (Person) model.get(index);
				
				final DefaultPCellComponent lblName = new DefaultPCellComponent();
				lblName.setModel(new DefaultPTextModel() {
					public String getText() {
						Person person = (Person) getValue();
						if (person == null) {
							return "<No Person>";
						}
						return person.getName();
					}
				});
				
				PButton btn = new PButton();
				btn.setContent(new PLabel("Click here!"));
				btn.addObs((PClickObs) (cmp) -> {
						p.setName(p.getName() + "*");
						lblName.getModel().setValue(p);
					});
				
				PersonCellComp cellComp = new PersonCellComp() {
					public void setElement(PModel model, PModelIndex index) {
						super.setElement(model, index);
						lblName.setElement(model, index);
					}
					public Object getElement() {
						return lblName.getElement();
					}
				};
				cellComp.setElement(model, index);
				cellComp.setLayout(new PListLayout(cellComp, ListAlignment.FROM_LEFT));
				cellComp.addChild(lblName, null);
				cellComp.addChild(btn, null);
				
				return cellComp;
			}
		});
//		list2.defineInput("test", new PInput() {
//			public Key getTriggerKey() {
//				return Key.ENTER;
//			}
//			public boolean canBeTriggered(PKeyboard keyboard) {
//				return list2.isEnabled();
//			}
//		}, new Runnable() {
//			public void run() {
//				PListIndex i = list2.getSelection().getLastSelected();
//				if (i != null) {
//					PersonCellComp p = (PersonCellComp) list2.getCellComponent(i);
//					PListLayout l = (PListLayout) p.getLayout();
//					PButton btn = (PButton) l.getChild(1);
//					btn.takeFocus();
//				}
//			}
//		});
		list2.setID("RIGHT");
		list2.setDragAndDropSupport(new DefaultPDnDSupport());
		splitPnl.setSecondComponent(list2);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PWrapLayout(btnPnl, ListAlignment.CENTERED_HORIZONTAL));
		bodyPnl.addChild(btnPnl, PBorderLayout.Constraint.BOTTOM);
		
		PButton btnAdd = new PButton();
		btnAdd.setContent(new PLabel("Add"));
		btnPnl.addChild(btnAdd, null);
		
		PButton btnRemove = new PButton();
		btnRemove.setContent(new PLabel("Remove"));
		btnPnl.addChild(btnRemove, null);
		
		final List<String> names = new ArrayList<>(Arrays.asList(new String[] {
				"A", "B", "C", "D", "E", "F", "G", "H", "I", 
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", 
				"S", "T", "U", "V", "W", "X", "Y", "Z",
		}));
		
		btnAdd.addObs((PClickObs) (cmp) -> {
				int index = (int) (Math.random() * names.size());
				list1.getModel().add(new PListIndex(0), names.remove(index));
			});
		btnRemove.addObs((PClickObs) (cmp) -> {
				PModel model = list1.getModel();
				PSelection select = list1.getSelection();
				List<PModelIndex> indices = select.getAllSelected();
				while (!indices.isEmpty()) {
					PModelIndex index = indices.get(0);
					if (model.canRemove(index)) {
						model.remove(index);
					}
				}
			});
	}
	
	public static class Person {
		
		private String name;
		
		public Person(String name) {
			setName(name);
		}
		
		public void setName(String value) {
			name = value;
		}
		
		public String getName() {
			return name;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append("{");
			sb.append(getName());
			sb.append("}");
			return sb.toString();
		}
		
	}
	
	public static abstract class PersonCellComp extends PPanel implements PCellComponent {
		
		protected PModel model;
		protected PModelIndex index;
		protected boolean selected = false;
		protected boolean dropHighLight = false;
		
		public void setSelected(boolean isSelected) {
			selected = isSelected;
			fireReRenderEvent();
		}
		
		public boolean isSelected() {
			return selected;
		}
		
		public void setDropHighlighted(boolean isHighlighted) {
			dropHighLight = isHighlighted;
			fireReRenderEvent();
		}
		
		public boolean isDropHighlighted() {
			return dropHighLight;
		}
		
		public void setElement(PModel model, PModelIndex index) {
			this.model = model;
			this.index = index;
		}
		
		public PModel getElementModel() {
			return model;
		}
		
		public PModelIndex getElementIndex() {
			return index;
		}
		
		public void defaultRender(PRenderer renderer) {
			super.defaultRender(renderer);
			if (isSelected()) {
				renderer.setColor(PColor.BLUE);
				renderer.strokeQuad(getBounds(), 2);
			}
			if (isDropHighlighted()) {
				renderer.setColor(PColor.RED);
				renderer.strokeTop(getBounds(), 2);
			}
		}
		
	}
}