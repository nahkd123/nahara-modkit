# Nahara's Modkit: Annotations (v1)
Including annotations and annotation processor.

## Enable APT for Eclipse IDE
Simply add ``com.diffplug.eclipse.apt`` to your project and you're done.

```groovy
plugins {
    id 'com.diffplug.eclipse.apt' version '3.42.2'
}
```

### Using APT with Eclipse Buildship
If you don't want to type ``gradlew eclipse`` everytime you made changes or you just want to use Eclipse Buildship, you have to add ``eclipseFactorypath`` to synchronization tasks:

```groovy
plugins {
    id 'eclipse'
}

eclipse {
    synchronizationTasks 'eclipseFactorypath'
}
```

After that, you have to enable annotation processing by right clicking your project > Properties > Java Compiler > Annotation Processing > Enable project specific settings.
