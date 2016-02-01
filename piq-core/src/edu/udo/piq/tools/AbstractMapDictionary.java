package edu.udo.piq.tools;

import java.util.Map;
import java.util.Map.Entry;

import edu.udo.piq.components.util.PDictionary;
import edu.udo.piq.components.util.PDictionaryObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractMapDictionary<E> implements PDictionary {
	
	protected final ObserverList<PDictionaryObs> obsList = PCompUtil
			.createDefaultObserverList();
	
	protected abstract Map<E, String> getTranslationMap();
	
	public void setTranslation(E symbol, String translation) {
		getTranslationMap().put(symbol, translation);
		fireTranslationChanged(symbol);
	}
	
	public void setAllTranslations(Map<E, String> translationMap) {
		for (Entry<E, String> entry : translationMap.entrySet()) {
			getTranslationMap().put(entry.getKey(), entry.getValue());
		}
		fireDictionaryChanged();
	}
	
	public void addObs(PDictionaryObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PDictionaryObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireTranslationChanged(Object term) {
		obsList.fireEvent((obs) -> obs.onTranslationChanged(term));
	}
	
	protected void fireDictionaryChanged() {
		obsList.fireEvent((obs) -> obs.onDictionaryChanged());
	}
	
}