package nahara.modkit.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nahara.modkit.annotations.v1.AutoRegister;
import nahara.modkit.annotations.v1.Dependencies;
import nahara.modkit.annotations.v1.Dependency;
import nahara.modkit.annotations.v1.EntryPoint;
import nahara.modkit.annotations.v1.Mod;
import nahara.modkit.scheduler.v1.Schedulers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

@Mod(modid = "nahara-modkit-example", version = "1.0.0")
@Dependencies({
	@Dependency(value = "fabricloader"),
	@Dependency(value = "fabric-api"),
	@Dependency(value = "minecraft", version = "~1.20.1")
})
@EntryPoint
public class ExampleMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("nahara-modkit-example");

	@AutoRegister public static final Item MY_ITEM = new Item(new FabricItemSettings());

	@Override
	public void onInitialize() {
		LOGGER.info("Hello world!");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("schedulertest").executes(ctx -> {
				ctx.getSource().sendMessage(Text.literal("Scheduling tasks..."));
				var scheduler = Schedulers.from(ctx.getSource().getServer());
				scheduler
				.scheduleNextTick(() -> ctx.getSource().sendMessage(Text.literal("Called on next tick!")))
				.andThen($ -> scheduler.wait(100))
				.afterThatDo($ -> { ctx.getSource().sendMessage(Text.literal("Waited for 5 seconds!")); });
				return 1;
			}));
		});
	}

	@EntryPoint
	public static void myEntryPoint() {
		LOGGER.info("very sus entry point indeed. no more ModInitializer");
	}
}
