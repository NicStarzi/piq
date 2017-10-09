package edu.udo.piq.swing.tests;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PSpinner;
import edu.udo.piq.components.collections.PColumnIndex;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PRowIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.collections.PTable;
import edu.udo.piq.components.collections.PTableMultiSelection;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPTableModel;
import edu.udo.piq.components.defaults.PSpinnerModelInt;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.components.textbased.PTextFieldObs;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PBorderLayout.Constraint;
import edu.udo.piq.layouts.PGridLayout;

public class SwingPTest_PTable extends AbstractSwingPTest {
	
	public static void main(String[] args) {
		new SwingPTest_PTable();
	}
	
	public SwingPTest_PTable() {
		super(640, 480);
	}
	
	public void buildGUI() {
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		/*
		 * Table & Model
		 */
		
		DefaultPTableModel tm = new DefaultPTableModel(3, 3);
		tm.set(1, 0, "Aaaa");
		tm.set(2, 1, "Bbbb");
		tm.set(0, 2, "Cccc");
		
		PTable table = new PTable(tm);
//		table.setOutputEncoder((element) -> Objects.toString(element, ""));
		table.setSelection(new PTableMultiSelection());
		bodyPnl.addChild(table, Constraint.CENTER);
		
		/*
		 * Control Elements
		 */
		
		PPanel ctrlPnl = new PPanel();
		bodyPnl.addChild(ctrlPnl, Constraint.BOTTOM);
		PGridLayout layout = new PGridLayout(ctrlPnl, 4, 3);
		ctrlPnl.setLayout(layout);
		
		PLabel lblCol = new PLabel("Column");
		ctrlPnl.addChild(lblCol, "0 0 alignX=R");
		
		PLabel lblRow = new PLabel("Row");
		ctrlPnl.addChild(lblRow, "0 1 alignX=R");
		
		PLabel lblValue = new PLabel("Value");
		ctrlPnl.addChild(lblValue, "0 2 alignX=R");
		
		PSpinner inputCol = new PSpinner(new PSpinnerModelInt(0, 0, 100));
		ctrlPnl.addChild(inputCol, "1 0 alignX=F");
		
		PSpinner inputRow = new PSpinner(new PSpinnerModelInt(0, 0, 100));
		ctrlPnl.addChild(inputRow, "1 1 alignX=F");
		
		PTextField inputValue = new PTextField();
		ctrlPnl.addChild(inputValue, "1 2 3 1 alignX=F");
		
		PButton btnAddColumn = new PButton(new PLabel("Add Column"));
		ctrlPnl.addChild(btnAddColumn, "2 0 alignX=F alignY=F");
		
		PButton btnRemoveColumn = new PButton(new PLabel("Remove Column"));
		ctrlPnl.addChild(btnRemoveColumn, "3 0 alignX=F alignY=F");
		
		PButton btnAddRow = new PButton(new PLabel("Add Row"));
		ctrlPnl.addChild(btnAddRow, "2 1 alignX=F alignY=F");
		
		PButton btnRemoveRow = new PButton(new PLabel("Remove Row"));
		ctrlPnl.addChild(btnRemoveRow, "3 1 alignX=F alignY=F");
		
		/*
		 * Observers & Program Logic
		 */
		
		btnAddColumn.addObs((PClickObs) (c) -> {
			int col = (int) inputCol.getModel().getValue();
			tm.add(new PColumnIndex(col), null);
		});
		btnRemoveColumn.addObs((PClickObs) (c) -> {
			int col = (int) inputCol.getModel().getValue();
			tm.remove(new PColumnIndex(col));
		});
		btnAddRow.addObs((PClickObs) (c) -> {
			int row = (int) inputRow.getModel().getValue();
			tm.add(new PRowIndex(row), null);
		});
		btnRemoveRow.addObs((PClickObs) (c) -> {
			int row = (int) inputRow.getModel().getValue();
			tm.remove(new PRowIndex(row));
		});
		table.addObs(new PSelectionObs() {
			public void onSelectionAdded(PSelection selection, PModelIndex index) {
				update();
			}
			public void onSelectionRemoved(PSelection selection, PModelIndex index) {
				update();
			}
			public void update() {
				String text;
				boolean enabled;
				PModelIndex index = table.getSelection().getOneSelected();
				if (index == null) {
					text = "<No Selection>";
					enabled = false;
				} else {
					Object selected = table.getModel().get(index);
					text = selected == null ? "null" : selected.toString();
					enabled = true;
				}
				inputValue.getModel().setValue(text);
				inputValue.setEnabled(enabled);
			}
		});
		inputValue.addObs((PTextFieldObs) self -> {
			PModelIndex index = table.getSelection().getOneSelected();
			table.getModel().set(index, self.getText());
		});
	}
	
}