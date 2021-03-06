package edu.udo.piq.tests.swing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.list.PList;
import edu.udo.piq.components.containers.PDropDownList;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.layouts.PBorderLayout;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPTextModel;
import edu.udo.piq.util.PModelFactory;

public class SwingPTest_Translation extends AbstractSwingPTest {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new SwingPTest_Translation();
	}
	
	private Map<Term, String> germanLanguageDictionary;
	private Map<Term, String> englishLanguageDictionary;
	private Map<Term, String> gibberishLanguageDictionary;
	private Map<Term, Map<Term, String>> translationMap;
	private PDropDownList languageList;
	private Map<Term, String> lastUsedTranslation;
	
	public SwingPTest_Translation() {
		super(480, 120);
	}
	
	@Override
	public void buildGUI() {
//		EnumPDictionary<Term> dict = new EnumPDictionary<>(Term.class);
//		dict.setTranslation(Term.CONFIRM, "Best�tigen");
		PModelFactory.setGlobalModelFactory(new PModelFactory());
		PModelFactory.getGlobalModelFactory().setDefaultModelFactoryFor(PLabel.class, TranslationTextModel::new);
		
		germanLanguageDictionary = new EnumMap<>(Term.class);
		germanLanguageDictionary.put(Term.CONFIRM, "Bestätigen");
		germanLanguageDictionary.put(Term.CANCEL, "Abbrechen");
		germanLanguageDictionary.put(Term.ENGLISH, "Englisch");
		germanLanguageDictionary.put(Term.GERMAN, "Deutsch");
		germanLanguageDictionary.put(Term.GIBBERISH, "Unsinn");
		germanLanguageDictionary.put(Term.GREETINGS, "Willkommen!");
		germanLanguageDictionary.put(Term.SELECT_LANGUAGE, "Wähle deine Sprache: ");
		germanLanguageDictionary.put(Term.NO_TERM, "<Kein Text>");
		
		englishLanguageDictionary = new EnumMap<>(Term.class);
		englishLanguageDictionary.put(Term.CONFIRM, "Confirm");
		englishLanguageDictionary.put(Term.CANCEL, "Cancel");
		englishLanguageDictionary.put(Term.ENGLISH, "English");
		englishLanguageDictionary.put(Term.GERMAN, "German");
		englishLanguageDictionary.put(Term.GIBBERISH, "Gibberish");
		englishLanguageDictionary.put(Term.GREETINGS, "Greetings!");
		englishLanguageDictionary.put(Term.SELECT_LANGUAGE, "Select your language: ");
		englishLanguageDictionary.put(Term.NO_TERM, "<No Text>");
		
		gibberishLanguageDictionary = new EnumMap<>(Term.class);
		gibberishLanguageDictionary.put(Term.CONFIRM, "shsasdasgas");
		gibberishLanguageDictionary.put(Term.CANCEL, "gsdgfsdasd");
		gibberishLanguageDictionary.put(Term.ENGLISH, "sdfhs");
		gibberishLanguageDictionary.put(Term.GERMAN, "sadf");
		gibberishLanguageDictionary.put(Term.GIBBERISH, "ashsfas");
		gibberishLanguageDictionary.put(Term.GREETINGS, "asdfhrzdhsddafdghjfghdgsdc!");
		gibberishLanguageDictionary.put(Term.SELECT_LANGUAGE, "sdghsfas: ");
		gibberishLanguageDictionary.put(Term.NO_TERM, "<sd thdfs>");
		
		translationMap = new EnumMap<>(Term.class);
		translationMap.put(Term.GERMAN, germanLanguageDictionary);
		translationMap.put(Term.ENGLISH, englishLanguageDictionary);
		translationMap.put(Term.GIBBERISH, gibberishLanguageDictionary);
		
		PPanel bodyPnl = new PPanel();
		bodyPnl.setLayout(new PBorderLayout(bodyPnl));
		root.setBody(bodyPnl);
		
		PPanel selectPnl = new PPanel();
		selectPnl.setLayout(new PListLayout(selectPnl, ListAlignment.LEFT_TO_RIGHT, 16));
		bodyPnl.addChild(selectPnl, PBorderLayout.BorderLayoutConstraint.TOP);
		
		PLabel lblGreetings = new PLabel(Term.GREETINGS);
		selectPnl.addChild(lblGreetings, null);
		
		PLabel lblSelectLng = new PLabel(Term.SELECT_LANGUAGE);
		selectPnl.addChild(lblSelectLng, null);
		
		languageList = new PDropDownList();
		languageList.getList().getModel().add(0, Term.GERMAN);
		languageList.getList().getModel().add(1, Term.ENGLISH);
		languageList.getList().getModel().add(2, Term.GIBBERISH);
		selectPnl.addChild(languageList, null);
		
		PPanel btnPnl = new PPanel();
		btnPnl.setLayout(new PListLayout(btnPnl, ListAlignment.CENTERED_LEFT_TO_RIGHT));
		bodyPnl.addChild(btnPnl, PBorderLayout.BorderLayoutConstraint.BOTTOM);
		
		PButton btnConfirm = new PButton();
		btnConfirm.addObs((PClickObs) (cmp) -> changeTranslation());
		btnConfirm.setContent(new PLabel(Term.CONFIRM));
		btnPnl.addChild(btnConfirm, null);
	}
	
	private void changeTranslation() {
		PList list = languageList.getList();
		List<PModelIndex> selectedIndices = list.getSelection().getAllSelected();
		
		if (selectedIndices.isEmpty()) {
			lastUsedTranslation = null;
		} else {
			Term selectedTerm = (Term) list.getModel().get(selectedIndices.get(0));
			lastUsedTranslation = translationMap.get(selectedTerm);
		}
		TranslationTextModel.setDictionary(lastUsedTranslation);
	}
	
	public static class TranslationTextModel extends AbstractPTextModel {
		
		private static Map<Term, String> dictionary = null;
		private static final List<TranslationTextModel> instances = new ArrayList<>();
		
		public static void setDictionary(Map<Term, String> newDictionary) {
			if (dictionary != newDictionary) {
				dictionary = newDictionary;
				for (TranslationTextModel model : instances) {
					model.fireChangeEvent(null);
				}
			}
		}
		
		private Term term = Term.NO_TERM;
		
		public TranslationTextModel() {
			instances.add(this);
		}
		
		@Override
		protected void setValueInternal(Object newValue) {
			term = (Term) newValue;
		}
		
		@Override
		public void setValue(Object value) {
			if (value != null && !(value instanceof Term)) {
				throw new IllegalArgumentException("value=" + value);
			}
			super.setValue(value);
		}
		
		@Override
		public Term getValue() {
			return term;
		}
		
		@Override
		public String getText() {
			Term actualTerm = term;
			if (actualTerm == null) {
				actualTerm = Term.NO_TERM;
			}
			if (dictionary == null) {
				return actualTerm.name();
			}
			String translation = dictionary.get(actualTerm);
			if (translation == null) {
				return actualTerm.name();
			}
			return translation;
		}
		
	}
	
	private static enum Term {
		
		NO_TERM, GREETINGS, SELECT_LANGUAGE, GERMAN, ENGLISH, GIBBERISH, CONFIRM, CANCEL,;
	
	}
	
}