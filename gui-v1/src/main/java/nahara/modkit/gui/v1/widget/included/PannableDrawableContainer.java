package nahara.modkit.gui.v1.widget.included;

import nahara.modkit.gui.v1.widget.Drawable;
import nahara.modkit.gui.v1.widget.Focusable;

/**
 * <p>
 * Like {@link DrawableContainer}, but user can click and drag the container to
 * move its children around by setting its translation.
 * </p>
 */
public class PannableDrawableContainer extends DrawableContainer implements Focusable<PannableDrawableContainer> {
	protected boolean panning = false;
	private int lastX, lastY;

	public boolean isPanning() { return panning; }

	@Override
	public boolean onMouseDown(int mouseX, int mouseY, float delta, int button) {
		boolean isInContainer = testGeometry(mouseX, mouseY);

		if (isInContainer) {
			for (int i = children.size() - 1; i >= 0; i--) {
				Drawable<?> child = children.get(i);
				if (child.onMouseDown(mouseX - x - translateX, mouseY - y - translateY, delta, button)) return true;
			}

			panning = true;
			manager.useFocus(this);
			lastX = mouseX + globalX;
			lastY = mouseY + globalY;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onMouseMove(int mouseX, int mouseY, float delta) {
		if (panning) {
			translateX += mouseX + globalX - lastX;
			translateY += mouseY + globalY - lastY;
			this.geometryRecomputeNeeded = true;
			lastX = mouseX + globalX;
			lastY = mouseY + globalY;
			return true;
		} else {
			return super.onMouseMove(mouseX, mouseY, delta);
		}
	}

	@Override
	public boolean onMouseUp(int mouseX, int mouseY, float delta, int button) {
		panning = false;
		if (manager.getFocus() == this) manager.useFocus(null);
		return super.onMouseUp(mouseX, mouseY, delta, button);
	}
}
