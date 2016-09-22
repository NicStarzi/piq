package edu.udo.piq.components.containers;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PTimer;
import edu.udo.piq.PTimerCallback;
import edu.udo.piq.layouts.PRingLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.ThrowException;

public class PRingMenu extends AbstractPLayoutOwner {
	
	public static final int DEFAULT_ANIMATION_DELAY = 1;
	
	protected List<PComponent> children = new ArrayList<>();
	protected PTimer openCloseTimer;
	protected double openedRot = 180;
	protected double closedRot = 0;
	protected int openTimeMillis = 500;
	protected int closeTimeMillis = 500;
	protected double openRad;
	protected double animStartRad;
	protected double animStartRot;
	protected int animDelay = DEFAULT_ANIMATION_DELAY;
	protected double animTimer = 0;
	protected boolean isOpen = false;
	
	public PRingMenu() {
		super();
		
		PRingLayout layout = new PRingLayout(this);
		setLayout(layout);
	}
	
	protected void setLayout(PLayout layout) {
		ThrowException.ifTypeCastFails(layout, PRingLayout.class, 
				"(layout instanceof PRingLayout) == false");
		super.setLayout(layout);
	}
	
	protected PRingLayout getLayoutInternal() {
		return (PRingLayout) super.getLayout();
	}
	
	public void setAnimationDelay(int value) {
		animDelay = value;
	}
	
	public int getAnimationDelay() {
		return animDelay;
	}
	
	public boolean isOpen() {
		return isOpen && openCloseTimer == null;
	}
	
	public boolean isClosed() {
		return !isOpen && openCloseTimer == null;
	}
	
	public boolean isOpening() {
		return isOpen && openCloseTimer != null;
	}
	
	public boolean isClosing() {
		return !isOpen && openCloseTimer != null;
	}
	
	public void addChild(PComponent component) {
		children.add(component);
		if (isOpen) {
			getLayoutInternal().addChild(component, null);
		}
	}
	
	public void removeChild(PComponent component) {
		children.remove(component);
		if (isOpen) {
			getLayoutInternal().removeChild(component);
		}
	}
	
	public void open() {
		if (isOpen) {
			return;
		}
		stopOpenCloseTimer();
		PRingLayout layout = getLayoutInternal();
		
		for (PComponent child : children) {
			layout.addChild(child, null);
		}
		
		isOpen = true;
		layout.setRotationInDeg(openedRot);
		openRad = layout.getPreferredRadius();
		layout.setRadius(0);
		layout.setRotationInDeg(0);
		
		startOpenCloseTimer(this::openAnimationStep);
	}
	
	protected void openAnimationStep(double deltaTime) {
		animTimer += deltaTime;
		double animPercent = animTimer / openTimeMillis;
		double radDiff = openRad - animStartRad;
		double rotDiff = openedRot - animStartRot;
		double radius = animStartRad + radDiff * animPercent;
		double rotation = animStartRot + rotDiff * animPercent;
		
		getLayoutInternal().setRadius(radius);
		getLayoutInternal().setRotationInDeg(rotation);
		if (animTimer >= openTimeMillis) {
			stopOpenCloseTimer();
		}
	}
	
	public void close() {
		if (!isOpen) {
			return;
		}
		stopOpenCloseTimer();
		startOpenCloseTimer(this::closeAnimationStep);
	}
	
	protected void closeAnimationStep(double deltaTime) {
		animTimer += deltaTime;
		double animPercent = animTimer / closeTimeMillis;
		double radDiff = -animStartRad;
		double rotDiff = closedRot - animStartRot;
		double radius = animStartRad + radDiff * animPercent;
		double rotation = animStartRot + rotDiff * animPercent;
		
		if (radius < 0) {
			radius = 0;
		}
		getLayoutInternal().setRadius(radius);
		getLayoutInternal().setRotationInDeg(rotation);
		if (animTimer >= closeTimeMillis) {
			onClosed();
		}
	}
	
	protected void onClosed() {
		stopOpenCloseTimer();
		getLayoutInternal().clearChildren();
		isOpen = false;
	}
	
	protected void startOpenCloseTimer(PTimerCallback callback) {
		PRingLayout layout = getLayoutInternal();
		animStartRad = layout.getRadius();
		animStartRot = layout.getRotationInDeg();
		
		animTimer = 0;
		openCloseTimer = new PTimer(this, callback);
		openCloseTimer.setDelay(getAnimationDelay());
		openCloseTimer.setRepeating(true);
		openCloseTimer.start();
	}
	
	protected void stopOpenCloseTimer() {
		if (openCloseTimer != null) {
			openCloseTimer.stop();
			openCloseTimer = null;
		}
	}
	
	public void defaultRender(PRenderer renderer) {
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(getBounds());
	}
	
}