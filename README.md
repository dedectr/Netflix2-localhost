<p align="center">
  <h1>🎬 VideoDL</h1>
  <p>Baixador de Vídeos do YouTube com Painel Web</p>
  <p>
    <img src="https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk" />
    <img src="https://img.shields.io/badge/Javalin-7.2.3-red?style=flat-square" />
    <img src="https://img.shields.io/badge/Maven-3.9-blue?style=flat-square&logo=apachemaven" />
    <img src="https://img.shields.io/badge/yt-dlp-latest-success?style=flat-square" />
  </p>
</p>

---

## 📋 Índice

- [Sobre](#-sobre)
- [Funcionalidades](#-funcionalidades)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Uso](#-uso)
- [API](#-api)
- [Estrutura](#-estrutura)
- [Painel Web](#-painel-web)

---

## 🎯 Sobre

Aplicação **local** para baixar vídeos e músicas do YouTube com interface web estilo Netflix. Suporta download de vídeos em várias qualidades, extração de áudio em MP3 e playlists completas.

> ⚠️ **Uso exclusivo local** — Sem autenticação, desenvolvido para uso pessoal no localhost.

---

## ✨ Funcionalidades

| Recurso | Descrição |
|---------|-----------|
| 🎬 **Download de Vídeo** | Em qualidades: Melhor, 1080p, 720p, 480p, 360p |
| 🎵 **Extração de Áudio** | Converte para MP3 automaticamente |
| 📋 **Playlist** | Baixe playlists inteiras com um clique |
| 🖥️ **Painel Web** | Interface escura estilo Netflix, responsiva |
| ▶️ **Player** | Assista direto pelo navegador |
| 🗑️ **Gerenciamento** | Liste e exclua arquivos baixados |
| 📊 **Status** | Monitore o servidor em tempo real |

---

## 📦 Pré-requisitos

| Ferramenta | Versão | Instalação |
|------------|--------|------------|
| **Java** | 21+ | `sudo apt install openjdk-21-jdk` |
| **Maven** | 3.9+ | `sudo apt install maven` |
| **yt-dlp** | latest | `pip install yt-dlp` |

---

## 🚀 Instalação

```bash
# Clone o repositório
git clone <url-do-repo>
cd backend

# Dê permissão ao script
chmod +x start.sh

# Inicie tudo
./start.sh
```

O script `start.sh` verifica automaticamente se Java e yt-dlp estão instalados, compila o projeto e inicia o servidor.

---

## 🎮 Uso

### Painel Web

Abra o navegador e acesse:

```
http://localhost:7000
```

### Abas Disponíveis

| Aba | Função |
|-----|--------|
| **Baixar** | Cole a URL, escolha formato/qualidade/playlist e baixe |
| **Status** | Veja status do servidor, arquivos e uptime |
| **Vídeos** | Liste, assista e exclua arquivos baixados |

---

## 🔌 API

### Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/status` | Status do servidor |
| `POST` | `/url` | Iniciar download |
| `GET` | `/api/downloads` | Listar vídeos |
| `DELETE` | `/api/downloads/{filename}` | Excluir vídeo |
| `GET` | `/api/stream/{filename}` | Stream de vídeo/áudio |

### Parâmetros do Download

Use query string na rota `POST /url`:

| Parâmetro | Valores | Padrão |
|-----------|---------|--------|
| `formato` | `VIDEO`, `AUDIO` | `VIDEO` |
| `qualidade` | `BEST`, `P1080`, `P720`, `P480`, `P360` | `BEST` |
| `playlist` | `true`, `false` | `false` |

### Exemplo cURL

```bash
# Baixar vídeo em 720p
curl -X POST "http://localhost:7000/url?formato=VIDEO&qualidade=P720" \
  -H "Content-Type: text/plain" \
  -d "https://www.youtube.com/watch?v=VIDEO_ID"

# Baixar MP3 de uma playlist
curl -X POST "http://localhost:7000/url?formato=AUDIO&playlist=true" \
  -H "Content-Type: text/plain" \
  -d "https://www.youtube.com/playlist?list=PLAYLIST_ID"

# Listar vídeos
curl http://localhost:7000/api/downloads

# Excluir vídeo
curl -X DELETE http://localhost:7000/api/downloads/meuvideo.mp4
```

---

## 📁 Estrutura

```
backend/
├── src/main/
│   ├── java/org/al3ncar/
│   │   ├── App.java                  # Classe principal
│   │   ├── controllers/
│   │   │   └── Server.java           # Rotas e configuração Javalin
│   │   └── utils/
│   │       └── BaixarVideo.java      # Lógica de download (yt-dlp)
│   └── resources/
│       └── webpanel/
│           └── index.html            # Painel web (estilo Netflix)
├── webpanel/                         # Arquivos estáticos adicionais
├── downloads/                        # Vídeos baixados (criado em runtime)
├── target/                           # Build Maven (ignorado)
├── pom.xml
├── start.sh                          # Script de inicialização
├── .gitignore
└── README.md
```

---

## 🖥️ Painel Web

O painel possui interface escura inspirada na Netflix com:

- **Design responsivo** — funciona em desktop e mobile
- **Navegação por abas** — Baixar, Status, Vídeos
- **Feedback visual** — toasts de sucesso/erro, barra de progresso
- **Player integrado** — assista vídeos direto no navegador
- **Auto-refresh** — status atualiza a cada 30s

---

## 🛠️ Stack

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 21 |
| Framework Web | Javalin 7 (Jetty 12) |
| Build | Maven 3.9 |
| Download | yt-dlp |
| Frontend | HTML + CSS + JS (vanilla) |
| Logging | SLF4J + SimpleLogger |

---

## 📄 Licença

Projeto pessoal para uso local.
