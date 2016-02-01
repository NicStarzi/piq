package edu.udo.piq.tools;

import edu.udo.piq.PInsets;

public abstract class AbstractPInsets implements PInsets {
	
	public int getHorizontal() {
		return getFromLeft() + getFromRight();
	}
	
	public int getVertical() {
		return getFromTop() + getFromBottom();
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof PInsets)) {
			return false;
		}
		PInsets other = (PInsets) obj;
		return getFromTop() == other.getFromTop() 
				&& getFromBottom() == other.getFromBottom() 
				&& getFromLeft() == other.getFromLeft() 
				&& getFromRight() == other.getFromRight();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [top=");
		builder.append(getFromTop());
		builder.append(", btm=");
		builder.append(getFromBottom());
		builder.append(", lft=");
		builder.append(getFromLeft());
		builder.append(", rgt=");
		builder.append(getFromRight());
		builder.append("]");
		return builder.toString();
	}
	
}