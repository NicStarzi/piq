package edu.udo.piq;

import edu.udo.piq.tools.ImmutablePColor;

/**
 * A read-only definition of color that allows reading color
 * information in both integer format or floating point format.<br>
 * Implementations of {@link PColor} do not need to be immutable
 * only because the interface is read-only.<br>
 * 
 * @author Nic Starzi
 * 
 * @see PRenderer
 * @see PRenderer#setColor(PColor)
 * @see PRenderer#setColor1(double, double, double, double)
 * @see PRenderer#setColor255(int, int, int, int)
 */
public interface PColor {
	
	/**
	 * An immutable PColor implementation representing absolute black with full transparency (alpha == 0).
	 */
	public static final PColor TRANSPARENT	= new ImmutablePColor(0.00, 0.00, 0.00, 0.00);
	/**
	 * An immutable PColor implementation representing absolute white.
	 */
	public static final PColor WHITE	= new ImmutablePColor(1.00, 1.00, 1.00);
	/**
	 * An immutable PColor implementation representing a grey tone of about 25%.
	 */
	public static final PColor GREY25	= new ImmutablePColor(0.25, 0.25, 0.25);
	/**
	 * An immutable PColor implementation representing a grey tone of about 50%.
	 */
	public static final PColor GREY50	= new ImmutablePColor(0.50, 0.50, 0.50);
	/**
	 * An immutable PColor implementation representing a grey tone of about 75%.
	 */
	public static final PColor GREY75	= new ImmutablePColor(0.75, 0.75, 0.75);
	/**
	 * An immutable PColor implementation representing a grey tone of about 87.5%.
	 */
	public static final PColor GREY875	= new ImmutablePColor(0.875, 0.875, 0.875);
	/**
	 * An immutable PColor implementation representing absolute black.
	 */
	public static final PColor BLACK	= new ImmutablePColor(0.00, 0.00, 0.00);
	/**
	 * An immutable PColor implementation representing absolute red.
	 */
	public static final PColor RED		= new ImmutablePColor(1.00, 0.00, 0.00);
	public static final PColor DARK_RED		= RED.mult1(0.5);
	public static final PColor LIGHT_RED	= RED.add1(0.5);
	/**
	 * An immutable PColor implementation representing absolute green.
	 */
	public static final PColor GREEN	= new ImmutablePColor(0.00, 1.00, 0.00);
	public static final PColor DARK_GREEN	= GREEN.mult1(0.5);
	public static final PColor LIGHT_GREEN	= GREEN.add1(0.5);
	/**
	 * An immutable PColor implementation representing absolute blue.
	 */
	public static final PColor BLUE		= new ImmutablePColor(0.00, 0.00, 1.00);
	public static final PColor DARK_BLUE	= BLUE.mult1(0.5);
	public static final PColor LIGHT_BLUE	= BLUE.add1(0.5);
	/**
	 * An immutable PColor implementation representing absolute magenta
	 * (red = 100%, blue = 100%).
	 */
	public static final PColor MAGENTA	= new ImmutablePColor(1.00, 0.00, 1.00);
	public static final PColor DARK_MAGENTA		= MAGENTA.mult1(0.5);
	public static final PColor LIGHT_MAGENTA	= MAGENTA.add1(0.5);
	/**
	 * An immutable PColor implementation representing absolute yellow
	 * (red = 100%, green = 100%).
	 */
	public static final PColor YELLOW	= new ImmutablePColor(1.00, 1.00, 0.00);
	public static final PColor DARK_YELLOW	= YELLOW.mult1(0.5);
	public static final PColor LIGHT_YELLOW	= YELLOW.add1(0.5);
	/**
	 * An immutable PColor implementation representing absolute teal
	 * (green = 100%, blue = 100%).
	 */
	public static final PColor TEAL		= new ImmutablePColor(0.00, 1.00, 1.00);
	public static final PColor DARK_TEAL	= TEAL.mult1(0.5);
	public static final PColor LIGHT_TEAL	= TEAL.add1(0.5);
	
	/**
	 * Returns the value for the red color component in integer format.<br>
	 * The returned value is between 0 (no red color) to 255 (full red
	 * color) inclusive.<br>
	 * 
	 * @return red color component between 0 and 255 inclusive.
	 */
	public default int getRed255() {
		return (int) (getRed1() * 255);
	}
	
	/**
	 * Returns the value for the red color component in floating point
	 * format.<br>
	 * The returned value is between 0.0 (no red color) to 1.0 (full
	 * red color) inclusive.<br>
	 * 
	 * @return red color component between 0.0 and 1.0 inclusive.
	 */
	public double getRed1();
	
	/**
	 * Returns the value for the green color component in integer format.<br>
	 * The returned value is between 0 (no green color) to 255 (full green
	 * color) inclusive.<br>
	 * 
	 * @return green color component between 0 and 255 inclusive.
	 */
	public default int getGreen255() {
		return (int) (getGreen1() * 255);
	}
	
	/**
	 * Returns the value for the green color component in floating point
	 * format.<br>
	 * The returned value is between 0.0 (no green color) to 1.0 (full
	 * green color) inclusive.<br>
	 * 
	 * @return green color component between 0.0 and 1.0 inclusive.
	 */
	public double getGreen1();
	
