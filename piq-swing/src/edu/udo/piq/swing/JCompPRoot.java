package edu.udo.piq.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PDialog;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PImageMeta;
import edu.udo.piq.PImageResource;
import edu.udo.piq.PRoot;
import edu.udo.piq.PStyleSheet;
import edu.udo.piq.tools.AbstractPRoot;
import edu.udo.piq.util.ThrowException;

public class JCompPRoot extends AbstractPRoot implements PRoot {
	
	private static final boolean SMART_RE_RENDER = true;
	
	private Window wnd;
	private final WindowListener wndListener = new WindowAdapter() {
		@Override
		public void windowIconified(WindowEvent e) {
			reRender(JCompPRoot.this);
		}
		@Override
		public void windowDeiconified(WindowEvent e) {
			reRender(JCompPRoot.this);
		}
	};
	private final JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;
		@Override
		public void addNotify() {
			super.addNotify();
			Component awtRoot = SwingUtilities.getRoot(panel);
			if (awtRoot != null && awtRoot instanceof Window) {
				if (wnd != null) {
					throw new IllegalStateException("wnd="+wnd);
				}
				wnd = (Window) awtRoot;
				wnd.addWindowListener(wndListener);
			}
		}
		@Override
		public void removeNotify() {
			super.removeNotify();
			if (wnd != null) {
				wnd.removeWindowListener(wndListener);
				wnd = null;
			}
		}
		@Override
		public void paintComponent(Graphics g) {
			render((Graphics2D) g);
		}
	};
	private final List<SwingPDialog> openedDialogs = new ArrayList<>();
	private final Map<FontInfo, AwtPFontResource> fontMap = new HashMap<>();
	private final Map<String, BufferedPImageResource> imgMap = new HashMap<>();
	private final SwingPRenderer renderer = new SwingPRenderer();
	private final SwingPMouse mouse = new SwingPMouse(this, panel);
	private final SwingPKeyboard keyboard = new SwingPKeyboard(panel);
	private final SwingPClipboard clipboard = new SwingPClipboard();
	private final PDnDManager dndManager = new PDnDManager(this);
	private final JPanelPBounds bounds = new JPanelPBounds(panel);
	protected double deltaTime = 0;
	
	public JCompPRoot() {
		super();
		panel.setFocusable(true);
		panel.requestFocus();
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				reRender(JCompPRoot.this);
				fireSizeChanged();
			}
			@Override
			public void componentResized(ComponentEvent e) {
				reRender(JCompPRoot.this);
				fireSizeChanged();
			}
		});
		super.mouse = mouse;
		super.keyboard = keyboard;
		super.clipboard = clipboard;
		super.dndManager = dndManager;
	}
	
	public JPanel getJPanel() {
		return panel;
	}
	
	/*
	 * Overwrites the protected super method
	 */
	@Override
	public void setStyleSheet(PStyleSheet styleSheet) {
		super.setStyleSheet(styleSheet);
	}
	
	@Override
	public void onMouseOverCursorChanged(PComponent component) {
		mouse.mouseOverCursorChanged(component);
	}
	
	@Override
	public double getDeltaTime() {
		return deltaTime;
	}
	
	@Override
	public void update(double deltaTime) {
		this.deltaTime = deltaTime;
		while (!openedDialogs.isEmpty()) {
			PDialog dlg = openedDialogs.get(openedDialogs.size() - 1);
			if (dlg.isDisposed()) {
				openedDialogs.remove(dlg);
			} else {
//				dlg.update();
//				mouse.update();
				return;
			}
		}
		super.update(deltaTime);
		
		mouse.update();
	}
	
	@Override
	public PDialog createDialog() {
		JDialog jDlg = new JDialog(SwingUtilities.getWindowAncestor(panel));
		jDlg.setSize(320, 240);
		jDlg.setLocationRelativeTo(panel);
		SwingPDialog pDlg = new SwingPDialog(this, jDlg);
		jDlg.setContentPane(pDlg.getPanel());
		
		openedDialogs.add(pDlg);
		
		return pDlg;
	}
	
	@Override
	public boolean isFontSupported(PFontResource font) {
		return font instanceof AwtPFontResource;
	}
	
	@Override
	public PFontResource fetchFontResource(String fontName, double pointSize, Style style)
			throws NullPointerException, IllegalArgumentException
	{
		FontInfo info = new FontInfo(fontName, pointSize, style);
		AwtPFontResource fontRes = fontMap.get(info);
		if (fontRes == null) {
			fontRes = new AwtPFontResource(new Font(fontName, getAwtStyle(info), (int) pointSize));
			fontMap.put(info, fontRes);
		}
		return fontRes;
	}
	
	private int getAwtStyle(FontInfo info) {
		Style style = info.getStyle();
		int awtStyle;
		switch (style) {
		case BOLD:
			awtStyle = Font.BOLD;
			break;
		case BOLD_ITALIC:
			awtStyle = Font.ITALIC | Font.BOLD;
			break;
		case ITALIC:
			awtStyle = Font.ITALIC;
			break;
		case PLAIN:
		default:
			awtStyle = Font.PLAIN;
		}
		return awtStyle;
	}
	
	@Override
	public boolean isImageSupported(PImageResource imageResource) {
		return imageResource instanceof BufferedPImageResource;
	}
	
	@Override
	public PImageResource fetchImageResource(Object imgID)
			throws NullPointerException
	{
		ThrowException.ifNull(imgID, "imgID == null");
		String imgPath;
		if (imgID instanceof String) {
			imgPath = (String) imgID;
		} else {
			imgPath = imgID.toString();
		}
		BufferedPImageResource imgRes = imgMap.get(imgPath);
		if (imgRes == null) {
			BufferedImage bufImg = null;
			try {
				bufImg = ImageIO.read(new File(imgPath));
			} catch (IOException e) {
				System.err.println(e.getMessage());
//				e.printStackTrace();
			}
			imgRes = new BufferedPImageResource(bufImg);
			imgMap.put(imgPath, imgRes);
		}
		return imgRes;
	}
	
	@Override
	public PImageResource createImageResource(int width, int height,
			PImageMeta metaInfo) throws IllegalArgumentException
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		BufferedPImageResource res = new BufferedPImageResource(img);
		return res;
	}
	
	@Override
	public PCursor createCustomCursor(PImageResource image, int offsetX,
			int offsetY) throws IllegalArgumentException {
		return null;
	}
	
	@Override
	public PBounds getBounds() {
		return bounds;
	}
	
	@Override
	public void reRender(PComponent component) {
		if (panel != null) {
//			System.out.println("JCompPRoot.reRender="+component);
			if (SMART_RE_RENDER) {
				reRenderSet.add(component);
			} else {
				reRenderSet.add(this);
			}
			panel.repaint();
		}
	}
	
	private void render(Graphics2D g) {
//		System.out.println("### RENDER ALL ###");
		renderer.setGraphics(g);
		PBounds bnds = getBounds();
		int rootFx = bnds.getWidth();
		int rootFy = bnds.getHeight();
		
		// A JCompPRoot always has its origin at (0,0) as per Swing convention
		defaultRootRender(renderer, 0, 0, rootFx, rootFy);
	}

	@Override
	public boolean isElusive() {
		return false;
	}
	
}