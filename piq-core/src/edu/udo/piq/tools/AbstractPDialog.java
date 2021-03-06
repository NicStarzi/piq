package edu.udo.piq.tools;

import edu.udo.piq.PCursor;
import edu.udo.piq.PDialog;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PImageMeta;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRoot;
import edu.udo.piq.style.PStyleSheet;

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
	public PFontResource fetchFontResource(Object fontID) {
		throwExceptionIfDisposed();
		return getSuperRoot().fetchFontResource(fontID);
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
	public boolean isImageSupported(PImageResource imageResource) {
		throwExceptionIfDisposed();
		return getSuperRoot().isImageSupported(imageResource);
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
	
	protected void throwExceptionIfDisposed() {
		if (isDisposed()) {
			throw new IllegalStateException(this+".isDisposed()");
		}
	}
	
}