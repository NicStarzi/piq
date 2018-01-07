package edu.udo.piq.components.collections;

import java.util.function.Function;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouse.VirtualMouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPCellComponent;
import edu.udo.piq.components.defaults.DefaultPCellFactory;
import edu.udo.piq.components.defaults.FixedSizePTableModel;
import edu.udo.piq.components.defaults.PTablePDnDSupport;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.dnd.PDnDSupport;
import edu.udo.piq.layouts.PTableLayout3;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;

public class PTable extends AbstractPLayoutOwner
	implements PDropComponent
{
	
	protected static final PColor BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor FOCUS_COLOR = PColor.GREY50;
	protected static final PColor DROP_HIGHLIGHT_COLOR = PColor.RED;
	protected static final int DRAG_AND_DROP_DISTANCE = 20;
	
	protected final ObserverList<PModelObs> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final ObserverList<PSelectionObs> selectionObsList
		= PiqUtil.createDefaultObserverList();
	protected final PSelectionObs selectionObs = new PSelectionObs() {
		@Override
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			selectionAdded((PTableCellIndex) index);
		}
		@Override
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			selectionRemoved((PTableCellIndex) index);
		}
		@Override
		public void onLastSelectedChanged(PSelection selection,
				PModelIndex prevLastSelected, PModelIndex newLastSelected)
		{
			if (hasFocus()) {
				fireReRenderEvent();
			}
		}
	};
	protected final PModelObs modelObs = new PModelObs() {
		@Override
		public void onContentAdded(PReadOnlyModel model, PModelIndex index, Object newContent) {
			getSelection().clearSelection();
			contentAdded((PTableIndex) index, null);
		}
		@Override
		public void onContentRemoved(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			getSelection().clearSelection();
			contentRemoved((PTableIndex) index, oldContent);
		}
		@Override
		public void onContentChanged(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			contentChanged((PTableCellIndex) index, oldContent);
		}
	};
	protected PTableSelection selection;
	protected PTableModel model;
	protected Function<Object, String> encoder;
	protected PCellFactory cellFactory;
	protected PDnDSupport dndSup;
	protected PModelIndex currentDnDHighlightIndex;
	protected PCellComponent currentDnDHighlightComponent;
	protected int lastDragX = -1;
	protected int lastDragY = -1;
	protected boolean isDragTagged = false;
	protected boolean enabled = true;
	
	public PTable(PTableModel model) {
		this();
		setModel(model);
	}
	
	public PTable() {
		super();
		setModel(PModelFactory.createModelFor(this, () -> new FixedSizePTableModel(0, 0), PTableModel.class));
		
		setLayout(new PTableLayout3(this));
		setDragAndDropSupport(new PTablePDnDSupport());
		setSelection(new PTableSingleSelection());
		setCellFactory(new DefaultPCellFactory());
		
		addObs(new PMouseObs() {
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PTable.this.onMouseButtonTriggred(mouse, btn);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PTable.this.onMouseReleased(mouse, btn);
			}
			@Override
			public void onMouseMoved(PMouse mouse) {
				PTable.this.onMouseMoved(mouse);
			}
		});
		addObs(new ReRenderPFocusObs());
	}
	
	protected void onMouseButtonTriggred(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOverThisOrChild(mouse)) {
			PTableCellIndex index = getIndexAt(mouse.getX(), mouse.getY());
			if (index != null) {
				if (mouse.isPressed(VirtualMouseButton.DRAG_AND_DROP)) {
					lastDragX = mouse.getX();
					lastDragY = mouse.getY();
					isDragTagged = true;
				}
				
				PKeyboard keyBoard = getKeyboard();
				if (keyBoard == null || !keyBoard.isModifierToggled(Modifier.CTRL)) {
					getSelection().clearSelection();
				}
				getSelection().addSelection(index);
				takeFocusNotFromDescendants();
			}
		}
	}
	
	protected void onMouseReleased(PMouse mouse, MouseButton btn) {
		if (isDragTagged && mouse.isReleased(VirtualMouseButton.DRAG_AND_DROP)) {
			isDragTagged = false;
		}
	}
	
	protected void onMouseMoved(PMouse mouse) {
		PDnDSupport dndSup = getDragAndDropSupport();
		if (dndSup != null && isDragTagged
				&& mouse.isPressed(VirtualMouseButton.DRAG_AND_DROP))
		{
			int mx = mouse.getX();
			int my = mouse.getY();
			int disX = Math.abs(lastDragX - mx);
			int disY = Math.abs(lastDragY - my);
			int dis = Math.max(disX, disY);
			if (dis >= DRAG_AND_DROP_DISTANCE) {
				if (dndSup.canDrag(this, mx, my)) {
					dndSup.startDrag(this, mx, my);
				}
			}
		}
	}
	
	@Override
	protected PTableLayout3 getLayoutInternal() {
		return (PTableLayout3) super.getLayout();
	}
	
	public void setSelection(PTableSelection listSelection) {
		if (getSelection() != null) {
			getSelection().clearSelection();
			getSelection().removeObs(selectionObs);
			selectionObsList.forEach(obs -> getSelection().removeObs(obs));
		}
		selection = listSelection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
			selectionObsList.forEach(obs -> getSelection().addObs(obs));
		}
	}
	
	@Override
	public PTableSelection getSelection() {
		return selection;
	}
	
	public void setModel(PTableModel listModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
			modelObsList.forEach(obs -> getModel().removeObs(obs));
		}
		model = listModel;
		rebuildCellComponents();
		if (getModel() != null) {
			PTableModel model = getModel();
			
			model.addObs(modelObs);
			modelObsList.forEach(obs -> model.addObs(obs));
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
	
	@Override
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
	
	@Override
	public PDnDSupport getDragAndDropSupport() {
		return dndSup;
	}
	
	@Override
	public void setEnabled(boolean value) {
		if (enabled != value) {
			enabled = value;
			fireReRenderEvent();
		}
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
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
	
	public void setOutputEncoder(Function<Object, String> outputEncoder) {
		encoder = outputEncoder;
		Function<Object, String> outEnc = getOutputEncoder();
		
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
	
	public Function<Object, String> getOutputEncoder() {
		return encoder;
	}
	
	@Override
	public boolean isFocusable() {
		return true;
	}
	
	@Override
	public boolean isStrongFocusOwner() {
		return true;
	}
	
	@Override
	public PTableCellIndex getIndexAt(int x, int y) {
		return getLayoutInternal().getIndexAt(x, y);
	}
	
	@Override
	public PTableCellIndex getDropIndex(int x, int y) {
		return getIndexAt(x, y);
	}
	
	@Override
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
		PTableModel model = getModel();
		int colCount = model.getColumnCount();
		int rowCount = model.getRowCount();
		PTableLayout3 layout = getLayoutInternal();
		if (index.isColumnIndex()) {
			int addedCol = index.getColumn();
			layout.addColumn(addedCol);
			for (int row = 0; row < rowCount; row++) {
				PTableCellIndex cellIndex = new PTableCellIndex(addedCol, row);
				PCellComponent cell = getCellFactory().makeCellComponent(model, cellIndex);
				layout.addChild(cell, cellIndex);
			}
			for (int col = addedCol + 1; col < colCount; col++) {
				for (int row = 0; row < rowCount; row++) {
					PTableCellIndex cellIndex = new PTableCellIndex(col, row);
					getCellComponent(cellIndex).setElement(model, cellIndex);
				}
			}
		} else {
			int addedRow = index.getRow();
			layout.addRow(addedRow);
			for (int col = 0; col < colCount; col++) {
				PTableCellIndex cellIndex = new PTableCellIndex(col, addedRow);
				PCellComponent cell = getCellFactory().makeCellComponent(model, cellIndex);
				layout.addChild(cell, cellIndex);
			}
			for (int row = addedRow + 1; row < rowCount; row++) {
				for (int col = 0; col < colCount; col++) {
					PTableCellIndex cellIndex = new PTableCellIndex(col, row);
					getCellComponent(cellIndex).setElement(model, cellIndex);
				}
			}
		}
	}
	
	protected void contentRemoved(PTableIndex index, Object oldContent) {
		PTableModel model = getModel();
		int colCount = model.getColumnCount();
		int rowCount = model.getRowCount();
		PTableLayout3 layout = getLayoutInternal();
		if (index.isColumnIndex()) {
			int removedCol = index.getColumn();
			layout.removeColumn(removedCol);
			for (int col = removedCol; col < colCount; col++) {
				for (int row = 0; row < rowCount; row++) {
					PTableCellIndex cellIndex = new PTableCellIndex(col, row);
					getCellComponent(cellIndex).setElement(model, cellIndex);
				}
			}
		} else {
			int removedRow = index.getRow();
			layout.removeRow(removedRow);
			for (int row = removedRow; row < rowCount; row++) {
				for (int col = 0; col < colCount; col++) {
					PTableCellIndex cellIndex = new PTableCellIndex(col, row);
					getCellComponent(cellIndex).setElement(model, cellIndex);
				}
			}
		}
	}
	
	protected void contentChanged(PTableCellIndex index, Object oldContent) {
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
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(BACKGROUND_COLOR);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		
		defaultRenderLines(renderer, x, y);
		
		defaultRenderFocus(renderer);
	}
	
	protected void defaultRenderLines(PRenderer renderer, int x, int y) {
		renderer.setColor(PColor.BLACK);
		
		PTableLayout3 layout = getLayoutInternal();
		int colCount = layout.getColumnCount();
		int rowCount = layout.getRowCount();
//		System.out.println("colCount="+colCount+", rowCount="+rowCount);
		int colGapW = layout.getCellGapWidth();
		int rowGapH = layout.getCellGapHeight();
		
		PSize prefSize = layout.getPreferredSize();
		int sizeFx = x + prefSize.getWidth();
		int sizeFy = y + prefSize.getHeight();
		renderer.strokeQuad(x, y, sizeFx + colGapW, sizeFy + rowGapH);
//		System.out.println("prefSize="+prefSize);
		
		if (colCount == 1) {
			for (int r = 1; r < rowCount; r++) {
				int rowH = layout.getRowHeight(r);
				int cy = y + rowH + (rowH + rowGapH) * (r - 1);
				int cfy = cy + rowGapH;
				renderer.drawQuad(x, cy, sizeFx, cfy);
			}
		} else if (rowCount == 1) {
			for (int c = 1; c < colCount; c++) {
				int colW = layout.getColumnWidth(c);
				
				int cx = x + colW + (colW + colGapW) * (c - 1);
				int cfx = cx + colGapW;
				renderer.drawQuad(cx, y, cfx, sizeFy);
			}
		} else {
			for (int c = 1; c < colCount; c++) {
				int colW = layout.getColumnWidth(c);
				
				for (int r = 1; r < rowCount; r++) {
					int cx = x + colW + (colW + colGapW) * (c - 1);
					int cfx = cx + colGapW;
					renderer.drawQuad(cx, y, cfx, sizeFy);
					
					int rowH = layout.getRowHeight(r);
					int cy = y + rowH + (rowH + rowGapH) * (r - 1);
					int cfy = cy + rowGapH;
					renderer.drawQuad(x, cy, sizeFx, cfy);
				}
			}
		}
	}
	
	protected void defaultRenderFocus(PRenderer renderer) {
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
				renderer.setRenderMode(renderer.getRenderModeOutlineDashed());
				renderer.drawQuad(cx, cy, cfx, cfy);
			}
		}
	}
	
	@Override
	public void addObs(PModelObs obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	@Override
	public void removeObs(PModelObs obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	@Override
	public void addObs(PSelectionObs obs) {
		selectionObsList.add(obs);
		if (getSelection() != null) {
			getSelection().addObs(obs);
		}
	}
	
	@Override
	public void removeObs(PSelectionObs obs) {
		selectionObsList.remove(obs);
		if (getSelection() != null) {
			getSelection().removeObs(obs);
		}
	}
	
}