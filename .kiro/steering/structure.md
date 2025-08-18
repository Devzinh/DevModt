# Project Structure

## Root Directory
```
DevModt/
├── .gradle/           # Gradle build cache and metadata
├── .idea/             # IntelliJ IDEA project files
├── .kiro/             # Kiro AI assistant configuration
├── gradle/            # Gradle wrapper files
├── src/               # Source code
├── build.gradle       # Main build configuration
├── settings.gradle    # Project settings
├── gradle.properties  # Gradle properties (currently empty)
├── gradlew           # Gradle wrapper script (Unix)
└── gradlew.bat       # Gradle wrapper script (Windows)
```

## Source Structure
```
src/
└── main/
    ├── java/
    │   └── me/
    │       └── devplugins/
    │           └── devModt/
    │               └── DevModt.java    # Main plugin class
    └── resources/
        └── plugin.yml                  # Plugin metadata
```

## Package Convention
- **Base Package**: `me.devplugins.devModt`
- **Main Class**: `DevModt` extends `JavaPlugin`
- **Group ID**: `me.devplugins`

## Key Files
- **Main Plugin Class**: `src/main/java/me/devplugins/devModt/DevModt.java`
  - Entry point with `onEnable()` and `onDisable()` methods
- **Plugin Manifest**: `src/main/resources/plugin.yml`
  - Contains plugin metadata, version, API version, and description
  - Version is templated from `build.gradle` during build

## Organization Guidelines
- Follow standard Maven/Gradle directory structure
- Keep plugin logic organized within the base package
- Use subpackages for different feature areas (commands, listeners, utils, etc.)
- Resources go in `src/main/resources/`
- Configuration files should be placed in resources directory