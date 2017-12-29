package edu.udo.piq;

import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;

public interface PLayoutPreference {
	
	public static final PLayoutPreference DEFAULT_LAYOUT_PREFERENCE = new PLayoutPreference() {
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
	};
	
	public AlignmentX getAlignmentX();
	
	public AlignmentY getAlignmentY();
	
	public PInsets getMargin();
	
}