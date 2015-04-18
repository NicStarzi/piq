package edu.udo.piq.components;

import java.util.ArrayList;
import java.util.List;

public class PRadioButtonGroup {
	
	private final List<PRadioButton> btnList = new ArrayList<>();
	private final PRadioButtonModelObs modelObs = new PRadioButtonModelObs() {
		public void onChange(PRadioButtonModel model) {
			System.out.println("onChange="+model.isSelected());
			if (model.isSelected()) {
				setSelected(model);
			}
		}
	};
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
		if (btnList.contains(radioBtn)) {
			throw new IllegalArgumentException("btnList.contains("+radioBtn+")=true");
		}
		radioBtn.addObs(modelObs);
		btnList.add(radioBtn);
		if (selectedModel != null && radioBtn.getModel() != null) {
			radioBtn.getModel().setSelected(true);
		} else {
			radioBtn.getModel().setSelected(false);
		}
	}
	
	public void remove(PRadioButton radioBtn) {
		if (btnList.remove(radioBtn)) {
			radioBtn.removeObs(modelObs);
		} else {
			throw new IllegalArgumentException("btnList.contains("+radioBtn+")=false");
		}
	}
	
	public void setSelected(PRadioButton button) {
		setSelected(button.getModel());
	}
	
	protected void setSelected(PRadioButtonModel model) {
		if (model == selectedModel) {
			return;
		}
		selectedModel = model;
		if (selectedModel != null && !selectedModel.isSelected()) {
			selectedModel.setSelected(true);
		} else {
			onModelChange();
		}
	}
	
	protected void onModelChange() {
		for (PRadioButton btn : btnList) {
			if (btn.getModel() != selectedModel) {
				btn.getModel().setSelected(false);
			}
		}
	}
	
}