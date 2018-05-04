package edu.udo.piq.components.containers;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PModelObs;
import edu.udo.piq.components.collections.PReadOnlyModel;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.collections.list.PList;
import edu.udo.piq.components.collections.list.PListIndex;
import edu.udo.piq.components.collections.list.PListModel;
import edu.udo.piq.components.collections.list.PListSingleSelection;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.util.ThrowException;

public class PDropDownList extends PDropDown {
	
	protected final PSelectionObs listSelectObs = new PSelectionObs() {
		@Override
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			setDisplayedIndex((PListIndex) index);
		}
		@Override
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			if (index.equals(getDisplayedIndex())) {
				setDisplayedIndex(null);
			}
		}
	};
	protected final PModelObs modelObs = new PModelObs() {
		@Override
		public void onContentAdded(PReadOnlyModel model, PModelIndex index,
				Object newContent)
		{
			if (getDisplayedIndex() == null) {
				getList().getSelection().addSelection(index);
			}
		}
		@Override
		public void onContentRemoved(PReadOnlyModel model, PModelIndex index,
				Object oldContent)
		{
			if (index.equals(displayedIndex)) {
				setDisplayedIndex(null);
				if (getList().getModel().getSize() > 0) {
					getList().getSelection().addSelection(new PListIndex(0));
				}
			}
		}
		@Override
		public void onContentChanged(PReadOnlyModel model, PModelIndex index,
				Object oldContent)
		{
			if (index.equals(displayedIndex)) {
				setDisplayedIndex((PListIndex) index);
			}
		}
	};
	protected final PKeyboardObs keyObs = new PKeyboardObs() {
		@Override
		public void onKeyTriggered(PKeyboard keyboard, ActualKey key) {
			if (isBodyVisible() && (key == ActualKey.ENTER || key == ActualKey.ESCAPE)) {
				hideDropDown();
			}
		}
	};
	protected PList list;
	protected PListIndex displayedIndex = null;
	
	public PDropDownList(Iterable<Object> initialContents) {
		this(new DefaultPListModel(initialContents));
	}
	
	public PDropDownList(Object ... initialContents) {
		this(new DefaultPListModel(initialContents));
	}
	
	public PDropDownList(PListModel initialModel) {
		this();
		getList().setModel(initialModel);
		if (initialModel.getSize() > 0) {
			setDisplayedContent(initialModel.get(0));
		}
	}
	
	public PDropDownList() {
		super();
		PList list = new PList();
		list.setDragAndDropSupport(null);
		list.setSelection(new PListSingleSelection());
		setList(list);
		setPreview(new PPreviewPLabel());
		
		addObs(new PDropDownObs() {
			@Override
			public void onBodyShown(PDropDown dropDown) {
				getBody().takeFocus();
			}
		});
		addObs(keyObs);
	}
	
	public void setList(PList listComponent) {
		if (getList() != null) {
			getList().removeObs(modelObs);
			getList().removeObs(listSelectObs);
		}
		list = listComponent;
		if (getList() != null) {
			listComponent.addObs(modelObs);
			listComponent.addObs(listSelectObs);
		}
		setBody(getList());
	}
	
	public PList getList() {
		return list;
	}
	
	public void setPreview(PPreviewComponent component) {
		setPreview((PComponent) component);
	}
	
	@Override
	public void setPreview(PComponent component) {
		ThrowException.ifTypeCastFails(component, PPreviewComponent.class,
				"!(component instanceof PPreviewComponent)");
		super.setPreview(component);
	}
	
	@Override
	public PPreviewComponent getPreview() {
		return (PPreviewComponent) super.getPreview();
	}
	
	public void setDisplayedIndex(int indexVal) {
		setDisplayedIndex(new PListIndex(indexVal));
	}
	
	public void setDisplayedIndex(PListIndex index) {
		displayedIndex = index;
		if (getPreview() == null) {
			return;
		}
		Object content = getDisplayedContent();
		getPreview().getModel().setValue(content);
	}
	
	public PListIndex getDisplayedIndex() {
		return displayedIndex;
	}
	
	public void setDisplayedContent(Object content) {
		PModelIndex index = getList().getModel().getIndexOf(content);
		ThrowException.ifNull(index, "index == null");
		setDisplayedIndex(ThrowException.ifTypeCastFails(index, PListIndex.class,
				"!(index instanceof PListIndex)"));
	}
	
	public Object getDisplayedContent() {
		PListIndex idx = getDisplayedIndex();
		if (idx == null) {
			return null;
		}
		return getList().getModel().get(idx);
	}
	
	public static class PPreviewPLabel extends PLabel implements PPreviewComponent {
		public PPreviewPLabel() {
			super();
		}
		public PPreviewPLabel(PTextModel model) {
			super(model);
		}
		public PPreviewPLabel(Object defaultModelValue) {
			super(defaultModelValue);
		}
	}
	
	public static interface PPreviewComponent extends PComponent {
		public PTextModel getModel();
	}
	
}