package nahara.modkit.gui.v1.widget.included;

import nahara.modkit.gui.v1.widget.AbstractDrawable;
import nahara.modkit.gui.v1.widget.Focusable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class Checkbox extends AbstractDrawable<Checkbox> implements Focusable<Checkbox> {
	{
		width = 14;
		height = 14;
	}

	protected boolean toggled = false;

	public boolean isToggled() { return toggled; }

	public void setToggled(boolean toggled) { this.toggled = toggled; }

	public Checkbox toggled(boolean toggled) {
		setToggled(toggled);
		return this;
	}

	@Override
	public void onRender(DrawContext context, float mouseX, float mouseY, float delta) {
		boolean hovering = manager.getHovering() == this || manager.getFocus() == this;
		int size = Math.min(width, height);

		context.drawBorder(x + 2, y + 2, size - 4, size - 4, hovering ? 0xFFFFFFFF : 0xFF7F7F7F);
		if (toggled) context.fill(x + 4, y + 4, x + size - 4, y + size - 4, 0xFFFFFFFF);
	}

	@Override
	public boolean onMouseDown(float mouseX, float mouseY, float delta, int button) {
		if (super.onMouseDown(mouseX, mouseY, delta, button)) {
			manager.useFocus(this);
			MinecraftClient.getInstance().getSoundManager()
				.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
			setToggled(!isToggled());
			return true;
		} else {
			return false;
		}
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
}
