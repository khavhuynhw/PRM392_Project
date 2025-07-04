# Java 17 Upgrade Guide

This document describes the changes made to upgrade the project from Java 8 to Java 17.

## Changes Made

### 1. App-level build.gradle.kts
- Updated `compileOptions` from `JavaVersion.VERSION_1_8` to `JavaVersion.VERSION_17`
- Added Java toolchain configuration:
```kotlin
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
```

### 2. Top-level build.gradle.kts
- Added global Java compilation settings for all projects:
```kotlin
allprojects {
    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }
}
```

## Benefits of Java 17

1. **Long-term Support**: Java 17 is an LTS (Long Term Support) version
2. **Performance**: Better performance and memory management
3. **Security**: Enhanced security features
4. **Modern Features**: Access to modern Java language features
5. **Future Compatibility**: Better compatibility with future Android and library updates

## Requirements

- **Android Studio**: Version 2023.1.1 or later
- **JDK**: Version 17 or later
- **Gradle**: Version 8.0 or later

## Verification Steps

1. **Check Java Version in Android Studio**:
   - Go to File → Project Structure
   - Under SDK Location, verify "Gradle JDK" is set to Java 17

2. **Check Gradle JDK**:
   - Go to File → Settings → Build, Execution, Deployment → Build Tools → Gradle
   - Verify "Gradle JDK" is set to Java 17

3. **Clean and Rebuild**:
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

## Troubleshooting

### If you encounter build errors:

1. **Update Android Studio** to the latest version
2. **Download JDK 17** if not already installed
3. **Set JAVA_HOME** environment variable to point to JDK 17
4. **Invalidate caches** in Android Studio (File → Invalidate Caches and Restart)

### Common Issues:

1. **"Unsupported class file major version"**: Ensure you're using JDK 17
2. **"Could not find toolchain"**: Download and configure JDK 17 in Android Studio
3. **"Gradle sync failed"**: Clean project and sync again

## Migration Notes

- All existing code should be compatible with Java 17
- No code changes were required for this upgrade
- The upgrade only affects the build configuration
- All existing functionality remains intact

## Next Steps

After upgrading to Java 17, you can:

1. **Use modern Java features** in your code (if needed)
2. **Update dependencies** to their latest versions
3. **Consider using Kotlin** for new code (optional)
4. **Enable additional compiler warnings** for better code quality

## Support

If you encounter any issues with the Java 17 upgrade:

1. Check the Android Studio documentation
2. Verify your JDK installation
3. Clean and rebuild the project
4. Check the Gradle logs for specific error messages 