	/**
	 * Returns the value for the blue color component in integer format.<br>
	 * The returned value is between 0 (no blue color) to 255 (full blue
	 * color) inclusive.<br>
	 * 
	 * @return blue color component between 0 and 255 inclusive.
	 */
	public default int getBlue255() {
		return (int) (getBlue1() * 255);
	}
	
	/**
	 * Returns the value for the blue color component in floating point
	 * format.<br>
	 * The returned value is between 0.0 (no blue color) to 1.0 (full
	 * blue color) inclusive.<br>
	 * 
	 * @return blue color component between 0.0 and 1.0 inclusive.
	 */
	public double getBlue1();
	
	/**
	 * Returns the value for the alpha color component in integer format.<br>
	 * The returned value is between 0 (no alpha color) to 255 (full alpha
	 * color) inclusive.<br>
	 * 
	 * @return alpha color component between 0 and 255 inclusive.
	 */
	public default int getAlpha255() {
		return (int) (getAlpha1() * 255);
	}
	
	/**
	 * Returns the value for the alpha color component in floating point
	 * format.<br>
	 * The returned value is between 0.0 (no alpha color) to 1.0 (full
	 * alpha color) inclusive.<br>
	 * 
	 * @return alpha color component between 0.0 and 1.0 inclusive.
	 */
	public double getAlpha1();
	
	public default PColor add(PColor other) {
		return add1(other.getRed1(), other.getGreen1(), other.getBlue1(), other.getAlpha1());
	}
	
	public default PColor mult(PColor other) {
		return mult1(other.getRed1(), other.getGreen1(), other.getBlue1(), other.getAlpha1());
	}
	
	public default PColor add1(double increment) {
		return add1(increment, increment, increment);
	}
	
	public default PColor add1(double r, double g, double b) {
		r = PColor.addColorComponent1(getRed1(), r);
		g = PColor.addColorComponent1(getGreen1(), g);
		b = PColor.addColorComponent1(getBlue1(), b);
		return new ImmutablePColor(r, g, b, getAlpha1());
	}
	
	public default PColor add1(double r, double g, double b, double a) {
		r = PColor.addColorComponent1(getRed1(), r);
		g = PColor.addColorComponent1(getGreen1(), g);
		b = PColor.addColorComponent1(getBlue1(), b);
		a = PColor.addColorComponent1(getAlpha1(), a);
		return new ImmutablePColor(r, g, b, a);
	}
	
	public default PColor mult1(double factor) {
		return mult1(factor, factor, factor);
	}
	
	public default PColor mult1(double r, double g, double b) {
		r = PColor.multColorComponent1(getRed1(), r);
		g = PColor.multColorComponent1(getGreen1(), g);
		b = PColor.multColorComponent1(getBlue1(), b);
		return new ImmutablePColor(r, g, b, getAlpha1());
	}
	
	public default PColor mult1(double r, double g, double b, double a) {
		r = PColor.multColorComponent1(getRed1(), r);
		g = PColor.multColorComponent1(getGreen1(), g);
		b = PColor.multColorComponent1(getBlue1(), b);
		a = PColor.multColorComponent1(getAlpha1(), a);
		return new ImmutablePColor(r, g, b, a);
	}
	
	public default PColor add255(int r, int g, int b) {
		r = PColor.addColorComponent255(getRed255(), r);
		g = PColor.addColorComponent255(getGreen255(), g);
		b = PColor.addColorComponent255(getBlue255(), b);
		return new ImmutablePColor(r, g, b, getAlpha255());
	}
	
	public default PColor add255(int r, int g, int b, int a) {
		r = PColor.addColorComponent255(getRed255(), r);
		g = PColor.addColorComponent255(getGreen255(), g);
		b = PColor.addColorComponent255(getBlue255(), b);
		a = PColor.addColorComponent255(getAlpha255(), a);
		return new ImmutablePColor(r, g, b, a);
	}
	
	public default PColor add255(int increment) {
		return add255(increment, increment, increment);
	}
	
	public default PColor mult255(int r, int g, int b) {
		r = PColor.multColorComponent255(getRed255(), r);
		g = PColor.multColorComponent255(getGreen255(), g);
		b = PColor.multColorComponent255(getBlue255(), b);
		return new ImmutablePColor(r, g, b, getAlpha255());
	}
	
	public default PColor mult255(int r, int g, int b, int a) {
		r = PColor.multColorComponent255(getRed255(), r);
		g = PColor.multColorComponent255(getGreen255(), g);
		b = PColor.multColorComponent255(getBlue255(), b);
		a = PColor.multColorComponent255(getAlpha255(), a);
		return new ImmutablePColor(r, g, b, a);
	}
	
	public default PColor mult255(int factor) {
		return mult255(factor, factor, factor);
	}
	
	public static double addColorComponent1(double c1, double c2) {
		double result = c1 + c2;
		if (result < 0) {
			return 0;
		}
		if (result > 1) {
			return 1;
		}
		return result;
	}
	
	public static double multColorComponent1(double c1, double c2) {
		double result = c1 * c2;
		if (result < 0) {
			return 0;
		}
		if (result > 1) {
			return 1;
		}
		return result;
	}
	
	public static int addColorComponent255(int c1, int c2) {
		int result = c1 + c2;
		if (result < 0) {
			return 0;
		}
		if (result > 255) {
			return 255;
		}
		return result;
	}
	
	public static int multColorComponent255(int c1, int c2) {
		return (int) (0.5 + 255.0 * PColor.multColorComponent1(c1 / 255.0, c2 / 255.0));
	}
	
}