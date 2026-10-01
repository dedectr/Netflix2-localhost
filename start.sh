#!/bin/bash

# ============================================
# Video Downloader - Start Script
# Inicia o servidor Java (Javalin) na porta 7000
# ============================================

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

echo "=========================================="
echo "  Video Downloader - Iniciando..."
echo "=========================================="
echo ""

# Verificar Java
echo "[1/4] Verificando Java..."
if ! command -v java &> /dev/null; then
    echo "ERRO: Java não encontrado!"
    echo "Instale Java 21 ou superior: sudo apt install openjdk-21-jdk"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
echo "    Java encontrado: $JAVA_VERSION"

# Verificar yt-dlp
echo ""
echo "[2/4] Verificando yt-dlp..."
if ! command -v yt-dlp &> /dev/null; then
    echo "    yt-dlp não encontrado. Tentando instalar..."
    if command -v pip &> /dev/null; then
        pip install yt-dlp
    elif command -v pip3 &> /dev/null; then
        pip3 install yt-dlp
    else
        echo "ERRO: yt-dlp não encontrado e pip não disponível!"
        echo "Instale yt-dlp manualmente: https://github.com/yt-dlp/yt-dlp"
        exit 1
    fi
fi
echo "    yt-dlp encontrado: $(yt-dlp --version)"

# Criar pasta downloads
echo ""
echo "[3/4] Preparando diretórios..."
mkdir -p downloads
echo "    Pasta 'downloads' criada/verificada"

# Compilar projeto
echo ""
echo "[4/4] Compilando projeto..."
if [ ! -f "target/backend-1.0.1.jar" ]; then
    echo "    JAR não encontrado, compilando..."
    if command -v mvn &> /dev/null; then
        mvn clean package -q
    else
        echo "ERRO: Maven não encontrado!"
        echo "Instale Maven: sudo apt install maven"
        exit 1
    fi
else
    echo "    JAR encontrado: target/backend-1.0.1.jar"
fi

# Iniciar servidor
echo ""
echo "=========================================="
echo "  Servidor iniciando em:"
echo "  http://localhost:7000"
echo "=========================================="
echo ""

java -Xmx2G -jar target/backend-1.0.1.jar
