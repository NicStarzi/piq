
package edu.udo.piq.components.collections.list;

import java.util.Objects;
import java.util.function.Function;

import edu.udo.piq.CallSuper;
import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PInsets;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouse.VirtualMouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.components.AbstractPInteractiveComponent;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PModelObs;
import edu.udo.piq.components.collections.PReadOnlyModel;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.defaults.DefaultPDnDSupport;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.components.util.SymbolicFontKey;
import edu.udo.piq.dnd.PDnDSupport;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.MutablePBounds;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.Throw;

public class PListHiPerf extends AbstractPInteractiveComponent implements PListLike {
	
	public static final Object FONT_ID = new SymbolicFontKey(PListHiPerf.class);
	protected static final PColor TEXT_COLOR = PColor.BLACK;
	protected static final PColor BACKGROUND_COLOR = PList.BACKGROUND_COLOR;
	protected static final PColor FOCUS_COLOR = PList.FOCUS_COLOR;
	protected static final PColor DROP_HIGHLIGHT_COLOR = PList.DROP_HIGHLIGHT_COLOR;
	protected static final int DRAG_AND_DROP_DISTANCE = PList.DRAG_AND_DROP_DISTANCE;
	
	public static interface PElementRenderer {
		public int getWidth(PRoot root, PModel model, PModelIndex index);
		public int getHeight(PRoot root);
		public void beforeRender(PRoot root, PRenderer renderer);
		public void renderBackground(PRoot root, PBounds maxElemBounds, PRenderer renderer, 
				PSelection selection, PModel model, PListIndex index);
		public void renderElement(PRoot root, PBounds maxElemBounds, PRenderer renderer, 
				PSelection selection, PModel model, PListIndex index);
		public void renderLastSelectedElement(PRoot root, PBounds maxElemBounds, 
				PRenderer renderer, PSelection selection, PModel model, PListIndex index);
		public void renderDropHighlightIndex(PRoot root, PBounds listBounds, PBounds maxElemBoundsAtIndex, 
				PRenderer renderer, PSelection selection, PModel model, PListIndex dropIdx);
		public void afterRender(PRoot root, PRenderer renderer);
	}
	/*
	 * This class is used to avoid unnecessary object allocation
	 * Instances of this class should _ONLY_ ever be used as arguments passed to 
	 * methods of the PElementRenderer interface. Instances should never be used 
	 * or cached outside of this PListHP.
	 */
	private static class MutableListIndex extends PListIndex {
		int index;
		public MutableListIndex() {
			super(0);
		}
		@Override
		public int getIndexValue() {
			return index;
		}
	}
	
