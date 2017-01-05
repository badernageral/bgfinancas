/*
Copyright 2012-2017 Jose Robson Mariano Alves

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
package badernageral.bgfinancas.biblioteca.sistema;

import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.idioma.Linguagem;
import badernageral.bgfinancas.modelo.Configuracao;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javafx.concurrent.Task;
import javafx.scene.control.MenuItem;

public final class Atualizacao {

    private final Linguagem idioma = Linguagem.getInstance();

    public Atualizacao(boolean mensagem, MenuItem menuItem) {
        menuItem.setDisable(true);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    URL url = new URL("http://badernageral.github.io/ultima_versao_bgfinancas.txt");
                    BufferedReader arquivo = new BufferedReader(new InputStreamReader(url.openStream()));
                    String versao_atual = arquivo.readLine();
                    arquivo.close();
                    if (isVersaoAtualizada(Configuracao.getPropriedade("versao"), versao_atual)) {
                        updateMessage("sim");
                    } else {
                        updateMessage("nao");
                    }
                } catch (Exception ex) {
                    updateMessage("internet");
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            switch (task.getMessage()) {
                case "sim":
                    if (mensagem) {
                        Janela.showMensagem(Status.SUCESSO, idioma.getMensagem("sistema_atualizado"));
                    }
                    break;
                case "nao":
                    if (Janela.showPergunta(idioma.getMensagem("sistema_desatualizado"))) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/badernageral/bgfinancas/releases"));
                        } catch (IOException | URISyntaxException ex) {
                            Janela.showException(ex);
                        }
                    }
                    break;
                default:
                    if (mensagem) {
                        Janela.showMensagem(Status.ERRO, idioma.getMensagem("sem_internet"));
                    }
                    break;
            }
            menuItem.setDisable(false);
        });
        new Thread(task).start();
    }

    public static Boolean isVersaoAtualizada(String sistema, String atual) {
        String[] vSistema = sistema.split("\\.");
        String[] vAtual = atual.split("\\.");
        int i = 0;
        while (i < vSistema.length && i < vAtual.length && vSistema[i].equals(vAtual[i])) {
            i++;
        }
        if (i < vSistema.length && i < vAtual.length) {
            return Integer.parseInt(vSistema[i]) >= Integer.parseInt(vAtual[i]);
        } else {
            return vSistema.length >= vAtual.length;
        }
    }

}
