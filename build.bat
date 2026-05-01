@echo off
setlocal

REM Variáveis do projeto
set PROJECT_DIR=%~dp0
set APP_NAME=BGFinancas
set APP_VERSION=3.8
set MAIN_JAR=bgfinancas-3.8.jar
set MAIN_CLASS=io.github.badernageral.bgfinancas.principal.Main
set OUTPUT_DIR=%PROJECT_DIR%dist
set TARGET_DIR=%PROJECT_DIR%target
set JAVAFX_VERSION=21
set JAVAFX_JMODS_DIR=%PROJECT_DIR%javafx-jmods
set JAVAFX_PLATFORM=win

REM Limpa diretórios antigos (exceto o javafx-jmods)
if exist "%OUTPUT_DIR%" rmdir /s /q "%OUTPUT_DIR%"
if exist "%TARGET_DIR%" rmdir /s /q "%TARGET_DIR%"

REM Compila o projeto com Maven
echo Compilando projeto...
call mvn clean package

if errorlevel 1 (
    echo Erro na compilacao!
    exit /b 1
)

REM Cria diretório de saída
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

REM Verifica se os jmods já existem
set JAVAFX_JMODS_PATH=%JAVAFX_JMODS_DIR%\javafx-jmods-%JAVAFX_VERSION%
if not exist "%JAVAFX_JMODS_PATH%" (
    echo Baixando JavaFX jmods...
    if not exist "%JAVAFX_JMODS_DIR%" mkdir "%JAVAFX_JMODS_DIR%"
    set JAVAFX_JMODS_URL=https://download2.gluonhq.com/openjfx/%JAVAFX_VERSION%/openjfx-%JAVAFX_VERSION%_%JAVAFX_PLATFORM%-x64_bin-jmods.zip
    powershell -Command "Invoke-WebRequest -Uri '%JAVAFX_JMODS_URL%' -OutFile '%JAVAFX_JMODS_DIR%\javafx-jmods.zip'"

    if errorlevel 1 (
        echo Erro ao baixar JavaFX jmods!
        exit /b 1
    )

    REM Extrai os jmods
    echo Extraindo JavaFX jmods...
    powershell -Command "Expand-Archive -Path '%JAVAFX_JMODS_DIR%\javafx-jmods.zip' -DestinationPath '%JAVAFX_JMODS_DIR%'"
) else (
    echo JavaFX jmods ja existentes, pulando download...
)

REM Encontra o diretório com os jmods
for /d %%d in ("%JAVAFX_JMODS_DIR%\javafx-jmods-%JAVAFX_VERSION%") do (
    set JAVAFX_JMODS_PATH=%%d
)

if "%JAVAFX_JMODS_PATH%" == "" (
    echo Erro ao encontrar o diretório dos jmods!
    exit /b 1
)

REM Detecta o Java Home
if "%JAVA_HOME%" == "" (
    for /f "tokens=*" %%i in ('where java') do set JAVA_CMD=%%i
    for %%i in ("%JAVA_CMD%") do set JAVA_BIN_DIR=%%~dpi
    for %%i in ("%JAVA_BIN_DIR%..") do set JAVA_HOME=%%~fi
)

echo Usando JAVA_HOME: %JAVA_HOME%
echo Usando JavaFX jmods em: %JAVAFX_JMODS_PATH%

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
    exit /b 1
)

REM Cria o instalador com jpackage
echo Criando instalador...
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
    --type msi

if errorlevel 1 (
    echo Erro ao criar instalador!
    exit /b 1
)

echo Build concluido com sucesso!
echo Arquivos gerados em: %OUTPUT_DIR%

endlocal
