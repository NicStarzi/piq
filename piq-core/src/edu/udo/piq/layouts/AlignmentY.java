package edu.udo.piq.layouts;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum AlignmentY {
	
	TOP,
	BOTTOM {
		public int getTopY(int topY, int maxHeight, int prefHeight) {
			return topY + maxHeight - prefHeight;
		}
	},
	CENTER {
		public int getTopY(int topY, int maxHeight, int prefHeight) {
			return topY + (maxHeight - prefHeight) / 2;
		}
	},
	FILL {
		public int getHeight(int topY, int maxHeight, int prefHeight) {
			return maxHeight;
		}
	},
	;
	public static final AlignmentY T = TOP;
	public static final AlignmentY B = BOTTOM;
	public static final AlignmentY C = CENTER;
	public static final AlignmentY F = FILL;
	
	public static final List<AlignmentY> ALL = 
			Collections.unmodifiableList(Arrays.asList(values()));
	public static final int COUNT = ALL.size();
	
	public static AlignmentY getByName(String name) {
		if (name.length() == 0) {
			return null;
		}
		if (name.length() == 1) {
			char ch = name.charAt(0);
			for (AlignmentY elem : ALL) {
				if (elem.name().charAt(0) == ch) {
					return elem;
				}
			}
		} else {
			for (AlignmentY elem : ALL) {
				if (elem.name().equalsIgnoreCase(name)) {
					return elem;
				}
			}
		}
		return null;
	}
	
	public int getTopY(int topY, int maxHeight, int prefHeight) {
		return topY;
	}
	
	public int getHeight(int topY, int maxHeight, int prefHeight) {
		return prefHeight;
	}
	
	public int getBottomY(int topY, int maxHeight, int prefHeight) {
		return topY + getHeight(topY, maxHeight, prefHeight);
	}
	
}