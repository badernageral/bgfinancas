/*
Copyright 2015 Jose Robson Mariano Alves

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

package badernageral.bgfinancas.principal;

import badernageral.bgfinancas.biblioteca.sistema.Atalho;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.banco.Conexao;
import badernageral.bgfinancas.biblioteca.banco.Database;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.idioma.Linguagem;
import badernageral.bgfinancas.modelo.Configuracao;
import badernageral.bgfinancas.modelo.Usuario;
import badernageral.bgfinancas.modulo.usuario.UsuarioFormularioControlador;
import badernageral.bgfinancas.modulo.utilitario.ConfiguracaoFormularioControlador;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private final Conexao banco = Conexao.getInstance();
    private final Linguagem idioma = Linguagem.getInstance();
    private ConfiguracaoFormularioControlador confControlador = null;
    private UsuarioFormularioControlador userControlador = null;

    @Override
    public void start(Stage palco) {
        Kernel.main = this;
        Kernel.palco = palco;
        Database.verificarBanco();
        idioma.carregarIdioma();
        if(new Usuario().listar().isEmpty()){
            iniciarPrimeiroAcesso();
        }else{
            iniciarLogin();
        }
        palco.show();
    }

    private void iniciarLogin() {
        try {
            Kernel.palco.setScene(criarCena(criarPainelLogin(),null));
            Kernel.palco.initStyle(StageStyle.TRANSPARENT);
        } catch (IOException ex) {
            Janela.showException(ex);
        }
    }
    
    private void iniciarPrimeiroAcesso() {
        try {
            Kernel.palco.setScene(criarCena(null,criarPainelConfiguracoes()));
            Kernel.palco.initStyle(StageStyle.TRANSPARENT);
            confControlador.setPrimeiroAcesso();
        } catch (IOException ex) {
            Janela.showException(ex);
        }
    }
    
    public void continuarPrimeiroAcesso() {
        try {
            idioma.carregarIdioma();
            Database.popularBanco();
            Kernel.palco.setScene(criarCena(null,criarPainelCadastroUsuario()));
            userControlador.setPrimeiroAcesso();
        } catch (IOException ex) {
            Janela.showException(ex);
        }
    }

    public void reiniciar() {
        try {
            idioma.carregarIdioma();
            Kernel.palco.hide();
            Kernel.palco = new Stage();
            Kernel.palco.setScene(criarCena(criarPainelPrincipal(),null));
            Kernel.palco.show();
        } catch (Exception ex) {
            Janela.showException(ex);
        }
    }

    private Scene criarCena(Pane painel, TitledPane tpainel) {
        Scene cena = (painel!=null) ? new Scene(painel) : new Scene(tpainel);
        cena.getStylesheets().add(getClass().getResource(Kernel.CSS_PRINCIPAL).toExternalForm());
        cena.getStylesheets().add(getClass().getResource(Kernel.CSS_AJUDA).toExternalForm());
        cena.getStylesheets().add(getClass().getResource(Kernel.CSS_TOOLTIP).toExternalForm());
        Kernel.cena = cena;
        Atalho.setAtalhos();
        return cena;
    }

    private Pane criarPainelLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane painel = loader.load(getClass().getResourceAsStream(Kernel.FXML_LOGIN));
        return painel;
    }
    
    private TitledPane criarPainelConfiguracoes() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        TitledPane painel = loader.load(getClass().getResourceAsStream(Configuracao.FXML_FORMULARIO));
        confControlador = loader.getController();
        return painel;
    }
    
    private TitledPane criarPainelCadastroUsuario() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        TitledPane painel = loader.load(getClass().getResourceAsStream(Usuario.FXML_FORMULARIO));
        userControlador = loader.getController();
        return painel;
    }

    private Pane criarPainelPrincipal() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane painel = loader.load(getClass().getResourceAsStream(Kernel.FXML_PRINCIPAL));
        return painel;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
