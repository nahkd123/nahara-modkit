package nahara.modkit.gui.v1.widget;

import java.util.function.Consumer;

import nahara.modkit.gui.v1.layout.Layout;

public abstract class AbstractDrawable<T extends AbstractDrawable<T>> extends AbstractWidget<T> implements Drawable<T> {
	// These are referenced geometry of this drawable
	// The final geometry that will be displayed will be calculated by Layout and
	// applied to this drawable with useComputedGeometry()
	protected int referenceX = 0, referenceY = 0, referenceWidth = 1, referenceHeight = 1;
	protected boolean visible = true, geometryRecomputeNeeded = false;
	protected Layout layout = new Layout();

	// Computed geometry info
	protected int x, y, width, height, globalX, globalY;

	@Override
	public int getX() { return referenceX; }

	@Override
	public void setX(int x) {
		this.referenceX = x;
		this.geometryRecomputeNeeded = true;
	}

	@Override
	public int getY() { return referenceY; }

	@Override
	public void setY(int y) {
		this.referenceY = y;
		this.geometryRecomputeNeeded = true;
	}

	@Override
	public int getWidth() { return referenceWidth; }

	@Override
	public void setWidth(int width) {
		this.referenceWidth = width;
		this.geometryRecomputeNeeded = true;
	}

	@Override
	public int getHeight() { return referenceHeight; }

	@Override
	public void setHeight(int height) {
		this.referenceHeight = height;
		this.geometryRecomputeNeeded = true;
	}

	@Override
	public boolean isVisible() { return visible; }

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
		this.geometryRecomputeNeeded = true;
	}

	@Override
	public void useComputedGeometry(int x, int y, int width, int height, int globalX, int globalY) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.globalX = globalX;
		this.globalY = globalY;
		this.geometryRecomputeNeeded = false;
	}

	@Override
	public void getComputedGeometry(int[] toArray) {
		if (toArray.length >= 1) toArray[0] = x;
		if (toArray.length >= 2) toArray[1] = y;
		if (toArray.length >= 3) toArray[2] = width;
		if (toArray.length >= 4) toArray[3] = height;
		if (toArray.length >= 5) toArray[4] = globalX;
		if (toArray.length >= 6) toArray[5] = globalY;
	}

	@Override
	public boolean geometryRecomputeNeeded() {
		return geometryRecomputeNeeded;
	}

	@Override
	public Layout getLayout() { return layout; }

	@Override
	public T layout(Consumer<Layout> consumer) {
		geometryRecomputeNeeded = true;
		return Drawable.super.layout(consumer);
	}
}
