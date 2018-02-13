package edu.udo.piq.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.CallSuper;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.util.Throw;
import edu.udo.piq.util.ThrowException;

public class PRadioButtonGroup {
	
	private final List<PRadioButton> btnList = new ArrayList<>();
	private final PSingleValueModelObs<Boolean> modelObs = this::onModelChange;
	private PRadioButtonModel selectedModel = null;
	
	public PRadioButtonGroup() {
		this(Collections.emptyList());
	}
	
	public PRadioButtonGroup(Iterable<PRadioButtonTuple> buttons) {
		for (PRadioButtonTuple tuple : buttons) {
			add(tuple.getRadioButton());
		}
	}
	
	public PRadioButtonGroup(PRadioButton ... buttons) {
		for (PRadioButton btn : buttons) {
			add(btn);
		}
	}
	
	public PRadioButtonGroup(PRadioButtonTuple ... buttons) {
		for (PRadioButtonTuple tuple : buttons) {
			add(tuple.getRadioButton());
		}
	}
	
	public int getButtonCount() {
		return btnList.size();
	}
	
	@CallSuper
	public void clear() {
		for (PRadioButton btn : btnList) {
			btn.removeObs(modelObs);
		}
		btnList.clear();
		selectedModel = null;
	}
	
	@CallSuper
	public void add(PRadioButton radioBtn) {
		ThrowException.ifIncluded(btnList, radioBtn, "btnList.contains(radioBtn) == true");
		radioBtn.addObs(modelObs);
		btnList.add(radioBtn);
		if (selectedModel != null && radioBtn.getModel() != null) {
			radioBtn.getModel().setSelected(true);
		} else {
			radioBtn.getModel().setSelected(false);
		}
	}
	
	@CallSuper
	public void remove(PRadioButton radioBtn) {
		ThrowException.ifExcluded(btnList, radioBtn, "btnList.contains(radioBtn) == false");
		btnList.remove(radioBtn);
		radioBtn.removeObs(modelObs);
		if (radioBtn.getModel() == selectedModel) {
			selectedModel = null;
		}
	}
	
	public void selectFirst() {
		setSelected(0);
	}
	
	public void selectLast() {
		setSelected(getButtonCount() - 1);
	}
	
	public void setSelected(int index) {
		Throw.ifNotWithin(0, getButtonCount(), index,
				() -> "index == " + (index)
				+ "; min=" + (0) + "; max=" + (getButtonCount()));
		setSelected(btnList.get(index));
	}
	
	public void setSelected(PRadioButton button) {
		setSelected(button.getModel());
	}
	
	@TemplateMethod
	protected void onModelChange(PSingleValueModel<Boolean> model, Boolean oldValue, Boolean newValue) {
		PRadioButtonModel radBtnMdl = (PRadioButtonModel) model;
		if (radBtnMdl.isSelected()) {
			setSelected(radBtnMdl);
		}
	}
	
	protected void setSelected(PRadioButtonModel model) {
		if (model == selectedModel) {
			return;
		}
		selectedModel = model;
		if (selectedModel != null && !selectedModel.isSelected()) {
			selectedModel.setSelected(true);
		} else {
			onSelectionChanged();
		}
	}
	
	protected void onSelectionChanged() {
		for (PRadioButton btn : btnList) {
			if (btn.getModel() != selectedModel) {
				btn.getModel().setSelected(false);
			}
		}
	}
	
}