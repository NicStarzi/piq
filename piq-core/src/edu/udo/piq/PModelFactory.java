package edu.udo.piq;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import edu.udo.piq.borders.PTitleBorder;
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
import edu.udo.piq.components.defaults.DefaultPTableModel;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.defaults.DefaultPTreeModel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextArea;
import edu.udo.piq.tools.AbstractPTextComponent;

public class PModelFactory {
	
	private static PModelFactory modelFact = null;
	
	public static PModelFactory getGlobalModelFactory() {
		return modelFact;
	}
	
	public static void setGlobalModelFactory(PModelFactory modelFactory) {
		modelFact = modelFactory;
	}
	
	@SuppressWarnings("unchecked")
	public static <E> E createModelFor(Object component, E defaultModel) {
		return createModelFor(component, () -> defaultModel, (Class<E>) defaultModel.getClass());
	}
	
	public static <E> E createModelFor(Object component,
			Supplier<? extends E> defaultModel,
			Class<? extends E> expectedModelClass)
	{
		PModelFactory factory = getGlobalModelFactory();
		if (factory == null) {
			return defaultModel.get();
		}
		return factory.getModelFor(component, defaultModel, expectedModelClass);
	}
	
	protected final Map<Class<?>, Supplier<?>> classToModelFactoryMap = new HashMap<>();
	{
		classToModelFactoryMap.put(AbstractPTextComponent.class, DefaultPTextModel::new);
		classToModelFactoryMap.put(PLabel.class, DefaultPTextModel::new);
		classToModelFactoryMap.put(PButton.class, DefaultPButtonModel::new);
		classToModelFactoryMap.put(PCheckBox.class, DefaultPCheckBoxModel::new);
		classToModelFactoryMap.put(PRadioButton.class, DefaultPRadioButtonModel::new);
		classToModelFactoryMap.put(PSlider.class, DefaultPSliderModel::new);
		classToModelFactoryMap.put(PProgressBar.class, DefaultPProgressBarModel::new);
		classToModelFactoryMap.put(PPicture.class, DefaultPPictureModel::new);
		classToModelFactoryMap.put(PSplitPanel.class, DefaultPSplitPanelModel::new);
		classToModelFactoryMap.put(PTextArea.class, DefaultPTextModel::new);
		classToModelFactoryMap.put(PList.class, DefaultPListModel::new);
		classToModelFactoryMap.put(PTable.class, DefaultPTableModel::new);
		classToModelFactoryMap.put(PTree.class, DefaultPTreeModel::new);
		classToModelFactoryMap.put(PTitleBorder.class, DefaultPTextModel::new);
	}
	
	@SuppressWarnings("unchecked")
	public <E> E getModelFor(Object component,
			Supplier<? extends E> defaultModel,
			Class<? extends E> expectedModelClass)
	{
		Object result = createModelBasedOnClass(component.getClass());
		if (expectedModelClass.isInstance(result)) {
			return (E) result;
		}
		return defaultModel.get();
	}
	
	protected Object createModelBasedOnClass(Class<?> objClass) {
		Supplier<?> fact = classToModelFactoryMap.get(objClass);
		if (fact == null) {
			return null;
		}
		return fact.get();
	}
	
	public void setDefaultModelFactoryFor(Class<?> componentClass, Supplier<?> modelFactory) {
		classToModelFactoryMap.put(componentClass, modelFactory);
	}
	
	public Supplier<?> getDefaultModelFactoryFor(Class<?> componentClass) {
		return classToModelFactoryMap.get(componentClass);
	}
	
}