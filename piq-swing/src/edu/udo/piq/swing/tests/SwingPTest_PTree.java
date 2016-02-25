package edu.udo.piq.swing.tests;

import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PCellComponentWrapper;
import edu.udo.piq.components.collections.PCellFactory;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTree;
import edu.udo.piq.components.collections.PTreeIndex;
import edu.udo.piq.components.collections.PTreeModel;
import edu.udo.piq.components.containers.PSplitPanel;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPTreeModel;
import edu.udo.piq.components.defaults.PTreePCellComponent;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.components.textbased.PTextFieldObs;
import edu.udo.piq.scroll.PScrollPanel;

public class SwingPTest_PTree extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PTree();
	}
	
	public SwingPTest_PTree() {
		super(640, 480);
	}
	
	public void buildGUI() {
		PSplitPanel splitPnl = new PSplitPanel();
		root.setBody(splitPnl);
		
		PTree tree = new PTree();
		PScrollPanel scroll = new PScrollPanel(tree);
		splitPnl.setFirstComponent(scroll);
		
		DefaultPTreeModel model = new DefaultPTreeModel();
		model.add(PTreeIndex.ROOT, "Root");
		model.add(new PTreeIndex(0), "Directory A");
		model.add(new PTreeIndex(0, 0), "Directory a1");
		model.add(new PTreeIndex(0, 1), "Directory a2");
		model.add(new PTreeIndex(1), "Directory B");
		model.add(new PTreeIndex(1, 0), "Directory b1");
		model.add(new PTreeIndex(2), "Directory C");
		model.add(new PTreeIndex(2, 0), "Directory c1");
		model.add(new PTreeIndex(2, 1), "Directory c2");
		model.add(new PTreeIndex(2, 2), "Directory c3");
		model.add(new PTreeIndex(3), "Directory D");
		tree.setModel(model);
		
		PList list = new PList(new DefaultPListModel("T1", "T2", "T3"));
		splitPnl.setSecondComponent(list);
		
		
		tree.setCellFactory(new PCellFactory() {
			public PCellComponent makeCellComponent(PModel model, PModelIndex index) {
				PTextField txtF = new PTextField();
				PCellComponentWrapper wrapper = new PCellComponentWrapper(txtF);
				wrapper.setContentDelegator((c, element, m, i) -> {
						txtF.getModel().setValue(element);
					});
				
				PTreePCellComponent cell = new PTreePCellComponent();
				cell.setSecondComponent(wrapper);
				cell.setElement(model, index);
				
				txtF.addObs((PTextFieldObs) (textField) -> {
						PTreeModel modelInner = cell.getElementModel();
						PTreeIndex indexInner = cell.getElementIndex();
						modelInner.set(indexInner, txtF.getText());
						cell.setElement(modelInner, indexInner);
					});
				
				return cell;
			}
		});
	}
}