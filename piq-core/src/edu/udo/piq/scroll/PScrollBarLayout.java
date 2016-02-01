package edu.udo.piq.scroll;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.MutablePSize;

public class PScrollBarLayout extends AbstractMapPLayout implements PLayout {
	
	private static final Class<?>[] compClasses = new Class<?>[Constraint.values().length];
	static {
		compClasses[Constraint.BTN1.ordinal()] = PScrollBarButton.class;
		compClasses[Constraint.BTN2.ordinal()] = PScrollBarButton.class;
		compClasses[Constraint.BG1.ordinal()] = PScrollBarBackground.class;
		compClasses[Constraint.BG2.ordinal()] = PScrollBarBackground.class;
		compClasses[Constraint.THUMB.ordinal()] = PScrollBarThumb.class;
	}
	
	private final PComponent[] comps = new PComponent[Constraint.values().length];
	private final MutablePSize prefSize = new MutablePSize();
	private Orientation ori;
	
	public PScrollBarLayout(PComponent component) {
		super(component);
		
		addObs(new PLayoutObs() {
			public void onChildAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
				Constraint cnstr = (Constraint) constraint;
				comps[cnstr.ordinal()] = child;
			}
			public void onChildRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
				Constraint cnstr = (Constraint) constraint;
				comps[cnstr.ordinal()] = null;
			}
		});
	}
	
	public PScrollBar getOwner() {
		return (PScrollBar) super.getOwner();
	}
	
	public void setOrientation(Orientation orientation) {
		ori = orientation;
		fireInvalidateEvent();
	}
	
	public Orientation getOrientation() {
		return ori;
	}
	
	public PComponent getChildForConstraint(Object constraint) {
		Constraint cnstr = (Constraint) constraint;
		return comps[cnstr.ordinal()];
	}
	
	public PScrollBarButton getFirstButton() {
		return (PScrollBarButton) getChildForConstraint(Constraint.BTN1);
	}
	
	public PScrollBarButton getSecondButton() {
		return (PScrollBarButton) getChildForConstraint(Constraint.BTN2);
	}
	
	public PScrollBarBackground getFirstBackground() {
		return (PScrollBarBackground) getChildForConstraint(Constraint.BG1);
	}
	
	public PScrollBarBackground getSecondBackground() {
		return (PScrollBarBackground) getChildForConstraint(Constraint.BG2);
	}
	
	public PScrollBarThumb getThumb() {
		return (PScrollBarThumb) getChildForConstraint(Constraint.THUMB);
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		if (constraint == null || !(constraint instanceof Constraint)) {
			return false;
		}
		Constraint cnstr = (Constraint) constraint;
		int id = cnstr.ordinal();
		return component.getClass() == compClasses[id] && comps[id] == null;
	}
	
	public void layOut() {
//		System.out.println("PScrollBarLayout.layOut");
		PBounds ob = getOwner().getBounds();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		int fx = ob.getFinalX();
		int fy = ob.getFinalY();
		
		boolean horizontal = getOrientation() == Orientation.HORIZONTAL;
		
		PComponent btn1 = getChildForConstraint(Constraint.BTN1);
		if (btn1 != null) {
			PSize prefSize = getPreferredSizeOf(btn1);
			int compX;
			int compY;
			int compW;
			int compH;
			if (horizontal) {
				compW = prefSize.getWidth();
				compH = h;
				compX = x;
				compY = y;
				w -= compW;
				x += compW;
			} else {
				compW = w;
				compH = prefSize.getHeight();
				compX = x;
				compY = y;
				h -= compH;
				y += compH;
			}
			setChildBounds(btn1, compX, compY, compW, compH);
		}
		PComponent btn2 = getChildForConstraint(Constraint.BTN2);
		if (btn2 != null) {
			PSize prefSize = getPreferredSizeOf(btn2);
			int compX;
			int compY;
			int compW;
			int compH;
			if (horizontal) {
				compW = prefSize.getWidth();
				compH = h;
				compX = fx - compW;
				compY = fy - compH;
				w -= compW;
				fx -= compW;
			} else {
				compW = w;
				compH = prefSize.getHeight();
				compX = fx - compW;
				compY = fy - compH;
				h -= compH;
				fy -= compH;
			}
			setChildBounds(btn2, compX, compY, compW, compH);
		}
		PScrollBarThumb thumb = (PScrollBarThumb) getChildForConstraint(Constraint.THUMB);
		int thumbX;
		int thumbY;
		int thumbW;
		int thumbH;
		if (thumb != null) {
			double scroll = getOwner().getModel().getScroll();
//			System.out.println("layout.scroll="+scroll);
			double size = getThumbSize();
			if (horizontal) {
				thumbW = (int) (w * size);
				thumbH = h;
				w -= thumbW;
				thumbX = x + (int) (w * scroll);
				thumbY = y;
			} else {
				thumbW = w;
				thumbH = (int) (h * size);
				h -= thumbH;
				thumbX = x;
				thumbY = y + (int) (h * scroll);
			}
			setChildBounds(thumb, thumbX, thumbY, thumbW, thumbH);
		} else {
			thumbX = x;
			thumbY = y;
			thumbW = 0;
			thumbH = 0;
		}
		PComponent bg1 = getChildForConstraint(Constraint.BG1);
		if (bg1 != null) {
			int compX;
			int compY;
			int compW;
			int compH;
			if (horizontal) {
				compX = x;
				compY = y;
				compW = thumbX - compX;
				compH = h;
				w -= compW;
			} else {
				compX = x;
				compY = y;
				compW = w;
				compH = thumbY - compY;
				h -= compH;
			}
			setChildBounds(bg1, compX, compY, compW, compH);
		}
		PComponent bg2 = getChildForConstraint(Constraint.BG2);
		if (bg2 != null) {
			int compX;
			int compY;
			int compW;
			int compH;
			if (horizontal) {
				compX = thumbX + thumbW;
				compY = y;
				compW = w;
				compH = h;
			} else {
				compX = x;
				compY = thumbY + thumbH;
				compW = w;
				compH = h;
			}
			setChildBounds(bg2, compX, compY, compW, compH);
		}
	}
	
	private double getThumbSize() {
		PScrollBarModel model = getOwner().getModel();
		int prefSize = model.getPreferredSize();
		if (prefSize == 0) {
			return 0;
		}
		int size = model.getSize();
		if (size >= prefSize) {
			return 0;
		}
//		System.out.println("layout.thumbSize="+((double) size / (double) prefSize));
		return (double) size / (double) prefSize;
	}
	
	public PSize getPreferredSize() {
		int prefW = 0;
		int prefH = 0;
		boolean horizontal = getOrientation() == Orientation.HORIZONTAL;
		for (int i = 0; i < comps.length; i++) {
			PComponent child = comps[i];
			if (child != null) {
				PSize childPrefSize = getPreferredSizeOf(child);
				int childPrefW = childPrefSize.getWidth();
				int childPrefH = childPrefSize.getHeight();
				if (horizontal) {
					if (childPrefH > prefH) {
						prefH = childPrefH;
					}
					prefW += childPrefW;
				} else {
					if (childPrefW > prefW) {
						prefW = childPrefW;
					}
					prefH += childPrefH;
				}
			}
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	public static enum Orientation {
		HORIZONTAL,
		VERTICAL,
		;
	}
	
	public static enum Constraint {
		BTN1,
		BTN2,
		BG1,
		BG2,
		THUMB,
		;
	}
	
}