# Nahara's Modkit
_Like [Nahara's Toolkit](https://github.com/nahkd123/nahara-toolkit), but for Fabric mods._

## Project overview
### Annotations (v1)
Use annotations to generate ``fabric.mod.json`` and ``<ID>.mixins.json`` instead of typing it out manually:

```java
@Mod(modid = "my-mod", version = "1.0.0")
@EntryPoint
public class MyModMain implements ModInitializer {
    @Override
    public void onInitialize() {
    }
}

@AutoMixin(isClient = false)
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
}
```

Using ``nahara-modkit-annotations-v1``

```groovy
dependencies {
    // Yes, we don't use modImplementation
    implementation annotationProcessor("me.nahkd:nahara-modkit-annotations-v1:1.0.0-SNAPSHOT")
}

// Include this if you want to use "${version}" in your annotation
// Like this: @Mod(modid = "my-mod", version = "${version}")
compileJava {
    options.compilerArgs << "-Anahara.modkit.expand=version:${project.version}"
}
```

### Scheduler (v1)
Schedule tasks that's synchronized with server thread.

See ``example-mod``.
