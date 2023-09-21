package nahara.modkit.example;

import nahara.modkit.annotations.v1.EntryPoint;
import nahara.modkit.annotations.v1.Env;
import nahara.modkit.annotations.v1.Mod;
import nahara.modkit.example.client.ExampleScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;

@Mod(modid = "nahara-modkit-example", version = "1.0.0") // FIXME
@EntryPoint(environment = Env.CLIENT)
public class ExampleModClient implements ClientModInitializer {
	private boolean openScreenNextTick = false;

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("openexamplescreen")
				.executes(ctx -> {
					openScreenNextTick = true;
					return 1;
				}));
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (openScreenNextTick) {
				openScreenNextTick = false;
				ExampleScreen screen = new ExampleScreen();
				client.setScreen(screen);
				client.inGameHud.getChatHud().addMessage(Text.literal("opening example screen"));
			}
		});
	}
}
