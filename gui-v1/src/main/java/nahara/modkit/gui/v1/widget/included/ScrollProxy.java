package nahara.modkit.gui.v1.widget.included;

import com.google.common.base.Preconditions;

import nahara.modkit.gui.v1.widget.AbstractDrawable;
import nahara.modkit.gui.v1.widget.Drawable;
import nahara.modkit.gui.v1.widget.Widget;
import nahara.modkit.gui.v1.widget.WidgetsManager;
import net.minecraft.client.gui.DrawContext;

/**
 * <p>
 * A proxy to another {@link Drawable} that adds scroll bars.
 * </p>
 */
public class ScrollProxy extends AbstractDrawable<ScrollProxy> {
	private Drawable<?> underlying;
	protected boolean verticalScrollbar = true, horizontalScrollbar = true;
	protected float scrollX = 0, scrollY = 0;

	public ScrollProxy(Drawable<?> underlying) {
		Preconditions.checkNotNull(underlying, "underlying drawable can't be null");
		this.underlying = underlying;
	}

	public Drawable<?> getUnderlying() { return underlying; }

	public boolean isVerticalScrollbar() { return verticalScrollbar; }

	public void setVerticalScrollbar(boolean verticalScrollbar) { this.verticalScrollbar = verticalScrollbar; }

	public ScrollProxy verticalScrollbar(boolean verticalScrollbar) {
		setVerticalScrollbar(verticalScrollbar);
		return this;
	}

	public boolean isHorizontalScrollbar() { return horizontalScrollbar; }

	public void setHorizontalScrollbar(boolean horizontalScrollbar) { this.horizontalScrollbar = horizontalScrollbar; }

	public ScrollProxy horizontalScrollbar(boolean horizontalScrollbar) {
		setHorizontalScrollbar(horizontalScrollbar);
		return this;
	}

	public float getScrollX() { return scrollX; }

	public void setScrollX(float scrollX) {
		int[] underlyingGeom = new int[6];
		underlying.getComputedGeometry(underlyingGeom);
		this.scrollX = Math.min(Math.max(scrollX, -(underlyingGeom[2] - width)), 0);
	}

	public ScrollProxy scrollX(float scrollX) {
		setScrollX(scrollX);
		return this;
	}

	public float getScrollY() { return scrollY; }

	public void setScrollY(float scrollY) {
		int[] underlyingGeom = new int[6];
		underlying.getComputedGeometry(underlyingGeom);
		this.scrollY = Math.min(Math.max(scrollY, -(underlyingGeom[3] - height)), 0);
	}

	public ScrollProxy scrollY(float scrollY) {
		setScrollY(scrollY);
		return this;
	}

	@Override
	public void useManager(WidgetsManager manager) {
		super.useManager(manager);
		((Widget<?>) underlying).useManager(manager);
	}

	@Override
	public void useComputedGeometry(int x, int y, int width, int height, int globalX, int globalY) {
		super.useComputedGeometry(x, y, width, height, globalX, globalY);
		underlying.getLayout().applyTo(underlying, width, height, Math.round(globalX + scrollX),
			Math.round(globalY + scrollY));
	}

	@Override
	public void onRender(DrawContext context, float mouseX, float mouseY, float delta) {
		int[] underlyingGeom = new int[6];
		int scrollX = Math.round(getScrollX());
		int scrollY = Math.round(getScrollY());
		underlying.getComputedGeometry(underlyingGeom);

		context.getMatrices().push();
		context.getMatrices().translate(x + scrollX, y + scrollY, 0);
		context.enableScissor(globalX, globalY, globalX + width, globalY + height);

		underlying.onRender(context, mouseX, mouseY, delta);

		context.getMatrices().translate(-scrollX, -scrollY, 0);
		float scrollXPages = underlyingGeom[2] / (float) width;
		if (scrollXPages > 1f) {
			float scrollXLeftProg = (-scrollX) / (float) underlyingGeom[2];
			float scrollXRightProg = (-scrollX + width) / (float) underlyingGeom[2];
			int scrollXWidth = Math.round((1f / scrollXPages) * width);
			int scrollXSpaces = width - scrollXWidth;
			int scrollXLeft = Math.round(scrollXSpaces * scrollXLeftProg);
			int scrollXRight = Math.round(scrollXSpaces * (1f - scrollXRightProg));
			context.fill(scrollXLeft, height - 2, width - scrollXRight, height, 0xFFFFFFFF);
		}

		float scrollYPages = underlyingGeom[3] / (float) height;
		if (scrollYPages > 1f) {
			float scrollYTopProg = (-scrollY) / (float) underlyingGeom[3];
			float scrollYBottomProg = (-scrollY + height) / (float) underlyingGeom[3];
			int scrollYHeight = Math.round((1f / scrollYPages) * height);
			int scrollYSpaces = height - scrollYHeight;
			int scrollYTop = Math.round(scrollYSpaces * scrollYTopProg);
			int scrollYBottom = Math.round(scrollYSpaces * (1f - scrollYBottomProg));
			context.fill(width - 2, scrollYTop, width, height - scrollYBottom, 0xFFFFFFFF);
		}

		context.disableScissor();
		context.getMatrices().pop();
	}

	@Override
	public boolean onMouseScroll(float mouseX, float mouseY, float deltaX, float deltaY) {
		boolean isInProxy = testGeometry(mouseX, mouseY);
		if (!isInProxy) return false;
		if (underlying.onMouseScroll(mouseX, mouseY, deltaX, deltaY)) return true;

		scrollX(getScrollX() + deltaX).scrollY(getScrollY() + deltaY);
		geometryRecomputeNeeded = true;
		return true;
	}

	@Override
	public boolean onMouseDown(float mouseX, float mouseY, float delta, int button) {
		boolean isInProxy = testGeometry(mouseX, mouseY);
		if (!isInProxy) return false;
		return underlying.onMouseDown(mouseX - x - scrollX, mouseY - y - scrollY, delta, button);
	}

	@Override
	public boolean onMouseMove(float mouseX, float mouseY, float delta) {
		boolean isInProxy = testGeometry(mouseX, mouseY);
		if (!isInProxy) return false;
		return underlying.onMouseMove(mouseX - x - scrollX, mouseY - y - scrollY, delta);
	}

	@Override
	public boolean onMouseUp(float mouseX, float mouseY, float delta, int button) {
		return underlying.onMouseUp(mouseX, mouseY, delta, button);
	}
}
