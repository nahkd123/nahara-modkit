package nahara.modkit.gui.v1;

import org.jetbrains.annotations.Nullable;

import nahara.modkit.gui.v1.widget.Drawable;
import nahara.modkit.gui.v1.widget.Focusable;
import nahara.modkit.gui.v1.widget.Widget;
import nahara.modkit.gui.v1.widget.WidgetsManager;
import nahara.modkit.gui.v1.widget.included.DrawableContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class NaharaScreen extends Screen implements WidgetsManager {
	protected DrawableContainer root = new DrawableContainer();
	protected Drawable<?> hovering = null;
	protected Focusable<?> focusing = null;
	protected boolean debugging = false;

	protected NaharaScreen(Text title) {
		super(title);
	}

	protected void add(Drawable<?>... children) {
		root.add(children);
	}

	@Override
	protected void init() {
		root.useManager(this);
		root.useComputedGeometry(0, 0, width, height, 0, 0);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		root.useManager(this);
		root.onMouseMove(mouseX, mouseY, delta);
		root.onRender(context, mouseX, mouseY, delta);

		if (isDebugging()) {
			int[] geom = new int[6];

			if (hovering != null) debug$drawBorder(context, hovering, 0xFFFF0000);
			if (focusing != null && focusing instanceof Drawable dw) debug$drawBorder(context, dw, 0xFF00FF00);

			context.getMatrices().push();
			context.getMatrices().translate(mouseX, mouseY, 0);
			context.fill(-1, -1, 1, 1, 0xFFFF0000);

			context.getMatrices().translate(4, 8, 0);
			debug$addText(context, "Mouse (" + mouseX + ", " + mouseY + ")", 0xFFFFFF);

			if (hovering != null) {
				hovering.getComputedGeometry(geom);
				boolean hitTest = hovering.testGeometry(mouseX - geom[4] + geom[0], mouseY - geom[5] + geom[1]);
				debug$addText(context, "Hovering (" + geom[4] + ", " + geom[5] + ") (hitTest = " + hitTest + ")",
					0xFF0000);
				context.getMatrices().translate(4, 0, 0);
				((Widget<?>) hovering).drawDebugLines(obj -> debug$addText(context, obj, 0xFF0000));
				context.getMatrices().translate(-4, 0, 0);
			}

			if (focusing != null) {
				if (focusing instanceof Drawable dw) {
					dw.getComputedGeometry(geom);
					boolean hitTest = dw.testGeometry(mouseX - geom[4] + geom[0], mouseY - geom[5] + geom[1]);
					debug$addText(context, "Focusing (" + geom[4] + ", " + geom[5] + ") (hitTest = " + hitTest + ")",
						0x00FF00);
				} else {
					debug$addText(context, "Focusing:", 0x00FF00);
				}

				context.getMatrices().translate(4, 0, 0);
				((Widget<?>) focusing).drawDebugLines(obj -> debug$addText(context, obj, 0x00FF00));
				context.getMatrices().translate(-4, 0, 0);
			}

			context.getMatrices().pop();
		}
	}

	private void debug$drawBorder(DrawContext context, Drawable<?> drawable, int color) {
		int[] geom = new int[6];
		drawable.getComputedGeometry(geom);
		context.drawBorder(geom[4] - 2, geom[5] - 2, geom[2] + 4, geom[3] + 4, color);
		// TODO draw anchor + origin
	}

	private void debug$addText(DrawContext context, Object obj, int color) {
		if (obj == null) return;
		Text text = obj instanceof Text t ? t : Text.literal(obj.toString());
		int width = textRenderer.getWidth(text);
		context.fill(-1, -1, width + 2, textRenderer.fontHeight - 1, 0xAF000000);
		context.drawText(textRenderer, text, 0, 0, color, false);
		context.getMatrices().translate(0, textRenderer.fontHeight, 0);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// Mouse down could affect focus, so we don't send interaction to focused
		// element
		return root.onMouseDown((float) mouseX, (float) mouseY, 0, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (focusing != null) {
			int[] geom = new int[6];
			((Drawable<?>) focusing).getComputedGeometry(geom);
			if (((Drawable<?>) focusing).onMouseUp((float) mouseX - geom[4], (float) mouseY - geom[5], 0, button))
				return true;
		}

		return root.onMouseUp((float) mouseX, (float) mouseY, 0, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		return root.onMouseScroll((float) mouseX, (float) mouseY, (float) scrollX * 10f, (float) scrollY * 10f);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (this.focusing != null) this.focusing.onCharacterTyped(chr, modifiers);
		return super.charTyped(chr, modifiers);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.focusing != null) this.focusing.onKeyDown(keyCode, scanCode, modifiers);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (this.focusing != null) this.focusing.onKeyUp(keyCode, scanCode, modifiers);
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public <T extends Drawable<T>> void useHovering(@Nullable T widget) {
		hovering = widget;
	}

	@Override
	public Drawable<?> getHovering() { return hovering; }

	@Override
	public <T extends Focusable<T>> void useFocus(@Nullable T widget) {
		if (focusing != null) focusing.onFocusLost();
		if ((focusing = widget) != null) widget.onFocusGain();
	}

	@Override
	public Focusable<?> getFocus() { return focusing; }

	@Override
	public boolean isDebugging() { return debugging && client.getDebugHud().shouldShowDebugHud(); }

	@Override
	public TextRenderer getTextRenderer() { return textRenderer; }

	@Override
	public MinecraftClient getClient() { return client; }
}
