package edu.udo.piq.components.collections;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPCellComponent;
import edu.udo.piq.components.defaults.DefaultPCellFactory;
import edu.udo.piq.components.defaults.DefaultPDnDSupport;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.components.util.ObjToStr;
import edu.udo.piq.layouts.PTableLayout3;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PTable extends AbstractPInputLayoutOwner 
	implements PDropComponent 
{
	
	protected static final PColor BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor FOCUS_COLOR = PColor.GREY50;
	protected static final PColor DROP_HIGHLIGHT_COLOR = PColor.RED;
	protected static final int DRAG_AND_DROP_DISTANCE = 20;
	
	protected final ObserverList<PModelObs> modelObsList
		= PCompUtil.createDefaultObserverList();
	protected final ObserverList<PSelectionObs> selectionObsList
		= PCompUtil.createDefaultObserverList();
	private final PSelectionObs selectionObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			selectionAdded((PTableCellIndex) index);
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			selectionRemoved((PTableCellIndex) index);
		}
		public void onLastSelectedChanged(PSelection selection,
				PModelIndex prevLastSelected, PModelIndex newLastSelected) 
		{
			if (hasFocus()) {
				fireReRenderEvent();
			}
		}
	};
	private final PModelObs modelObs = new PModelObs() {
		public void onContentAdded(PModel model, PModelIndex index, Object newContent) {
			contentAdded((PTableIndex) index, null);
		}
		public void onContentRemoved(PModel model, PModelIndex index, Object oldContent) {
			contentRemoved((PTableIndex) index, oldContent);
		}
		public void onContentChanged(PModel model, PModelIndex index, Object oldContent) {
			contentChanged((PTableCellIndex) index, oldContent);
		}
	};
	protected PTableSelection selection;
	protected PTableModel model;
	protected ObjToStr encoder;
	protected PCellFactory cellFactory;
	protected PDnDSupport dndSup;
	protected PModelIndex currentDnDHighlightIndex;
	protected PCellComponent currentDnDHighlightComponent;
	protected int lastDragX = -1;
	protected int lastDragY = -1;
	protected boolean isDragTagged = false;
	
	public PTable(PTableModel model) {
		this();
		setModel(model);
	}
	
	public PTable() {
		super();
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PTableModel defaultModel = new FixedSizePTableModel(0, 0);
		if (modelFac != null) {
			defaultModel = (PTableModel) modelFac.getModelFor(this, defaultModel);
		}
		
		setLayout(new PTableLayout3(this));
		setDragAndDropSupport(new DefaultPDnDSupport());
		setSelection(new PTableSingleSelection());
		setCellFactory(new DefaultPCellFactory());
		setModel(model);
		
		addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				PTable.this.onMouseButtonTriggred(mouse, btn);
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				PTable.this.onMouseReleased(mouse, btn);
			}
			public void onMouseMoved(PMouse mouse) {
				PTable.this.onMouseMoved(mouse);
			}
		});
		addObs(new ReRenderPFocusObs());
	}
	
	protected void onMouseButtonTriggred(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOverThisOrChild()) {
			PTableCellIndex index = getIndexAt(mouse.getX(), mouse.getY());
			if (index != null) {
				if (mouse.isPressed(MouseButton.DRAG_AND_DROP)) {
					lastDragX = mouse.getX();
					lastDragY = mouse.getY();
					isDragTagged = true;
				}
				
				PKeyboard keyBoard = getKeyboard();
				if (keyBoard == null || !keyBoard.isModifierToggled(Modifier.CTRL)) {
					getSelection().clearSelection();
				}
				getSelection().addSelection(index);
				takeFocus();
			}
		}
	}
	
	protected void onMouseReleased(PMouse mouse, MouseButton btn) {
		if (isDragTagged && mouse.isReleased(MouseButton.DRAG_AND_DROP)) {
			isDragTagged = false;
		}
	}
	
	protected void onMouseMoved(PMouse mouse) {
		PDnDSupport dndSup = getDragAndDropSupport();
		if (dndSup != null && isDragTagged 
				&& mouse.isPressed(MouseButton.DRAG_AND_DROP)) 
		{
			int mx = mouse.getX();
			int my = mouse.getY();
			int disX = Math.abs(lastDragX - mx);
			int disY = Math.abs(lastDragY - my);
			int dis = Math.max(disX, disY);
			if (dis >= DRAG_AND_DROP_DISTANCE) {
				//FIXME
//				if (dndSup.canDrag(this, mx, my)) {
//					dndSup.startDrag(this, mx, my);
//				}
			}
		}
	}
	
	protected PTableLayout3 getLayoutInternal() {
		return (PTableLayout3) super.getLayout();
	}
	
	public void setSelection(PTableSelection listSelection) {
		if (getSelection() != null) {
			getSelection().clearSelection();
			getSelection().removeObs(selectionObs);
			for (PSelectionObs obs : selectionObsList) {
				getSelection().removeObs(obs);
			}
		}
		selection = listSelection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
			for (PSelectionObs obs : selectionObsList) {
				getSelection().addObs(obs);
			}
		}
	}
	
	public PTableSelection getSelection() {
		return selection;
	}
	
	public void setModel(PTableModel listModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
			for (PModelObs obs : modelObsList) {
				getModel().removeObs(obs);
			}
		}
		model = listModel;
		rebuildCellComponents();
		if (getModel() != null) {
			PTableModel model = getModel();
			
			model.addObs(modelObs);
			for (PModelObs obs : modelObsList) {
				model.addObs(obs);
			}
		}
	}
	
	protected void rebuildCellComponents() {
		PTableLayout3 layout = getLayoutInternal();
		layout.removeAllColumnsAndRows();
		
		PTableModel model = getModel();
		if (model != null) {
			for (int col = 0; col < model.getColumnCount(); col++) {
				getLayoutInternal().addColumn();
			}
			for (int row = 0; row < model.getRowCount(); row++) {
				getLayoutInternal().addRow();
				for (int col = 0; col < model.getColumnCount(); col++) {
					PTableCellIndex cellIndex = new PTableCellIndex(col, row);
					PCellComponent cell = getCellFactory().makeCellComponent(model, cellIndex);
					layout.addChild(cell, cellIndex);
				}
			}
		}
	}
	
	public PTableModel getModel() {
		return model;
	}
	
	public void setCellFactory(PCellFactory listCellFactory) {
		cellFactory = listCellFactory;
		getLayoutInternal().clearChildren();
		
		PTableModel model = getModel();
		if (model != null) {
			throw new IllegalStateException();
//			rebuildCellComponents();
		}
	}
	
