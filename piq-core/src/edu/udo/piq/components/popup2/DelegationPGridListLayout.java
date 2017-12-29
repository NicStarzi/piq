package edu.udo.piq.components.popup2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PInsets;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.components.collections.PColumnIndex;
import edu.udo.piq.components.collections.PRowIndex;
import edu.udo.piq.components.collections.PTableCellIndex;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PComponentLayoutData;
import edu.udo.piq.layouts.PGridListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;

public class DelegationPGridListLayout extends PGridListLayout {
	
	private final List<PComponent> delegateChildren = new ArrayList<>();
	private final PComponentObs delegateCompObs = new PComponentObs() {
		@Override
		public void onLayoutChanged(PComponent component, 
				PReadOnlyLayout currentLayout, PReadOnlyLayout oldLayout) 
		{
			DelegationPGridListLayout.this.onDelegateLayoutChanged(component, currentLayout, oldLayout);
		}
	};
	private final PLayoutObs delegateLayoutObs = new PLayoutObs() {
		@Override
		public void onChildAdded(PReadOnlyLayout layout, PComponentLayoutData data) {
			DelegationPGridListLayout.this.onDelegateChildAdded(layout, data);
		}
		@Override
		public void onChildRemoved(PReadOnlyLayout layout, PComponentLayoutData data) {
			DelegationPGridListLayout.this.onDelegateChildRemoved(layout, data);
		}
		@Override
		public void onLayoutInvalidated(PReadOnlyLayout layout) {
			DelegationPGridListLayout.this.onDelegateLayoutInvalidated(layout);
		}
	};
	
	public DelegationPGridListLayout(PComponent owner, int numberOfColumns) {
		this(owner, DEFAULT_LIST_ALIGNMENT, numberOfColumns);
	}
	
	public DelegationPGridListLayout(PComponent owner, ListAlignment alignment, int numberOfColumns) {
		super(owner, alignment, numberOfColumns);
	}
	
	protected void onDelegateLayoutChanged(PComponent component, 
			PReadOnlyLayout currentLayout, PReadOnlyLayout oldLayout) 
	{
		DelegatedPLayout previousLayout = (DelegatedPLayout) oldLayout;
		previousLayout.removeObs(delegateLayoutObs);
		previousLayout.forEach(childData -> {
			super.onChildRemoved(childData);
		});
		
		DelegatedPLayout newLayout = (DelegatedPLayout) currentLayout;
		newLayout.addObs(delegateLayoutObs);
		newLayout.forEach(childData -> {
			onChildAdded(childData);
		});
	}
	
	protected void onDelegateChildAdded(PReadOnlyLayout layout, PComponentLayoutData data) {
		onChildAdded(data);
	}
	
	protected void onDelegateChildRemoved(PReadOnlyLayout layout, PComponentLayoutData data) {
		super.onChildRemoved(data);
	}
	
	protected void onDelegateLayoutInvalidated(PReadOnlyLayout layout) {
		invalidate();
	}
	