	protected final ObserverList<PModelObs> modelObsList = PiqUtil.createDefaultObserverList();
	protected final ObserverList<PSelectionObs> selectionObsList = PiqUtil.createDefaultObserverList();
	protected final PSelectionObs selectionObs = new PSelectionObs() {
		@Override
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			PListHiPerf.this.onSelectionAdded((PListIndex) index);
		}
		@Override
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			PListHiPerf.this.onSelectionRemoved((PListIndex) index);
		}
		@Override
		public void onLastSelectedChanged(PSelection selection,
				PModelIndex prevLastSelected, PModelIndex newLastSelected)
		{
			PListHiPerf.this.onLastSelectedChanged(prevLastSelected, newLastSelected);
		}
	};
	protected final PModelObs modelObs = new PModelObs() {
		@Override
		public void onContentAdded(PReadOnlyModel model, PModelIndex index, Object newContent) {
			PListHiPerf.this.onContentAdded((PListIndex) index, newContent);
		}
		@Override
		public void onContentRemoved(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			PListHiPerf.this.onContentRemoved((PListIndex) index, oldContent);
		}
		@Override
		public void onContentChanged(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			PListHiPerf.this.onContentChanged((PListIndex) index, oldContent);
		}
	};
	protected final MutablePBounds clippedBounds = new MutablePBounds();
	protected final MutablePBounds tmpBounds = new MutablePBounds();
	protected final MutableListIndex tmpIdx = new MutableListIndex();
	protected PListSelection selection;
	protected PListModel model;
	protected Function<Object, String> encoder;
	protected PDnDSupport dndSup;
	protected PListIndex currentDnDHighlightIndex;
	protected PElementRenderer elemRenderer;
	protected PInsets contentInsets = PListLayout.DEFAULT_INSETS;
	protected int gap = PListLayout.DEFAULT_GAP;
	protected int lastDragX = -1;
	protected int lastDragY = -1;
	protected boolean isDragTagged = false;
	
	public PListHiPerf(PListModel model) {
		this();
		setModel(model);
	}
	
	public PListHiPerf() {
		super();
		
		setDragAndDropSupport(new DefaultPDnDSupport());
		setSelection(new PListMultiSelection());
		setElementRenderer(new PListHPDefaultElementRenderer());
		setModel(PModelFactory.createModelFor(this, DefaultPListModel::new, PListModel.class));
		
		addObs(new PMouseObs() {
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PListHiPerf.this.onMouseButtonTriggred(mouse, btn);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PListHiPerf.this.onMouseReleased(mouse, btn);
			}
			@Override
			public void onMouseMoved(PMouse mouse) {
				PListHiPerf.this.onMouseMoved(mouse);
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
	
	@CallSuper
	@TemplateMethod
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
	
	@CallSuper
	@TemplateMethod
	protected void onMouseReleased(PMouse mouse, MouseButton btn) {
		if (isDragTagged && mouse.isReleased(VirtualMouseButton.DRAG_AND_DROP)) {
			isDragTagged = false;
		}
	}
	
	@CallSuper
	@TemplateMethod
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
	
	public ListAlignment getAlignment() {
		return ListAlignment.TOP_TO_BOTTOM;
	}
	
	public void setGap(int value) {
		Throw.ifLess(0, value, () -> "value="+value);
		if (getGap() != value) {
			gap = value;
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	}
	
	public int getGap() {
		return gap;
	}
	
	public void setInsets(PInsets value) {
		Throw.ifNull(value, "value == null");
		contentInsets = value;
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public PInsets getInsets() {
		return contentInsets;
	}
	
	@Override
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
	
	@Override
	public void setModel(PListModel listModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
			modelObsList.forEach(obs -> getModel().removeObs(obs));
		}
		model = listModel;
		if (getModel() != null) {
			PListModel model = getModel();
			
			model.addObs(modelObs);
			modelObsList.forEach(obs -> model.addObs(obs));
			for (PModelIndex index : model) {
				onContentAdded((PListIndex) index, model.get(index));
			}
		}
	}
	
	@Override
	public PListModel getModel() {
		return model;
	}
	
	public void setElementRenderer(PElementRenderer renderer) {
		if (Objects.equals(getElementRenderer(), renderer)) {
			return;
		}
		Throw.ifNull(renderer, "value == null");
		elemRenderer = renderer;
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public PElementRenderer getElementRenderer() {
		return elemRenderer;
	}
	
	public void setOutputEncoder(Function<Object, String> outputEncoder) {
		if (Objects.equals(getOutputEncoder(), outputEncoder)) {
			return;
		}
		encoder = outputEncoder;
		checkForPreferredSizeChange();
		fireReRenderEvent();
	}
	
	public Function<Object, String> getOutputEncoder() {
		return encoder;
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
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		if (getModel() == null) {
			return null;
		}
		PBounds bnds = getBounds();
		PInsets insets = getInsets();
		int bndsX = bnds.getX() + insets.getFromLeft();
		int bndsY = bnds.getY() + insets.getFromTop();
		int bndsFx = bnds.getFinalX() - insets.getFromRight();
		int bndsFy = bnds.getFinalY() - insets.getFromBottom();
		if (x < bndsX || x > bndsFx || y < bndsY || y > bndsFy) {
			return null;
		}
		int gap = getGap();
		int elemH = getElementRenderer().getHeight(root);
		int index = Math.max(0, (y - bndsY)) / (elemH + gap);// - gap / 2
		return new PListIndex(index);
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
	
	public PBounds getBoundsForIndex(PListIndex index) {
		return getBoundsForIndex(index.getIndexValue(), null);
	}
	
	public PBounds getBoundsForIndex(int index) {
		return getBoundsForIndex(index, null);
	}
	
	public PBounds getBoundsForIndex(PListIndex index, MutablePBounds result) {
		return getBoundsForIndex(index.getIndexValue(), result);
	}
	
	public PBounds getBoundsForIndex(int index, MutablePBounds result) {
		PListModel model = getModel();
		if (model == null || !model.contains(index)) {
			return null;
		}
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		PBounds bounds = getBoundsWithoutBorder();
		PInsets insets = getInsets();
		int gap = getGap();
		int elemH = getElementRenderer().getHeight(root);
		int idxX = bounds.getX() + insets.getFromLeft();
		int idxY = bounds.getY() + insets.getFromTop() + index * (elemH + gap);
		int idxW = bounds.getWidth();
		int idxH = elemH;
		if (result == null) {
			return new ImmutablePBounds(idxX, idxY, idxW, idxH);
		}
		result.set(idxX, idxY, idxW, idxH);
		return result;
	}
	
	public boolean isIndexVisible(PListIndex index) {
		PBounds indexBounds = getBoundsForIndex(index, tmpBounds);
		if (indexBounds == null) {
			return false;
		}
		PBounds clippedBounds = PiqUtil.fillClippedBounds(this.clippedBounds, this);
		return clippedBounds != null && clippedBounds.hasIntersection(indexBounds);
	}
	
	@Override
	public void setDropHighlight(PModelIndex index) {
		currentDnDHighlightIndex = (PListIndex) index;
	}
	
	public boolean isDropHighlighted() {
		return currentDnDHighlightIndex != null;
	}
	
	@TemplateMethod
	protected void onContentAdded(PListIndex index, Object newContent) {
		getSelection().clearSelection();
		checkForPreferredSizeChange();
		fireReRenderEvent();
	}
	
	@TemplateMethod
	protected void onContentRemoved(PListIndex index, Object oldContent) {
		getSelection().removeSelection(index);
		checkForPreferredSizeChange();
		fireReRenderEvent();
	}
	
	@TemplateMethod
	protected void onContentChanged(PListIndex index, Object oldContent) {
		checkForPreferredSizeChange();
		fireReRenderEvent();
	}
	
	@TemplateMethod
	protected void onSelectionAdded(PListIndex index) {
		fireReRenderEvent();
	}
	
	@TemplateMethod
	protected void onSelectionRemoved(PListIndex index) {
		fireReRenderEvent();
	}
	
	@TemplateMethod
	protected void onLastSelectedChanged(PModelIndex prevLastSelected, PModelIndex newLastSelected) {
		if (hasFocus()) {
			fireReRenderEvent();
		}
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		PBounds clippedBounds = PiqUtil.fillClippedBounds(this.clippedBounds, this);
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(BACKGROUND_COLOR);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
		
		PRoot root = getRoot();
		if (root == null) {
			return;
		}
		PListModel model = getModel();
		if (model == null || model.isEmpty()) {
			return;
		}
		PListSelection selection = getSelection();
		PInsets insets = getInsets();
		x += insets.getFromLeft();
		y += insets.getFromTop();
		fx -= insets.getFromRight();
		fy -= insets.getFromBottom();
		
		PElementRenderer elemRender = getElementRenderer();
		int elemX = x;
		int elemY = y;
		int elemMaxW = fx - x;
		int elemH = elemRender.getHeight(root);
		int gap = getGap();
		int lineH = elemH + gap;
		
		int clippedY = clippedBounds.getY();
		int clippedH = clippedBounds.getHeight();
		int startIdx = Math.max(0, (clippedY - y)) / lineH;
		int idxCount = Math.min(model.getSize(), startIdx + ((clippedH + lineH - 1) / lineH));
		
		elemRender.beforeRender(root, renderer);
		// render all backgrounds
		for (int idx = startIdx; idx < idxCount; idx++) {
			tmpBounds.set(elemX, elemY, elemMaxW, elemH);
			tmpIdx.index = idx;
			elemRender.renderBackground(root, tmpBounds, renderer, selection, model, tmpIdx);
			elemY += elemH + gap;
		}
		// reset elemY variable which was changed in the background render loop
		elemY = y;
		// render all elements (icons, labels, etc)
		for (int idx = startIdx; idx < idxCount; idx++) {
			tmpBounds.set(elemX, elemY, elemMaxW, elemH);
			tmpIdx.index = idx;
			elemRender.renderElement(root, tmpBounds, renderer, selection, model, tmpIdx);
			elemY += elemH + gap;
		}
		// render drop highlight (if required)
		if (isDropHighlighted()) {
			renderer.setColor(DROP_HIGHLIGHT_COLOR);
			int dropIdx = currentDnDHighlightIndex.getIndexValue();
			if (dropIdx >= startIdx && dropIdx < idxCount) {
				PBounds cellBounds = getBoundsForIndex(dropIdx, tmpBounds);
				elemRender.renderDropHighlightIndex(root, clippedBounds, cellBounds, 
						renderer, selection, model, currentDnDHighlightIndex);
			}
		}
		// render highlight for last selected index (if required)
		if (hasFocus() && selection != null) {
			PListIndex lastSelectedIndex = selection.getLastSelected();
			if (lastSelectedIndex != null) {
				int idxVal = lastSelectedIndex.getIndexValue();
				if (idxVal >= startIdx && idxVal < idxCount) {
					PBounds cellBounds = getBoundsForIndex(lastSelectedIndex, tmpBounds);
					elemRender.renderLastSelectedElement(root, cellBounds, renderer, 
							selection, model, lastSelectedIndex);
				}
			}
		}
		elemRender.afterRender(root, renderer);
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