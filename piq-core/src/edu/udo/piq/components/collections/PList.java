package edu.udo.piq.components.collections;

import java.util.function.Function;
import java.util.function.Predicate;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PInsets;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouse.VirtualMouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.actions.FocusOwnerAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PAccelerator.KeyInputType;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.StandardComponentActionKey;
import edu.udo.piq.components.defaults.DefaultPCellComponent;
import edu.udo.piq.components.defaults.DefaultPCellFactory;
import edu.udo.piq.components.defaults.DefaultPDnDSupport;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class PList extends AbstractPLayoutOwner implements PDropComponent {
	
	protected static final PColor BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor FOCUS_COLOR = PColor.GREY25;
	protected static final PColor DROP_HIGHLIGHT_COLOR = PColor.RED;
	protected static final int DRAG_AND_DROP_DISTANCE = 20;
	
	public static final Predicate<PList> CONDITION_MOVE_SELECTION = self -> self.isEnabled()
			&& self.getModel() != null
			&& self.getSelection() != null
			&& self.getSelection().getLastSelected() != null;
	public static final PActionKey KEY_NEXT = StandardComponentActionKey.MOVE_NEXT;
	public static final PAccelerator ACCELERATOR_PRESS_DOWN = new PAccelerator(
			ActualKey.DOWN, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.PRESS);
	public static final PComponentAction ACTION_PRESS_DOWN = new FocusOwnerAction<>(
			PList.class, true,
			ACCELERATOR_PRESS_DOWN,
			CONDITION_MOVE_SELECTION,
			self -> self.moveSelectedIndex(1));
	
	public static final PActionKey KEY_PREV = StandardComponentActionKey.MOVE_PREV;
	public static final PAccelerator ACCELERATOR_PRESS_UP = new PAccelerator(
			ActualKey.UP, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.PRESS);
	public static final PComponentAction ACTION_PRESS_UP = new FocusOwnerAction<>(
			PList.class, true,
			ACCELERATOR_PRESS_UP,
			CONDITION_MOVE_SELECTION,
			self -> self.moveSelectedIndex(-1));
	
	protected final ObserverList<PModelObs> modelObsList = PiqUtil.createDefaultObserverList();
	protected final ObserverList<PSelectionObs> selectionObsList = PiqUtil.createDefaultObserverList();
	protected final PSelectionObs selectionObs = new PSelectionObs() {
		@Override
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			selectionAdded((PListIndex) index);
		}
		@Override
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			selectionRemoved((PListIndex) index);
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
			contentAdded((PListIndex) index, newContent);
		}
		@Override
		public void onContentRemoved(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			contentRemoved((PListIndex) index, oldContent);
			getSelection().removeSelection(index);
		}
		@Override
		public void onContentChanged(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			contentChanged((PListIndex) index, oldContent);
		}
	};
	protected PListSelection selection;
	protected PListModel model;
	protected Function<Object, String> encoder;
	protected PCellFactory cellFactory;
	protected PDnDSupport dndSup;
	protected PModelIndex currentDnDHighlightIndex;
	protected PCellComponent currentDnDHighlightComponent;
	protected int lastDragX = -1;
	protected int lastDragY = -1;
	protected boolean isDragTagged = false;
	protected boolean enabled = true;
	
	public PList(PListModel model) {
		this();
		setModel(model);
	}
	
	public PList() {
		super();
		
		setLayout(new PListLayout(this, ListAlignment.TOP_TO_BOTTOM, 1));
		setDragAndDropSupport(new DefaultPDnDSupport());
		setSelection(new PListMultiSelection());
		setCellFactory(new DefaultPCellFactory());
		setModel(PModelFactory.createModelFor(this, DefaultPListModel::new, PListModel.class));
		
		addObs(new PMouseObs() {
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PList.this.onMouseButtonTriggred(mouse, btn);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PList.this.onMouseReleased(mouse, btn);
			}
			@Override
			public void onMouseMoved(PMouse mouse) {
				PList.this.onMouseMoved(mouse);
			}
		});
		addObs(new ReRenderPFocusObs());
		
		addActionMapping(KEY_NEXT, ACTION_PRESS_DOWN);
		addActionMapping(KEY_PREV, ACTION_PRESS_UP);
		addActionMapping(KEY_COPY, ACTION_COPY);
		addActionMapping(KEY_CUT, ACTION_CUT);
		addActionMapping(KEY_PASTE, ACTION_PASTE);
		addActionMapping(KEY_DELETE, ACTION_DELETE);
	}
	
	protected void onMouseButtonTriggred(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOverThisOrChild(mouse)) {
			PListIndex index = getIndexAt(mouse.getX(), mouse.getY());
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
		if (dndSup != null && isDragTagged && mouse.isPressed(VirtualMouseButton.DRAG_AND_DROP)) {
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
	
	protected void moveSelectedIndex(int moveOffset) {
		PListIndex lastSelected = getSelection().getLastSelected();
		int nextSelectedVal = lastSelected.getIndexValue() + moveOffset;
		if (nextSelectedVal >= 0 && nextSelectedVal < getModel().getSize()) {
			PListIndex nextSelected = new PListIndex(nextSelectedVal);
			
			PKeyboard keyBoard = getKeyboard();
			if (keyBoard == null || !keyBoard.isModifierToggled(Modifier.CTRL)) {
				getSelection().clearSelection();
			}
			
			getSelection().addSelection(nextSelected);
		}
	}
	
	@Override
	protected PListLayout getLayoutInternal() {
		return (PListLayout) super.getLayout();
	}
	
	public void setAlignment(ListAlignment value) {
		getLayoutInternal().setAlignment(value);
	}
	
	public ListAlignment getAlignment() {
		return getLayoutInternal().getAlignment();
	}
	
	public void setGap(int value) {
		getLayoutInternal().setGap(value);
	}
	
	public int getGap() {
		return getLayoutInternal().getGap();
	}
	
	public void setInsets(PInsets value) {
		getLayoutInternal().setInsets(value);
	}
	
	public PInsets getInsets() {
		return getLayoutInternal().getInsets();
	}
	
	public void setSelection(PListSelection listSelection) {
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
	public PListSelection getSelection() {
		return selection;
	}
	
	public void setModel(PListModel listModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
			modelObsList.forEach(obs -> getModel().removeObs(obs));
		}
		model = listModel;
		getLayoutInternal().clearChildren();
		if (getModel() != null) {
			PListModel model = getModel();
			
			model.addObs(modelObs);
			modelObsList.forEach(obs -> model.addObs(obs));
			for (PModelIndex index : model) {
				contentAdded((PListIndex) index, model.get(index));
			}
		}
	}
	
	@Override
	public PListModel getModel() {
		return model;
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
	
	public boolean isSynchronizedWithModel() {
		PListModel model = getModel();
		
		int elementCount = model.getSize();
		int cellCount = getLayout().getChildCount();
		if (elementCount != cellCount) {
			return false;
		}
		
		for (PModelIndex index : model) {
			PCellComponent cell = getCellComponent((PListIndex) index);
			if (cell == null) {
				return false;
			}
			Object element = model.get(index);
			if (element != cell.getElement() && !element.equals(cell.getElement())) {
				return false;
			}
		}
		return true;
	}
	
	public void synchronizeWithModel() {
		getLayoutInternal().clearChildren();
		for (PModelIndex index : getModel()) {
			contentAdded((PListIndex) index, getModel().get(index));
		}
	}
	
	public void setOutputEncoder(Function<Object, String> outputEncoder) {
		encoder = outputEncoder;
		Function<Object, String> outEnc = getOutputEncoder();
		
		PCellFactory cellFactory = getCellFactory();
		if (cellFactory instanceof DefaultPCellFactory) {
			((DefaultPCellFactory) cellFactory).setOutputEncoder(outEnc);
		}
		
		PListModel model = getModel();
		if (model != null) {
			for (PModelIndex index : model) {
				PCellComponent cell = getCellComponent((PListIndex) index);
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
	
	public void setCellFactory(PCellFactory listCellFactory) {
		cellFactory = listCellFactory;
		
		Function<Object, String> outEnc = getOutputEncoder();
		PCellFactory cellFactory = getCellFactory();
		if (cellFactory instanceof DefaultPCellFactory) {
			((DefaultPCellFactory) cellFactory).setOutputEncoder(outEnc);
		}
		
		getLayoutInternal().clearChildren();
		PListModel model = getModel();
		if (model != null) {
			for (int i = 0; i < model.getSize(); i++) {
				contentAdded(new PListIndex(i), model.get(i));
			}
		}
	}
	
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
	public PListIndex getIndexAt(int x, int y) {
		if (getModel() == null) {
			return null;
		}
		PListLayout layout = getLayoutInternal();
		for (int i = 0; i < layout.getChildCount(); i++) {
			PComponent child = getLayoutInternal().getChild(i);
			if (layout.getChildBounds(child).contains(x, y)) {
				return new PListIndex(i);
			}
		}
		return null;
	}
	
	@Override
	public PListIndex getDropIndex(int x, int y) {
		PListIndex index = getIndexAt(x, y);
		if (index == null && getModel() != null) {
			if (getBounds().contains(x, y)) {
				return new PListIndex(getModel().getSize());
			}
		}
		return index;
	}
	
	@Override
	public void setDropHighlight(PModelIndex index) {
		if (currentDnDHighlightComponent != null) {
			currentDnDHighlightComponent.setDropHighlighted(false);
		}
		currentDnDHighlightIndex = index;
		if (index != null) {
			currentDnDHighlightComponent = getCellComponent((PListIndex) index);
			if (currentDnDHighlightComponent != null) {
				currentDnDHighlightComponent.setDropHighlighted(true);
			}
		}
	}
	
	public boolean isDropHighlighted() {
		return currentDnDHighlightComponent != null || currentDnDHighlightIndex != null;
	}
	
	protected void contentAdded(PListIndex index, Object newContent) {
		PListModel model = getModel();
		Integer layoutIndex = Integer.valueOf(index.getIndexValue());
		
		PCellComponent newCell = getCellFactory().makeCellComponent(model, index);
		getLayoutInternal().addChild(newCell, layoutIndex);
		
		PListLayout layout = getLayoutInternal();
		for (int i = layoutIndex + 1; i < layout.getChildCount(); i++) {
			PListIndex cellIndex = new PListIndex(i);
			PCellComponent cell = getCellComponent(cellIndex);
			cell.setElement(model, cellIndex);
		}
	}
	
	protected void contentRemoved(PListIndex index, Object oldContent) {
		getLayoutInternal().removeChild(getCellComponent(index));
		
		PListModel model = getModel();
		PListLayout layout = getLayoutInternal();
		for (int i = index.getIndexValue(); i < layout.getChildCount(); i++) {
			PListIndex cellIndex = new PListIndex(i);
			PCellComponent cell = getCellComponent(cellIndex);
			cell.setElement(model, cellIndex);
		}
	}
	
	protected void contentChanged(PListIndex index, Object oldContent) {
		getCellComponent(index).setElement(getModel(), index);
	}
	
	protected void selectionAdded(PListIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(true);
		}
	}
	
	protected void selectionRemoved(PListIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(false);
		}
	}
	
	public PCellComponent getCellComponent(PListIndex index) {
		return (PCellComponent) getLayoutInternal().getChild(index.getIndexValue());
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(BACKGROUND_COLOR);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
		
		// If highlighted but no cell component is highlighted => highlight the
		// end of the list
		if (isDropHighlighted() && currentDnDHighlightComponent == null) {
			renderer.setColor(DROP_HIGHLIGHT_COLOR);
			
			// Check whether there are any cell components in the list
			int lastIndex = getModel().getSize() - 1;
			if (lastIndex == -1) {
				// No components in the list, highlight top of the list
				renderer.drawQuad(x, y, fx, y + 2);
			} else {
				// Highlight bottom of the last cell component in the list
				PListIndex lastListIndex = new PListIndex(lastIndex);
				PCellComponent lastCellComp = getCellComponent(lastListIndex);
				PBounds lastCellBounds = lastCellComp.getBounds();
				int cx = lastCellBounds.getX();
				int cy = lastCellBounds.getFinalY();
				int cfx = lastCellBounds.getFinalX();
				int cfy = cy + 2;
				
				renderer.drawQuad(cx, cy, cfx, cfy);
			}
		}
		if (hasFocus() && getSelection() != null) {
			PListIndex lastSelectedIndex = getSelection().getLastSelected();
			if (lastSelectedIndex != null) {
				PCellComponent cellComp = getCellComponent(lastSelectedIndex);
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
	public boolean isFocusable() {
		return true;
	}
	
	@Override
	public boolean isStrongFocusOwner() {
		return true;
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