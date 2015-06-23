package edu.udo.piq.tools;

import edu.udo.piq.PDialog;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PDesignSheet;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRoot;

public abstract class AbstractPDialog extends AbstractPRoot implements PDialog {
	
	private boolean disposed = false;
	
	protected abstract PRoot getSuperRoot();
	
	public void dispose() throws IllegalStateException {
		throwExceptionIfDisposed();
		disposed = true;
	}
	
	public boolean isDisposed() {
		return disposed;
	}
	
	public PDialog createDialog() {
		return getSuperRoot().createDialog();
	}
	
	public PFontResource fetchFontResource(String fontName, double pointSize,
			Style style) throws NullPointerException 
	{
		throwExceptionIfDisposed();
		return getSuperRoot().fetchFontResource(fontName, pointSize, style);
	}
	
	public PImageResource fetchImageResource(String imgPath)
			throws NullPointerException 
	{
		throwExceptionIfDisposed();
		return getSuperRoot().fetchImageResource(imgPath);
	}
	
	protected void setDesignSheet(PDesignSheet designSheet) {
		throw new UnsupportedOperationException();
	}
	
	public PDesignSheet getDesignSheet() {
		throwExceptionIfDisposed();
		return getSuperRoot().getDesignSheet();
	}
	
	public PMouse getMouse() {
		throwExceptionIfDisposed();
		return getSuperRoot().getMouse();
	}
	
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