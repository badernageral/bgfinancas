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

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import io.github.badernageral.bgfinancas.modelo.Configuracao;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class SupabaseFormularioControlador implements Initializable, ControladorFormulario {

    public static final String FXML = Kernel.RAIZ + "/modulo/utilitario/SupabaseFormulario.fxml";

    private static final int TOTAL_ETAPAS = 5;

    private static final String SQL_POLITICAS =
        "-- Permite upload (criar e sobrescrever)\n" +
        "CREATE POLICY \"backup_insert\"\n" +
        "ON storage.objects FOR INSERT\n" +
        "TO authenticated\n" +
        "WITH CHECK (bucket_id = 'bgfinancas-backup');\n\n" +
        "CREATE POLICY \"backup_update\"\n" +
        "ON storage.objects FOR UPDATE\n" +
        "TO authenticated\n" +
        "USING (bucket_id = 'bgfinancas-backup');\n\n" +
        "-- Permite download\n" +
        "CREATE POLICY \"backup_select\"\n" +
        "ON storage.objects FOR SELECT\n" +
        "TO authenticated\n" +
        "USING (bucket_id = 'bgfinancas-backup');\n\n" +
        "-- Permite exclusão (para sobrescrever)\n" +
        "CREATE POLICY \"backup_delete\"\n" +
        "ON storage.objects FOR DELETE\n" +
        "TO authenticated\n" +
        "USING (bucket_id = 'bgfinancas-backup');";

    private final Linguagem idioma = Linguagem.getInstance();

    @FXML private TitledPane formulario;
    @FXML private Label labelEtapa;
    @FXML private Label labelTitulo;
    @FXML private VBox conteudo;
    @FXML private HBox grupoBotao;

    private int etapa = 0;

    private final Button botaoAnterior = new Button();
    private final Button botaoProximo = new Button();
    private final Button botaoSalvar = new Button();
    private final Button botaoCancelar = new Button();
    private final Button botaoTestar = new Button();

    private TextField campoUrl;
    private TextField campoChave;
    private TextField campoEmail;
    private PasswordField campoSenha;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("supabase_configurar"));
        botaoAnterior.setText(idioma.getMensagem("anterior"));
        botaoProximo.setText(idioma.getMensagem("proximo"));
        botaoSalvar.setText(idioma.getMensagem("cadastrar"));
        botaoCancelar.setText(idioma.getMensagem("cancelar"));
        botaoTestar.setText(idioma.getMensagem("supabase_testar_conexao"));
        botaoAnterior.getStyleClass().add("BotaoCancelar");
        botaoProximo.getStyleClass().add("BotaoFinalizar");
        botaoSalvar.getStyleClass().add("BotaoFinalizar");
        botaoCancelar.getStyleClass().add("BotaoCancelar");
        botaoTestar.getStyleClass().add("BotaoCancelar");
        botaoAnterior.setOnAction(e -> irParaEtapa(etapa - 1));
        botaoProximo.setOnAction(e -> irParaEtapa(etapa + 1));
        botaoSalvar.setOnAction(e -> acaoFinalizar());
        botaoCancelar.setOnAction(e -> acaoCancelar());
        botaoTestar.setOnAction(e -> testarConexao());
        irParaEtapa(1);
    }

    private void irParaEtapa(int e) {
        etapa = e;
        labelEtapa.setText(idioma.getMensagem("supabase_passo") + " " + etapa + " " + idioma.getMensagem("de") + " " + TOTAL_ETAPAS);
        conteudo.getChildren().clear();
        grupoBotao.getChildren().clear();
        switch (etapa) {
            case 1: etapa1(); break;
            case 2: etapa2(); break;
            case 3: etapa3(); break;
            case 4: etapa4(); break;
            case 5: etapa5(); break;
        }
        if (etapa > 1) grupoBotao.getChildren().add(botaoAnterior);
        if (etapa < TOTAL_ETAPAS) grupoBotao.getChildren().add(botaoProximo);
        if (etapa == TOTAL_ETAPAS) {
            grupoBotao.getChildren().add(botaoTestar);
            grupoBotao.getChildren().add(botaoSalvar);
        }
        grupoBotao.getChildren().add(botaoCancelar);
    }

    private void etapa1() {
        labelTitulo.setText(idioma.getMensagem("supabase_passo1_titulo"));
        Label instrucoes = instrucaoLabel(idioma.getMensagem("supabase_passo1_inst"));
        conteudo.getChildren().add(instrucoes);
    }

    private void etapa2() {
        labelTitulo.setText(idioma.getMensagem("supabase_passo2_titulo"));
        Label instrucoes = instrucaoLabel(idioma.getMensagem("supabase_passo2_inst"));
        conteudo.getChildren().add(instrucoes);
    }

    private void etapa3() {
        labelTitulo.setText(idioma.getMensagem("supabase_passo3_titulo"));
        Label instrucoes = instrucaoLabel(idioma.getMensagem("supabase_passo3_inst"));
        TextArea sql = new TextArea(SQL_POLITICAS);
        sql.setEditable(false);
        sql.setWrapText(false);
        sql.setPrefHeight(200);
        sql.setStyle("-fx-font-family: monospace; -fx-font-size: 11;");
        Button copiar = new Button(idioma.getMensagem("supabase_copiar_sql"));
        copiar.getStyleClass().add("BotaoCancelar");
        copiar.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(SQL_POLITICAS);
            Clipboard.getSystemClipboard().setContent(content);
            Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("supabase_sql_copiado"), Duracao.CURTA);
        });
        conteudo.getChildren().addAll(instrucoes, sql, copiar);
    }

    private void etapa4() {
        labelTitulo.setText(idioma.getMensagem("supabase_passo4_titulo"));
        Label instrucoes = instrucaoLabel(idioma.getMensagem("supabase_passo4_inst"));
        conteudo.getChildren().add(instrucoes);
    }

    private void etapa5() {
        labelTitulo.setText(idioma.getMensagem("supabase_passo5_titulo"));
        Label instrucoes = instrucaoLabel(idioma.getMensagem("supabase_passo5_inst"));

        campoUrl = new TextField(Configuracao.getPropriedade("supabase_url") != null
                ? Configuracao.getPropriedade("supabase_url") : "");
        campoUrl.setPromptText("https://xyzxyz.supabase.co");

        campoChave = new TextField(Configuracao.getPropriedade("supabase_key") != null
                ? Configuracao.getPropriedade("supabase_key") : "");
        campoChave.setPromptText("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        campoEmail = new TextField(Configuracao.getPropriedade("supabase_email") != null
                ? Configuracao.getPropriedade("supabase_email") : "");
        campoEmail.setPromptText("seu@email.com");

        campoSenha = new PasswordField();
        campoSenha.setPromptText("••••••••");
        String senhaAtual = Configuracao.getPropriedade("supabase_senha");
        if (senhaAtual != null && !senhaAtual.isEmpty()) {
            campoSenha.setText(senhaAtual);
        }

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(labelCampo(idioma.getMensagem("supabase_url")), 0, 0);
        form.add(campoUrl, 1, 0);
        form.add(labelCampo(idioma.getMensagem("supabase_chave")), 0, 1);
        form.add(campoChave, 1, 1);
        form.add(labelCampo(idioma.getMensagem("supabase_email")), 0, 2);
        form.add(campoEmail, 1, 2);
        form.add(labelCampo(idioma.getMensagem("senha")), 0, 3);
        form.add(campoSenha, 1, 3);

        campoUrl.setPrefWidth(380);
        campoChave.setPrefWidth(380);
        campoEmail.setPrefWidth(380);
        campoSenha.setPrefWidth(380);

        conteudo.getChildren().addAll(instrucoes, form);
    }

    private void testarConexao() {
        if (!validarCamposCredenciais()) return;
        String url = campoUrl.getText().trim();
        String chave = campoChave.getText().trim();
        botaoTestar.setDisable(true);
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                try {
                    HttpClient client = HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(8))
                            .build();
                    String baseUrl = extrairBaseUrl(url);
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl + "/rest/v1/"))
                            .header("apikey", chave)
                            .GET()
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    return response.statusCode() < 500;
                } catch (Exception ex) {
                    return false;
                }
            }
        };
        task.setOnSucceeded(ev -> {
            botaoTestar.setDisable(false);
            boolean ok = task.getValue();
            if (ok) {
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("supabase_conexao_ok"), Duracao.LONGA);
            } else {
                Janela.showTooltip(Status.ERRO, idioma.getMensagem("supabase_conexao_erro"), Duracao.LONGA);
            }
        });
        task.setOnFailed(ev -> {
            botaoTestar.setDisable(false);
            Janela.showTooltip(Status.ERRO, idioma.getMensagem("supabase_conexao_erro"), Duracao.LONGA);
        });
        new Thread(task).start();
    }

    @Override
    public void acaoFinalizar() {
        if (!validarCamposCredenciais()) return;
        Configuracao.setPropriedade("supabase_url", campoUrl.getText().trim());
        Configuracao.setPropriedade("supabase_key", campoChave.getText().trim());
        Configuracao.setPropriedade("supabase_email", campoEmail.getText().trim());
        Configuracao.setPropriedade("supabase_senha", campoSenha.getText());
        Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("supabase_salvo"), Duracao.CURTA);
        Animacao.fadeInOutClose(formulario);
    }

    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
    }

    private boolean validarCamposCredenciais() {
        if (campoUrl == null) return false;
        if (campoUrl.getText().trim().isEmpty() || campoChave.getText().trim().isEmpty()
                || campoEmail.getText().trim().isEmpty() || campoSenha.getText().isEmpty()) {
            Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("supabase_campos_obrigatorios"), Duracao.CURTA);
            return false;
        }
        return true;
    }

    private Label instrucaoLabel(String texto) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setMaxWidth(510);
        return label;
    }

    private String extrairBaseUrl(String url) {
        try {
            URI uri = URI.create(url);
            return uri.getScheme() + "://" + uri.getHost();
        } catch (Exception e) {
            return url.replaceAll("/+$", "").replaceAll("(/rest|/auth|/storage).*$", "");
        }
    }

    private Label labelCampo(String texto) {
        Label label = new Label(texto + ":");
        label.setStyle("-fx-alignment: CENTER_RIGHT;");
        label.setMinWidth(120);
        return label;
    }

    @Override
    public void acaoCadastrar(int botao) { }

    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) { }

    @Override
    public void selecionarComboItem(int combo, Item item) { }

}