	@Override
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return (constraint instanceof PDelegateRowIndex && cmp.getLayout() instanceof DelegatedPLayout) 
				|| super.canAdd(cmp, constraint);
	}
	
	protected boolean isDelegated(PComponent maybeGrandchild) {
		PComponent maybeChild = maybeGrandchild.getParent();
		return maybeChild.getParent() == getOwner();
	}
	
	@Override
	protected void onChildAdded(PComponentLayoutData data) {
		Object constraint = data.getConstraint();
		PComponent child = data.getComponent();
		if (!(constraint instanceof PDelegateRowIndex)) {
			super.onChildAdded(data);
			return;
		}
		delegateChildren.add(child);
		child.addObs(delegateCompObs);
		DelegatedPLayout childLayout = (DelegatedPLayout) child.getLayout();
		childLayout.addObs(delegateLayoutObs);
		
		PDelegateRowIndex rowIdx = (PDelegateRowIndex) constraint;
		childLayout.forEach(childData -> {
			PColumnIndex colIdx = (PColumnIndex) childData.getConstraint();
			Object grandChildConstr = new PTableCellIndex(colIdx, rowIdx);
			onChildAdded(new PComponentLayoutData(childData.getComponent(), grandChildConstr));
		});
	}
	
	@Override
	protected void onChildRemoved(PComponentLayoutData data) {
		if (!(data.getConstraint() instanceof PDelegateRowIndex)) {
			super.onChildRemoved(data);
			return;
		}
		PComponent child = data.getComponent();
		DelegatedPLayout childLayout = (DelegatedPLayout) child.getLayout();
		childLayout.forEach(childData -> {
			super.onChildRemoved(childData);
		});
		childLayout.removeObs(delegateLayoutObs);
		child.removeObs(delegateCompObs);
		delegateChildren.remove(child);
	}
	
	@Override
	protected void clearAllDataInternal() {
		for (PComponent child : delegateChildren) {
			child.removeObs(delegateCompObs);
			child.getLayout().removeObs(delegateLayoutObs);
		}
		delegateChildren.clear();
		super.clearAllDataInternal();
	}
	
	@Override
	public PComponent getChild(int colIdx, int rowIdx) {
		PComponent cmpAtCell = super.getChild(colIdx, rowIdx);
		if (isDelegated(cmpAtCell)) {
			return cmpAtCell.getParent();
		}
		return cmpAtCell;
	}
	
	@Override
	public PComponent getChildForConstraint(Object constraint) {
		if (constraint instanceof PDelegateRowIndex) {
			for (PComponentLayoutData data : getAllData()) {
				if (Objects.equals(data.getConstraint(), constraint)) {
					return data.getComponent();
				}
			}
			return null;
		}
		return super.getChildForConstraint(constraint);
	}
	
	@Override
	protected void setChildCell(PComponent child, int x, int y, 
			int width, int height, AlignmentX alignX, AlignmentY alignY) 
	{
		if (child == null) {
			return;
		}
		if (isDelegated(child)) {
			DelegatedPLayout delLayout = (DelegatedPLayout) child.getParent().getLayout();
			delLayout.delegatedSetChildBounds(child, x, y, width, height, alignX, alignY);
			updateDelegateChildBounds(child.getParent());
		} else {
			super.setChildCell(child, x, y, width, height, alignX, alignY);
		}
	}
	
	protected void updateDelegateChildBounds(PComponent child) {
		PBounds ownerBnds = getOwner().getBoundsWithoutBorder();
		int minX = ownerBnds.getFinalX();
		int minY = ownerBnds.getFinalY();
		int maxX = ownerBnds.getX();
		int maxY = ownerBnds.getY();
		
//		DelegatedPLayout childLayout = (DelegatedPLayout) child.getLayout();
		for (PComponent grandChild : child.getChildren()) {
			PBounds grandChildCellBnds = grandChild.getLayoutData().getCellBounds();
			minX = Math.min(minX, grandChildCellBnds.getX());
			minY = Math.min(minY, grandChildCellBnds.getY());
			maxX = Math.max(maxX, grandChildCellBnds.getFinalX());
			maxY = Math.max(maxY, grandChildCellBnds.getFinalY());
		}
		int childW = maxX - minX;
		int childH = maxY - minY;
		
		PBorder childBorder = child.getBorder();
		if (childBorder != null) {
			PInsets insets = childBorder.getInsets(child);
			childW += insets.getWidth();
			childH += insets.getHeight();
			minX -= insets.getFromLeft();
			minY -= insets.getFromTop();
		}
		setChildCellFilled(getDataFor(child), minX, minY, childW, childH);
	}
	
	public static class PDelegateRowIndex extends PRowIndex {
		
		public PDelegateRowIndex(int rowIndex) {
			super(rowIndex);
		}
		
	}
	
}