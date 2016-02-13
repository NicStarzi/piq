package edu.udo.piq.components.popup;

public class PPopupSubMenuComponent {
	
	private PPopupBodyProvider bodyProvider;
	private PPopupBorderProvider borderProvider;
	private PPopupOptionsProvider optionsProvider;
	
	public PPopupSubMenuComponent() {
		setBodyProvider(PPopup.DEFAULT_BODY_PROVIDER);
		setBorderProvider(PPopup.DEFAULT_BORDER_PROVIDER);
	}
	
	public void setBodyProvider(PPopupBodyProvider provider) {
		bodyProvider = provider;
	}
	
	public PPopupBodyProvider getBodyProvider() {
		return bodyProvider;
	}
	
	public void setBorderProvider(PPopupBorderProvider provider) {
		borderProvider = provider;
	}
	
	public PPopupBorderProvider getBorderProvider() {
		return borderProvider;
	}
	
	public void setOptionsProvider(PPopupOptionsProvider provider) {
		optionsProvider = provider;
	}
	
	public PPopupOptionsProvider getOptionsProvider() {
		return optionsProvider;
	}
	
}