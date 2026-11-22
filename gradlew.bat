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
