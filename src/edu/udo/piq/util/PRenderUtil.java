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
		strokeTop(renderer, x, y, fx, fy, lineWidth);
		strokeBottom(renderer, x, y, fx, fy, lineWidth);
		strokeLeft(renderer, x, y, fx, fy, lineWidth);
		strokeRight(renderer, x, y, fx, fy, lineWidth);
	}
	
	public static void strokeTop(PRenderer renderer, int x, int y, int fx, int fy) {
		strokeTop(renderer, x, y, fx, fy, 1);
	}
	
	public static void strokeTop(PRenderer renderer, int x, int y, int fx, int fy, int lineWidth) {
		renderer.drawQuad(x, y, fx, y + lineWidth);
	}
	
	public static void strokeBottom(PRenderer renderer, int x, int y, int fx, int fy) {
		strokeBottom(renderer, x, y, fx, fy, 1);
	}
	
	public static void strokeBottom(PRenderer renderer, int x, int y, int fx, int fy, int lineWidth) {
		renderer.drawQuad(x, fy - lineWidth, fx, fy);
	}
	
	public static void strokeLeft(PRenderer renderer, int x, int y, int fx, int fy) {
		strokeLeft(renderer, x, y, fx, fy, 1);
	}
	
	public static void strokeLeft(PRenderer renderer, int x, int y, int fx, int fy, int lineWidth) {
		renderer.drawQuad(x, y, x + lineWidth, fy);
	}
	
	public static void strokeRight(PRenderer renderer, int x, int y, int fx, int fy) {
		strokeRight(renderer, x, y, fx, fy, 1);
	}
	
	public static void strokeRight(PRenderer renderer, int x, int y, int fx, int fy, int lineWidth) {
		renderer.drawQuad(fx - lineWidth, y, fx, fy);
	}
	
}