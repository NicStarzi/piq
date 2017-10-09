package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PPoint;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.MutablePPoint;
import edu.udo.piq.util.ThrowException;

public class PRingLayout extends AbstractMapPLayout {
	
	protected final List<PComponent> compList = new ArrayList<>();
	protected PSize[] compPrefSizes;
	protected MutablePPoint[] compOrigins;
	protected double rotation;
	protected double prefRadius;
	protected double curRadius;
	protected double maxDimSize = 0;
	protected boolean usePrefRad = true;
	protected boolean prefRadInvalid = true;
	
	public PRingLayout(PComponent owner) {
		super(owner);
	}
	
	@Override
	protected void onChildAdded(PComponent child, Object constraint) {
		if (constraint == null) {
			compList.add(child);
		} else {
			compList.add((Integer) constraint, child);
		}
		int index = compList.indexOf(child);
		for (int i = index; i < compList.size(); i++) {
			Integer con = Integer.valueOf(i);
			setChildConstraint(compList.get(i), con);
		}
		setPrefRadiusInvalid(true);
		invalidate();
	}
	
	@Override
	protected void onChildRemoved(PCompInfo removedCompInfo) {
		int index = (Integer) removedCompInfo.getConstraint();
		ThrowException.ifNotEqual(removedCompInfo.getComponent(), compList.get(index),
				"compList.get(index) != removedComponent");
		compList.remove(index);
		for (int i = index; i < compList.size(); i++) {
			Integer con = Integer.valueOf(i);
			setChildConstraint(compList.get(i), con);
		}
		setPrefRadiusInvalid(true);
		invalidate();
	}
	
	@Override
	protected void clearAllInfosInternal() {
		super.clearAllInfosInternal();
		setPrefRadiusInvalid(true);
		compList.clear();
	}
	
	@Override
	protected void onChildCleared(PComponent child, Object constraint) {
		setPrefRadiusInvalid(true);
	}
	
	@Override
	protected void onChildPrefSizeChanged(PComponent child) {
		super.onChildPrefSizeChanged(child);
		setPrefRadiusInvalid(true);
	}
	
	public void setRotationInDeg(double value) {
		setRotationInRad(Math.toRadians(value));
	}
	
	public void setRotationInRad(double value) {
		if (rotation != value) {
			rotation = value;
			setPrefRadiusInvalid(true);
			invalidate();
		}
	}
	
	public double getRotationInRad() {
		return rotation;
	}
	
	public double getRotationInDeg() {
		return Math.toDegrees(getRotationInRad());
	}
	
	public void setUsePreferredRadius(boolean value) {
		if (usePrefRad != value) {
			usePrefRad = value;
			invalidate();
		}
	}
	
	protected void setPrefRadiusInvalid(boolean value) {
		prefRadInvalid = value;
	}
	
	public boolean isUsePreferredRadius() {
		return usePrefRad;
	}
	
	public double getPreferredRadius() {
		if (prefRadInvalid) {
			calculatePrefRadiusAndOrigins();
		}
		return prefRadius;
	}
	
	public void setRadius(double value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (curRadius != value) {
			curRadius = value;
			setUsePreferredRadius(false);
			invalidate();
		}
	}
	
	public double getRadius() {
		return curRadius;
	}
	
	public PComponent getChild(int index) {
		if (index < 0 || index >= compList.size()) {
			return null;
		}
		return compList.get(index);
	}
	
	@Override
	public PComponent getChildAt(int x, int y) {
		for (int i = 0; i < compList.size(); i++) {
			PComponent child = compList.get(i);
			PBounds bnds = getChildBounds(child);
			if (bnds.contains(x, y)) {
				return child;
			}
		}
		return null;
	}
	
	public int getIndexAt(int x, int y) {
		for (int i = 0; i < compList.size(); i++) {
			PComponent child = compList.get(i);
			PBounds bnds = getChildBounds(child);
			if (bnds.contains(x, y)) {
				return i;
			}
		}
		return compList.size();
	}
	
	public int getChildIndex(PComponent child) {
		if (child == null) {
			throw new NullPointerException();
		}
		return compList.indexOf(child);
	}
	
	@Override
	public Object getChildConstraint(PComponent child) throws NullPointerException {
		return Integer.valueOf(getChildIndex(child));
	}
	
	@Override
	public PComponent getChildForConstraint(Object constraint) {
		return getChild((Integer) constraint);
	}
	
