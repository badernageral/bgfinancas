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

package io.github.badernageral.bgfinancas.modulo.grupo;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Acao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Calculadora;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import io.github.badernageral.bgfinancas.template.botao.BotaoFormulario;
import io.github.badernageral.bgfinancas.modelo.Grupo;
import javafx.scene.control.TitledPane;

public final class GrupoFormularioControlador implements Initializable, ControladorFormulario {
       
    private Acao acao;
    
    @FXML private TitledPane formulario;
    @FXML private BotaoFormulario botaoController;
    @FXML private Label labelNome;
    @FXML private Label labelValor;
    @FXML private TextField nome;
    @FXML private TextField valor;
    @FXML private Label ajuda;
    
    private ControladorFormulario controlador = null;
    private Grupo modelo;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Botao.prepararBotaoModal(this, botaoController);
        Calculadora.preparar(valor, ajuda);
        labelNome.setText(idioma.getMensagem("nome")+":");
        labelValor.setText(idioma.getMensagem("valor")+":");
        formulario.setText(idioma.getMensagem("cota"));
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
        if(controlador != null){
            controlador.selecionarComboCategoria(1, null);
        }
    }
    
    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            if(acao == Acao.CADASTRAR){
                Grupo categoria = new Grupo(null, nome.getText(), valor.getText());
                categoria.cadastrar();
                if(controlador==null){
                    Kernel.principal.acaoGrupoItem();
                    Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                    Animacao.fadeInOutClose(formulario);
                }else{
                    Animacao.fadeOutClose(formulario);
                    categoria = new Grupo().setNome(categoria.getNome()).consultar();
                    controlador.selecionarComboCategoria(1, categoria);
                }
            }else if(acao == Acao.ALTERAR){
                Grupo categoria = new Grupo(modelo.getIdCategoria(), nome.getText(), valor.getText());
                categoria.alterar();
                Kernel.controlador.acaoFiltrar(true);
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }
        }
    }
    
    public void cadastrar(GrupoFormularioControlador controlador){
        acao = Acao.CADASTRAR;
        this.controlador = controlador;
        botaoController.getBotaoFinalizar().setText(idioma.getMensagem("cadastrar"));
    }
    
    public void alterar(Grupo modelo){
        this.modelo = modelo;
        acao = Acao.ALTERAR;
        botaoController.getBotaoFinalizar().setText(idioma.getMensagem("alterar"));
        nome.setText(modelo.getNome());
        valor.setText(modelo.getValor().toString());
    }
    
    private boolean validarFormulario(){
        try {
            Validar.textFieldDecimal(valor);
            Validar.textField(nome);
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
