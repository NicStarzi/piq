package edu.udo.piq;

import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.tools.ImmutablePInsets;

public class DefaultPLayoutPreference implements PLayoutPreference {
	
	protected AlignmentX alignX = AlignmentX.CENTER;
	protected AlignmentY alignY = AlignmentY.CENTER;
	protected PInsets margin = PInsets.ZERO_INSETS;
	protected PInsets padding = PInsets.ZERO_INSETS;
	
	public void setAlignment(AlignmentX valueX, AlignmentY valueY) {
		setAlignmentX(valueX);
		setAlignmentY(valueY);
	}
	
	public void setAlignmentX(AlignmentX value) {
		alignX = value;
	}
	
	@Override
	public AlignmentX getAlignmentX() {
		return alignX;
	}
	
	public void setAlignmentY(AlignmentY value) {
		alignY = value;
	}
	
	@Override
	public AlignmentY getAlignmentY() {
		return alignY;
	}
	
	public void setMargin(int top, int bottom, int left, int right) {
		setMargin(new ImmutablePInsets(top, bottom, left, right));
	}
	
	public void setMargin(int horizontal, int vertical) {
		setMargin(new ImmutablePInsets(horizontal, vertical));
	}
	
	public void setMargin(int allEqual) {
		setMargin(new ImmutablePInsets(allEqual));
	}
	
	public void setMargin(PInsets value) {
		margin = value;
	}
	
	@Override
	public PInsets getMargin() {
		return margin;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [margin=");
		sb.append(getMargin());
		sb.append(", alignmentX=");
		sb.append(getAlignmentX());
		sb.append(", alignmentY=");
		sb.append(getAlignmentY());
		sb.append("]");
		return sb.toString();
	}
	
}