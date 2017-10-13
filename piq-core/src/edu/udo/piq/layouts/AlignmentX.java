package edu.udo.piq.layouts;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum AlignmentX {
	
	LEFT,
	RIGHT {
		public int getLeftX(int minX, int maxWidth, int prefWidth) {
			return minX + maxWidth - prefWidth;
		}
	},
	CENTER {
		public int getLeftX(int minX, int maxWidth, int prefWidth) {
			return minX + (maxWidth - prefWidth) / 2;
		}
	},
	FILL {
		public int getWidth(int minX, int maxWidth, int prefWidth) {
			return maxWidth;
		}
	},
	;
	public static final AlignmentX L = LEFT;
	public static final AlignmentX R = RIGHT;
	public static final AlignmentX C = CENTER;
	public static final AlignmentX F = FILL;
	
	public static final List<AlignmentX> ALL = 
			Collections.unmodifiableList(Arrays.asList(values()));
	public static final int COUNT = ALL.size();
	
	public static AlignmentX getByName(String name) {
		if (name.length() == 0) {
			return null;
		}
		if (name.length() == 1) {
			char ch = name.charAt(0);
			for (AlignmentX elem : ALL) {
				if (elem.name().charAt(0) == ch) {
					return elem;
				}
			}
		} else {
			for (AlignmentX elem : ALL) {
				if (elem.name().equalsIgnoreCase(name)) {
					return elem;
				}
			}
		}
		return null;
	}
	
	public int getLeftX(int minX, int maxWidth, int prefWidth) {
		return minX;
	}
	
	public int getWidth(int minX, int maxWidth, int prefWidth) {
		return prefWidth;
	}
	
	public int getRightX(int minX, int maxWidth, int prefWidth) {
		return minX + getWidth(minX, maxWidth, prefWidth);
	}
	
}