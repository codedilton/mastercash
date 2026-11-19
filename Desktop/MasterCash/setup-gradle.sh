#!/bin/bash
# Script para configurar o Gradle Wrapper no projeto MasterCash
# Execute com: bash setup-gradle.sh

set -e

echo "🔧 Configurando Gradle Wrapper para MasterCash..."

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GRADLE_VERSION="8.2"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
WRAPPER_DIR="$PROJECT_DIR/gradle/wrapper"
GRADLE_DIST_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

# Criar diretórios necessários
mkdir -p "$WRAPPER_DIR"
mkdir -p "$GRADLE_USER_HOME/wrapper/dists"

# Baixar o Gradle distribution
echo "📥 Baixando Gradle $GRADLE_VERSION..."
GRADLE_ZIP="$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-bin.zip"

if [ ! -f "$GRADLE_ZIP" ]; then
    curl -L -o "$GRADLE_ZIP" "$GRADLE_DIST_URL"
    echo "✅ Gradle baixado em $GRADLE_ZIP"
else
    echo "✅ Gradle já existe em $GRADLE_ZIP"
fi

# Extrair o Gradle
GRADLE_HOME="$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-all"
if [ ! -d "$GRADLE_HOME" ]; then
    echo "📦 Extraindo Gradle..."
    unzip -o "$GRADLE_ZIP" -d "$GRADLE_USER_HOME/wrapper/dists/"
    echo "✅ Gradle extraído em $GRADLE_HOME"
else
    echo "✅ Gradle já extraído"
fi

# Criar o gradle-wrapper.jar a partir da distribuição
# O wrapper.jar está dentro da distribuição do Gradle
WRAPPER_JAR_SRC="$GRADLE_HOME/lib/gradle-wrapper.jar" 2>/dev/null || true
if [ -f "$WRAPPER_JAR_SRC" ]; then
    cp "$WRAPPER_JAR_SRC" "$WRAPPER_DIR/gradle-wrapper.jar"
    echo "✅ gradle-wrapper.jar copiado"
else
    # Tentar encontrar o jar em qualquer lugar na distribuição
    FOUND_JAR=$(find "$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-"* -name "gradle-wrapper.jar" 2>/dev/null | head -1)
    if [ -n "$FOUND_JAR" ]; then
        cp "$FOUND_JAR" "$WRAPPER_DIR/gradle-wrapper.jar"
        echo "✅ gradle-wrapper.jar copiado de $FOUND_JAR"
    else
        echo "⚠️  Não foi possível localizar gradle-wrapper.jar automaticamente"
        echo "   O Android Studio vai criar o wrapper na primeira sincronização"
    fi
fi

# Criar o script gradlew (Unix/Linux/Mac)
cat > "$PROJECT_DIR/gradlew" << GRADLEW_EOF
#!/bin/bash

APP_HOME=\$(cd "\$(dirname "\$0")" && pwd)
APP_NAME="Gradle"
APP_BASE_NAME=\$(basename "\$0")

# Diretório do usuário para Gradle
GRADLE_USER_HOME_DEFAULT="\$HOME/.gradle"
GRADLE_USER_HOME="\${GRADLE_USER_HOME:-\$GRADLE_USER_HOME_DEFAULT}"

# Local do Gradle
GRADLE_VERSION="8.2"
GRADLE_HOME="\$GRADLE_USER_HOME/wrapper/dists/gradle-\${GRADLE_VERSION}-all"

# Se não encontrar, tenta qualquer versão instalada
if [ ! -d "\$GRADLE_HOME" ]; then
    GRADLE_HOME=\$(find "\$GRADLE_USER_HOME/wrapper/dists" -maxdepth 1 -type d -name "gradle-*" 2>/dev/null | head -1)
fi

if [ -z "\$GRADLE_HOME" ] || [ ! -d "\$GRADLE_HOME" ]; then
    echo "Erro: Gradle não encontrado. Execute setup-gradle.sh primeiro."
    exit 1
fi

# Usar o java do JAVA_HOME ou do sistema
if [ -n "\$JAVA_HOME" ]; then
    JAVA_CMD="\$JAVA_HOME/bin/java"
else
    JAVA_CMD=java
fi

# Executar o Gradle wrapper
exec "\$JAVA_CMD" -jar "\$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "\$@"
GRADLEW_EOF

chmod +x "$PROJECT_DIR/gradlew"

# Criar o gradlew.bat (Windows)
cat > "$PROJECT_DIR/gradlew.bat" << GRADLEW_BAT_EOF
@if "%DEBUG%"=="" @echo off
@rem Gradle wrapper script for MasterCash

set "APP_HOME=%~dp0"
set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "GRADLE_VERSION=8.2"
set "GRADLE_HOME=%GRADLE_USER_HOME%\wrapper\dists\gradle-%GRADLE_VERSION%-all"

if not exist "%GRADLE_HOME%" (
    for /d %%d in ("%GRADLE_USER_HOME%\wrapper\dists\gradle-*") do set "GRADLE_HOME=%%d"
)

if not exist "%GRADLE_HOME%" (
    echo Erro: Gradle nao encontrado. Execute setup-gradle.sh primeiro.
    exit /b 1
)

java -jar "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" %*
GRADLEW_BAT_EOF

echo ""
echo "✅ Gradle Wrapper configurado com sucesso!"
echo ""
echo "Para compilar o projeto:"
echo "  cd /home/goob/Desktop/MasterCash"
echo "  ./gradlew assembleDebug"
echo ""
echo "Ou simplesmente abra o projeto no Android Studio."
