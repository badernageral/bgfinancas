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

package badernageral.bgfinancas.modulo.usuario;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import badernageral.bgfinancas.modelo.Usuario;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.sistema.Botao;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.tipo.Acao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import badernageral.bgfinancas.template.botao.BotaoFormulario;
import javafx.scene.control.TitledPane;

public final class UsuarioFormularioControlador implements Initializable, ControladorFormulario {
       
    private Acao acao;
    
    @FXML private TitledPane formulario;
    @FXML private BotaoFormulario botaoController;
    @FXML private Label labelNome;
    @FXML private Label labelUsuario;
    @FXML private Label labelSenha;
    @FXML private Label labelSenhaConfirmar;
    @FXML private TextField nome;
    @FXML private TextField usuario;
    @FXML private PasswordField senha;
    @FXML private PasswordField senhaConfirmar;
    
    private Usuario modelo;
    private boolean primeiroAcesso = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("usuario"));
        Botao.prepararBotaoModal(this, botaoController);
        labelNome.setText(idioma.getMensagem("nome")+":");
        labelUsuario.setText(idioma.getMensagem("usuario")+":");
        labelSenha.setText(idioma.getMensagem("senha")+":");
        labelSenhaConfirmar.setText(idioma.getMensagem("senha_confirmar")+":");
    }
    
    public void setPrimeiroAcesso(){
        primeiroAcesso = true;
        cadastrar();
    }
    
    @Override
    public void acaoCancelar() {
        if(primeiroAcesso){
            System.exit(0);
        }else{
            Animacao.fadeInOutClose(formulario);
        }
    }
    
    public void cadastrar(){
        acao = Acao.CADASTRAR;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("cadastrar"));
    }
    
    public void alterar(Usuario modelo){
        this.modelo = modelo;
        acao = Acao.ALTERAR;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("alterar"));
        nome.setText(modelo.getNome());
        usuario.setText(modelo.getUsuario());
    }
    
    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            if(acao == Acao.CADASTRAR){
                Usuario u = new Usuario(null, nome.getText(), usuario.getText(), senha.getText());
                u.cadastrar();
                if(primeiroAcesso){
                    Kernel.master.reiniciar();
                }else{
                    Kernel.principal.acaoUsuario();
                    Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                    Animacao.fadeInOutClose(formulario);
                }
            }else if(acao == Acao.ALTERAR){
                Usuario u = new Usuario(modelo.getIdUsuario(), nome.getText(), usuario.getText(), senha.getText());
                u.alterar();
                Kernel.principal.acaoUsuario();
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }
        }
    }
    
    private boolean validarFormulario(){
        try {
            Validar.textField(nome);
            Validar.textField(usuario);
            Validar.passwordField(senha,senhaConfirmar);
            return true;
        } catch (Erro ex) {
            return false;
        }
    }

    @Override
    public void acaoCadastrar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void selecionarComboItem(int combo, Item item) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

}
