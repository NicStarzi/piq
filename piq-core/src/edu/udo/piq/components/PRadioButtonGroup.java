package edu.udo.piq.components;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.util.ThrowException;

public class PRadioButtonGroup {
	
	private final List<PRadioButton> btnList = new ArrayList<>();
	private final PRadioButtonModelObs modelObs = this::onModelChange;
	private PRadioButtonModel selectedModel = null;
	
	public PRadioButtonGroup() {
		this(new PRadioButton[] {});
	}
	
	public PRadioButtonGroup(PRadioButton ... buttons) {
		for (PRadioButton btn : buttons) {
			add(btn);
		}
	}
	
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
	
	public void remove(PRadioButton radioBtn) {
		ThrowException.ifExcluded(btnList, radioBtn, "btnList.contains(radioBtn) == false");
		btnList.remove(radioBtn);
		radioBtn.removeObs(modelObs);
	}
	
	public void setSelected(PRadioButton button) {
		setSelected(button.getModel());
	}
	
	protected void onModelChange(PRadioButtonModel model) {
		if (model.isSelected()) {
			setSelected(model);
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