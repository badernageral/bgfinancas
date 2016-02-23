/*
Copyright 2012-2015 Jose Robson Mariano Alves

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

package badernageral.bgfinancas.modulo.conta;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.sistema.Botao;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.utilitario.Lista;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.tipo.Acao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import badernageral.bgfinancas.template.botao.BotaoFormulario;
import badernageral.bgfinancas.modelo.Conta;
import javafx.scene.control.TitledPane;

public final class ContaFormularioControlador implements Initializable, ControladorFormulario {
    
    private Acao acao;
       
    @FXML private TitledPane formulario;
    @FXML private BotaoFormulario botaoController;
    @FXML private Label labelNome;
    @FXML private Label labelValor;
    @FXML private Label labelAtivada;
    @FXML private Label labelSaldoTotal;
    @FXML private TextField nome;
    @FXML private TextField valor;
    @FXML private ComboBox<String> ativada;
    @FXML private ComboBox<String> saldoTotal;
    
    private ControladorFormulario controlador = null;
    private Conta modelo;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("conta"));
        Botao.prepararBotaoModal(this, botaoController);
        labelNome.setText(idioma.getMensagem("nome")+":");
        labelValor.setText(idioma.getMensagem("saldo_atual")+":");
        labelAtivada.setText(idioma.getMensagem("ativada")+":");
        labelSaldoTotal.setText(idioma.getMensagem("tipo")+":");
        Lista.setSimNao(ativada);
        Lista.setPoupancaCredito(saldoTotal);
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
                Conta conta = new Conta(null, nome.getText(), valor.getText(), ativada.getSelectionModel().getSelectedItem(), saldoTotal.getSelectionModel().getSelectedItem());
                conta.cadastrar();
                if(controlador==null){
                    Kernel.controlador.acaoFiltrar(true);
                    Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                    Animacao.fadeInOutClose(formulario);
                }else{
                    Animacao.fadeOutClose(formulario);
                    conta = new Conta().setNome(conta.getNome()).consultar();
                    controlador.selecionarComboCategoria(1, conta);
                }
            }else if(acao == Acao.ALTERAR){
                Conta conta = new Conta(modelo.getIdCategoria(), nome.getText(), valor.getText(), ativada.getSelectionModel().getSelectedItem(), saldoTotal.getSelectionModel().getSelectedItem());
                conta.alterar();
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
    
    public void alterar(Conta modelo){
        acao = Acao.ALTERAR;
        this.modelo = modelo;
        botaoController.getBotaoFinalizar().setText(idioma.getMensagem("alterar"));
        nome.setText(modelo.getNome());
        valor.setText(modelo.getValor());
        ativada.getSelectionModel().select(modelo.getAtivada());
        saldoTotal.getSelectionModel().select(modelo.getSaldoTotal());
    }
    
    private boolean validarFormulario(){
        try {
            Validar.textField(nome);
            Validar.textField(valor);
            Validar.textFieldDecimal(valor);
            Validar.comboBox(saldoTotal);
            Validar.comboBox(ativada);
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
