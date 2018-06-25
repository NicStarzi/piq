package edu.udo.piq.components.collections.table;

import java.util.function.Function;

import edu.udo.piq.components.PSingleValueModel;
import edu.udo.piq.components.PSingleValueModelObs;
import edu.udo.piq.components.PSingleValueModelOwner;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.util.PModelFactory;

public class PTableColumn<ELEM_T> implements PSingleValueModelOwner<Object, PTextModel> {
	
	/**
	 * <p>An observer for the {@link #model text model} for the header of this column.</p>
	 * <p>This observer should never be registered at any other model and it should only 
	 * ever be registered at exactly 1 model or none at all.</p>
	 */
	protected final PSingleValueModelObs<Object> modelObs = this::onModelValueChanged;
	/**
	 * <p>The initial index of this column. Set in the constructor and never changed.</p>
	 * <p>This must not necessarily correspond with the index this column is visually 
	 * displayed at. It is only the index from which content is sourced from the table model.</p>
	 */
	protected final PColumnIndex colIndex;
	protected final Class<ELEM_T> elemCls;
	/**
	 * <p>This model contains the text that is displayed for the column header.</p>
	 * <p>The {@link #modelObs} should always be registered at no other model than this.</p>
	 */
	protected PTextModel model;
	/**
	 * <p>This function converts contents from the table model to strings that are to be 
	 * displayed within the cells of the table. If null the {@link Object#toString()} 
	 * methods of the contents are used.</p>
	 */
	protected Function<Object, String> encoder = Object::toString;
	
	public PTableColumn(int columnIndex, Class<ELEM_T> elementClass) {
		this(new PColumnIndex(columnIndex), elementClass);
	}
	
	public PTableColumn(PColumnIndex columnIndex, Class<ELEM_T> elementClass) {
		colIndex = columnIndex;
		elemCls = elementClass;
		setModel(PModelFactory.createModelFor(this, DefaultPTextModel::new, PTextModel.class));
	}
	
	public PTableColumn(int columnIndex, Class<ELEM_T> elementClass, Object initialModelValue) {
		this(new PColumnIndex(columnIndex), elementClass, initialModelValue);
	}
	
	public PTableColumn(PColumnIndex columnIndex, Class<ELEM_T> elementClass, Object initialModelValue) {
		this(columnIndex, elementClass);
		setModelValue(initialModelValue);
	}
	
//	public PTableColumn(int columnIndex, Object initialModelValue, Function<Object, String> outputEncoder) {
//		this(new PColumnIndex(columnIndex), initialModelValue, outputEncoder);
//	}
	
//	public PTableColumn(PColumnIndex columnIndex, Object initialModelValue, Function<Object, String> outputEncoder) {
//		this(columnIndex);
//		setModelValue(initialModelValue);
//	}
	
	public PTableColumn(PColumnIndex columnIndex, Class<ELEM_T> elementClass, PTextModel initialModel) {
		colIndex = columnIndex;
		elemCls = elementClass;
		setModel(initialModel);
	}
	
//	public PTableColumn(PColumnIndex columnIndex, PTextModel initialModel, Function<Object, String> outputEncoder) {
//		this(columnIndex, initialModel);
//	}
	
	protected void onModelValueChanged(PSingleValueModel<Object> model, Object oldValue, Object newValue) {
//		firePreferredSizeChangedEvent();
//		fireReRenderEvent();
	}
	
	public PColumnIndex getColumnIndex() {
		return colIndex;
	}
	
	public Class<ELEM_T> getElementClass() {
		return elemCls;
	}
	
	@SuppressWarnings("unchecked")
	public ELEM_T getElementFrom(PTableModel model, int rowIndex) {
		Object elem = model.get(getColumnIndex().getColumn(), rowIndex);
		if (elem == null) {
			return null;
		}
		if (getElementClass().isInstance(elem)) {
			return (ELEM_T) elem;
		}
		throw new IllegalStateException("elementType="+elem.getClass()+"; expectedType="+getElementClass());
	}
	
	@Override
	public void setModel(PTextModel model) {
		PTextModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
//			modelObsList.fireEvent(obs -> oldModel.removeObs(obs));
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
//			modelObsList.fireEvent(obs -> model.addObs(obs));
		}
//		firePreferredSizeChangedEvent();
//		fireReRenderEvent();
	}
	
	@Override
	public PTextModel getModel() {
		return model;
	}
	
	public String getText() {
		if (getModel() == null) {
			return "";
		}
		Object text = getModel().getText();
		if (text == null) {
			return "";
		}
		return text.toString();
	}
	
}