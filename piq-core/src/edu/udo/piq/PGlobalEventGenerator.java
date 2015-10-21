package edu.udo.piq;

public interface PGlobalEventGenerator extends PComponent {
	
	public void setGlobalEventProvider(PGlobalEventProvider provider);
	
	public PGlobalEventProvider getGlobalEventProvider();
	
	public default boolean hasGlobalEventProvider() {
		return getGlobalEventProvider() != null;
	}
	
	public default void fireGlobalEvent() {
		PGlobalEventProvider eventProvider = getGlobalEventProvider();
		if (eventProvider == null) {
			return;
		}
		PRoot root = getRoot();
		if (root == null) {
			return;
		}
		Object eventData = eventProvider.getEventDataFor(this);
		root.fireGlobalEvent(this, eventData);
	}
	
}