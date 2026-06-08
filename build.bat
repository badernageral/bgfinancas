@echo off
setlocal EnableExtensions

REM Variáveis do projeto
set PROJECT_DIR=%~dp0
set APP_NAME=BGFinancas
set APP_VERSION=3.8.1
set MAIN_JAR=bgfinancas-3.8.1.jar
set MAIN_CLASS=io.github.badernageral.bgfinancas.principal.Main
set OUTPUT_DIR=%PROJECT_DIR%dist
set TARGET_DIR=%PROJECT_DIR%target
set JAVAFX_VERSION=26.0.1
set JAVAFX_JMODS_DIR=%PROJECT_DIR%javafx-jmods
set JAVAFX_PLATFORM=win
set JAVAFX_JMODS_PLATFORM_DIR=%JAVAFX_JMODS_DIR%\%JAVAFX_PLATFORM%
set JAVAFX_DOWNLOAD_PLATFORM=windows

REM Detecta/valida Java (JDK)
if not "%JAVA_HOME%" == "" (
    if exist "%JAVA_HOME%\bin\java.exe" goto :java_ready
)

set "JAVA_CMD="
for /f "delims=" %%i in ('where jpackage 2^>nul') do if not defined JAVA_CMD set "JAVA_CMD=%%i"
for /f "delims=" %%i in ('where java 2^>nul') do if not defined JAVA_CMD set "JAVA_CMD=%%i"

if not defined JAVA_CMD (
    for /f "delims=" %%i in ('where /r "%ProgramFiles%\Java" jpackage.exe 2^>nul') do if not defined JAVA_CMD set "JAVA_CMD=%%i"
    for /f "delims=" %%i in ('where /r "%ProgramFiles%\Eclipse Adoptium" jpackage.exe 2^>nul') do if not defined JAVA_CMD set "JAVA_CMD=%%i"
    for /f "delims=" %%i in ('where /r "%ProgramFiles%\Zulu" jpackage.exe 2^>nul') do if not defined JAVA_CMD set "JAVA_CMD=%%i"
    for /f "delims=" %%i in ('where /r "%ProgramFiles%\Microsoft" jpackage.exe 2^>nul') do if not defined JAVA_CMD set "JAVA_CMD=%%i"
    for /f "delims=" %%i in ('where /r "%ProgramFiles%\BellSoft" jpackage.exe 2^>nul') do if not defined JAVA_CMD set "JAVA_CMD=%%i"
    for /f "delims=" %%i in ('where /r "%LOCALAPPDATA%\Programs" jpackage.exe 2^>nul') do if not defined JAVA_CMD set "JAVA_CMD=%%i"
)

if not defined JAVA_CMD (
    echo Erro: Java/JDK nao encontrado.
    echo Para gerar o instalador no Windows, instale um JDK - recomendado JDK 26+ - com jlink e jpackage.
    echo Depois, defina JAVA_HOME apontando para a pasta do JDK e reabra o terminal.
    pause
    exit /b 1
)

for %%i in ("%JAVA_CMD%") do set "JAVA_BIN_DIR=%%~dpi"
for %%i in ("%JAVA_BIN_DIR%..") do set "JAVA_HOME=%%~fi"

:java_ready

set "PATH=%JAVA_HOME%\bin;%PATH%"

set "JPACKAGE_WIN_CONSOLE="
if /i "%BG_DEBUG%"=="1" set "JPACKAGE_WIN_CONSOLE=--win-console"

REM Limpa diretórios antigos (exceto o javafx-jmods)
if exist "%OUTPUT_DIR%" rmdir /s /q "%OUTPUT_DIR%"
if exist "%TARGET_DIR%" rmdir /s /q "%TARGET_DIR%"

REM Compila o projeto com Maven
:maven_build
call mvn clean package

REM Cria diretório de saída
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

set JAVAFX_JMODS_PATH=%JAVAFX_JMODS_PLATFORM_DIR%\javafx-jmods-%JAVAFX_VERSION%
if not exist "%JAVAFX_JMODS_PATH%" goto :jmods_download
echo JavaFX jmods ja existentes, pulando download...
goto :jmods_ready

:jmods_download
echo Baixando JavaFX jmods...
if not exist "%JAVAFX_JMODS_PLATFORM_DIR%" mkdir "%JAVAFX_JMODS_PLATFORM_DIR%"
set "JAVAFX_JMODS_PATH=%JAVAFX_JMODS_PLATFORM_DIR%\javafx-jmods-%JAVAFX_VERSION%"
set "JAVAFX_JMODS_ZIP=%JAVAFX_JMODS_PLATFORM_DIR%\javafx-jmods.zip"
set "JAVAFX_JMODS_URL=https://download2.gluonhq.com/openjfx/%JAVAFX_VERSION%/openjfx-%JAVAFX_VERSION%_%JAVAFX_DOWNLOAD_PLATFORM%-x64_bin-jmods.zip"

call :download_file "%JAVAFX_JMODS_URL%" "%JAVAFX_JMODS_ZIP%"
if errorlevel 1 goto :jmods_download_error

call :verify_file_size "%JAVAFX_JMODS_ZIP%" 1000000
if errorlevel 1 goto :jmods_download_small

echo Extraindo JavaFX jmods...
powershell -NoProfile -Command "Expand-Archive -Force -Path '%JAVAFX_JMODS_ZIP%' -DestinationPath '%JAVAFX_JMODS_PLATFORM_DIR%'"
goto :jmods_ready