	@Override
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return (constraint == null || constraint instanceof Integer) && !compList.contains(cmp);
	}
	
	@Override
	protected void onInvalidated() {
		int compCount = compList.size();
		if (compPrefSizes == null || compPrefSizes.length != compCount) {
			compPrefSizes = new PSize[compCount];
			compOrigins = new MutablePPoint[compCount];
			for (int i = 0; i != compCount; i++) {
				compOrigins[i] = new MutablePPoint(0, 0);
			}
		}
		calculateCompSizesAndMaxDim();
		if (isUsePreferredRadius()) {
			if (prefRadInvalid) {
				calculatePrefRadiusAndOrigins();
			}
		}
		calculateOrigins();
		calculatePrefSize();
	}
	
	protected void calculateCompSizesAndMaxDim() {
		int sumPrefW = 0;
		int sumPrefH = 0;
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = getPreferredSizeOf(comp);
			compPrefSizes[i] = compPrefSize;
			int compPrefW = compPrefSize.getWidth();
			int compPrefH = compPrefSize.getHeight();
			sumPrefW += compPrefW + 1;
			sumPrefH += compPrefH + 1;
		}
		maxDimSize = Math.max(sumPrefW, sumPrefH);
	}
	
	protected void calculatePrefRadiusAndOrigins() {
		int compCount = compList.size();
		if (compCount == 0) {
			prefRadius = 0;
			return;
		} else if (compCount == 1) {
			prefRadius = 0;
			setPrefRadiusInvalid(false);
			return;
		}
		double angleInDeg = 360.0 / compCount;
		double angleInRad = Math.toRadians(angleInDeg);
		double maxRadius = maxDimSize / 2.0;
		double minRadius = maxDimSize / (2.0 * Math.PI);
		double radius = minRadius + (maxRadius - minRadius) * 0.2;
		double delta = 5;
		int maxIterCount = 10;
		int iterCount = 0;
		double rotation = getRotationInRad();
		while (iterCount++ < maxIterCount) {
			for (int i = 0; i < compCount; i++) {
				compOrigins[i].setTo(PPoint.ORIGIN);
				compOrigins[i].moveByAngleInRad(rotation + angleInRad * i, radius);
			}
			if (areCompsOverlapping(compOrigins, compPrefSizes)) {
				minRadius = radius;
			} else {
				maxRadius = radius;
			}
			radius = minRadius + (maxRadius - minRadius) / 2;
			if (minRadius > maxRadius - delta) {
				break;
			}
		}
		prefRadius = radius;
		setPrefRadiusInvalid(false);
	}
	
	protected boolean areCompsOverlapping(PPoint[] points, PSize[] sizes) {
		ThrowException.ifNull(points, "points == null");
		ThrowException.ifNull(sizes, "sizes == null");
		int length = points.length;
		ThrowException.ifNotEqual(length, sizes.length, "sizes.length != points.length");
		for (int a = 0; a < length - 1; a++) {
			PPoint pointA = points[a];
			PSize sizeA = sizes[a];
			int wA = sizeA.getWidth();
			int hA = sizeA.getHeight();
			double xA = pointA.getX() - wA / 2 - 1;
			double yA = pointA.getY() - hA / 2 - 1;
			double fxA = xA + wA + 2;
			double fyA = yA + hA + 2;
			for (int b = a + 1; b < length; b++) {
				PPoint pointB = points[b];
				PSize sizeB = sizes[b];
				int wB = sizeB.getWidth();
				int hB = sizeB.getHeight();
				double xB = pointB.getX() - wB / 2 - 1;
				double yB = pointB.getY() - hB / 2 - 1;
				double fxB = xB + wB + 2;
				double fyB = yB + hB + 2;
				
				if (xA <= fxB && yA <= fyB && fxA >= xB && fyA >= yB) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected void calculateOrigins() {
		int compCount = compList.size();
		double angleInDeg = 360.0 / compCount;
		double angleInRad = Math.toRadians(angleInDeg);
		double radius = isUsePreferredRadius() ? getPreferredRadius() : getRadius();
		double rotation = getRotationInRad();
		for (int i = 0; i < compCount; i++) {
			compOrigins[i].setTo(PPoint.ORIGIN);
			compOrigins[i].moveByAngleInRad(rotation + angleInRad * i, radius);
		}
	}
	
	protected void calculatePrefSize() {
		int compCount = compList.size();
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (int i = 0; i < compCount; i++) {
			PPoint point = compOrigins[i];
			PSize size = compPrefSizes[i];
			int compW = size.getWidth();
			int compH = size.getHeight();
			int compX = (int) point.getX() - compW / 2;
			int compY = (int) point.getY() - compH / 2;
			int compFx = compX + compW;
			int compFy = compY + compH;
			
			if (compX < minX) {
				minX = compX;
			}
			if (compY < minY) {
				minY = compY;
			}
			if (compFx > maxX) {
				maxX = compFx;
			}
			if (compFy > maxY) {
				maxY = compFy;
			}
		}
		prefSize.setWidth(maxX - minX + 1);
		prefSize.setHeight(maxY - minY + 1);
	}
	
	@Override
	protected void layOutInternal() {
		PBounds bounds = getOwner().getBounds();
		int x = bounds.getX();
		int y = bounds.getX();
		int w = bounds.getWidth();
		int h = bounds.getHeight();
		int centerX = x + w / 2;
		int centerY = y + h / 2;
		
		int compCount = compList.size();
		for (int i = 0; i < compCount; i++) {
			PComponent comp = compList.get(i);
			PPoint point = compOrigins[i];
			PSize size = compPrefSizes[i];
			int compW = size.getWidth();
			int compH = size.getHeight();
			int compX = (int) point.getX() - compW / 2;
			int compY = (int) point.getY() - compH / 2;
			
			setChildBounds(comp, centerX + compX, centerY + compY, compW, compH);
		}
	}
	
}