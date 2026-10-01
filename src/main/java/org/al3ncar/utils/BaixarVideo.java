package org.al3ncar.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BaixarVideo {

    public enum Formato {
        VIDEO, AUDIO
    }

    public enum Qualidade {
        BEST("best"),
        P1080("bestvideo[height<=1080]+bestaudio/best[height<=1080]"),
        P720("bestvideo[height<=720]+bestaudio/best[height<=720]"),
        P480("bestvideo[height<=480]+bestaudio/best[height<=480]"),
        P360("bestvideo[height<=360]+bestaudio/best[height<=360]"),
        AUDIO_BEST("bestaudio/best"),
        AUDIO_320("bestaudio/best");

        private final String ytdlpFormat;

        Qualidade(String ytdlpFormat) {
            this.ytdlpFormat = ytdlpFormat;
        }

        public String getYtdlpFormat() {
            return ytdlpFormat;
        }
    }

    public static class DownloadOptions {
        public Formato formato = Formato.VIDEO;
        public Qualidade qualidade = Qualidade.BEST;
        public boolean playlist = false;
        public String outputPath = "./downloads/%(title)s.%(ext)s";

        public DownloadOptions() {}

        public DownloadOptions(Formato formato, Qualidade qualidade) {
            this.formato = formato;
            this.qualidade = qualidade;
        }

        public DownloadOptions(Formato formato, Qualidade qualidade, boolean playlist) {
            this.formato = formato;
            this.qualidade = qualidade;
            this.playlist = playlist;
        }
    }

    public static void SoBaixar(String url) {
        SoBaixar(url, new DownloadOptions());
    }

    public static void SoBaixar(String url, DownloadOptions options) {
        List<String> command = new ArrayList<>();
        command.add("yt-dlp");

        // Formato: vídeo ou áudio
        if (options.formato == Formato.AUDIO) {
            command.add("-x");
            command.add("--audio-format");
            command.add("mp3");
            command.add("--audio-quality");
            command.add("0"); // melhor qualidade de áudio
        }

        // Qualidade
        command.add("-f");
        command.add(options.qualidade.getYtdlpFormat());

        // Playlist
        if (options.playlist) {
            command.add("--yes-playlist");
        } else {
            command.add("--no-playlist");
        }

        // Output
        command.add("-o");
        command.add(options.outputPath);

        // Merge para mp4 quando possível
        if (options.formato == Formato.VIDEO) {
            command.add("--merge-output-format");
            command.add("mp4");
        }

        // URL
        command.add(url);

        System.out.println("Comando: " + String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Download concluído com sucesso!");
            } else {
                System.out.println("Erro no download. Código: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
