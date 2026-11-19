#!/bin/sh

# Gradle wrapper script for MasterCash project
# This script downloads and runs Gradle automatically

APP_HOME=$(cd "$(dirname "$0")" && pwd)
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Default JVM options
DEFAULT_JVM_OPTS=""

# Determine the Java command to use
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Check if Gradle is installed locally, otherwise download it
GRADLE_USER_HOME_DEFAULT="$HOME/.gradle"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$GRADLE_USER_HOME_DEFAULT}"
GRADLE_VERSION="8.2"
GRADLE_DIST_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
GRADLE_ZIP="$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-bin.zip"
GRADLE_HOME="$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-all"

# Try to find gradle in the wrapper dists
if [ -d "$GRADLE_HOME" ]; then
    GRADLE_CMD="$GRADLE_HOME/bin/gradle"
elif command -v gradle >/dev/null 2>&1; then
    GRADLE_CMD=$(command -v gradle)
else
    echo "Gradle not found. Please install Gradle or use Android Studio to import the project."
    echo "Alternatively, run: sdkmanager --install 'build-tools;36.0.0'"
    exit 1
fi

exec "$JAVACMD" $DEFAULT_JVM_OPTS -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
