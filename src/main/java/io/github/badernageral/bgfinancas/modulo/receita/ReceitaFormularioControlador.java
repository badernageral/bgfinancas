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

package io.github.badernageral.bgfinancas.modulo.receita;

import io.github.badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Acao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Operacao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Calculadora;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Datas;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import io.github.badernageral.bgfinancas.template.botao.BotaoFormulario;
import io.github.badernageral.bgfinancas.template.botao.BotaoListaCategoria;
import io.github.badernageral.bgfinancas.modelo.Conta;
import io.github.badernageral.bgfinancas.modelo.Receita;
import io.github.badernageral.bgfinancas.modelo.ReceitaItem;
import io.github.badernageral.bgfinancas.modulo.conta.ContaFormularioControlador;
import io.github.badernageral.bgfinancas.modulo.planejamento.PlanejamentoControlador;
import io.github.badernageral.bgfinancas.modulo.receita.item.ReceitaItemFormularioControlador;
import io.github.badernageral.bgfinancas.template.botao.BotaoListaItem;
import java.math.RoundingMode;
import java.time.LocalDate;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public final class ReceitaFormularioControlador implements Initializable, ControladorFormulario {
       
    @FXML private TitledPane formulario;
    @FXML private GridPane tabela;
    @FXML private Label labelItem;
    @FXML private Label labelConta;
    @FXML private Label labelDescricao;
    @FXML private Label labelValor;
    @FXML private Label labelData;
    @FXML private BotaoListaItem itemController;
    @FXML private BotaoListaCategoria contaController;
    @FXML private TextField descricao;
    @FXML private TextField valor;
    @FXML private Label ajuda1;
    @FXML private DatePicker data;
    @FXML private BotaoFormulario botaoController;
    
    private final CheckBox checkAgendar = new CheckBox();
    private final Spinner qtdMeses = new Spinner();
    private final ToggleButton valorParcela = new ToggleButton();
    private final Label ajuda = new Label();
    private Boolean confirmar = false;
    
    private Receita modelo;
    
    private Acao acao;
    private PlanejamentoControlador controlador = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("receita"));
        Botao.prepararBotaoModal(this, botaoController, itemController, contaController);
        Calculadora.preparar(valor, ajuda1);
        labelItem.setText(idioma.getMensagem("item")+":");
        labelConta.setText(idioma.getMensagem("conta")+":");
        labelData.setText(idioma.getMensagem("data")+":");
        labelDescricao.setText(idioma.getMensagem("descricao")+":");
        labelValor.setText(idioma.getMensagem("valor")+":");
        new ReceitaItem().montarSelectItem(itemController.getComboItem());
        new Conta().montarSelectCategoria(contaController.getComboCategoria());
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        if(botao==1){
            Animacao.fadeOutInvisivel(itemController.getComboItem(), formulario);
            ReceitaItemFormularioControlador controladorItem = Janela.abrir(ReceitaItem.FXML_FORMULARIO, idioma.getMensagem("receita"));
            controladorItem.cadastrar(this, itemController.getComboItem().getEditor().getText());
        }else{
            Animacao.fadeOutInvisivel(contaController.getComboCategoria(), formulario);
            ContaFormularioControlador controladorConta = Janela.abrir(Conta.FXML_FORMULARIO, idioma.getMensagem("receita"));
            controladorConta.cadastrar(this);
        }
    }
    
    @Override
    public void selecionarComboItem(int combo, Item item) {
        if(item!=null){
            new ReceitaItem().montarSelectItem(itemController.getComboItem());
            itemController.setItemSelecionado(item);
        }
        Animacao.fadeInInvisivel(itemController.getComboItem(), formulario);
    }
    
    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) {
        if(categoria!=null){
            new Conta().montarSelectCategoria(contaController.getComboCategoria());
            contaController.setCategoriaSelecionada(categoria);
        }
        Animacao.fadeInInvisivel(contaController.getComboCategoria(), formulario);
    }
    
    public void cadastrar(PlanejamentoControlador controlador){
        acao = Acao.CADASTRAR;
        this.controlador = controlador;
        if(controlador!=null){
            data.setValue(LocalDate.now().withMonth(controlador.getData().getMonthValue()).withYear(controlador.getData().getYear()));
        }else{
            data.setValue(LocalDate.now());
        }
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("cadastrar"));
        tabela.getChildren().remove(botaoController.getStackPane());
        tabela.add(botaoController.getStackPane(), 1, 6);
        Label labelAgendada = new Label(idioma.getMensagem("agendar")+":");
        tabela.add(labelAgendada, 0, 5);
        qtdMeses.setPrefWidth(100);
        qtdMeses.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
        qtdMeses.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        Ajuda.estilizarBotaoDica(qtdMeses, ajuda, idioma.getMensagem("ajuda_parcela_agendada"), Duracao.MUITO_LONGA);
        valorParcela.getStyleClass().add("Botao");
        valorParcela.getStyleClass().add("BotaoFim");
        valorParcela.setText("/");
        valorParcela.setOnAction(e -> {
            if(valorParcela.isSelected()){
                valorParcela.setText("=");
            }else{
                valorParcela.setText("/");
            }
        });
        HBox grupoAgendar = new HBox();
        grupoAgendar.getChildren().addAll(checkAgendar,qtdMeses,valorParcela,ajuda);
        tabela.add(grupoAgendar, 1, 5);
        if(controlador!=null){
            checkAgendar.setSelected(true);
            checkAgendar.setDisable(true);
            eventoReceitaAgendada();
        }else{
            qtdMeses.setVisible(false);
            valorParcela.setVisible(false);
            ajuda.setVisible(false);
        }
        checkAgendar.setOnAction(e -> {
            eventoReceitaAgendada();
        });
    }
    
    private void eventoReceitaAgendada(){
        tabela.getChildren().remove(botaoController.getStackPane());
        if(checkAgendar.isSelected()){
            qtdMeses.setVisible(true);
            valorParcela.setVisible(true);
            ajuda.setVisible(true);
            tabela.add(botaoController.getStackPane(), 1, 7);
        }else{
            qtdMeses.setVisible(false);
            valorParcela.setVisible(false);
            ajuda.setVisible(false);
            tabela.add(botaoController.getStackPane(), 1, 6);
        }
    }
    
    public void alterar(Receita modelo){
        this.modelo = modelo;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("alterar"));
        ReceitaItem item = new ReceitaItem().setIdItem(modelo.getIdItem()).consultar();
        if(item != null){
            itemController.setItemSelecionado(item);
        }
        Conta conta = new Conta().setIdCategoria(modelo.getIdConta()).consultar();
        if(conta != null){
            contaController.setCategoriaSelecionada(conta);
        }
        itemController.getComboItem().setDisable(true);
        itemController.getBotaoCadastrar().setDisable(true);
        data.setValue(modelo.getData());
        descricao.setText(modelo.getDescricao());
        valor.setText(modelo.getValor().toString());
        if(modelo.getAgendada().equals("1")){
            Button bConfirmar = new Button(idioma.getMensagem("confirmar"));
            botaoController.getGrupoBotao().getChildren().add(0, bConfirmar);
            bConfirmar.setOnAction(e -> {
                confirmar = true;
                acaoFinalizar();
            });
        }
    }

    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            if(acao == Acao.CADASTRAR){
                if(checkAgendar.isSelected()){
                    LocalDate dataCadastro = data.getValue();
                    int j = Integer.parseInt(qtdMeses.getValue().toString());
                    for(int i=1;i<=j;i++){
                        String dvalor = valor.getText();
                        if(!valorParcela.isSelected()){
                            dvalor = new BigDecimal(dvalor).divide(new BigDecimal(j), 2, RoundingMode.HALF_UP).toString();
                        }
                        Receita item = new Receita(null, contaController.getIdCategoria(), itemController.getIdItem(), descricao.getText(), dvalor, dataCadastro, Datas.getHoraAtual(), "1");
                        if(j>1){ item.setParcela(i+"/"+j); }
                        item.cadastrar();
                        dataCadastro = dataCadastro.plusMonths(1);
                    }
                    if(controlador!=null){
                        Kernel.controlador.acaoFiltrar(true);
                    }else{
                        Kernel.principal.acaoPlanejamento(data.getValue().getMonthValue(), data.getValue().getYear());
                    }
                }else{
                    Receita item = new Receita(null, contaController.getIdCategoria(), itemController.getIdItem(), descricao.getText(), valor.getText(), data.getValue(), Datas.getHoraAtual(), "0");
                    item.cadastrar();
                    new Conta().alterarSaldo(Operacao.INCREMENTAR, contaController.getIdCategoria(), valor.getText());
                    Kernel.principal.acaoReceita();
                }
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }else{
                if(!modelo.getAgendada().equals("1")){
                    Boolean contaMudou = !(modelo.getIdConta().equals(contaController.getIdCategoria()));
                    if(contaMudou){
                        new Conta().alterarSaldo(Operacao.DECREMENTAR, modelo.getIdConta(), modelo.getValor().toString());
                        new Conta().alterarSaldo(Operacao.INCREMENTAR, contaController.getIdCategoria(), modelo.getValor().toString());
                    }
                    Boolean valorMudou = !(modelo.getValor().equals(valor.getText()));
                    if(valorMudou){
                        BigDecimal valorDiferenca = modelo.getValor();
                        valorDiferenca = valorDiferenca.subtract(new BigDecimal(valor.getText()));
                        new Conta().alterarSaldo(Operacao.DECREMENTAR, modelo.getIdConta(), valorDiferenca.toString());
                    }
                }else{
                    if(confirmar){
                        modelo.setAgendada("0");
                        new Conta().alterarSaldo(Operacao.INCREMENTAR, contaController.getIdCategoria(), valor.getText());
                    }
                }
                modelo.setIdConta(contaController.getComboCategoria().getValue());
                modelo.setValor(valor.getText());
                modelo.setDescricao(descricao.getText());
                modelo.setData(Datas.toSqlData(data.getValue()));
                modelo.alterar();
                Kernel.controlador.acaoFiltrar(true);
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }
        }
    }
    
    private boolean validarFormulario(){
        try {
            Validar.textFieldDecimal(valor);
            Validar.autoFiltro(itemController.getAutoFiltro(), itemController.getComboItem());
            Validar.comboBox(contaController.getComboCategoria());
            Validar.datePicker(data);
            return true;
        } catch (Erro ex) {
            return false;
        }
    }
    
    public void lancamentoGuiado(Item item){
        itemController.setItemSelecionado(item);
        itemController.getComboItem().setDisable(true);
        itemController.getBotaoCadastrar().setDisable(true);
        itemController.getBotaoCadastrar().setVisible(false);
    }

}
