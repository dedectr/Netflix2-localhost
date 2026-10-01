package org.al3ncar.controllers;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.al3ncar.utils.BaixarVideo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private final Javalin app;

    public Server() {
        app = Javalin.create(config -> {
            config.jetty.port = 7000;
            config.staticFiles.add("/webpanel", Location.CLASSPATH);

            config.requestLogger.http((ctx, ms) ->
                log.info("{} {} -> {} ({} ms)",
                    ctx.method(), ctx.path(), ctx.status(), ms)
            );

            config.events.serverStarted(() ->
                log.info("Servidor rodando em http://localhost:7000")
            );

            // Status
            config.routes.get("/status", ctx -> {
                Map<String, Object> status = new HashMap<>();
                status.put("status", "online");
                status.put("uptime", System.currentTimeMillis());
                ctx.json(status);
            });

            // Baixar vídeo
            config.routes.post("/url", ctx -> {
                String url = ctx.body();

                if (url == null || url.isBlank()) {
                    ctx.status(400).json(Map.of("erro", "URL vazia"));
                    return;
                }

                // Parâmetros opcionais via query string
                String formatoParam = ctx.queryParam("formato");
                String qualidadeParam = ctx.queryParam("qualidade");
                String playlistParam = ctx.queryParam("playlist");

                BaixarVideo.DownloadOptions options = new BaixarVideo.DownloadOptions();

                // Formato: video ou audio
                if (formatoParam != null) {
                    try {
                        options.formato = BaixarVideo.Formato.valueOf(formatoParam.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        // mantém padrão
                    }
                }

                // Qualidade
                if (qualidadeParam != null) {
                    try {
                        options.qualidade = BaixarVideo.Qualidade.valueOf(qualidadeParam.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        // mantém padrão
                    }
                }

                // Playlist
                if (playlistParam != null) {
                    options.playlist = Boolean.parseBoolean(playlistParam);
                }

                log.info("URL recebida: {} | Formato: {} | Qualidade: {} | Playlist: {}",
                    url, options.formato, options.qualidade, options.playlist);

                try {
                    BaixarVideo.SoBaixar(url, options);
                    ctx.status(200).json(Map.of("status", "ok"));
                } catch (Exception e) {
                    log.error("Falha ao baixar vídeo: {}", url, e);
                    ctx.status(500).json(Map.of("erro", "Falha ao processar URL"));
                }
            });

            // Listar vídeos baixados
            config.routes.get("/api/downloads", ctx -> {
                File downloadsDir = new File("downloads");
                List<Map<String, Object>> videos = new ArrayList<>();

                if (downloadsDir.exists() && downloadsDir.isDirectory()) {
                    File[] files = downloadsDir.listFiles((dir, name) -> {
                        String lower = name.toLowerCase();
                        return lower.endsWith(".mp4") || lower.endsWith(".webm") ||
                               lower.endsWith(".mkv") || lower.endsWith(".mp3") ||
                               lower.endsWith(".m4a") || lower.endsWith(".ogg");
                    });

                    if (files != null) {
                        for (File f : files) {
                            Map<String, Object> video = new HashMap<>();
                            video.put("name", f.getName());
                            video.put("size", f.length());
                            video.put("lastModified", f.lastModified());
                            videos.add(video);
                        }
                    }
                }

                ctx.json(Map.of("videos", videos, "count", videos.size()));
            });

            // Deletar vídeo
            config.routes.delete("/api/downloads/{filename}", ctx -> {
                String filename = ctx.pathParam("filename");
                if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                    ctx.status(400).json(Map.of("erro", "Nome inválido"));
                    return;
                }

                File file = new File("downloads/" + filename);
                if (file.exists() && file.delete()) {
                    ctx.json(Map.of("status", "ok"));
                } else {
                    ctx.status(404).json(Map.of("erro", "Arquivo não encontrado"));
                }
            });

            // Stream vídeo
            config.routes.get("/api/stream/{filename}", ctx -> {
                String filename = ctx.pathParam("filename");
                if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                    ctx.status(400).result("Nome inválido");
                    return;
                }

                File file = new File("downloads/" + filename);
                if (file.exists()) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".mp3")) {
                        ctx.contentType("audio/mpeg");
                    } else if (name.endsWith(".m4a")) {
                        ctx.contentType("audio/mp4");
                    } else if (name.endsWith(".ogg")) {
                        ctx.contentType("audio/ogg");
                    } else {
                        ctx.contentType("video/mp4");
                    }
                    ctx.result(new java.io.FileInputStream(file));
                } else {
                    ctx.status(404).result("Arquivo não encontrado");
                }
            });
        });
    }

    public void start() {
        app.start();
    }
}
