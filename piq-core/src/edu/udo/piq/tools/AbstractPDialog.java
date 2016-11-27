package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PDialog;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PGlobalEventObs;
import edu.udo.piq.PImageMeta;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRoot;
import edu.udo.piq.PStyleSheet;

public abstract class AbstractPDialog extends AbstractPRoot implements PDialog {
	
	private boolean disposed = false;
	
	protected abstract PRoot getSuperRoot();
	
	@Override
	public void dispose() throws IllegalStateException {
		throwExceptionIfDisposed();
		disposed = true;
	}
	
	@Override
	public boolean isDisposed() {
		return disposed;
	}
	
	@Override
	public PDialog createDialog() {
		return getSuperRoot().createDialog();
	}
	
	@Override
	public PFontResource fetchFontResource(String fontName, double pointSize,
			Style style) throws NullPointerException
	{
		throwExceptionIfDisposed();
		return getSuperRoot().fetchFontResource(fontName, pointSize, style);
	}
	
	@Override
	public boolean isFontSupported(PFontResource font) {
		throwExceptionIfDisposed();
		return getSuperRoot().isFontSupported(font);
	}
	
	@Override
	public PImageResource fetchImageResource(Object imgID)
			throws NullPointerException
	{
		throwExceptionIfDisposed();
		return getSuperRoot().fetchImageResource(imgID);
	}
	
	@Override
	public PImageResource createImageResource(int width, int height,
			PImageMeta metaInfo) throws IllegalArgumentException
	{
		throwExceptionIfDisposed();
		return getSuperRoot().createImageResource(width, height, metaInfo);
	}
	
	@Override
	public PCursor createCustomCursor(PImageResource image, int offsetX, int offsetY) {
		throwExceptionIfDisposed();
		return getSuperRoot().createCustomCursor(image, offsetX, offsetY);
	}
	
	@Override
	protected void setStyleSheet(PStyleSheet styleSheet) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public PStyleSheet getStyleSheet() {
		throwExceptionIfDisposed();
		return getSuperRoot().getStyleSheet();
	}
	
	@Override
	public PMouse getMouse() {
		throwExceptionIfDisposed();
		return getSuperRoot().getMouse();
	}
	
	@Override
	public PKeyboard getKeyboard() {
		throwExceptionIfDisposed();
		return getSuperRoot().getKeyboard();
	}
	
	@Override
	public void fireGlobalEvent(PComponent source, Object eventData)
			throws NullPointerException
	{
		getSuperRoot().fireGlobalEvent(source, eventData);
	}
	
	@Override
	public void addObs(PGlobalEventObs obs) throws NullPointerException {
		getSuperRoot().addObs(obs);
	}
	
	@Override
	public void removeObs(PGlobalEventObs obs) throws NullPointerException {
		getSuperRoot().removeObs(obs);
	}
	
	protected void throwExceptionIfDisposed() {
		if (isDisposed()) {
			throw new IllegalStateException(this+".isDisposed()");
		}
	}
	
}