package edu.udo.piq.tests.swing;

import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PInsets;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.actions.MutableAction;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.components.PCheckBoxModel;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PSingleValueModelObs;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PCellComponentWrapper;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTree;
import edu.udo.piq.components.collections.PTreeIndex;
import edu.udo.piq.components.collections.PTreeModel;
import edu.udo.piq.components.collections.list.PList;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.containers.PSplitPanel;
import edu.udo.piq.components.defaults.DefaultPCellComponent;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPTreeModel;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.components.defaults.PTreePCellComponent;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.components.textbased.PTextFieldObs;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PGridLayout;
import edu.udo.piq.scroll2.PScrollPanel;

public class SwingPTest_PTree extends AbstractSwingPTest {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new SwingPTest_PTree();
	}
	
	public SwingPTest_PTree() {
		super(640, 480);
	}
	
	@Override
	public void buildGUI() {
		PPanel body = new PPanel();
		body.setLayout(new PBorderLayout(body));
		root.setBody(body);
		
		PSplitPanel splitPnl = new PSplitPanel();
		body.addChild(splitPnl, PBorderLayout.BorderLayoutConstraint.CENTER);
		
		PTree tree = new PTree();
		PScrollPanel scroll = new PScrollPanel(tree);
		splitPnl.setFirstComponent(scroll);
		
		DefaultPTreeModel model = new DefaultPTreeModel();
		model.add(PTreeIndex.ROOT, "Root");
		model.add(new PTreeIndex(0), "A");
		model.add(new PTreeIndex(0, 0), "a1");
		model.add(new PTreeIndex(0, 1), "a2");
		model.add(new PTreeIndex(1), "B");
		model.add(new PTreeIndex(1, 0), "b1");
		model.add(new PTreeIndex(2), "C");
		model.add(new PTreeIndex(2, 0), "c1");
		model.add(new PTreeIndex(2, 1), "c2");
		model.add(new PTreeIndex(2, 2), "c3");
		model.add(new PTreeIndex(3), "D");
		tree.setModel(model);
		
		PList list = new PList(new DefaultPListModel("T1", "T2", "T3"));
		splitPnl.setSecondComponent(list);
		
		PPanel ctrlPnl = new PPanel();
		ctrlPnl.setLayout(new PGridLayout(ctrlPnl, 2, 3));
		body.addChild(ctrlPnl, PBorderLayout.BorderLayoutConstraint.BOTTOM);
		
		ctrlPnl.addChild(new PLabel("Gap Size"), "0 0");
		PSpinner inputGap = new PSpinner(new PSpinnerModelInt(0, 0, 100));
		ctrlPnl.addChild(inputGap, "1 0 alignX=F");
		inputGap.addObs((mdl, oldVal) -> tree.setGap((int) mdl.getValue()));
		inputGap.getModel().setValue(tree.getGap());
		
		ctrlPnl.addChild(new PLabel("Indent Size"), "0 1");
		PSpinner inputIndent = new PSpinner(new PSpinnerModelInt(0, 0, 300));
		ctrlPnl.addChild(inputIndent, "1 1 alignX=FILL");
		inputIndent.addObs((mdl, oldVal) -> tree.setIndentSize((int) mdl.getValue()));
		inputIndent.getModel().setValue(tree.getIndentSize());
		
		PCheckBoxTuple inputHideRoot = new PCheckBoxTuple(new PLabel("Hide Root"));
		ctrlPnl.addChild(inputHideRoot, "0 2 2 1 alignX=Left");
		inputHideRoot.addObs((PSingleValueModelObs) (mdl, oldVal, newVal) -> tree.setHideRootNode(((PCheckBoxModel) mdl).isChecked()));
		inputHideRoot.getCheckBox().getModel().setValue(tree.isHideRootNode());
		
		tree.setCellFactory((mdl, idx) -> new ComplicatedTreeCell(mdl, idx));
	}
	
	public static class ComplicatedTreeCell extends PTreePCellComponent {
		
		private final DefaultPCellComponent label;
		private final PCellComponentWrapper labelWrapper;
		private final PTextField editor;
		
		public ComplicatedTreeCell(PModel model, PModelIndex index) {
			editor = new PTextField();
			label = new DefaultPCellComponent() {
				@Override
				public String getText() {
					return ComplicatedTreeCell.this.getText();
				}
			};
			labelWrapper = new PCellComponentWrapper(editor);
			labelWrapper.getLayout().setInsets(PInsets.ZERO_INSETS);
			label.addObs(new PMouseObs() {
				@Override
				public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
					if (btn == MouseButton.LEFT && clickCount == 2 && label.isMouseOver(mouse)) {
						setEditorVisible(true);
					}
				}
			});
			
			labelWrapper.setContentDelegator((comp, element, mdl, idx) -> {
				editor.getModel().setValue(element);
			});
			editor.addObs((PTextFieldObs) (textField) -> {
				PTreeModel modelInner = getElementModel();
				PTreeIndex indexInner = getElementIndex();
				modelInner.set(indexInner, editor.getText());
				setElement(modelInner, indexInner);
				setEditorVisible(false);
			});
			editor.addObs(new PFocusObs() {
				@Override
				public void onFocusLost(PComponent oldOwner) {
					setEditorVisible(false);
				}
			});
			MutableAction cancelEditAction = new MutableAction();
			cancelEditAction.setAccelerator(ActualKey.ESCAPE);
			cancelEditAction.setPerformAction(root -> {
				PTree tree = editor.getAncestors().getNextOfType(PTree.class);
				setEditorVisible(false);
				tree.takeFocus();
			});
			editor.addActionMapping(new PActionKey("CANCEL_EDIT"), cancelEditAction);
			setSecondComponent(label);
			setElement(model, index);
		}
		
		protected void setEditorVisible(boolean value) {
			value &= getElementModel().getChildCount(getElementIndex()) == 0;
			
			PCellComponent inner;
			if (value) {
				inner = labelWrapper;
			} else {
				inner = label;
			}
			if (inner != getSecondComponent()) {
				PModel model = getElementModel();
				PModelIndex index = getElementIndex();
				
				setSecondComponent(inner);
				inner.setElement(model, index);
				editor.tryToTakeFocus();
				editor.getSelection().selectAll(editor.getModel());
			}
		}
		
//		@Override
//		public void setSelected(boolean value) {
////			setEditorVisible(value);
//			super.setSelected(value);
//		}
		
		protected String getText() {
			String txt = getElement() == null ? "null" : getElement().toString();
			if (getParent() == null) {
				return txt;
			}
			// We check for inconsistencies here
			PTreeIndex index = getElementIndex();
			StringBuilder sb = new StringBuilder(txt);
			sb.append(" [");
			for (int i = 0; i < index.getDepth(); i++) {
				if (i != 0) {
					sb.append(", ");
				}
				sb.append(index.getChildIndex(i));
			}
			sb.append("]");
			return sb.toString();
		}
		
	}
	
}