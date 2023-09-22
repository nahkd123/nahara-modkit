package nahara.modkit.gui.v1.layout;

import com.google.common.base.Preconditions;

import nahara.modkit.gui.v1.widget.Drawable;

public class Layout {
	private Anchor origin = Anchor.TOP_LEFT;
	private Anchor anchor = Anchor.TOP_LEFT;
	private Axis[] fillAxes = new Axis[0];

	public Anchor getOrigin() { return origin; }

	public Layout setOrigin(Anchor origin) {
		Preconditions.checkNotNull(origin, "origin can't be null");
		this.origin = origin;
		return this;
	}

	public Anchor getAnchor() { return anchor; }

	public Layout setAnchor(Anchor anchor) {
		Preconditions.checkNotNull(anchor, "anchor can't be null");
		this.anchor = anchor;
		return this;
	}

	public Axis[] getFillAxes() { return fillAxes; }

	public Layout setFillAxes(Axis... fillAxes) {
		this.fillAxes = fillAxes;
		return this;
	}

	public void applyTo(Drawable<?> drawable, int parentWidth, int parentHeight, int parentGlobalX, int parentGlobalY) {
		boolean fillAxisX = false, fillAxisY = false;

		for (Axis axis : fillAxes) {
			switch (axis) {
			case X:
				fillAxisX = true;
				break;
			case Y:
				fillAxisY = true;
				break;
			default:
				break;
			}
		}

		int anchorX = switch (origin) {
		case TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> 0;
		case TOP_MIDDLE, MIDDLE, BOTTOM_MIDDLE -> drawable.getWidth() / 2;
		case TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT -> drawable.getWidth();
		default -> 0;
		};
		int anchorY = switch (origin) {
		case TOP_LEFT, TOP_MIDDLE, TOP_RIGHT -> 0;
		case MIDDLE_LEFT, MIDDLE, MIDDLE_RIGHT -> drawable.getHeight() / 2;
		case BOTTOM_LEFT, BOTTOM_MIDDLE, BOTTOM_RIGHT -> drawable.getHeight();
		default -> 0;
		};

		int originX = switch (origin) {
		case TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> 0;
		case TOP_MIDDLE, MIDDLE, BOTTOM_MIDDLE -> parentWidth / 2;
		case TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT -> parentWidth;
		default -> 0;
		};
		int originY = switch (origin) {
		case TOP_LEFT, TOP_MIDDLE, TOP_RIGHT -> 0;
		case MIDDLE_LEFT, MIDDLE, MIDDLE_RIGHT -> parentHeight / 2;
		case BOTTOM_LEFT, BOTTOM_MIDDLE, BOTTOM_RIGHT -> parentHeight;
		default -> 0;
		};

		int drawX = fillAxisX ? 0 : originX - anchorX + drawable.getX();
		int drawY = fillAxisY ? 0 : originY - anchorY + drawable.getY();
		int drawW = fillAxisX ? parentWidth : drawable.getWidth();
		int drawH = fillAxisY ? parentHeight : drawable.getHeight();
		drawable.useComputedGeometry(drawX, drawY, drawW, drawH, parentGlobalX + drawX, parentGlobalY + drawY);
	}
}
