package edu.udo.piq.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

import edu.udo.piq.PRoot;

public class JFramePRootHighPerformance extends SwingPRoot implements PRoot {
	
//	private final JFrame frame;
//
//	public JFramePRootHighPerformance() {
//		super(new JFrame());
//		frame = (JFrame) super.getAwtComponent();
//
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setResizable(false);
////		frame.setAlwaysOnTop(true);
////		frame.setUndecorated(true);
//		frame.setSize(getScreenW(), getScreenH());
//		frame.setLocationRelativeTo(null);
//		frame.pack();
//		Insets borders = frame.getInsets();
//		int offsetW = borders.left + borders.right;
//		int offsetH = borders.top + borders.bottom;
//		frame.setSize(getScreenW() + offsetW, getScreenH() + offsetH);
//
//		frame.getContentPane().setIgnoreRepaint(true);
//		frame.setIgnoreRepaint(true);
//		frame.setFocusable(true);
//		frame.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent e) {
//				JFramePRootHighPerformance.this.onKeyStateChanged(e, true);
//			}
//			@Override
//			public void keyReleased(KeyEvent e) {
//				JFramePRootHighPerformance.this.onKeyStateChanged(e, false);
//			}
//		});
//	}
//
//	public void show() {
//		frame.setVisible(true);
//		gameLoop();
//	}
//
//	public int getScreenW() {
//		return 640;
//	}
//
//	public int getScreenH() {
//		return 480;
//	}
//
//	public void renderAll() {
//		int w = bounds.getWidth();
//		int h = bounds.getHeight();
//
//		Deque<RenderStackInfo> renderStack = AbstractPRoot.createRenderStack(this, reRenderSet, 0, 0, w, h);
//		AbstractPRoot.defaultRootRender(this, renderer, renderStack);
//		reRenderSet.clear();
//	}
//
//	private void gameLoop() {
//		BufferedImage drawImg = createAcceleratedImg(
//				getScreenW(), getScreenH(), BufferedImage.OPAQUE);
//		int frameX = frame.getInsets().left;
//		int frameY = frame.getInsets().top;
//		int frameW = frame.getContentPane().getWidth();
//		int frameH = frame.getContentPane().getHeight();
//
//		frame.createBufferStrategy(2);
//		BufferStrategy buffer = frame.getBufferStrategy();
//
//		long ts = System.nanoTime();
//		long lastTs = ts;
//
//		long timeSleeping = 15 * 1000 * 1000;
//		long timeYielding = 16 * 1000 * 1000;
//
//		// re-render every 60 frames.
//		int frameCount = 0;
//
//		while (frame.isDisplayable()) {
//			ts = System.nanoTime();
//			long timeSince = ts - lastTs;
////			System.out.println(timeSince);
//			if (timeSince < timeSleeping) {
//				// sleeping for 15 of 16 milliseconds
//				long sleepMillis = (timeYielding - timeSince) / 1_000_000;
////				System.out.println("sleep="+sleepMillis);
//				try {
//					Thread.sleep(sleepMillis);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				continue;
//			} else {
//				// busy waiting the last millisecond
//				while (timeSince < timeYielding) {
//					Thread.yield();
//					ts = System.nanoTime();
//					timeSince = ts - lastTs;
//				}
//			}
//			lastTs = ts;
//
//			update(timeSince / 1000000.0);
//			reLayOutAll(10);
//			frameCount++;
//
//			boolean render;
//			boolean lost = false;
//			do {
//				boolean restored = false;
//				do {
//					render = frameCount % 60 == 0 || lost || restored || reRenderSet.getSize() > 0;
//					if (render) {
////						System.out.println("render");
//						Graphics2D drawG = drawImg.createGraphics();
//						renderer.setAwtGraphics(drawG);
//						renderAll();
//						drawG.dispose();
//
//						Graphics bufferG = buffer.getDrawGraphics();
//						bufferG.drawImage(drawImg, frameX, frameY, frameW, frameH, null);
//						bufferG.dispose();
//					}
//					restored = buffer.contentsRestored();
//					if (restored) {
//						System.out.println("restored");
//					}
//				} while (restored);
//
//				if (render) {
//					buffer.show();
//				}
//				lost = buffer.contentsLost();
//				if (lost) {
//					System.out.println("lost");
//				}
//			} while (lost);
//		}
//	}
//
//	private void onKeyStateChanged(KeyEvent evt, boolean pressed) {
//		System.out.println(evt);
//	}
//
//	public static BufferedImage loadImage(String filePath) {
//		try {
//			filePath = "res/"+filePath;
//			if (!filePath.toLowerCase().endsWith(".png")) {
//				filePath = filePath + ".png";
//			}
//			File file = new File(filePath);
//
//			BufferedImage loadedImg = ImageIO.read(file);
//			return createAcceleratedImgCopy(loadedImg);
//		} catch (Exception e) {
//			System.err.println("Unable to load file '"+filePath+"; error="+e.getMessage());
//			return createAcceleratedImg(1, 1, BufferedImage.TYPE_INT_ARGB);
//		}
//	}
//
//	public static BufferedImage createAcceleratedImg(final int w, final int h, final int transparency) {
//		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		GraphicsDevice gd = ge.getDefaultScreenDevice();
//		GraphicsConfiguration gc = gd.getDefaultConfiguration();
//		BufferedImage compImg = gc.createCompatibleImage(w, h, transparency);
//		return compImg;
//	}
//
//	public static BufferedImage createAcceleratedImgCopy(final BufferedImage img) {
//		BufferedImage compImg = createAcceleratedImg(img.getWidth(),
//				img.getHeight(), img.getTransparency());
//		Graphics compG = compImg.createGraphics();
//		compG.drawImage(img, 0, 0, null);
//		compG.dispose();
//		return compImg;
//	}
	
