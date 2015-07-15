package edu.udo.piq.tutorial;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimerCallback;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PTimer;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.MutablePSize;

public class ClickSpeedTester extends AbstractPComponent {
	
	private static final String	DEFAULT_FONT_NAME = "Arial";
	private static final int	DEFAULT_FONT_SIZE = 16;
	private static final Style	DEFAULT_FONT_STYLE = Style.PLAIN;
	private static final int	DEFAULT_PADDING = 8;
	
	private static final int STATUS_NOT_STARTED	= 0;
	private static final int STATUS_COUNT_DOWN	= 1;
	private static final int STATUS_CLICK_READY	= 2;
	private static final int STATUS_SHOW_RESULT	= 3;
	private static final int STATUS_ERROR	= 4;
	
	private static final int TIMER_DELAY = 100;
	private static final int TIME_PER_STEP = 1;
	private static final int TIME_SHOW_RESULTS = 10;
	private static final int TIME_SHOW_ERROR = 5;
	
	private final MutablePSize prefSize = new MutablePSize();
	private final PTimer timer;
	private int status = STATUS_NOT_STARTED;
	private int step = -1;
	private long timeStamp;
	private long timeSince;
	
	public ClickSpeedTester() {
		addObs(new PMouseObs() {
			public void buttonTriggered(PMouse mouse, MouseButton btn) {
				if (btn == MouseButton.LEFT && isMouseOver()) {
					onClick();
				}
			}
		});
		timer = new PTimer(this, new PTimerCallback() {
			public void onTick() {
				ClickSpeedTester.this.onTick();
			}
		});
		timer.setRepeating(true);
	}
	
	private void onClick() {
		if (status == STATUS_NOT_STARTED) {
			startCountDown();
		} else if (status == STATUS_COUNT_DOWN) {
			displayError();
		} else if (status == STATUS_CLICK_READY) {
			displayResults();
		} else if (status == STATUS_SHOW_RESULT) {
			rewind();
		}
	}
	
	private void startCountDown() {
		step = 3;
		timer.setDelay(TIMER_DELAY * TIME_PER_STEP);
		timer.start();
		setStatus(STATUS_COUNT_DOWN);
	}
	
	private void displayError() {
		step = -1;
		timer.stop();
		timer.setDelay(TIMER_DELAY * TIME_SHOW_ERROR);
		timer.start();
		setStatus(STATUS_ERROR);
	}
	
	private void displayResults() {
		step = -1;
		timeSince = (System.nanoTime() - timeStamp) / 1000;
		timer.stop();
		timer.setDelay(TIMER_DELAY * TIME_SHOW_RESULTS);
		timer.start();
		setStatus(STATUS_SHOW_RESULT);
	}
	
	private void onTick() {
		if (status == STATUS_COUNT_DOWN) {
			countDown();
		} else if (status == STATUS_SHOW_RESULT) {
			rewind();
		} else if (status == STATUS_ERROR) {
			rewind();
		}
	}
	
	private void countDown() {
		step -= 1;
		if (step == 0) {
			timeStamp = System.nanoTime();
			timer.stop();
			setStatus(STATUS_CLICK_READY);
		} else {
			setStatus(STATUS_COUNT_DOWN);
		}
	}
	
	private void rewind() {
		step = -1;
		timer.stop();
		setStatus(STATUS_NOT_STARTED);
	}
	
	private void setStatus(int value) {
		status = value;
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	private String getText() {
		if (status == STATUS_NOT_STARTED) {
			return "Click to start!";
		} else if (status == STATUS_COUNT_DOWN) {
			return "" + step;
		} else if (status == STATUS_ERROR) {
			return "Too early! Wait for the count down.";
		} else if (status == STATUS_CLICK_READY) {
			return "NOW!";
		} else if (status == STATUS_SHOW_RESULT) {
			return "Reaction Time: "+timeSince+" micro seconds";
		}
		return "<Error>";
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.strokeTop(x, y, fx, fy);
		renderer.strokeLeft(x, y, fx, fy);
		renderer.setColor(PColor.BLACK);
		renderer.strokeRight(x, y, fx, fy);
		renderer.strokeBottom(x, y, fx, fy);
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		
		PFontResource font = getDefaultFont();
		if (font == null) {
			return;
		}
		String text = getText();
		PSize textSize = font.getSize(text);
		int txtX = x + bounds.getWidth() / 2 - textSize.getWidth() / 2;
		int txtY = y + bounds.getHeight() / 2 - textSize.getHeight() / 2;
		
		renderer.setColor(PColor.BLACK);
		renderer.drawString(font, text, txtX, txtY);
	}
	
	public PSize getDefaultPreferredSize() {
		PFontResource font = getDefaultFont();
		if (font == null) {
			prefSize.setWidth(DEFAULT_PADDING * 2);
			prefSize.setHeight(DEFAULT_PADDING * 2);
			return prefSize;
		}
		PSize textSize = font.getSize(getText());
		prefSize.setWidth(textSize.getWidth() + DEFAULT_PADDING * 2);
		prefSize.setHeight(textSize.getHeight() + DEFAULT_PADDING * 2);
		return prefSize;
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	private PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.fetchFontResource(DEFAULT_FONT_NAME, 
				DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
	}
	
}