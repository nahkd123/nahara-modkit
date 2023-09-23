package nahara.modkit.gui.v1.widget.included;

import org.jetbrains.annotations.Nullable;

import nahara.modkit.gui.v1.widget.AbstractDrawable;
import nahara.modkit.gui.v1.widget.Focusable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

/**
 * <p>
 * A custom button implementation.
 * </p>
 * <p>
 * <b>Listening for clicks:</b> You can listen for clicks by creating an
 * anonymous class of {@link Button}:
 * 
 * <pre>
 * new NaharaButton() {
 * 	&#64;Override
 * 	public boolean onMouseDown(int mouseX, int mouseY, float delta, int button) {
 * 		if (super.onMouseDown(mouseX, mouseY, delta, button)) {
 * 			LOGGER.info("button is clicked!");
 * 			return true;
 * 		} else {
 * 			return false;
 * 		}
 * 	}
 * }.width(100).height(24);
 * </pre>
 * </p>
 * 
 * @see #setLabel(Text)
 * @see #setPressed(boolean)
 */
public class Button extends AbstractDrawable<Button> implements Focusable<Button> {
	{
		width = 100;
		height = 24;
	}

	protected Text label = null;
	protected boolean pressed = false;
	private boolean pressing = false;

	public Text getLabel() { return label == null ? Text.empty() : label; }

	public void setLabel(Text label) { this.label = label == null ? Text.empty() : label; }

	public Button label(@Nullable Text label) {
		setLabel(label);
		return this;
	}

	public boolean isPressed() { return pressed; }

	public void setPressed(boolean pressed) { this.pressed = pressed; }

	/**
	 * <p>
	 * Unlike {@link #isPressed()}, this method returns whether this button has a
	 * pressed state or user is pressing this button.
	 * </p>
	 * 
	 * @return The pressing state of the button.
	 */
	public boolean isPressing() { return pressed || pressing; }

	public Button pressed(boolean pressed) {
		this.pressed = pressed;
		return this;
	}

	@Override
	public void onRender(DrawContext context, float mouseX, float mouseY, float delta) {
		Text label = getLabel();
		boolean hovering = manager.getHovering() == this || manager.getFocus() == this;
		boolean pressing = isPressing();
		int lightningHeight = Math.max(height / 7, 2);
		int lightningTop = pressing ? 0xFF6F6F6F : 0xFFBFBFBF;
		int lightningMid = pressing ? 0xFF8F8F8F : 0xFF9F9F9F;
		int lightningBot = pressing ? 0xFFAFAFAF : 0xFF7F7F7F;

		context.enableScissor(globalX, globalY, globalX + width, globalY + height);
		context.fill(x, y, x + width, y + height, lightningMid);
		context.fill(x, y, x + width, y + lightningHeight, lightningTop);
		context.fill(x, y + height - lightningHeight, x + width, y + height, lightningBot);
		context.drawBorder(x, y, width, height, hovering ? 0xFFFFFFFF : 0xFF000000);

		if (label != null) {
			int labelWidth = manager.getTextRenderer().getWidth(label);
			int labelHeight = manager.getTextRenderer().fontHeight;
			context.drawText(
				manager.getTextRenderer(), label,
				x + (width - labelWidth) / 2 + 1, y + (height - labelHeight) / 2 + 2,
				0x4F4F4F, false);
			context.drawText(
				manager.getTextRenderer(), label,
				x + (width - labelWidth) / 2, y + (height - labelHeight) / 2 + 1,
				0xFFFFFF, false);
		}

		context.disableScissor();
	}

	@Override
	public boolean onMouseMove(float mouseX, float mouseY, float delta) {
		if (super.onMouseMove(mouseX, mouseY, delta)) {
			manager.useHovering(this);
			return true;
		} else {
			if (manager.getHovering() == this) manager.useHovering(null);
			return false;
		}
	}

	@Override
	public boolean onMouseDown(float mouseX, float mouseY, float delta, int button) {
		if (super.onMouseDown(mouseX, mouseY, delta, button)) {
			manager.useFocus(this);
			pressing = true;
			MinecraftClient.getInstance().getSoundManager()
				.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onMouseUp(float mouseX, float mouseY, float delta, int button) {
		pressing = false;
		return super.onMouseUp(mouseX, mouseY, delta, button);
	}
}
