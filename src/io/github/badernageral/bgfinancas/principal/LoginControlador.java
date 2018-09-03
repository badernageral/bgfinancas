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
package io.github.badernageral.bgfinancas.principal;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.modelo.Usuario;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public final class LoginControlador implements Initializable, ControladorFormulario {
    
    @FXML private Text slogan;
    @FXML private Text labelUsuario;
    @FXML private Text labelSenha;
    @FXML private TextField usuario;
    @FXML private PasswordField senha;
    @FXML private Button entrar;
    @FXML private Button sair;
    @FXML private Text mensagem;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        slogan.setText(idioma.getMensagem("gerenciador_despesas"));
        labelUsuario.setText(idioma.getMensagem("usuario")+":");
        labelSenha.setText(idioma.getMensagem("senha")+":");
        entrar.setText(idioma.getMensagem("entrar"));
        sair.setText(idioma.getMensagem("sair"));
        mensagem.setText("");
    }
    
    public void focoSenha(){
        senha.requestFocus();
    }
    
    @Override
    public void acaoFinalizar() {
        if(validarFormulario()){
            if(new Usuario().setUsuario(usuario.getText()).setSenha(senha.getText()).consultar()!=null){
                delayReiniciar();
            }else{
                mensagem.setText(idioma.getMensagem("erro_login"));
                senha.requestFocus();
            }
        }
    }
    
    private void delayReiniciar(){
        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws Exception {
                Thread.sleep(500);
                return null;
            }
        };
        task.setOnSucceeded((WorkerStateEvent event) -> {
            Kernel.main.reiniciar();
        });
        Thread th = new Thread(task);
        th.start();
    }

    @Override
    public void acaoCancelar() {
        System.exit(0);
    }
    
    private boolean validarFormulario(){
        try {
            Validar.textField(usuario);
            Validar.passwordField(senha);
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
