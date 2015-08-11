package edu.udo.piq;

/**
 * This interface defines render modes that a {@link PRenderer} can use 
 * to apply to primitive rendering operations.<br>
 * Primitive rendering operations are {@link PRenderer#drawEllipse(int, int, int, int)}, 
 * {@link PRenderer#drawLine(float, float, float, float, float)}, 
 * {@link PRenderer#drawPolygon(float[], float[])}, 
 * {@link PRenderer#drawTriangle(float, float, float, float, float, float)} and all kinds of 
 * {@link PRenderer#drawQuad(PBounds)}.<br>
 * Non-primitive rendering operations are 
 * {@link PRenderer#drawImage(PImageResource, float, float, float, float)}, 
 * {@link PRenderer#drawImage(PImageResource, int, int, int, int, float, float, float, float)}, 
 * and {@link PRenderer#drawString(PFontResource, String, float, float)}.<br>
 * <br>
 * Each implementation of PRenderer must support at least 3 render modes, 
 * namely {@link PRenderer#getRenderModeFill()}, 
 * {@link PRenderer#getRenderModeOutline()} and 
 * {@link PRenderer#getRenderModeOutlineDashed()}.<br>
 * An implementation is free to support more render modes then these and 
 * to expose them in an implementation dependent fashion.<br>
 * <br>
 * Render modes are supposed to be used like {@link Enum enums} but without enforcing 
 * an upper limit for platform dependent implementations.<br>
 * 
 * @author NicStarzi
 */
public interface PRenderMode {
}