package edu.udo.piq.components.defaults;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PCheckBoxModel;
import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PModelObs;
import edu.udo.piq.components.collections.PReadOnlyModel;
import edu.udo.piq.components.collections.PTree;
import edu.udo.piq.components.collections.PTreeIndex;
import edu.udo.piq.components.collections.PTreeModel;
import edu.udo.piq.layouts.PTupleLayout;
import edu.udo.piq.layouts.PTupleLayout.Constraint;
import edu.udo.piq.tools.AbstractPCheckBoxModel;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PTreePCellComponent extends AbstractPLayoutOwner implements PCellComponent {
	
	protected final PModelObs modelObs = new PModelObs() {
		public void onContentAdded(PReadOnlyModel model, PModelIndex index, Object newContent) {
			PTreePCellComponent.this.onContentAdded(model, index, newContent);
		}
		public void onContentRemoved(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			PTreePCellComponent.this.onContentRemoved(model, index, oldContent);
		}
		public void onContentChanged(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			PTreePCellComponent.this.onContentChanged(model, index, oldContent);
		}
	};
	
	public PTreePCellComponent() {
		super();
		setLayout(new PTupleLayout(this));
	}
	
	protected PTupleLayout getLayoutInternal() {
		return (PTupleLayout) super.getLayout();
	}
	
	public void setSecondComponent(PCellComponent comp) {
		getLayoutInternal().setChildForConstraint(comp, Constraint.SECOND);
	}
	
	public PCellComponent getSecondComponent() {
		return (PCellComponent) getLayoutInternal().getSecond();
	}
	
	protected void setCheckBox(PCheckBox checkBox) {
		if (getCheckBox() != null) {
			getLayoutInternal().removeChild(Constraint.FIRST);
		}
		if (checkBox != null) {
			getLayoutInternal().addChild(checkBox, Constraint.FIRST);
		}
		fireReRenderEvent();
	}
	
	public PCheckBox getCheckBox() {
		return (PCheckBox) getLayoutInternal().getFirst();
	}
	
	public boolean isChecked() {
		PCheckBox chkBx = getCheckBox();
		if (chkBx == null) {
			return false;
		}
		return chkBx.isChecked();
	}
	
	public void setSelected(boolean value) {
		getSecondComponent().setSelected(value);
	}
	
	public boolean isSelected() {
		return getSecondComponent().isSelected();
	}
	
	public void setDropHighlighted(boolean value) {
		getSecondComponent().setDropHighlighted(value);
	}
	
	public boolean isDropHighlighted() {
		return getSecondComponent().isDropHighlighted();
	}
	
	public void setElement(PModel model, PModelIndex index) {
		if (getElementModel() != null) {
			getElementModel().removeObs(modelObs);
		}
		getSecondComponent().setElement(model, index);
		if (getElementModel() != null) {
			getElementModel().addObs(modelObs);
		}
		setCheckBoxShown(canBeExpanded());
	}
	
	public PTreeModel getElementModel() {
		return (PTreeModel) getSecondComponent().getElementModel();
	}
	
	public Object getElement() {
		return getSecondComponent().getElement();
	}
	
	public PTreeIndex getElementIndex() {
		return (PTreeIndex) getSecondComponent().getElementIndex();
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	public boolean canBeExpanded() {
		PTreeModel model = getElementModel();
		PTreeIndex index = getElementIndex();
		if (model == null || index == null) {
			return false;
		}
		return model.getChildCount(index) > 0;
	}
	
	protected void onContentAdded(PReadOnlyModel model, PModelIndex index, Object newContent) {
		if (index instanceof PTreeIndex) {
			PTreeIndex treeIndex = (PTreeIndex) index;
			if (treeIndex.getDepth() > 0 && treeIndex.createParentIndex().equals(getElementIndex())) {
				toggleCheckBoxOnOrOff(index);
			}
		}
	}
	
	protected void onContentRemoved(PReadOnlyModel model, PModelIndex index, Object oldContent) {
		if (index instanceof PTreeIndex) {
			PTreeIndex treeIndex = (PTreeIndex) index;
			if (treeIndex.getDepth() > 0 && treeIndex.createParentIndex().equals(getElementIndex())) {
				toggleCheckBoxOnOrOff(index);
			}
		}
	}
	
	protected void onContentChanged(PReadOnlyModel model, PModelIndex index, Object oldContent) {
		// Intentionally left blank.
	}
	
	protected void toggleCheckBoxOnOrOff(PModelIndex index) {
		PTreeModel treeModel = getElementModel();
		PTreeIndex treeIndex = getElementIndex();
		setCheckBoxShown(treeModel.getChildCount(treeIndex) > 0);
	}
	
	protected void setCheckBoxShown(boolean isCheckBoxShown) {
		if (isCheckBoxShown) {
			if (getCheckBox() == null) {
				setCheckBox(new PTreeCellCheckbox(
						new NodeExpandedPCheckBoxModel(this)));
			}
		} else {
			setCheckBox(null);
		}
	}
	
	public static class PTreeCellCheckbox extends PCheckBox {
		
		public PTreeCellCheckbox(PCheckBoxModel model) {
			super();
			setModel(model);
		}
		
		public void defaultRender(PRenderer renderer) {
			PBounds bnds = getBounds();
			int x = bnds.getX();
			int y = bnds.getY();
			int fx = bnds.getFinalX();
			int fy = bnds.getFinalY();
			
			renderer.setColor(PColor.WHITE);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
			
			renderer.setRenderMode(renderer.getRenderModeOutline());
			renderer.setColor(PColor.BLACK);
			renderer.drawQuad(x, y, fx, fy);
			
			int padding = 3;
			x += padding;
			y += padding;
			fx -= padding;
			fy -= padding;
			
			int centerY = y + (fy - y) / 2;
			renderer.setRenderMode(renderer.getRenderModeFill());
			renderer.drawQuad(x, centerY - 1, fx, centerY + 1);
			if (isChecked()) {
				int centerX = x + (fx - x) / 2;
				renderer.drawQuad(centerX - 1, y, centerX + 1, fy);
			}
		}
		
		public boolean isFocusable() {
			return false;
		}
	}
	
	public static class NodeExpandedPCheckBoxModel extends AbstractPCheckBoxModel {
		
		protected final PTreePCellComponent owner;
		
		public NodeExpandedPCheckBoxModel(PTreePCellComponent cellComp) {
			owner = cellComp;
		}
		
		public void toggleChecked() {
			PTree tree = (PTree) owner.getParent();
			tree.setIndexExpanded(owner.getElementIndex(), isChecked());
		}
		
		public boolean isChecked() {
			PTree tree = (PTree) owner.getParent();
			return !tree.isIndexExpanded(owner.getElementIndex());
		}
		
	}
	
}