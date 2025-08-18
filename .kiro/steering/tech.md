# Technology Stack

## Build System
- **Build Tool**: Gradle with Groovy DSL
- **Java Version**: 21 (source and target compatibility)
- **Encoding**: UTF-8

## Framework & Dependencies
- **Platform**: Spigot/Bukkit API for Minecraft 1.21
- **Main Dependency**: `org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT`
- **Plugin Runner**: `xyz.jpenilla.run-paper` plugin for development testing

## Repositories
- Maven Central
- Spigot MC Repository: `https://hub.spigotmc.org/nexus/content/repositories/snapshots/`

## Common Commands

### Build & Development
```bash
# Build the plugin
./gradlew build

# Run development server with plugin loaded
./gradlew runServer

# Clean build artifacts
./gradlew clean

# Compile Java sources
./gradlew compileJava
```

### Windows Commands
```cmd
# Build the plugin
gradlew.bat build

# Run development server
gradlew.bat runServer

# Clean build
gradlew.bat clean
```

## Development Notes
- Plugin JAR is automatically loaded when using `runServer` task
- `plugin.yml` is processed with version templating during build
- Development server runs Minecraft 1.21