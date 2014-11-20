package edu.udo.piq.util;

import edu.udo.piq.PBounds;
import edu.udo.piq.PRenderer;

public class PRenderUtil {
	
	private PRenderUtil() {}
	
	public static void strokeQuad(PRenderer renderer, PBounds bounds) {
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		strokeQuad(renderer, x, y, fx, fy);
	}
	
	public static void strokeQuad(PRenderer renderer, PBounds bounds, int lineWidth) {
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		strokeQuad(renderer, x, y, fx, fy, lineWidth);
	}
	
	public static void strokeQuad(PRenderer renderer, int x, int y, int fx, int fy) {
		strokeQuad(renderer, x, y, fx, fy, 1);
	}
	
	public static void strokeQuad(PRenderer renderer, int x, int y, int fx, int fy, int lineWidth) {
		// top
		renderer.drawQuad(x, y, fx, y + lineWidth);
		// bottom
		renderer.drawQuad(x, fy - lineWidth, fx, fy);
		// left
		renderer.drawQuad(x, y, x + lineWidth, fy);
		// right
		renderer.drawQuad(fx - lineWidth, y, fx, fy);
	}
	
}