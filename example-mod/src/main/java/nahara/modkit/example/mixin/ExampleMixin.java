package nahara.modkit.example.mixin;

import org.spongepowered.asm.mixin.Mixin;

import nahara.modkit.annotations.v1.AutoMixin;
import net.minecraft.server.MinecraftServer;

@AutoMixin(isClient = false)
@Mixin(MinecraftServer.class)
public abstract class ExampleMixin {
}
