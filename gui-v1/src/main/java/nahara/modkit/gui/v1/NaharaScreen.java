package nahara.modkit.gui.v1;

import org.jetbrains.annotations.Nullable;

import nahara.modkit.gui.v1.widget.Drawable;
import nahara.modkit.gui.v1.widget.Focusable;
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

			if (hovering != null) {
				hovering.getComputedGeometry(geom);
				context.drawBorder(geom[4], geom[5], geom[2], geom[3], 0xFFFF0000);
			}

			context.getMatrices().push();
			context.getMatrices().translate(mouseX, mouseY, 0);
			context.drawText(textRenderer, "Mouse (" + mouseX + ", " + mouseY + ")", 0, 0, 0xFF0000, true);
			context.getMatrices().pop();
		}
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
