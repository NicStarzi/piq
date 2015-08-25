package edu.udo.piq;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.PProgressBar;
import edu.udo.piq.components.PRadioButton;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.collections.PTable;
import edu.udo.piq.components.collections.PTree;
import edu.udo.piq.components.containers.PSplitPanel;
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.components.defaults.DefaultPCheckBoxModel;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPPictureModel;
import edu.udo.piq.components.defaults.DefaultPProgressBarModel;
import edu.udo.piq.components.defaults.DefaultPRadioButtonModel;
import edu.udo.piq.components.defaults.DefaultPSliderModel;
import edu.udo.piq.components.defaults.DefaultPSplitPanelModel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.defaults.DefaultPTreeModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextArea;

public abstract class PModelFactory {
	
	private static PModelFactory modelFact = null;
	
	public static PModelFactory getGlobalModelFactory() {
		return modelFact;
	}
	
	public static void setGlobalModelFactory(PModelFactory modelFactory) {
		modelFact = modelFactory;
	}
	
	public Object getModelFor(PComponent component, Object defaultModel) {
		if (component instanceof PLabel) {
			return getLabelModel();
		} if (component instanceof PButton) {
			return getButtonModel();
		} if (component instanceof PCheckBox) {
			return getCheckBoxModel();
		} if (component instanceof PRadioButton) {
			return getRadioButtonModel();
		} if (component instanceof PSlider) {
			return getSliderModel();
		} if (component instanceof PProgressBar) {
			return getProgressBarModel();
		} if (component instanceof PPicture) {
			return getPictureModel();
		} if (component instanceof PSplitPanel) {
			return getSplitPanelModel();
		} if (component instanceof PTextArea) {
			return getTextAreaModel();
		} if (component instanceof PList) {
			return getListModel();
		} if (component instanceof PTable) {
			return getTableModel();
		} if (component instanceof PTree) {
			return getTreeModel();
		}
		return defaultModel;
	}
	
	protected Object getLabelModel() {
		return new DefaultPTextModel();
	}
	
	protected Object getButtonModel() {
		return new DefaultPButtonModel();
	}
	
	protected Object getCheckBoxModel() {
		return new DefaultPCheckBoxModel();
	}
	
	protected Object getSliderModel() {
		return new DefaultPSliderModel();
	}
	
	protected Object getRadioButtonModel() {
		return new DefaultPRadioButtonModel();
	}
	
	protected Object getProgressBarModel() {
		return new DefaultPProgressBarModel();
	}
	
	protected Object getPictureModel() {
		return new DefaultPPictureModel();
	}
	
	protected Object getSplitPanelModel() {
		return new DefaultPSplitPanelModel();
	}
	
	protected Object getTextAreaModel() {
		return new DefaultPTextModel();
	}
	
	protected Object getListModel() {
		return new DefaultPListModel();
	}
	
	protected Object getTableModel() {
//		return new DefaultPTableModel();
		return null;
	}
	
	protected Object getTreeModel() {
		return new DefaultPTreeModel();
	}
	
}