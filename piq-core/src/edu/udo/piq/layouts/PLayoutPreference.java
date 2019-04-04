package edu.udo.piq.layouts;

import edu.udo.piq.PInsets;

public interface PLayoutPreference {
	
	public static final PLayoutPreference FIXED_LAYOUT_PREFERENCE = new PLayoutPreference() {
		@Override
		public PInsets getMargin() {
			return PInsets.ZERO_INSETS;
		}
		@Override
		public AlignmentY getAlignmentY() {
			return AlignmentY.CENTER;
		}
		@Override
		public AlignmentX getAlignmentX() {
			return AlignmentX.CENTER;
		}
		@Override
		public String toString() {
			return "DEFAULT_LAYOUT_PREFERENCE";
		}
		@Override
		public void setAlignmentX(AlignmentX value) {}
		@Override
		public void setAlignmentY(AlignmentY value) {}
		@Override
		public void setMargin(int top, int bottom, int left, int right) {}
	};
	
	public void setAlignmentX(AlignmentX value);
	
	public AlignmentX getAlignmentX();
	
	public void setAlignmentY(AlignmentY value);
	
	public AlignmentY getAlignmentY();
	
	public default void setAlignment(AlignmentX alignmentX, AlignmentY alignmentY) {
		setAlignmentX(alignmentX);
		setAlignmentY(alignmentY);
	}
	
	public default void setMargin(PInsets value) {
		setMargin(value.getFromTop(), value.getFromBottom(), value.getFromLeft(), value.getFromRight());
	}
	
	public default void setMargin(int allEqual) {
		setMargin(allEqual, allEqual, allEqual, allEqual);
	}
	
	public default void setMargin(int horizontal, int vertical) {
		setMargin(horizontal, horizontal, vertical, vertical);
	}
	
	public void setMargin(int top, int bottom, int left, int right);
	
	public PInsets getMargin();
	
}