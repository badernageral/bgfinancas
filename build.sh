#!/bin/bash

# Variáveis do projeto
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_NAME="BGFinancas"
APP_VERSION="3.8"
MAIN_JAR="bgfinancas-3.8.jar"
MAIN_CLASS="io.github.badernageral.bgfinancas.principal.Main"
OUTPUT_DIR="$PROJECT_DIR/dist"
TARGET_DIR="$PROJECT_DIR/target"
JAVAFX_VERSION="21"
JAVAFX_JMODS_DIR="$PROJECT_DIR/javafx-jmods"

# Detecta o sistema operacional
OS=$(uname -s)
if [ "$OS" = "Linux" ]; then
    JAVAFX_PLATFORM="linux"
elif [ "$OS" = "Darwin" ]; then
    JAVAFX_PLATFORM="mac"
else
    echo "Sistema operacional não suportado: $OS"
    exit 1
fi

# Limpa diretórios antigos (exceto o javafx-jmods)
rm -rf "$OUTPUT_DIR" "$TARGET_DIR"

# Compila o projeto com Maven
echo "Compilando projeto..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "Erro na compilação!"
    exit 1
fi

# Cria diretório de saída
mkdir -p "$OUTPUT_DIR"

# Verifica se os jmods já existem
JAVAFX_JMODS_PATH="$JAVAFX_JMODS_DIR/javafx-jmods-${JAVAFX_VERSION}"
if [ ! -d "$JAVAFX_JMODS_PATH" ]; then
    echo "Baixando JavaFX jmods..."
    mkdir -p "$JAVAFX_JMODS_DIR"
    JAVAFX_JMODS_URL="https://download2.gluonhq.com/openjfx/${JAVAFX_VERSION}/openjfx-${JAVAFX_VERSION}_${JAVAFX_PLATFORM}-x64_bin-jmods.zip"
    curl -L -o "$JAVAFX_JMODS_DIR/javafx-jmods.zip" "$JAVAFX_JMODS_URL"

    if [ $? -ne 0 ]; then
        echo "Erro ao baixar JavaFX jmods!"
        exit 1
    fi

    # Extrai os jmods
    echo "Extraindo JavaFX jmods..."
    unzip -q "$JAVAFX_JMODS_DIR/javafx-jmods.zip" -d "$JAVAFX_JMODS_DIR"
else
    echo "JavaFX jmods já existentes, pulando download..."
fi

# Detecta o Java Home
if [ -z "$JAVA_HOME" ]; then
    JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
fi

echo "Usando JAVA_HOME: $JAVA_HOME"
echo "Usando JavaFX jmods em: $JAVAFX_JMODS_PATH"

# Lista de módulos necessários
MODULES="java.base,java.sql,java.logging,java.xml,javafx.base,javafx.controls,javafx.fxml,javafx.graphics"

# Cria runtime customizado com jlink
echo "Criando runtime customizado..."
"$JAVA_HOME/bin/jlink" \
    --module-path "$JAVA_HOME/jmods:$JAVAFX_JMODS_PATH" \
    --add-modules $MODULES \
    --output "$OUTPUT_DIR/runtime" \
    --compress=2 \
    --no-header-files \
    --no-man-pages

if [ $? -ne 0 ]; then
    echo "Erro ao criar runtime!"
    exit 1
fi

# Cria o instalador com jpackage
echo "Criando instalador..."
"$JAVA_HOME/bin/jpackage" \
    --name "$APP_NAME" \
    --app-version "$APP_VERSION" \
    --vendor "BGFinancas" \
    --description "Aplicativo de Finanças Pessoais" \
    --input "$TARGET_DIR" \
    --main-jar "$MAIN_JAR" \
    --main-class "$MAIN_CLASS" \
    --runtime-image "$OUTPUT_DIR/runtime" \
    --dest "$OUTPUT_DIR" \
    --icon "$PROJECT_DIR/recursos/icone.png" \
    --java-options "-Dfile.encoding=UTF-8" \
    --type deb

if [ $? -ne 0 ]; then
    echo "Erro ao criar instalador!"
    exit 1
fi

echo "Build concluído com sucesso!"
echo "Arquivos gerados em: $OUTPUT_DIR"
