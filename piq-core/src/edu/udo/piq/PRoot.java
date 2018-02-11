package edu.udo.piq;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.borders.PTitleBorder;
import edu.udo.piq.components.PProgressBar;
import edu.udo.piq.components.containers.PGlassPanel;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.defaults.DefaultPDnDSupport;
import edu.udo.piq.components.popup.PMenuBar;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextArea;
import edu.udo.piq.components.util.StandardFontKey;
import edu.udo.piq.components.util.SymbolicFontKey;
import edu.udo.piq.components.util.SymbolicImageKey;
import edu.udo.piq.dnd.PDnDManager;
import edu.udo.piq.dnd.PDnDSupport;
import edu.udo.piq.dnd.PDnDTransfer;
import edu.udo.piq.layouts.PComponentLayoutData;
import edu.udo.piq.layouts.PLayout;
import edu.udo.piq.layouts.PLayoutPreference;
import edu.udo.piq.layouts.PReadOnlyLayout;
import edu.udo.piq.layouts.PRootLayout;
import edu.udo.piq.layouts.PRootLayout.Constraint;
import edu.udo.piq.style.PStyleBorder;
import edu.udo.piq.style.PStyleComponent;
import edu.udo.piq.style.PStyleSheet;
import edu.udo.piq.style.PStyleable;
import edu.udo.piq.tools.AbstractPTextComponent;
import edu.udo.piq.util.DepthFirstDescendantIterator;
import edu.udo.piq.util.PiqUtil;

/**
 * The root of a GUI tree. Such a root is also a {@link PComponent}.<br>
 * A {@link PRoot} holds information about how a GUI is supposed to be
 * rendered, how user input is obtained and how other system resources
 * can be accessed.<br>
 * This interface is usually implemented by a platform dependent third
 * party implementation. There is also an abstract default implementation
 * which can be used as a base for new PRoot implementations.
 * 
 * @author Nic Starzi
 */
public interface PRoot extends PComponent {

	/**
	 * <p>A root always returns itself as its root.</p>
	 * @return this root
	 * @author Nic
	 */
	@Override
	public default PRoot getRoot() {
		return this;
	}
	
	/**
	 * <p>Returns the {@link PReadOnlyLayout layout} of this {@link PRoot} which
	 * is always an instance of {@link PRootLayout}.</p>
	 * <p>The root layout supports a body, an {@link PRootOverlay overlay} and a {@link PMenuBar menu bar}.</p>
	 * 
	 * @return a non-null {@link PRootLayout}
	 * @see #setBody(PComponent)
	 * @see #getBody()
	 * @see #getOverlay()
	 * @see #getMenuBar()
	 * @author Nic
	 */
	@Override
	public PRootLayout getLayout();
	
	/**
	 * <p>A root never has a parent.
	 * Always throws an exception.</p>
	 * @param parent ignored
	 * @throws UnsupportedOperationException
	 * @author Nic
	 */
	@Override
	public default void setParent(PComponent parent)
			throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	/**
	 * <p>A root never has a parent.</p>
	 * @return always {@code null}
	 * @author Nic
	 */
	@Override
	public default PComponent getParent() {
		return null;
	}
	
	/**
	 * <p>A root is never laid out.</p>
	 * @return always {@code null}
	 * @author Nic
	 */
	@Override
	default PComponentLayoutData getLayoutData() {
		return null;
	}
	
	/**
	 * <p>A root is never laid out.</p>
	 * @return always {@code null}
	 * @author Nic
	 */
	@Override
	default PLayoutPreference getLayoutPreference() {
		return PComponent.super.getLayoutPreference();
	}
	
	/**
	 * <p>A root can not be styled.
	 * Always throws an exception.</p>
	 * @param style ignored
	 * @throws UnsupportedOperationException
	 * @author Nic
	 */
	@Override
	public default void setCustomStyle(PStyleComponent style)
			throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	/**
	 * <p>A root can not be styled. This method will always return {@code null}.</p>
	 * @return always {@code null}
	 * @author Nic
	 */
	@Override
	public default PStyleComponent getCustomStyle() {
		return null;
	}
	
