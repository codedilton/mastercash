#!/bin/bash

APP_HOME=$(cd "$(dirname "$0")" && pwd)
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Diretório do usuário para Gradle
GRADLE_USER_HOME_DEFAULT="$HOME/.gradle"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$GRADLE_USER_HOME_DEFAULT}"

# Local do Gradle
GRADLE_VERSION="8.2"
GRADLE_HOME="$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-all"

# Se não encontrar, tenta qualquer versão instalada
if [ ! -d "$GRADLE_HOME" ]; then
    GRADLE_HOME=$(find "$GRADLE_USER_HOME/wrapper/dists" -maxdepth 1 -type d -name "gradle-*" 2>/dev/null | head -1)
fi

if [ -z "$GRADLE_HOME" ] || [ ! -d "$GRADLE_HOME" ]; then
    echo "Erro: Gradle não encontrado. Execute setup-gradle.sh primeiro."
    exit 1
fi

# Usar o java do JAVA_HOME ou do sistema
if [ -n "$JAVA_HOME" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
else
    JAVA_CMD=java
fi

# Executar o Gradle wrapper
exec "$JAVA_CMD" -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
