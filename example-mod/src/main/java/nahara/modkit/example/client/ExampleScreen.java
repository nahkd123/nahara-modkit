package nahara.modkit.example.client;

import nahara.modkit.gui.v1.NaharaScreen;
import nahara.modkit.gui.v1.layout.Anchor;
import nahara.modkit.gui.v1.layout.Axis;
import nahara.modkit.gui.v1.widget.included.Box;
import nahara.modkit.gui.v1.widget.included.Button;
import nahara.modkit.gui.v1.widget.included.Checkbox;
import nahara.modkit.gui.v1.widget.included.DrawableContainer;
import nahara.modkit.gui.v1.widget.included.FlowContainer;
import nahara.modkit.gui.v1.widget.included.Slider;
import nahara.modkit.gui.v1.widget.included.Textbox;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ExampleScreen extends NaharaScreen {
	public ExampleScreen() {
		super(Text.literal("Example Nahara Screen"));
		debugging = true;

		add(new FlowContainer().flowAxis(Axis.Y).x(4).y(12).width(100).height(200).add(
			new Button().label(Text.literal("Controls")).x(0).y(0).width(100).height(24),
			new Button().label(Text.literal("Button (Nx24)")).x(0).width(100).height(24),
			new Button().label(Text.literal("Button (Nx14)")).x(0).width(100).height(14),
			new Textbox().content("Hello World!").x(0).width(100).height(24),
			new Textbox().content("Hello World!").x(0).width(100).height(14),
			new Slider().value(0.5f).x(0).width(100).height(24),
			new Slider().value(0.25f).x(0).width(100).height(14),
			new Checkbox().x(0).width(100).height(24),
			new Checkbox().x(0).width(100).height(14)));

		var layoutDemo = new DrawableContainer().x(108).y(12).width(100).height(100);
		for (Anchor pos : Anchor.values()) {
			layoutDemo.add(new DrawableContainer().x(0).y(0).width(15).height(15)
				.layout(layout -> layout.setAnchor(pos).setOrigin(pos))
				.add(
					new Box().x(0).y(0).width(10).height(10).backgroundColor(0xFFFF0000),
					new Box().x(5).y(5).width(10).height(10).backgroundColor(0xFF00FF00)));
		}
		add(layoutDemo);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawText(textRenderer, "Welcome to ExampleScreen!", 4, 2, 0xFFFFFF, true);
	}
}