	protected static final double MILLISECOND_FACTOR = 1000 * 1000;

	protected final JFrame frame;
	protected long timeSleeping = 15 * (long) MILLISECOND_FACTOR;
	protected long timeYielding = 16 * (long) MILLISECOND_FACTOR;
	protected int layoutMaxIterCount = 10;

	public JFramePRootHighPerformance() {
		super(new JFrame());
		frame = (JFrame) super.getAwtComponent();
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(new JComponent() {
			private static final long serialVersionUID = 1L;
			{setIgnoreRepaint(true);}
			@Override public void paintComponent(Graphics g) {}
		});
		frame.setIgnoreRepaint(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				onOpen();
			}
			@Override
			public void windowClosed(WindowEvent e) {
				onClose();
			}
		});
	}

	public JFrame getJFrame() {
		return frame;
	}

	public void dispose() {
		frame.dispose();
	}

	public void setVisible(boolean value) {
		getJFrame().setVisible(value);
	}

	protected void onOpen() {
//		new Thread(this::renderLoop).start();
//		renderLoop();
	}

	protected void onClose() {
	}

	protected boolean isRenderNeeded() {
		return reRenderSet.getSize() > 0;
	}

//	protected void renderAll(int w, int h) {
//		Deque<RenderStackInfo> renderStack = AbstractPRoot.createRenderStack(this, reRenderSet, 0, 0, w, h);
//		AbstractPRoot.defaultRootRender(this, renderer, renderStack);
////		reRenderSet.clear();
//	}

	public void renderLoop() {
		BufferedImage drawImg = createAcceleratedImg(
				640, 480, BufferedImage.OPAQUE);

		JFrame frame = getJFrame();
		GraphicsConfiguration graConf = frame.getGraphicsConfiguration();
		int frameW = bounds.getWidth();
		int frameH = bounds.getHeight();
//		VolatileImage drawBuf = graConf.createCompatibleVolatileImage(frameW, frameH);

		frame.createBufferStrategy(2);
		BufferStrategy buffer = frame.getBufferStrategy();

		long ts = System.nanoTime();
		long lastTs = ts;

		// force re-render every few frames.
		int frameCount = 0;

		while (frame.isDisplayable()) {
			ts = System.nanoTime();
			long timeSince = ts - lastTs;
//			System.out.println(timeSince);
			if (timeSince < timeSleeping) {
				// sleeping for timeSleeping milliseconds
				long sleepMillis = (timeYielding - timeSince) / (long) MILLISECOND_FACTOR;
//				System.out.println("sleep="+sleepMillis);
				try {
					Thread.sleep(sleepMillis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			} else {
				// busy waiting the last millisecond
				while (timeSince < timeYielding) {
//					System.out.println("yield="+timeSince);
					Thread.yield();
					ts = System.nanoTime();
					timeSince = ts - lastTs;
				}
			}
			lastTs = ts;

//			System.out.println("time="+(timeSince / MILLISECOND_FACTOR));
			update(timeSince / MILLISECOND_FACTOR);
			reLayOutAll(layoutMaxIterCount);
			frameCount++;

			graConf = frame.getGraphicsConfiguration();
			int frameX = frame.getInsets().left;
			int frameY = frame.getInsets().top;
			frameW = bounds.getWidth();
			frameH = bounds.getHeight();

			boolean render;
			boolean lost = false;
			/*
 do {
      int returnCode = vImg.validate(getGraphicsConfiguration());
      if (returnCode == VolatileImage.IMAGE_RESTORED) {
          // Contents need to be restored
          renderOffscreen();      // restore contents
      } else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
          // old vImg doesn't work with new GraphicsConfig; re-create it
          vImg = createVolatileImage(w, h);
          renderOffscreen();
      }
      gScreen.drawImage(vImg, 0, 0, this);
 } while (vImg.contentsLost());

			 */
			do {
				boolean restored = false;
				do {
					render = frameCount % 60 == 0 || lost || restored || isRenderNeeded();
					if (render) {
						System.out.println("render");
						Graphics2D drawG = drawImg.createGraphics();
						renderer.setAwtGraphics(drawG);
						fullRootRender();
						reRenderSet.clear();
						drawG.dispose();

//						do {
//							int valCode = drawBuf.validate(graConf);
//							if (valCode == VolatileImage.IMAGE_RESTORED) {
//								renderIntoBuffer(drawBuf, graConf, frameW, frameH);
//							} else if (valCode == VolatileImage.IMAGE_INCOMPATIBLE) {
//								drawBuf = graConf.createCompatibleVolatileImage(frameW, frameH);
//								renderIntoBuffer(drawBuf, graConf, frameW, frameH);
//							}
//						} while (drawBuf.contentsLost());
//						reRenderSet.clear();

						Graphics bufferG = buffer.getDrawGraphics();
//						Graphics2D bufferG = (Graphics2D) buffer.getDrawGraphics();
//						bufferG.translate(frameX, frameY);
//						renderer.setAwtGraphics(bufferG);
//						renderAll();
						bufferG.drawImage(drawImg, frameX, frameY, frameW, frameH, null);
						bufferG.dispose();
					}
					restored = buffer.contentsRestored();
					if (restored) {
						System.out.println("restored");
					}
				} while (restored);

				if (render) {
					buffer.show();
				}
				lost = buffer.contentsLost();
				if (lost) {
					System.out.println("lost");
				}
			} while (lost);
		}
	}
	
	protected void renderIntoBuffer(VolatileImage drawBuf, GraphicsConfiguration graConf, int frameW, int frameH) {
		if (drawBuf.validate(graConf)
				== VolatileImage.IMAGE_INCOMPATIBLE)
		{
			drawBuf = graConf.createCompatibleVolatileImage(frameW, frameH);
		}
		Graphics2D drawG = drawBuf.createGraphics();
		renderer.setAwtGraphics(drawG);
		fullRootRender();
		drawG.dispose();
	}
	
	public static BufferedImage createAcceleratedImg(final int w, final int h, final int transparency) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		BufferedImage compImg = gc.createCompatibleImage(w, h, transparency);
		return compImg;
	}
	
	public static BufferedImage createAcceleratedImgCopy(final BufferedImage img) {
		BufferedImage compImg = createAcceleratedImg(img.getWidth(),
				img.getHeight(), img.getTransparency());
		Graphics compG = compImg.createGraphics();
		compG.drawImage(img, 0, 0, null);
		compG.dispose();
		return compImg;
	}
	
}