:jmods_download_error
echo Erro ao baixar JavaFX jmods.
echo Dica: pode ser bloqueio de rede/proxy/antivirus. Baixe manualmente o arquivo abaixo e salve como "%JAVAFX_JMODS_ZIP%":
echo %JAVAFX_JMODS_URL%
exit /b 1

:jmods_download_small
del /q "%JAVAFX_JMODS_ZIP%" >nul 2>&1
echo Erro ao baixar JavaFX jmods.
echo O download veio muito pequeno (provavel bloqueio/proxy). Baixe manualmente:
echo %JAVAFX_JMODS_URL%
exit /b 1

:jmods_ready

if not exist "%JAVAFX_JMODS_PATH%" (
    echo Erro ao encontrar o diretório dos jmods!
    pause
    exit /b 1
)

echo Usando JAVA_HOME: %JAVA_HOME%
echo Usando JavaFX jmods em: %JAVAFX_JMODS_PATH%

if not exist "%JAVA_HOME%\bin\jlink.exe" (
    echo Erro: jlink nao encontrado em "%JAVA_HOME%\bin". Configure JAVA_HOME para um JDK, nao um JRE.
    pause
    exit /b 1
)

if not exist "%JAVA_HOME%\bin\jpackage.exe" (
    echo Erro: jpackage nao encontrado em "%JAVA_HOME%\bin". Configure JAVA_HOME para um JDK, nao um JRE.
    pause
    exit /b 1
)

REM Lista de módulos necessários
set MODULES=java.base,java.sql,java.logging,java.xml,javafx.base,javafx.controls,javafx.fxml,javafx.graphics

REM Cria runtime customizado com jlink
echo Criando runtime customizado...
"%JAVA_HOME%\bin\jlink" ^
    --module-path "%JAVA_HOME%\jmods;%JAVAFX_JMODS_PATH%" ^
    --add-modules %MODULES% ^
    --output "%OUTPUT_DIR%\runtime" ^
    --compress=2 ^
    --no-header-files ^
    --no-man-pages

if errorlevel 1 (
    echo Erro ao criar runtime!
    pause
    exit /b 1
)

set WIX_OK=0
where candle >nul 2>&1
if errorlevel 1 (
    if exist "C:\PROGRA~2\WiX Toolset v3.11\bin\candle.exe" set "PATH=%PATH%;C:\PROGRA~2\WiX Toolset v3.11\bin"
    if exist "C:\PROGRA~2\WiX Toolset v3.14\bin\candle.exe" set "PATH=%PATH%;C:\PROGRA~2\WiX Toolset v3.14\bin"
    if exist "%ProgramFiles%\WiX Toolset v3.11\bin\candle.exe" set "PATH=%PATH%;%ProgramFiles%\WiX Toolset v3.11\bin"
    if exist "%ProgramFiles%\WiX Toolset v3.14\bin\candle.exe" set "PATH=%PATH%;%ProgramFiles%\WiX Toolset v3.14\bin"
)

where candle >nul 2>&1
if not errorlevel 1 (
    where light >nul 2>&1
    if not errorlevel 1 set WIX_OK=1
)

if not "%WIX_OK%"=="1" (
    echo Erro: WiX Toolset nao encontrado no PATH. Nao e possivel gerar MSI.
    echo Instale o WiX Toolset e garanta candle.exe e light.exe no PATH.
    pause
    exit /b 1
)

echo Criando instalador MSI...
"%JAVA_HOME%\bin\jpackage" ^
    --name "%APP_NAME%" ^
    --app-version "%APP_VERSION%" ^
    --vendor "BGFinancas" ^
    --description "Aplicativo de Financas Pessoais" ^
    --input "%TARGET_DIR%" ^
    --main-jar "%MAIN_JAR%" ^
    --main-class "%MAIN_CLASS%" ^
    --runtime-image "%OUTPUT_DIR%\runtime" ^
    --dest "%OUTPUT_DIR%" ^
    --icon "%PROJECT_DIR%recursos\icone.ico" ^
    --java-options "-Dfile.encoding=UTF-8" ^
    %JPACKAGE_WIN_CONSOLE% ^
    --win-upgrade-uuid "4ACD670F-C540-4980-8F21-4EEB85ACA268" ^
    --win-per-user-install ^
    --win-menu ^
    --win-shortcut ^
    --win-menu-group "%APP_NAME%" ^
    --type msi

if errorlevel 1 (
    echo Erro ao criar instalador MSI!
    pause
    exit /b 1
)

echo Build concluido com sucesso!
echo Arquivos gerados em: %OUTPUT_DIR%

endlocal
goto :eof

:download_file
set "URL=%~1"
set "OUT=%~2"
if exist "%OUT%" del /q "%OUT%" >nul 2>&1

where curl >nul 2>&1
if not errorlevel 1 (
    curl -fL --ssl-no-revoke --retry 3 --retry-delay 2 -A "Mozilla/5.0" -o "%OUT%" "%URL%"
    if not errorlevel 1 exit /b 0
)

powershell -NoProfile -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%URL%' -OutFile '%OUT%' -Headers @{ 'User-Agent'='Mozilla/5.0' }"
exit /b %errorlevel%

:verify_file_size
set "FILE=%~1"
set "MIN=%~2"
if not exist "%FILE%" exit /b 1
for %%A in ("%FILE%") do set "SIZE=%%~zA"
if %SIZE% LSS %MIN% exit /b 1
exit /b 0
