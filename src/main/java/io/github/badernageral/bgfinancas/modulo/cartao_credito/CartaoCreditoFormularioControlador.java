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

package io.github.badernageral.bgfinancas.modulo.cartao_credito;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import static io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario.idioma;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Acao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Calculadora;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Lista;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import io.github.badernageral.bgfinancas.template.botao.BotaoFormulario;
import io.github.badernageral.bgfinancas.modelo.CartaoCredito;
import javafx.scene.control.TitledPane;

public final class CartaoCreditoFormularioControlador implements Initializable, ControladorFormulario {
    
    private Acao acao;
       
    @FXML private TitledPane formulario;
    @FXML private BotaoFormulario botaoController;
    @FXML private Label labelNome;
    @FXML private Label labelLimite;
    @FXML private Label labelVencimento;
    @FXML private Label labelAtivado;
    @FXML private TextField nome;
    @FXML private TextField limite;
    @FXML private Label ajuda;
    @FXML private ComboBox<String> vencimento;
    @FXML private ComboBox<String> ativado;
    
    private ControladorFormulario controlador = null;
    private CartaoCredito modelo;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("cartao_credito"));
        Botao.prepararBotaoModal(this, botaoController);
        Calculadora.preparar(limite, ajuda);
        labelNome.setText(idioma.getMensagem("nome")+":");
        labelLimite.setText(idioma.getMensagem("limite")+":");
        labelVencimento.setText(idioma.getMensagem("dia_vencimento")+":");
        labelAtivado.setText(idioma.getMensagem("ativado")+":");
        vencimento.setPromptText(idioma.getMensagem("selecione"));
        vencimento.getItems().addAll("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31");
        Lista.setSimNao(ativado);
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
        if(controlador != null){
            controlador.selecionarComboItem(0, null);
        }
    }
    
    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            if(acao == Acao.CADASTRAR){
                CartaoCredito cartao = new CartaoCredito(null, nome.getText(), limite.getText(), vencimento.getSelectionModel().getSelectedItem(), ativado.getSelectionModel().getSelectedItem());
                cartao.cadastrar();
                if(controlador==null){
                    Kernel.controlador.acaoFiltrar(true);
                    Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                    Animacao.fadeInOutClose(formulario);
                }else{
                    Animacao.fadeOutClose(formulario);
                    cartao = new CartaoCredito().setNome(cartao.getNome()).consultar();
                    controlador.selecionarComboCategoria(3, cartao);
                }
            }else if(acao == Acao.ALTERAR){
                CartaoCredito cartao = new CartaoCredito(modelo.getIdCategoria(), nome.getText(), limite.getText(), vencimento.getSelectionModel().getSelectedItem(), ativado.getSelectionModel().getSelectedItem());
                cartao.alterar();
                Kernel.controlador.acaoFiltrar(true);
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }
        }
    }
    
    public void cadastrar(ControladorFormulario controlador){
        acao = Acao.CADASTRAR;
        this.controlador = controlador;
        botaoController.getBotaoFinalizar().setText(idioma.getMensagem("cadastrar"));
    }
    
    public void alterar(CartaoCredito modelo){
        acao = Acao.ALTERAR;
        this.modelo = modelo;
        botaoController.getBotaoFinalizar().setText(idioma.getMensagem("alterar"));
        nome.setText(modelo.getNome());
        limite.setText(modelo.getLimite().toString());
        vencimento.getSelectionModel().select(modelo.getVencimento().toString());
        ativado.getSelectionModel().select(modelo.getAtivado());
    }
    
    private boolean validarFormulario(){
        try {
            Validar.textFieldDecimal(limite);
            Validar.textField(nome);
            Validar.comboBox(vencimento);
            Validar.comboBox(ativado);
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
