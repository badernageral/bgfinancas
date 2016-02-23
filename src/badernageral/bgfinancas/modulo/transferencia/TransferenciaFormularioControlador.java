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

package badernageral.bgfinancas.modulo.transferencia;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.sistema.Botao;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.tipo.Acao;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Operacao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.biblioteca.utilitario.Datas;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import badernageral.bgfinancas.template.botao.BotaoFormulario;
import badernageral.bgfinancas.modelo.Conta;
import badernageral.bgfinancas.modelo.Transferencia;
import badernageral.bgfinancas.modelo.TransferenciaItem;
import badernageral.bgfinancas.modulo.conta.ContaFormularioControlador;
import badernageral.bgfinancas.modulo.transferencia.item.TransferenciaItemFormularioControlador;
import badernageral.bgfinancas.template.botao.BotaoListaItem;
import java.time.LocalDate;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;

public final class TransferenciaFormularioControlador implements Initializable, ControladorFormulario {
       
    @FXML private TitledPane formulario;
    @FXML private Label labelItem;
    @FXML private Label labelContaOrigem;
    @FXML private Label labelContaDestino;
    @FXML private Label labelDescricao;
    @FXML private Label labelValor;
    @FXML private Label labelData;
    @FXML private BotaoListaItem itemController;
    @FXML private ComboBox<Categoria> contaOrigem;
    @FXML private ComboBox<Categoria> contaDestino;
    @FXML private TextField descricao;
    @FXML private TextField valor;
    @FXML private DatePicker data;
    @FXML private BotaoFormulario botaoController;
    
    private Transferencia Modelo;
    
    private Acao acao;
    private TransferenciaControlador controlador = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("transferencia"));
        Botao.prepararBotaoModal(this, botaoController, itemController);
        labelItem.setText(idioma.getMensagem("item")+":");
        labelContaOrigem.setText(idioma.getMensagem("conta_origem")+":");
        labelContaDestino.setText(idioma.getMensagem("conta_destino")+":");
        labelData.setText(idioma.getMensagem("data")+":");
        labelDescricao.setText(idioma.getMensagem("descricao")+":");
        labelValor.setText(idioma.getMensagem("valor")+":");
        new TransferenciaItem().montarSelectItem(itemController.getComboItem());
        new Conta().montarSelectCategoria(contaOrigem);
        new Conta().montarSelectCategoria(contaDestino);
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        if(botao==1){
            Animacao.fadeOutInvisivel(itemController.getComboItem(), formulario);
            TransferenciaItemFormularioControlador controladorItem = Janela.abrir(TransferenciaItem.FXML_FORMULARIO, idioma.getMensagem("transferencia"));
            controladorItem.cadastrar(this, itemController.getComboItem().getEditor().getText());
        }else{
            Animacao.fadeOutInvisivel(contaOrigem, formulario);
            ContaFormularioControlador controladorConta = Janela.abrir(Conta.FXML_FORMULARIO, idioma.getMensagem("transferencia"));
            controladorConta.cadastrar(this);
        }
    }
    
    @Override
    public void selecionarComboItem(int combo, Item item) {
        if(item!=null){
            new TransferenciaItem().montarSelectItem(itemController.getComboItem());
            itemController.setItemSelecionado(item);
        }
        Animacao.fadeInInvisivel(itemController.getComboItem(), formulario);
    }
    
    public void cadastrar(TransferenciaControlador controlador){
        acao = Acao.CADASTRAR;
        this.controlador = controlador;
        data.setValue(LocalDate.now());
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("cadastrar"));
    }
    
    public void alterar(Transferencia modelo){
        Modelo = modelo;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("alterar"));
        TransferenciaItem item = new TransferenciaItem().setIdItem(Modelo.getIdItem()).consultar();
        if(item != null){
            itemController.setItemSelecionado(item);
        }
        Conta contaOri = new Conta().setIdCategoria(Modelo.getIdContaOrigem()).consultar();
        if(contaOri != null){
            contaOrigem.getSelectionModel().select(contaOri);
        }
        Conta contaDes = new Conta().setIdCategoria(Modelo.getIdContaDestino()).consultar();
        if(contaDes != null){
            contaDestino.getSelectionModel().select(contaDes);
        }
        itemController.getComboItem().setDisable(true);
        itemController.getBotaoCadastrar().setDisable(true);
        data.setValue(Modelo.getDataLocal());
        descricao.setText(Modelo.getDescricao());
        valor.setText(Modelo.getValor());
    }

    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            String idContaOrigem = contaOrigem.getSelectionModel().getSelectedItem().getIdCategoria();
            String idContaDestino = contaDestino.getSelectionModel().getSelectedItem().getIdCategoria();
            if(acao == Acao.CADASTRAR){
                Transferencia item = new Transferencia(null, idContaOrigem, idContaDestino, itemController.getIdItem(), descricao.getText(), valor.getText(), data.getValue(), Datas.getHoraAtual());
                item.cadastrar();
                new Conta().alterarSaldo(Operacao.DECREMENTAR, idContaOrigem, valor.getText());
                new Conta().alterarSaldo(Operacao.INCREMENTAR, idContaDestino, valor.getText());
                Kernel.principal.acaoTransferencia();
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }else{
                Boolean contaOrigemMudou = !(Modelo.getIdContaOrigem().equals(idContaOrigem));
                if(contaOrigemMudou){
                    new Conta().alterarSaldo(Operacao.INCREMENTAR, Modelo.getIdContaOrigem(), Modelo.getValor());
                    new Conta().alterarSaldo(Operacao.DECREMENTAR, idContaOrigem, Modelo.getValor());
                }
                Modelo.setIdContaOrigem(contaOrigem.getValue());
                Boolean contaDestinoMudou = !(Modelo.getIdContaDestino().equals(idContaDestino));
                if(contaDestinoMudou){
                    new Conta().alterarSaldo(Operacao.DECREMENTAR, Modelo.getIdContaDestino(), Modelo.getValor());
                    new Conta().alterarSaldo(Operacao.INCREMENTAR, idContaDestino, Modelo.getValor());
                }
                Modelo.setIdContaDestino(contaDestino.getValue());
                Boolean valorMudou = !(Modelo.getValor().equals(valor.getText()));
                if(valorMudou){
                    BigDecimal valorDiferenca = new BigDecimal(Modelo.getValor());
                    valorDiferenca = valorDiferenca.subtract(new BigDecimal(valor.getText()));
                    new Conta().alterarSaldo(Operacao.INCREMENTAR, Modelo.getIdContaOrigem(), valorDiferenca.toString());
                    new Conta().alterarSaldo(Operacao.DECREMENTAR, Modelo.getIdContaDestino(), valorDiferenca.toString());
                }
                Modelo.setValor(valor.getText());
                Modelo.setDescricao(descricao.getText());
                Modelo.setData(data.getValue());
                Modelo.alterar();
                Kernel.principal.acaoTransferencia();
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }
        }
    }
    
    private boolean validarFormulario(){
        try {
            Validar.autoFiltro(itemController.getAutoFiltro(), itemController.getComboItem());
            Validar.comboBox(contaOrigem);
            Validar.comboBox(contaDestino);
            Validar.igualdade(contaOrigem, contaDestino);
            Validar.datePicker(data);
            Validar.textFieldDecimal(valor);
            return true;
        } catch (Erro ex) {
            return false;
        }
    }

    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

}
