package nahara.modkit.gui.v1.widget.included;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nahara.modkit.gui.v1.widget.AbstractDrawable;
import nahara.modkit.gui.v1.widget.Drawable;
import nahara.modkit.gui.v1.widget.Widget;
import net.minecraft.client.gui.DrawContext;

/**
 * <p>
 * This container contains all children that are in {@link Drawable} type. The
 * purpose of this is to structure the GUI in a form of "tree", where you can
 * toggle visibility of its children by changing the visibility of this
 * container, or move its children by moving this container.
 * </p>
 * 
 * @see #add(Drawable...)
 */
public class DrawableContainer extends AbstractDrawable<DrawableContainer> {
	protected List<Drawable<?>> children = new ArrayList<>();
	protected int translateX = 0, translateY = 0;

	public List<Drawable<?>> getReadonlyChildren() { return Collections.unmodifiableList(children); }

	public DrawableContainer add(Drawable<?>... children) {
		for (Drawable<?> child : children) this.children.add(child);
		return this;
	}

	public DrawableContainer remove(Drawable<?>... children) {
		for (Drawable<?> target : children) this.children.remove(target);
		return this;
	}

	public DrawableContainer insert(int index, Drawable<?> child) {
		this.children.add(index, child);
		return this;
	}

	public int getTranslateX() { return translateX; }

	public DrawableContainer setTranslateX(int translateX) {
		this.translateX = translateX;
		this.geometryRecomputeNeeded = true;
		return this;
	}

	public int getTranslateY() { return translateY; }

	public DrawableContainer setTranslateY(int translateY) {
		this.translateY = translateY;
		this.geometryRecomputeNeeded = true;
		return this;
	}

	public DrawableContainer setTranslate(int x, int y) {
		this.translateX = x;
		this.translateY = y;
		this.geometryRecomputeNeeded = true;
		return this;
	}

	@Override
	public void useComputedGeometry(int x, int y, int width, int height, int globalX, int globalY) {
		super.useComputedGeometry(x, y, width, height, globalX, globalY);
		for (Drawable<?> child : children) recomputeChild(child);
	}

	protected void recomputeChild(Drawable<?> child) {
		if (!child.isVisible()) return;
		((Widget<?>) child).useManager(manager);
		child.getLayout().applyTo(child, width, height, globalX + translateX, globalY + translateY);
	}

	@Override
	public void onRender(DrawContext context, int mouseX, int mouseY, float delta) {
		context.getMatrices().push();
		context.getMatrices().translate(x + getTranslateX(), y + getTranslateY(), 0);
		context.enableScissor(globalX, globalY, globalX + width, globalY + height);

		for (Drawable<?> child : children) {
			if (!child.isVisible()) continue;
			if (child.geometryRecomputeNeeded()) recomputeChild(child);
			((Widget<?>) child).useManager(manager);
			child.onRender(context, mouseX - x, mouseY - y, delta);
		}

		if (manager.isDebugging()) {
			context.getMatrices().translate(-getTranslateX(), -getTranslateY(), 0);
			context.drawBorder(0, 0, width, height, 0xAFFF0000);
		}

		context.disableScissor();
		context.getMatrices().pop();
	}

	@Override
	public boolean onMouseMove(int mouseX, int mouseY, float delta) {
		for (int i = children.size() - 1; i >= 0; i--) {
			Drawable<?> child = children.get(i);
			if (child.onMouseMove(mouseX - x - getTranslateX(), mouseY - y - getTranslateY(), delta)) return true;
		}

		if (super.onMouseMove(mouseX, mouseY, delta)) {
			manager.useHovering(this);
			return true;
		} else {
			if (manager.getHovering() == this) manager.useHovering(null);
			return false;
		}
	}

	@Override
	public boolean onMouseDown(int mouseX, int mouseY, float delta, int button) {
		boolean isInContainer = testGeometry(mouseX, mouseY);

		if (isInContainer) {
			for (int i = children.size() - 1; i >= 0; i--) {
				Drawable<?> child = children.get(i);
				if (child.onMouseDown(mouseX - x - getTranslateX(), mouseY - y - getTranslateY(), delta, button))
					return true;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onMouseUp(int mouseX, int mouseY, float delta, int button) {
		for (int i = children.size() - 1; i >= 0; i--) {
			Drawable<?> child = children.get(i);
			if (child.onMouseUp(mouseX - x - getTranslateX(), mouseY - y - getTranslateY(), delta, button)) return true;
		}

		return super.onMouseMove(mouseX, mouseY, delta);
	}

	@Override
	public boolean onMouseScroll(int mouseX, int mouseY, int deltaX, int deltaY) {
		boolean isInContainer = testGeometry(mouseX, mouseY);
		if (isInContainer) {
			for (int i = children.size() - 1; i >= 0; i--) {
				Drawable<?> child = children.get(i);
				if (child.onMouseScroll(mouseX - x - getTranslateX(), mouseY - y - getTranslateY(), deltaX, deltaY))
					return true;
			}

			return false;
		} else {
			return false;
		}
	}
}
