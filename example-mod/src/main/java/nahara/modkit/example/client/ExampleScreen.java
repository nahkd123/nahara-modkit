package nahara.modkit.example.client;

import nahara.modkit.gui.v1.NaharaScreen;
import nahara.modkit.gui.v1.layout.Anchor;
import nahara.modkit.gui.v1.widget.included.Box;
import nahara.modkit.gui.v1.widget.included.NaharaButton;
import net.minecraft.text.Text;

public class ExampleScreen extends NaharaScreen {
	public ExampleScreen() {
		super(Text.literal("Example Nahara Screen"));

		for (Anchor a : Anchor.values()) {
			root.add(new Box()
				.x(0).y(0).width(20).height(20)
				.layout(layout -> layout.setAnchor(a).setOrigin(a)));
			root.add(new NaharaButton()
				.label(Text.literal(a.toString()))
				.x(0).y(0).width(100).height(14)
				.layout(layout -> layout.setAnchor(a).setOrigin(a)));
		}

		debugging = true;
	}
}