//	private void rebuildCellComponents() {
//		PTableModel model = getModel();
//		for (PModelIndex index : model) {
//			contentAdded((PTableCellIndex) index, null);
//		}
//		for (int col = 0; col < model.getColumnCount(); col++) {
//			for (int row = 0; row < model.getRowCount(); row++) {
//				PTableIndex index = new PTableIndex(col, row);
//				contentAdded(index, null);
//			}
//		}
//	}
	
	public PCellFactory getCellFactory() {
		return cellFactory;
	}
	
	public void setDragAndDropSupport(PDnDSupport support) {
		dndSup = support;
	}
	
	public PDnDSupport getDragAndDropSupport() {
		return dndSup;
	}
	
	//TODO
//	public void synchronizeWithModel() {
//		getLayoutInternal().clearChildren();
//		for (PModelIndex index : getModel()) {
//			contentAdded((PListIndex) index, getModel().get(index));
//		}
//	}
//	
//	public List<Object> getAllSelectedContent() {
//		PSelection select = getSelection();
//		PModel model = getModel();
//		List<PModelIndex> indices = select.getAllSelected();
//		if (indices.isEmpty()) {
//			return Collections.emptyList();
//		}
//		if (indices.size() == 1) {
//			PModelIndex index = indices.get(0);
//			Object element = model.get(index);
//			return Collections.singletonList(element);
//		}
//		List<Object> result = new ArrayList<>(indices.size());
//		for (PModelIndex index : indices) {
//			result.add(model.get(index));
//		}
//		return result;
//	}
	
	public void setOutputEncoder(ObjToStr outputEncoder) {
		encoder = outputEncoder;
		ObjToStr outEnc = getOutputEncoder();
		
		PCellFactory cellFactory = getCellFactory();
		if (cellFactory instanceof DefaultPCellFactory) {
			((DefaultPCellFactory) cellFactory).setOutputEncoder(outEnc);
		}
		
		PTableModel model = getModel();
		if (model != null) {
			for (PModelIndex index : model) {
				PCellComponent cell = getCellComponent((PTableCellIndex) index);
				if (cell instanceof DefaultPCellComponent) {
					((DefaultPCellComponent) cell).setOutputEncoder(outEnc);
					cell.setElement(model, index);
				}
			}
		}
	}
	
	public ObjToStr getOutputEncoder() {
		return encoder;
	}
	
	public PTableCellIndex getIndexAt(int x, int y) {
		return getLayoutInternal().getIndexAt(x, y);
	}
	
	public PTableCellIndex getDropIndex(int x, int y) {
		return getIndexAt(x, y);
	}
	
	public void setDropHighlight(PModelIndex index) {
		if (currentDnDHighlightComponent != null) {
			currentDnDHighlightComponent.setDropHighlighted(false);
		}
		currentDnDHighlightIndex = index;
		if (index != null) {
			currentDnDHighlightComponent = getCellComponent((PTableCellIndex) index);
			if (currentDnDHighlightComponent != null) {
				currentDnDHighlightComponent.setDropHighlighted(true);
			}
		}
	}
	
	public boolean isDropHighlighted() {
		return currentDnDHighlightComponent != null 
				|| currentDnDHighlightIndex != null;
	}
	
	protected void contentAdded(PTableIndex index, Object newContent) {
		System.out.println("PTable.contentAdded idx="+index);
		
		PTableModel model = getModel();
		PTableLayout3 layout = getLayoutInternal();
		if (index.isColumnIndex()) {
			int col = index.getColumn();
			layout.addColumn(col);
			for (int row = 0; row < model.getRowCount(); row++) {
				PTableCellIndex cellIndex = new PTableCellIndex(col, row);
				PCellComponent cell = getCellFactory().makeCellComponent(model, cellIndex);
				layout.addChild(cell, cellIndex);
			}
		} else {
			int row = index.getRow();
			layout.addRow(row);
			for (int col = 0; col < model.getColumnCount(); col++) {
				PTableCellIndex cellIndex = new PTableCellIndex(col, row);
				PCellComponent cell = getCellFactory().makeCellComponent(model, cellIndex);
				layout.addChild(cell, cellIndex);
			}
		}
	}
	
	protected void contentRemoved(PTableIndex index, Object oldContent) {
		System.out.println("PTable.contentRemoved idx="+index);
		
		PTableModel model = getModel();
		PTableLayout3 layout = getLayoutInternal();
		if (index.isColumnIndex()) {
			int col = index.getColumn();
			for (int row = model.getRowCount() - 1; row >= 0; row--) {
				PTableCellIndex cellIndex = new PTableCellIndex(col, row);
				layout.removeChild(cellIndex);
			}
		} else {
			int row = index.getRow();
			layout.removeRow(row);
			for (int col = model.getColumnCount() - 1; col >= 0; col--) {
				PTableCellIndex cellIndex = new PTableCellIndex(col, row);
				layout.removeChild(cellIndex);
			}
		}
	}
	
	protected void contentChanged(PTableCellIndex index, Object oldContent) {
		System.out.println("PTable.contentChanged idx="+index);
		getCellComponent(index).setElement(getModel(), index);
	}
	
	protected void selectionAdded(PTableCellIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(true);
		}
	}
	
	protected void selectionRemoved(PTableCellIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(false);
		}
	}
	
	public PCellComponent getCellComponent(PTableCellIndex index) {
		return (PCellComponent) getLayoutInternal().getChildForConstraint(index);
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(BACKGROUND_COLOR);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		renderer.setColor(PColor.BLACK);
		
		PTableLayout3 layout = getLayoutInternal();
		int colGapW = layout.getCellGapWidth();
		int rowGapH = layout.getCellGapHeight();
		
		PSize prefSize = layout.getPreferredSize();
		int sizeFx = x + prefSize.getWidth();
		int sizeFy = y + prefSize.getHeight();
		renderer.strokeQuad(x, y, sizeFx + colGapW, sizeFy + rowGapH);
		
		for (int c = 1; c < layout.getColumnCount(); c++) {
			int colW = layout.getColumnWidth(c);
			
			for (int r = 1; r < layout.getRowCount(); r++) {
				int cx = x + colW + (colW + colGapW) * (c - 1);
				int cfx = cx + colGapW;
				renderer.drawQuad(cx, y, cfx, sizeFy);
				
				int rowH = layout.getRowHeight(r);
				int cy = y + rowH + (rowH + rowGapH) * (r - 1);
				int cfy = cy + rowGapH;
				renderer.drawQuad(x, cy, sizeFx, cfy);
			}
		}
		if (hasFocus() && getSelection() != null) {
			PTableCellIndex lastSelectedIndex = getSelection().getLastSelected();
			if (lastSelectedIndex != null) {
				PCellComponent cellComp = getCellComponent(lastSelectedIndex);
//				System.out.println("cellComp="+cellComp);
				PBounds cellBounds = cellComp.getBounds();
				int cx = cellBounds.getX() - 1;
				int cy = cellBounds.getY() - 1;
				int cfx = cellBounds.getFinalX() + 1;
				int cfy = cellBounds.getFinalY() + 1;
				
				renderer.setColor(FOCUS_COLOR);
//				renderer.setColor(PColor.BLACK);
				renderer.setRenderMode(renderer.getRenderModeOutlineDashed());
				renderer.drawQuad(cx, cy, cfx, cfy);
			}
		}
	}
	
	public void addObs(PModelObs obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PModelObs obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	public void addObs(PSelectionObs obs) {
		selectionObsList.add(obs);
		if (getSelection() != null) {
			getSelection().addObs(obs);
		}
	}
	
	public void removeObs(PSelectionObs obs) {
		selectionObsList.remove(obs);
		if (getSelection() != null) {
			getSelection().removeObs(obs);
		}
	}

}