package edu.udo.piq.components.collections.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.components.AbstractPInteractiveLayoutOwner;
import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PDropComponent;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PModelObs;
import edu.udo.piq.components.collections.PReadOnlyModel;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.collections.PTableSelection;
import edu.udo.piq.components.collections.PTableSingleSelection;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.layouts.PTableLayout3;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;

public class PTableLike extends AbstractPInteractiveLayoutOwner implements PDropComponent {
	
	static class Person {
		String name;
		double height;
		int age;
		public Person(String name, double height, int age) {
			super();
			this.name = name;
			this.height = height;
			this.age = age;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public double getHeight() {
			return height;
		}
		public void setHeight(double height) {
			this.height = height;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
	}
	
	static void test() {
		PListTableModel<Person> personModel = new PListTableModel<>(Person::getName, Person::getAge, Person::getHeight);
		personModel.add(new Person("Alice", 1.70, 27));
		
		PTableColumn<String> name = new PTableColumn<>(0, String.class, "Name");
		PTableColumn<Integer> age = new PTableColumn<>(1, Integer.class, "Age");
		
		PFixedColumnTableModel model = new PFixedColumnTableModel(2);
		model.addRow("Alice", 27);
		model.addRow("Bob", 19);
		
		PTableLike table = new PTableLike();
		table.setColumns(name, age);
		table.setModel(model);
	}
	
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
			PTableLike.this.onSelectionAdded((PTableCellIndex) index);
		}
		@Override
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			PTableLike.this.onSelectionRemoved((PTableCellIndex) index);
		}
		@Override
		public void onLastSelectedChanged(PSelection selection,
				PModelIndex prevLastSelected, PModelIndex newLastSelected)
		{
			PTableCellIndex lastIdx = (PTableCellIndex) prevLastSelected;
			PTableCellIndex newIdx = (PTableCellIndex) newLastSelected;
			PTableLike.this.onLastSelectionChanged(lastIdx, newIdx);
		}
	};
	protected final PModelObs modelObs = new PModelObs() {
		@Override
		public void onContentAdded(PReadOnlyModel model, PModelIndex index, Object newContent) {
			PTableLike.this.onModelElementAdded((PTableCellIndex) index, newContent);
		}
		@Override
		public void onContentRemoved(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			PTableLike.this.onModelElementRemoved((PTableCellIndex) index, oldContent);
		}
		@Override
		public void onContentChanged(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			PTableLike.this.onModelElementChanged((PTableCellIndex) index, oldContent);
		}
	};
	protected final List<PTableColumn<?>> columns = new ArrayList<>();
	protected PTableSelection selection;
	protected PTableModel model;
	
	public PTableLike() {
		super();
		setModel(PModelFactory.createModelFor(this, () -> new PFixedColumnTableModel(0), PTableModel.class));
		
		setLayout(new PTableLayout3(this));
//		setDragAndDropSupport(new PTablePDnDSupport());
		setSelection(new PTableSingleSelection());
//		setCellFactory(new DefaultPCellFactory());
		
//		addObs(new PMouseObs() {
//			@Override
//			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
//				PTable.this.onMouseButtonTriggred(mouse, btn);
//			}
//			@Override
//			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
//				PTable.this.onMouseReleased(mouse, btn);
//			}
//			@Override
//			public void onMouseMoved(PMouse mouse) {
//				PTable.this.onMouseMoved(mouse);
//			}
//		});
		addObs(new ReRenderPFocusObs());
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
	
	public void setModel(PTableModel tableModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
			modelObsList.forEach(obs -> getModel().removeObs(obs));
		}
		model = tableModel;
		if (getModel() != null) {
			PTableModel model = getModel();
			
			model.addObs(modelObs);
			modelObsList.forEach(obs -> model.addObs(obs));
		}
	}
	
	@Override
	public PTableModel getModel() {
		return model;
	}
	
	public void setColumns(PTableColumn<?> ... columns) {
		setColumns(Arrays.asList(columns));
	}
	
	public void setColumns(Collection<PTableColumn<?>> columns) {
		setColumns((Iterable<PTableColumn<?>>) columns);
	}
	
	public void setColumns(Iterable<PTableColumn<?>> columns) {
		this.columns.clear();
		for (PTableColumn<?> col : columns) {
			this.columns.add(col);
		}
		checkForPreferredSizeChange();
	}
	
	public List<PTableColumn<?>> getColumns() {
		return Collections.unmodifiableList(columns);
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
	public void setDropHighlight(PModelIndex index) {}
	
	public PCellComponent getCellComponent(PTableCellIndex index) {
		return (PCellComponent) getLayoutInternal().getChildForConstraint(index);
	}
	
	public boolean isColumnVisible(int columnIndex) {
		for (PTableColumn<?> col : columns) {
			if (col.getColumnIndex().getColumn() == columnIndex) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isColumnVisible(PTableIndex index) {
		return isColumnVisible(index.getColumn());
	}
	
	@TemplateMethod
	protected void onSelectionAdded(PTableCellIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(true);
		}
	}
	
	@TemplateMethod
	protected void onSelectionRemoved(PTableCellIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(false);
		}
	}
	
	@TemplateMethod
	protected void onLastSelectionChanged(PTableCellIndex lastIndex, PTableCellIndex newIndex) {
		if (hasFocus()) {
			fireReRenderEvent();
		}
	}
	
	@TemplateMethod
	protected void onModelElementAdded(PTableCellIndex index, Object content) {
		if (!isColumnVisible(index)) {
			return;
		}
		getSelection().clearSelection();
	}
	
	@TemplateMethod
	protected void onModelElementRemoved(PTableCellIndex index, Object content) {
		if (!isColumnVisible(index)) {
			return;
		}
		getSelection().clearSelection();
	}
	
	@TemplateMethod
	protected void onModelElementChanged(PTableCellIndex index, Object content) {
		if (isColumnVisible(index)) {
			getCellComponent(index).setElement(getModel(), index);
		}
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		renderer.setColor(BACKGROUND_COLOR);
		renderer.drawQuad(getBoundsWithoutBorder());
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