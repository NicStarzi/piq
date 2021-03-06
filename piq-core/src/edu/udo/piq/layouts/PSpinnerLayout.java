package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PSpinnerPart;
import edu.udo.piq.layouts.PSpinnerLayout.Constraint;

public class PSpinnerLayout extends AbstractEnumPLayout<Constraint> {
	
	public PSpinnerLayout(PComponent component) {
		super(component, Constraint.class);
	}
	
	protected String getErrorMsgConstraintIllegal() {
		return "constraint.getClass() != Constraint.class";
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return super.canAdd(cmp, constraint) && cmp instanceof PSpinnerPart;
	}
	
	public PSpinnerPart getEditor() {
		return (PSpinnerPart) getChildForConstraint(Constraint.EDITOR);
	}
	
	public PSpinnerPart getNextButton() {
		return (PSpinnerPart) getChildForConstraint(Constraint.BTN_NEXT);
	}
	
	public PSpinnerPart getPrevButton() {
		return (PSpinnerPart) getChildForConstraint(Constraint.BTN_PREV);
	}
	
	protected void layOutInternal() {
		PBounds ob = getOwner().getBounds();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		
		PComponent editor = getEditor();
		PComponent btnNext = getNextButton();
		PComponent btnPrev = getPrevButton();
		PSize btnNextSize = getPreferredSizeOf(btnNext);
		PSize btnPrevSize = getPreferredSizeOf(btnPrev);
		int btnW = Math.max(btnNextSize.getWidth(), btnPrevSize.getWidth());
		limit(btnW, 0, w);
		int editorW = w - btnW;
		
		int btnNextH = btnNextSize.getHeight();
		int btnPrevH = btnPrevSize.getHeight();
		int totalPrefH = btnNextH + btnPrevH;
		if (totalPrefH > 0) {
			double btnNextHPercent = btnNextH / (double) totalPrefH;
			double btnPrevHPercent = btnNextH / (double) totalPrefH;
			btnNextH = (int) Math.ceil(h * btnNextHPercent);
			btnPrevH = (int) Math.floor(h * btnPrevHPercent);
		} else {
			btnNextH = btnPrevH = h / 2;
		}
		if (btnNext != null) {
			setChildCellFilled(btnNext, x + editorW, y, btnW, btnNextH);
		}
		if (btnPrev != null) {
			setChildCellFilled(btnPrev, x + editorW, y + btnNextH, btnW, btnPrevH);
		}
		if (editor != null) {
			setChildCellFilled(editor, x, y, editorW, h);
		}
	}
	
	protected void onInvalidated() {
		PComponent editor = getChildForConstraint(Constraint.EDITOR);
		PComponent btnNext = getChildForConstraint(Constraint.BTN_NEXT);
		PComponent btnPrev = getChildForConstraint(Constraint.BTN_PREV);
		PSize editorSize = getPreferredSizeOf(editor);
		PSize btnNextSize = getPreferredSizeOf(btnNext);
		PSize btnPrevSize = getPreferredSizeOf(btnPrev);
		int btnW = Math.max(btnNextSize.getWidth(), btnPrevSize.getWidth());
		int btnH = btnNextSize.getHeight() + btnPrevSize.getHeight();
		
		prefSize.setWidth(editorSize.getWidth() + btnW);
		prefSize.setHeight(Math.max(editorSize.getHeight(), btnH));
	}
	
	public static int limit(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}
	
	public static enum Constraint {
		EDITOR,
		BTN_NEXT,
		BTN_PREV,
		;
	}
	
}