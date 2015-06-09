package edu.udo.piq.comps.selectcomps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PList extends AbstractPLayoutOwner 
	implements PSelectionComponent 
{
	
	protected static final PColor BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor DROP_HIGHLIGHT_COLOR = PColor.RED;
	protected static final int DRAG_AND_DROP_DISTANCE = 16;
	
	private final PSelectionObs selectionObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			selectionAdded((PListIndex) index);
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			selectionRemoved((PListIndex) index);
		}
	};
	private final PModelObs modelObs = new PModelObs() {
		public void onContentAdded(PModel model, PModelIndex index, Object newContent) {
			getSelection().clearSelection();
			contentAdded((PListIndex) index, newContent);
		}
		public void onContentRemoved(PModel model, PModelIndex index, Object oldContent) {
			contentRemoved((PListIndex) index, oldContent);
			getSelection().removeSelection(index);
		}
		public void onContentChanged(PModel model, PModelIndex index, Object oldContent) {
			contentChanged((PListIndex) index, oldContent);
		}
	};
	private PListSelection selection;
	private PListModel model;
	private PCellFactory cellFactory;
	
	public PList() {
		this(new DefaultPListModel());
	}
	
	public PList(PListModel model) {
		super();
		setLayout(new PListLayout(this, ListAlignment.FROM_TOP, 1));
		setSelection(new PListMultiSelection());
		setCellFactory(new DefaultPCellFactory());
		setModel(model);
		
		addObs(new PMouseObs() {
			public void buttonTriggered(PMouse mouse, MouseButton btn) {
				PList.this.onMouseButtonTriggred(mouse, btn);
			}
		});
	}
	
	protected void onMouseButtonTriggred(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOverThisOrChild()) {
			PListIndex index = getIndexAt(mouse.getX(), mouse.getY());
			if (index != null) {
				PKeyboard keyBoard = getKeyboard();
				if (keyBoard == null || !keyBoard.isModifierToggled(Modifier.CTRL)) {
					getSelection().clearSelection();
				}
				getSelection().addSelection(index);
			}
		}
	}
	
	protected PListLayout getLayoutInternal() {
		return (PListLayout) super.getLayout();
	}
	
	public void setSelection(PListSelection listSelection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
		}
		selection = listSelection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
		}
	}
	
	public PListSelection getSelection() {
		return selection;
	}
	
	public void setModel(PListModel listModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		model = listModel;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		getLayoutInternal().clearChildren();
		if (model != null) {
			for (int i = 0; i < model.getSize(); i++) {
				contentAdded(new PListIndex(i), model.get(i));
			}
		}
	}
	
	public PListModel getModel() {
		return model;
	}
	
	public List<Object> getAllSelectedContent() {
		PSelection select = getSelection();
		PModel model = getModel();
		List<PModelIndex> indices = select.getAllSelected();
		if (indices.isEmpty()) {
			return Collections.emptyList();
		}
		List<Object> result = new ArrayList<>(indices.size());
		for (PModelIndex index : indices) {
			result.add(model.get(index));
		}
		return result;
	}
	
	public void setCellFactory(PCellFactory listCellFactory) {
		cellFactory = listCellFactory;
	}
	
	public PCellFactory getCellFactory() {
		return cellFactory;
	}
	
	public PListIndex getIndexAt(int x, int y) {
		PListLayout layout = getLayoutInternal();
		Collection<PComponent> children = layout.getChildren();
		for (int i = 0; i < children.size(); i++) {
			PComponent child = getLayoutInternal().getChild(i);
			if (layout.getChildBounds(child).contains(x, y)) {
				return new PListIndex(i);
			}
		}
		return null;
	}
	
	protected void contentAdded(PListIndex index, Object newContent) {
		PCellComponent cell = getCellFactory().makeCellComponent(getModel(), index);
		getLayoutInternal().addChild(cell, Integer.valueOf(index.getIndexValue()));
	}
	
	protected void contentRemoved(PListIndex index, Object oldContent) {
		getLayoutInternal().removeChild(getCellComponent(index));
	}
	
	protected void contentChanged(PListIndex index, Object oldContent) {
		getCellComponent(index).setElement(getModel(), index);
	}
	
	protected void selectionAdded(PListIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			System.out.println("select :: "+index+"; was="+cellComp.isSelected());
			cellComp.setSelected(true);
		}
	}
	
	protected void selectionRemoved(PListIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			System.out.println("unselect :: "+index+"; was="+cellComp.isSelected());
			cellComp.setSelected(false);
		}
	}
	
	protected PCellComponent getCellComponent(PListIndex index) {
		return (PCellComponent) getLayoutInternal().
				getChild(index.getIndexValue());
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(BACKGROUND_COLOR);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
		
//		if (isDropHighlighted()) {
//			renderer.setColor(DROP_HIGHLIGHT_COLOR);
//			
//			PListModel model = getModel();
//			int highestIndex = model.getElementCount() - 1;
//			if (highestIndex == -1) {
//				renderer.drawQuad(x, y, fx, y + 2);
//			} else {
//				PListCellComponent cellComp = (PListCellComponent) getLayoutInternal().getChild(highestIndex);
//				PBounds cellBounds = cellComp.getBounds();
//				int cx = cellBounds.getX();
//				int cy = cellBounds.getFinalY();
//				int cfx = cellBounds.getFinalX();
//				int cfy = cy + 2;
//				
//				renderer.drawQuad(cx, cy, cfx, cfy);
//			}
//		}
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	public void addObs(PModelObs obs) {
	}
	
	public void removeObs(PModelObs obs) {
	}
	
	public void addObs(PSelectionObs obs) {
	}
	
	public void removeObs(PSelectionObs obs) {
	}
	
}