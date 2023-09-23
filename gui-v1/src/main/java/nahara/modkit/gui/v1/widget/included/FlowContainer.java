package nahara.modkit.gui.v1.widget.included;

import nahara.modkit.gui.v1.layout.Axis;
import nahara.modkit.gui.v1.widget.Drawable;
import nahara.modkit.gui.v1.widget.Widget;
import net.minecraft.client.gui.DrawContext;

/**
 * <p>
 * Like {@link DrawableContainer}, but its children are placed next to each
 * other.
 * </p>
 * <p>
 * Child layouts that can affect position like origin or anchor will not have
 * any effects.
 * </p>
 * <p>
 * Translations will not have any effects in this container.
 * </p>
 */
public class FlowContainer extends DrawableContainer {
	protected Axis flowAxis = Axis.X;

	public Axis getFlowAxis() { return flowAxis; }

	public void setFlowAxis(Axis flowAxis) { this.flowAxis = flowAxis; }

	public FlowContainer flowAxis(Axis flowAxis) {
		setFlowAxis(flowAxis);
		return this;
	}

	@Override
	public FlowContainer add(Drawable<?>... children) {
		super.add(children);
		geometryRecomputeNeeded = true;
		return this;
	}

	@Override
	public FlowContainer remove(Drawable<?>... children) {
		super.remove(children);
		geometryRecomputeNeeded = true;
		return this;
	}

	@Override
	public FlowContainer insert(int index, Drawable<?> child) {
		super.insert(index, child);
		geometryRecomputeNeeded = true;
		return this;
	}

	@Override
	public int getTranslateX() { return 0; }

	@Override
	public int getTranslateY() { return 0; }

	@Override
	public FlowContainer setTranslateX(int translateX) {
		return this;
	}

	@Override
	public FlowContainer setTranslateY(int translateY) {
		return this;
	}

	protected void applyFlow() {
		int flowPos = 0;

		for (Drawable<?> child : children) {
			((Widget<?>) child).useManager(manager);
			switch (getFlowAxis()) {
			case X:
				child.useComputedGeometry(
					flowPos, child.getY(),
					child.getWidth(), child.getLayout().hasFillAxis(Axis.Y) ? height : child.getHeight(),
					globalX + flowPos, globalY);
				flowPos += child.getWidth();
				break;
			case Y:
				child.useComputedGeometry(
					child.getX(), flowPos,
					child.getLayout().hasFillAxis(Axis.X) ? width : child.getWidth(), child.getHeight(),
					globalX, globalY + flowPos);
				flowPos += child.getHeight();
				break;
			default:
				break;
			}
		}

		switch (getFlowAxis()) {
		case X:
			setWidth(width = flowPos);
			break;
		case Y:
			setHeight(height = flowPos);
			break;
		}
	}

	@Override
	public void useComputedGeometry(int x, int y, int width, int height, int globalX, int globalY) {
		super.useComputedGeometry(x, y, width, height, globalX, globalY);
		applyFlow();
	}

	@Override
	public void onRender(DrawContext context, int mouseX, int mouseY, float delta) {
		applyFlow();
		super.onRender(context, mouseX, mouseY, delta);
	}
}
