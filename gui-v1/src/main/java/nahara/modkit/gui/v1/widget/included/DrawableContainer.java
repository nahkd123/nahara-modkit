package nahara.modkit.gui.v1.widget.included;

import java.util.ArrayList;
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
 * @see #getChildren()
 * @see #add(Drawable...)
 */
public class DrawableContainer extends AbstractDrawable<DrawableContainer> {
	protected List<Drawable<?>> children = new ArrayList<>();

	public List<Drawable<?>> getChildren() { return children; }

	public DrawableContainer add(Drawable<?>... children) {
		for (Drawable<?> child : children) this.children.add(child);
		return this;
	}

	@Override
	public void useComputedGeometry(int x, int y, int width, int height, int globalX, int globalY) {
		super.useComputedGeometry(x, y, width, height, globalX, globalY);

		for (Drawable<?> child : children) {
			if (!child.isVisible()) continue;
			((Widget<?>) child).useManager(manager);
			child.getLayout().applyTo(child, width, height, globalX, globalY);
		}
	}

	@Override
	public void onRender(DrawContext context, int mouseX, int mouseY, float delta) {
		context.getMatrices().push();
		context.getMatrices().translate(x, y, 0);
		context.enableScissor(globalX, globalY, globalX + width, globalY + height);

		for (Drawable<?> child : children) {
			if (!child.isVisible()) continue;
			((Widget<?>) child).useManager(manager);
			child.onRender(context, mouseX - x, mouseY - y, delta);
		}

		if (manager.isDebugging()) context.drawBorder(x, y, width, height, 0xAFFF0000);

		context.disableScissor();
		context.getMatrices().pop();
	}

	@Override
	public boolean onMouseMove(int mouseX, int mouseY, float delta) {
		for (int i = children.size() - 1; i >= 0; i--) {
			Drawable<?> child = children.get(i);
			if (child.onMouseMove(mouseX - x, mouseY - y, delta)) return true;
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
		for (int i = children.size() - 1; i >= 0; i--) {
			Drawable<?> child = children.get(i);
			if (child.onMouseDown(mouseX - x, mouseY - y, delta, button)) return true;
		}

		return super.onMouseMove(mouseX, mouseY, delta);
	}

	@Override
	public boolean onMouseUp(int mouseX, int mouseY, float delta, int button) {
		for (int i = children.size() - 1; i >= 0; i--) {
			Drawable<?> child = children.get(i);
			if (child.onMouseUp(mouseX - x, mouseY - y, delta, button)) return true;
		}

		return super.onMouseMove(mouseX, mouseY, delta);
	}
}
