/*
Copyright 2012-2018 Jose Robson Mariano Alves

This file is part of bgfinancas.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/
package io.github.badernageral.bgfinancas.modulo.utilitario;

import io.github.badernageral.bgfinancas.biblioteca.banco.Conexao;
import io.github.badernageral.bgfinancas.biblioteca.banco.Database;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import io.github.badernageral.bgfinancas.modelo.Configuracao;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javafx.concurrent.Task;

public class BackupOnline {

    private final Conexao banco = Conexao.getInstance();
    private final Linguagem idioma = Linguagem.getInstance();

    private static final String BUCKET = "bgfinancas-backup";
    private static final String ARQUIVO = "backup.bgf";

    public void exportar() {
        if (!credenciaisConfiguradas()) return;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String url = Configuracao.getPropriedade("supabase_url");
                String key = Configuracao.getPropriedade("supabase_key");
                String email = Configuracao.getPropriedade("supabase_email");
                String senha = Configuracao.getPropriedade("supabase_senha");
                banco.executeUpdate("CHECKPOINT");
                banco.executeUpdate("SHUTDOWN COMPACT");
                byte[] zipBytes;
                try {
                    String dbPath = banco.getUrl(false);
                    zipBytes = compactarParaMemoria(
                        new File(dbPath + ".properties"),
                        new File(dbPath + ".script")
                    );
                } finally {
                    banco.conectar();
                }
                String token = autenticar(url, key, email, senha);
                upload(url, key, token, zipBytes);
                return null;
            }
        };
        task.setOnSucceeded(e ->
            Janela.showMensagem(Status.SUCESSO, idioma.getMensagem("backup_online_exportado"))
        );
        task.setOnFailed(e ->
            Janela.showMensagem(Status.ERRO,
                idioma.getMensagem("backup_online_erro") + ":\n" + task.getException().getMessage())
        );
        new Thread(task).start();
    }

    public void importar() {
        if (!credenciaisConfiguradas()) return;
        if (!Janela.showPergunta(idioma.getMensagem("backup_online_confirmar_importar"))) return;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String url = Configuracao.getPropriedade("supabase_url");
                String key = Configuracao.getPropriedade("supabase_key");
                String email = Configuracao.getPropriedade("supabase_email");
                String senha = Configuracao.getPropriedade("supabase_senha");
                String token = autenticar(url, key, email, senha);
                byte[] zipBytes = download(url, key, token);
                banco.executeUpdate("CHECKPOINT");
                banco.executeUpdate("SHUTDOWN COMPACT");
                try {
                    String dirPath = banco.getUrl(true);
                    descompactarDaMemoria(zipBytes, new File(dirPath));
                } finally {
                    banco.conectar();
                    Database.verificarBanco();
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            Janela.showMensagem(Status.SUCESSO, idioma.getMensagem("backup_importado_sucesso"));
            Kernel.main.reiniciar();
        });
        task.setOnFailed(e ->
            Janela.showMensagem(Status.ERRO,
                idioma.getMensagem("backup_online_erro") + ":\n" + task.getException().getMessage())
        );
        new Thread(task).start();
    }

    private boolean credenciaisConfiguradas() {
        String url = Configuracao.getPropriedade("supabase_url");
        String key = Configuracao.getPropriedade("supabase_key");
        String email = Configuracao.getPropriedade("supabase_email");
        String senha = Configuracao.getPropriedade("supabase_senha");
        if (url == null || url.isBlank() || key == null || key.isBlank()
                || email == null || email.isBlank() || senha == null || senha.isBlank()) {
            Janela.showMensagem(Status.ADVERTENCIA, idioma.getMensagem("backup_online_sem_credenciais"));
            return false;
        }
        return true;
    }

    private String autenticar(String url, String key, String email, String senha) throws Exception {
        String baseUrl = extrairBaseUrl(url);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        String body = "{\"grant_type\":\"password\",\"email\":\"" + escapeJson(email) + "\",\"password\":\"" + escapeJson(senha) + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/v1/token?grant_type=password"))
                .header("apikey", key)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception(idioma.getMensagem("backup_online_erro_autenticacao") + " (HTTP " + response.statusCode() + ")");
        }
        return extrairToken(response.body());
    }

    private void upload(String url, String key, String token, byte[] data) throws Exception {
        String baseUrl = extrairBaseUrl(url);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/storage/v1/object/" + BUCKET + "/" + ARQUIVO))
                .header("apikey", key)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/octet-stream")
                .header("x-upsert", "true")
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new Exception(idioma.getMensagem("backup_online_erro_upload") + " (HTTP " + response.statusCode() + ")");
        }
    }

    private byte[] download(String url, String key, String token) throws Exception {
        String baseUrl = extrairBaseUrl(url);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/storage/v1/object/" + BUCKET + "/" + ARQUIVO))
                .header("apikey", key)
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new Exception(idioma.getMensagem("backup_online_erro_download") + " (HTTP " + response.statusCode() + ")");
        }
        return response.body();
    }

    private byte[] compactarParaMemoria(File... arquivos) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            byte[] buffer = new byte[4096];
            for (File arquivo : arquivos) {
                try (FileInputStream fis = new FileInputStream(arquivo)) {
                    zos.putNextEntry(new ZipEntry(arquivo.getName()));
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                }
            }
        }
        return baos.toByteArray();
    }

    private void descompactarDaMemoria(byte[] zipData, File diretorio) throws IOException {
        byte[] buffer = new byte[4096];
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File arquivo = new File(diretorio, entry.getName());
                try (FileOutputStream fos = new FileOutputStream(arquivo)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zis.closeEntry();
            }
        }
    }

    private String extrairToken(String json) throws Exception {
        String marker = "\"access_token\":\"";
        int start = json.indexOf(marker);
        if (start < 0) throw new Exception("Token não encontrado na resposta de autenticação");
        start += marker.length();
        int end = json.indexOf("\"", start);
        if (end < 0) throw new Exception("Resposta de autenticação malformada");
        return json.substring(start, end);
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String extrairBaseUrl(String url) {
        try {
            URI uri = URI.create(url);
            return uri.getScheme() + "://" + uri.getHost();
        } catch (Exception e) {
            return url.replaceAll("/+$", "").replaceAll("(/rest|/auth|/storage).*$", "");
        }
    }

}
