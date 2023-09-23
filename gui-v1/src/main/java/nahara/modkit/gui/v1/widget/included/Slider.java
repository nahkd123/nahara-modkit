package nahara.modkit.gui.v1.widget.included;

import java.text.DecimalFormat;
import java.util.function.Consumer;

import nahara.modkit.gui.v1.widget.AbstractDrawable;
import nahara.modkit.gui.v1.widget.Focusable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Slider extends AbstractDrawable<Slider> implements Focusable<Slider> {
	{
		width = 100;
		height = 24;
	}

	protected float value = 0f;
	private boolean pressing = false;

	public float getValue() { return value; }

	public void setValue(float value) { this.value = value; }

	public Slider value(float value) {
		setValue(value);
		return this;
	}

	@Override
	public void onRender(DrawContext context, float mouseX, float mouseY, float delta) {
		boolean hovering = manager.getHovering() == this;
		boolean focused = manager.getFocus() == this || pressing;
		if (hovering || focused) context.drawBorder(x, y, width, height, focused ? 0xFFFFFFFF : 0xFF7F7F7F);

		int handleWidth = Math.max(width / 50, 5);
		int handleX = Math.round((width - 10) * Math.max(Math.min(getValue(), 1f), 0f));

		context.drawHorizontalLine(x + 5, x + width - 5, y + height / 2 - 1,
			(focused || hovering) ? 0xFFFFFFFF : 0xFF7F7F7F);
		context.fill(
			x + 5 + handleX - handleWidth / 2, y + 2,
			x + 5 + handleX + handleWidth / 2, y + height - 2,
			0xFFFFFFFF);
	}

	@Override
	public boolean onMouseDown(float mouseX, float mouseY, float delta, int button) {
		if (super.onMouseDown(mouseX, mouseY, delta, button)) {
			manager.useFocus(this);
			pressing = true;
			MinecraftClient.getInstance().getSoundManager()
				.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
			setValue(Math.max(Math.min((mouseX - 5) / (width - 10), 1f), 0f));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onMouseMove(float mouseX, float mouseY, float delta) {
		if (pressing) {
			setValue(Math.max(Math.min((mouseX - 5) / (width - 10), 1f), 0f));
			return true;
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
	public boolean onMouseUp(float mouseX, float mouseY, float delta, int button) {
		pressing = false;
		return super.onMouseUp(mouseX, mouseY, delta, button);
	}

	private static final DecimalFormat DEBUG_FORMATTER = new DecimalFormat("#0.000");

	@Override
	public void drawDebugLines(Consumer<Object> debugConsumer) {
		debugConsumer.accept(Text.literal("Slider = ")
			.append(Text.literal(DEBUG_FORMATTER.format(getValue())).styled(s -> s.withColor(Formatting.GREEN))));
	}
}