	/**
	 * <p>A root can not be styled.
	 * Always throws an exception.</p>
	 * @param style ignored
	 * @throws UnsupportedOperationException
	 * @author Nic
	 */
	@Override
	public default void setInheritedStyle(PStyleComponent style)
			throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	/**
	 * <p>A root can not be styled. This method will always return {@code null}.</p>
	 * @return always {@code null}
	 * @author Nic
	 */
	@Override
	public default PStyleComponent getInheritedStyle() {
		return null;
	}
	
	/**
	 * <p>A root can not be styled.
	 * Always throws an exception.</p>
	 * @param value ignored
	 * @throws UnsupportedOperationException
	 * @author Nic
	 */
	@Override
	public default void setStyleID(Object value) {
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	/**
	 * <p>A root does not support drag and drop.</p>
	 * @return always {@code null}
	 * @author Nic
	 */
	@Override
	public default PDnDSupport getDragAndDropSupport() {
		return null;
	}
	
	@Override
	public default void addActionMapping(PActionKey featureKey, PComponentAction feature) {
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	@Override
	public default void removeActionMapping(PActionKey actionKey) {
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	@Override
	public default void clearActionMap() {
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	@Override
	public default PComponentAction getAction(PActionKey actionKey) {
		return null;
	}
	
	@Override
	public default Collection<PComponentAction> getAllActions() {
		return Collections.emptySet();
	}
	
	@Override
	public default boolean hasActionForKey(PActionKey actionKey) {
		return false;
	}
	
	@Override
	public default boolean hasAction(PComponentAction action) {
		return false;
	}
	
	/**
	 * <p>A root never has a {@link PBorder border}.</p>
	 * @return always {@code null}
	 * @author Nic
	 */
	@Override
	public default PBorder getBorder() {
		return null;
	}
	
	/**
	 * <p>A root is never rendered.
	 * Always throws an exception.</p>
	 * @param renderer ignored
	 * @throws UnsupportedOperationException
	 * @author Nic
	 */
	@Override
	public default void defaultRender(PRenderer renderer)
			throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("this instanceof PRoot");
	}
	
	/**
	 * <p>A root is never rendered. This value is meaningless.</p>
	 * @return true
	 * @author Nic
	 */
	@Override
	public default boolean defaultFillsAllPixels() {
		return true;
	}
	
	/**
	 * <p>A root is should never be picked. This value is always true.</p>
	 * @return true
	 * @author Nic
	 */
	@Override
	public default boolean isIgnoredByPicking() {
		return true;
	}
	
	/**
	 * <p>A root should never get focus. This value is always false.</p>
	 * @return false
	 * @author Nic
	 */
	@Override
	public default boolean isFocusable() {
		return false;
	}
	
	/**
	 * <p>A root can not be {@link #isIgnoredByPicking() picked}.
	 * Always throws an exception.</p>
	 * @param cursor ignored
	 * @throws UnsupportedOperationException
	 * @author Nic
	 */
	@Override
	public default void setMouseOverCursor(PCursor cursor) {
		throw new UnsupportedOperationException("PRoot");
	}
	
	/**
	 * <p>Returns the current {@link PBounds} of this root. The bounds of a
	 * root should always have an X- and Y-coordinate of 0. Since a root
	 * does not have a parent, its size is not defined by a layout.</p>
	 * 
	 * @return a non-null instance of {@link PBounds}.
	 * @author Nic
	 */
	@Override
	public PBounds getBounds();
	
	/**
	 * <p>Returns the current style sheet for the GUI of this root. A
	 * {@link PStyleSheet} is used to quickly change the style of all
	 * components within a GUI.</p>
	 * 
	 * <p>How the style sheet is set is not defined within this interface.
	 * A root implementation may not support the use of style sheets.</p>
	 * 
	 * @return an instance of {@link PStyleSheet} or null
	 * @author Nic
	 * @see PStyleSheet
	 * @see PStyleable
	 * @see PStyleComponent
	 * @see PStyleBorder
	 */
	public PStyleSheet getStyleSheet();
	
	/**
	 * <p>This method is called by a {@link PComponent component} when
	 * it needs to be re-rendered. After this method has been called
	 * the root will schedule a re-rendering of the GUI tree in parts
	 * or completely at some time in the near future.</p>
	 * 
	 * <p>User code does not need to ever call this method. It is intended
	 * for internal use only.</p>
	 * 
	 * @param component		the component that needs re-rendering
	 * @author Nic
	 * @see PRenderer
	 * @see PiqUtil#fireReRenderEventFor(PComponent)
	 */
	public void scheduleReRender(PComponent component);
	
	/**
	 * <p>This method is called by a {@link PComponent component} when
	 * its mouse-over {@link PCursor cursor} has changed. It may be needed
	 * by a root to correctly update the displayed mouse cursor.</p>
	 * 
	 * <p>User code does not need to ever call this method. It is intended
	 * for internal use only.</p>
	 * 
	 * @param component		the component for which the cursor has changed
	 * @author Nic
	 * @see PCursor
	 * @see #createCustomCursor(PImageResource, int, int)
	 * @see PMouse
	 * @see PMouse#getCurrentCursor()
	 * @see PMouse#getCursorDefault()
	 * @see PMouse#getCursorText()
	 * @see PMouse#getCursorBusy()
	 * @see PMouse#getCursorScroll()
	 * @see PMouse#getCursorHand()
	 * @see PComponent#getMouseOverCursor(PMouse)
	 * @see PComponent#setMouseOverCursor(PCursor)
	 */
	public void onMouseOverCursorChanged(PComponent component);
	
	/**
	 * <p>This method is called by a {@link PComponent component} which needs
	 * to have its {@link PLayout layout} done again. After this method has
	 * been called the root will schedule a layout operation for all layouts
	 * of the GUI tree in parts or completely at some time in the near future.</p>
	 * 
	 * <p>User code does not need to ever call this method. It is intended
	 * for internal use only.</p>
	 * 
	 * @param component					the component that requested the layouting
	 * @throws NullPointerException		if component is null
	 * @throws IllegalStateException	if component is not part of this GUI
	 * @author Nic
	 * @see PReadOnlyLayout
	 * @see PReadOnlyLayout#layOut()
	 * @see PLayout
	 * @see PComponent#getLayout()
	 * @see PComponent#redoLayOut()
	 * @see #reLayOutTheEntireGui()
	 */
	public void scheduleLayout(PComponent component);
	
	/**
	 * <p>Causes an immediate {@link PLayout#layOut() layout} of all {@link PLayout layouts}
	 * within the GUI of this root. This is a costly operation and should only ever be used
	 * in cases of incorrectly laid out layouts.</p>
	 * 
	 * @author Nic
	 * @see #scheduleLayout(PComponent)
	 * @see PReadOnlyLayout
	 * @see PReadOnlyLayout#layOut()
	 * @see PLayout
	 * @see PComponent#getLayout()
	 * @see PComponent#redoLayOut()
	 */
	public default void reLayOutTheEntireGui() {
		//TODO: Not a very nice solution but it works.
		for (PComponent comp : new DepthFirstDescendantIterator(this)) {
			PReadOnlyLayout layout = comp.getLayout();
			if (layout != null) {
				// it is important to invalidate the layout before calling the layOut method.
				layout.invalidate();
				layout.layOut();
			}
		}
	}
	
	/**
	 * <p>Creates an instance of a {@link PDialog} and returns it.
	 * The created dialog will use the same {@link PStyleSheet} as this
	 * root. The dialog is not disposed and not shown initially.</p>
	 * 
	 * <p>An implementation may decide to recycle dialog objects in
	 * which case the returned dialog may not be a newly created
	 * instance. References to dialogs should not be kept after they
	 * are disposed. No assumptions should be made about whether a
	 * created dialog is a new instance or not.</p>
	 * 
	 * @return an instance of {@link PDialog}
	 * @see PDialog
	 * @see PDialog#dispose()
	 * @see PDialog#show()
	 * @see #getStyleSheet()
	 * @author Nic
	 */
	public PDialog createDialog();
	
	/**
	 * <p>Returns an instance of {@link PFontResource} for the given
	 * font identifier. This method should always return the same
	 * resource for the same identifier. All font resource objects
	 * returned by this method are safe to be used for rendering
	 * within the GUI of this root. The validity of a font resource
	 * object can be checked with the {@link #isFontSupported(PFontResource)}
	 * method.</p>
	 * 
	 * <p>Common types of font identifiers include:<pre>
	 * * {@link SymbolicFontKey} which defines a unique constant
	 * * {@link StandardFontKey} which defines a font by name, size and style
	 * * {@link File} which should refer to a true type font file or similar font file
	 * * {@link String} which can be interpreted as the path to a file
	 * </pre>An implementation is not limited to only the above types of identifiers,
	 * nor is it required to support all of these.</p>
	 * 
	 * <p>The implementation of this method should try to not return
	 * {@code null}. A standard font resource should be provided for
	 * any unknown font identifiers. </p>
	 * 
	 * <p>Any implementation is supposed to support the preset font
	 * resources from the core library. These are:<pre>
	 * * {@link AbstractPTextComponent#FONT_ID}
	 * * {@link PLabel#FONT_ID}
	 * * {@link PTextArea#FONT_ID}
	 * * {@link PProgressBar#FONT_ID}
	 * * {@link PTitleBorder#FONT_ID}
	 * </pre>All of the above font identifiers are instances of {@link SymbolicFontKey}.
	 * </p>
	 * 
	 * <p>If {@link PDisposable#dispose() disposing} of font resources is necessary
	 * then either the root or the PFontResource implementation should take care of
	 * the disposal. Once disposed, a font resource should no longer be considered
	 * valid as defined by {@link #isFontSupported(PFontResource)}. The font identifier
	 * of the disposed font should still be able to fetch a valid font resource after
	 * disposal.</p>
	 * 
	 * @param fontID		an identifier for the font resource
	 * @return an instance of {@link PFontResource}
	 * @see PFontResource
	 * @see #isFontSupported(PFontResource)
	 * @see PRenderer#drawString(PFontResource, String, float, float)
	 * @author Nic
	 */
	public PFontResource fetchFontResource(Object fontID);
	
	/**
	 * <p>Checks if the given {@link PFontResource} can be used for
	 * {@link PRenderer#drawString(PFontResource, String, float, float) rendering}
	 * within the GUI of this root. Any font resource returned by the method
	 * {@link #fetchFontResource(Object)} of this root should be supported unless
	 * it has been {@link PDisposable#dispose() disposed}. If a font resource is
	 * not supported it must not be used for size calculations or rendering within
	 * the GUI of this root. A new font resource must be fetched via the
	 * {@link #fetchFontResource(Object)} method.</p>
	 * 
	 * @param font		a non-null instance of PFontResource
	 * @return			true if the given font resource can be used for rendering, otherwise false
	 * @throws			NullPointerException if font is null
	 * @see PFontResource
	 * @see #fetchFontResource(Object)
	 * @see PRenderer#drawString(PFontResource, String, float, float)
	 * @author Nic
	 */
	public boolean isFontSupported(PFontResource font);
	
	/**
	 * <p>Returns an instance of {@link PImageResource} for the given
	 * image identifier. This method should always return the same
	 * resource for the same identifier. All image resource objects
	 * returned by this method are safe to be used for rendering
	 * within the GUI of this root. The validity of an image resource
	 * object can be checked with the {@link #isImageSupported(PImageResource)}
	 * method.</p>
	 * 
	 * <p>Common types of image identifiers include:<pre>
	 * * {@link SymbolicImageKey} which defines a unique constant
	 * * {@link File} which should refer to an image file
	 * * {@link String} which can be interpreted as the path to a file
	 * </pre>An implementation is not limited to only the above types of identifiers,
	 * nor is it required to support all of these.</p>
	 * 
	 * <p>The implementation of this method should try to not return
	 * {@code null}. An empty standard image resource should be provided for
	 * any unknown image identifiers. </p>
	 * 
	 * <p>Any implementation is supposed to support the preset image
	 * resources from the core library. These are:<pre>
	 * * {@link DefaultPDnDSupport#IMAGE_ID_DND_POSSIBLE}
	 * * {@link DefaultPDnDSupport#IMAGE_ID_DND_IMPOSSIBLE}
	 * </pre>All of the above image identifiers are instances of {@link SymbolicImageKey}.
	 * </p>
	 * 
	 * <p>If {@link PDisposable#dispose() disposing} of image resources is necessary
	 * then either the root or the PImageResource implementation should take care of
	 * the disposal. Once disposed, an image resource should no longer be considered
	 * valid as defined by {@link #isImageSupported(PImageResource)}. The image identifier
	 * of the disposed image should still be able to fetch a valid image resource after
	 * disposal.</p>
	 * 
	 * @param imageID		an identifier for the image resource
	 * @return an instance of {@link PImageResource}
	 * @see PImageResource
	 * @see #isImageSupported(PImageResource)
	 * @see PRenderer#drawImage(PImageResource, float, float, float, float)
	 * @see PRenderer#drawImage(PImageResource, int, int, int, int, float, float, float, float)
	 * @author Nic
	 */
	
	public PImageResource fetchImageResource(Object imageID);
	
	/**
	 * <p>Checks if the given {@link PImageResource} can be used for
	 * {@link PRenderer#drawImage(PImageResource, float, float, float, float) rendering}
	 * within the GUI of this root. Any image resource returned by the method
	 * {@link #fetchImageResource(Object)} of this root should be supported unless
	 * it has been {@link PDisposable#dispose() disposed}. If an image resource is
	 * not supported it must not be used for size calculations or rendering within
	 * the GUI of this root. A new image resource must be fetched via the
	 * {@link #fetchImageResource(Object)} method.</p>
	 * 
	 * @param image		a non-null instance of {@link PImageResource}
	 * @return			true if the given image resource can be used for rendering, otherwise false
	 * @throws			NullPointerException if image is null
	 * @see PImageResource
	 * @see #fetchImageResource(Object)
	 * @see PRenderer#drawImage(PImageResource, float, float, float, float)
	 * @see PRenderer#drawImage(PImageResource, int, int, int, int, float, float, float, float)
	 * @author Nic
	 */
	public boolean isImageSupported(PImageResource image);
	
	
	/**
	 * 
	 * @param width				the width of the image, must be positive (> 0)
	 * @param height			the height of the image, must be positive (> 0)
	 * @param metaInfo			platform dependent meta information needed to create an image.
	 * 							<code>null</code> is always a valid input which will result in a
	 * 							default value being used.
	 * @return					a newly created {@link PImageResource}
	 * @throws IllegalArgumentException		if either width or height are less then or equal to 0
	 */
	public PImageResource createImageResource(int width, int height, PImageMeta metaInfo)
			throws IllegalArgumentException;
	
	/**
	 * Constructs and returns an instance of {@link PCursor} that uses the given
	 * image as its graphic. The offset is a translation of the image relative to
	 * the mouse location.<br>
	 * 
	 * @param image		an {@link PImageResource image resource} to be used for the cursors graphic
	 * @param offsetX	a translation on the X-axis in pixels relative to the mouse location, negative values go to the left
	 * @param offsetY	a translation on the Y-axis in pixels relative to the mouse location, negative values go to the left
	 * @return a newly constructed custom {@link PCursor}
	 * @throws IllegalArgumentException if image is null
	 * @see PComponent#getMouseOverCursor(PMouse)
	 * @see #fetchImageResource(Object imgID)
	 * @see #createImageResource(int width, int height, PImageMeta metaInfo)
	 */
	public PCursor createCustomCursor(PImageResource image, int offsetX, int offsetY) throws IllegalArgumentException;
	
	/**
	 * Returns an implementation of {@link PMouse}.<br>
	 * The implementation is platform dependent.<br>
	 * This method might return null if the GUI does not support a
	 * mouse.<br>
	 * 
	 * @return an implementation of {@link PMouse} or null if a mouse is not supported
	 * @see PMouse
	 * @see PKeyboard
	 */
	@Override
	public PMouse getMouse();
	
	/**
	 * Returns an implementation of {@link PKeyboard}.<br>
	 * The implementation is platform dependent.<br>
	 * This method might return null if the GUI does not support a
	 * keyboard.<br>
	 * 
	 * @return an implementation of {@link PKeyboard} or null if a keyboard is not supported
	 * @see PKeyboard
	 * @see PMouse
	 */
	@Override
	public PKeyboard getKeyboard();
	
	/**
	 * Returns an implementation of {@link PClipboard}.<br>
	 * The implementation is platform dependent.<br>
	 * This method might return null if the GUI does not support a
	 * clipboard.<br>
	 * 
	 * @return an implementation of {@link PClipboard} or null if a clipboard is not supported
	 */
	public PClipboard getClipboard();
	
	/**
	 * Returns the overlay for this {@link PRoot} or null if no overlay is supported.<br>
	 * The overlay is used (among other possible uses) for drop down menus, tool tips and the
	 * visual representation of a drag and drop.<br>
	 * Custom components are free to use the overlay for other effects that need to be drawn
	 * freely on top of the rest of the GUI.<br>
	 * If the returned overlay is not null it will never be null within the life cycle of
	 * this {@link PRoot}.<br>
	 * <br>
	 * A common default implementation for {@link PRootOverlay} is {@link PGlassPanel}.<br>
	 * 
	 * @return an instance of {@link PRootOverlay} or null if an overlay is not supported
	 * @see PGlassPanel
	 * @see PDnDManager
	 * @see PRootLayout
	 */
	public default PRootOverlay getOverlay() {
		return getLayout().getOverlay();
	}
	
	/**
	 * Sets the body {@link PComponent} of this {@link PRoot} to the given component.<br>
	 * Although it does not seem to be very useful it is possible to set the body to null.
	 * If the body of a root is null the GUI will not contain anything except the overlay
	 * and he root will fill the background with an arbitrary default color.<br>
	 * 
	 * @param component			the new body for this {@link PRoot} or null
	 * @see PRootLayout
	 */
	public default void setBody(PComponent component) {
		getLayout().setChildForConstraint(component, Constraint.BODY);
	}
	
	/**
	 * Returns the body {@link PComponent} of this {@link PRoot}, this is usually a
	 * {@link PPanel} but could be any kind of component or even null.<br>
	 * 
	 * @return					the body component of this root or null
	 * @see PRootLayout
	 */
	public default PComponent getBody() {
		return getLayout().getBody();
	}
	
	public default void setMenuBar(PComponent component) {
		getLayout().setChildForConstraint(component, Constraint.MENUBAR);
	}
	
	public default PComponent getMenuBar() {
		return getLayout().getMenuBar();
	}
	
	/**
	 * Returns the drag and drop manager for this {@link PRoot} or null if drag and drop is
	 * not supported.<br>
	 * The returned value for this method does never change during the life cycle of this
	 * {@link PRoot}.<br>
	 * <br>
	 * A drag and drop manager is used to manage the dragging and dropping of a
	 * {@link PDnDTransfer} after it was dispatched by a {@link PDnDSupport} of a component.<br>
	 * The drag and drop manager is also responsible for adding the visual representation of
	 * a drag to the {@link PRootOverlay} of this root.<br>
	 * <br>
	 * It does not make sense to support a drag and drop manager without a {@link PMouse}
	 * since the mouse is the standard way of controlling a drag and drop action. It is,
	 * however, possible to subclass the {@link PDnDManager} class to create custom drag
	 * and drop controls.<br>
	 * 
	 * @return an instance of {@link PDnDManager} or null if drag and drop is not supported
	 * @see PDnDSupport
	 * @see PDnDTransfer
	 * @see #getOverlay()
	 * @see PComponent#getDragAndDropSupport()
	 */
	@Override
	public PDnDManager getDragAndDropManager();
	
	/**
	 * Returns the currently active PFocusTraversal which is controlling how keyboard presses
	 * affect focus traversal.
	 * @return an instance of {@link PFocusTraversal} or null if keyboard controlled focus traversal is disabled at the moment.
	 * @see PFocusTraversal
	 * @see #getFocusTraversal()
	 * @see #setFocusOwner(PComponent)
	 * @see #getFocusOwner()
	 */
	public PFocusTraversal getActiveFocusTraversal();
	
	/**
	 * Returns the {@link PComponent} that currently has the focus within
	 * the GUI.<br>
	 * This can be null if no component has focus right now.<br>
	 * 
	 * @return the component with focus or null
	 * @see #setFocusOwner(PComponent)
	 */
	public PComponent getFocusOwner();
	
	public PComponent getLastStrongFocusOwner();
	
	/**
	 * Sets the focused {@link PComponent} of this GUI to the given component.<br>
	 * If <code>component</code> is null then no component will have focus anymore.<br>
	 * 
	 * @param component the new focused component or null
	 * @see #getFocusOwner()
	 */
	public void setFocusOwner(PComponent component);
	
	/**
	 * Returns the number of milliseconds in between the two most recent update ticks of
	 * all {@link PTimer PTimers}.
	 * @return	the milliseconds between the last two ticks of all PTimers
	 */
	public double getDeltaTime();
	
	/**
	 * Registers the given {@link PTimer} at this root.<br>
	 * A registered Timer will be ticked once every millisecond.<br>
	 * A timer can not be registered more then once.<br>
	 * 
	 * @param timer						the timer that is to be registered
	 * @throws NullPointerException		if timer is null
	 * @throws IllegalArgumentException	if timer was already registered
	 * @see PTimer
	 * @see #unregisterTimer(PTimer)
	 */
	public void registerTimer(PTimer timer) throws NullPointerException;
	
	/**
	 * Unregisters the given {@link PTimer} from this root.<br>
	 * The unregistered timer will no longer be ticked.<br>
	 * If the timer has not been registered before, an
	 * {@link IllegalArgumentException} will be thrown.<br>
	 * 
	 * @param timer						the timer that is to be unregistered
	 * @throws NullPointerException		if timer is null
	 * @throws IllegalArgumentException	if timer was not registered
	 * @see PTimer
	 * @see #registerTimer(PTimer)
	 */
	public void unregisterTimer(PTimer timer) throws NullPointerException;
	
	/**
	 * Registers a {@link PFocusObs} at this {@link PRoot}.<br>
	 * Whenever a {@link PComponent} takes or loses focus all registered focus
	 * observers will be notified.<br>
	 * 
	 * @param obs the observer to be registered
	 * @throws NullPointerException if obs is null
	 * @see #removeObs(PComponentObs)
	 * @see #getFocusOwner()
	 * @see #setFocusOwner(PComponent)
	 */
	@Override
	public void addObs(PFocusObs obs) throws NullPointerException;
	
	/**
	 * Unregisters a {@link PFocusObs} at this {@link PRoot}.<br>
	 * If the observer was not registered before this method does nothing.<br>
	 * 
	 * @param obs the observer to be registered
	 * @throws NullPointerException if obs is null
	 * @see #addObs(PComponentObs)
	 * @see #getFocusOwner()
	 * @see #setFocusOwner(PComponent)
	 */
	@Override
	public void removeObs(PFocusObs obs) throws NullPointerException;
	
	//TODO:
	public void addObs(PRootObs obs) throws NullPointerException;
	
	//TODO:
	public void removeObs(PRootObs obs) throws NullPointerException;
	
	public void fireComponentAddedToGui(PComponent addedComponent);
	
	public void fireComponentRemovedFromGui(PComponent parent, PComponent removedComponent);
	
	public void fireStyleSheetChanged(PStyleSheet oldStyleSheet);
}