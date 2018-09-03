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

package io.github.badernageral.bgfinancas.modulo.despesa;

import io.github.badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Controlador;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFiltro;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Tabela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Acao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Operacao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Posicao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.AreaTransferencia;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.AutoCompletarTextField;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Datas;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.modelo.CartaoCredito;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import io.github.badernageral.bgfinancas.template.modulo.ListaConta;
import io.github.badernageral.bgfinancas.modelo.Conta;
import io.github.badernageral.bgfinancas.modelo.Despesa;
import io.github.badernageral.bgfinancas.modelo.DespesaItem;
import io.github.badernageral.bgfinancas.modulo.despesa.item.DespesaItemFormularioControlador;
import java.math.RoundingMode;
import java.util.Arrays;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public final class DespesaCadastroMultiploControlador implements Initializable, Controlador, ControladorFiltro {
    
    private final String TITULO = idioma.getMensagem("despesas")+" - "+idioma.getMensagem("cadastrar");
    
    @FXML private Button voltar;
    @FXML private Button cadastrarItem;
    @FXML private Button excluir;
    @FXML private Button finalizar;
    @FXML private GridPane barraSuperior;
    @FXML private GridPane barraInferior;
    private final AutoCompletarTextField<DespesaItem> item = new AutoCompletarTextField<>(this,this);
    @FXML private ComboBox<Categoria> conta;
    @FXML private DatePicker data;
    @FXML private Label labelTitulo;
    @FXML private Label labelDespesa;
    @FXML private Label labelData;
    @FXML private Label labelConta;
    @FXML private Label labelDespesaValor;
    @FXML private Label labelValorTotal;
    @FXML private Label labelDespesaAgendada;
    @FXML private ListaConta listaContaController;
    @FXML private TableView<Despesa> tabelaLista;
    private final Tabela<Despesa> tabela = new Tabela<>();
    private ObservableList<Despesa> itens;
    private BigDecimal saldoTotal;
    
    @FXML private HBox grupoAgendar;
    @FXML private CheckBox checkDespesaAgendada;
    private final CheckBox checkCartaoCredito = new CheckBox();
    private final Spinner qtdMeses = new Spinner();
    private final ToggleButton valorParcela = new ToggleButton();
    private final Label ajuda = new Label();
    private final ComboBox<Categoria> listaCartaoCredito = new ComboBox<>();
    private final Label labelCartaoCredito = new Label(idioma.getMensagem("cartao_credito")+":");
    
    private AreaTransferencia<Despesa> areaTransferencia = new AreaTransferencia();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(TITULO);
        Botao.prepararBotao(new Button[]{cadastrarItem}, null, excluir, voltar);
        Botao.prepararBotaoFinalizar(finalizar, this);
        tabela.prepararTabela(tabelaLista, 1);
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("categoria"), "nomeCategoria");
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("item"), "nomeItem");
        tabela.adicionarColunaNumero(tabelaLista, idioma.getMensagem("quantidade"), "quantidade");
        tabela.adicionarColunaNumero(tabelaLista, idioma.getMensagem("valor"), "valor");
        data.setValue(LocalDate.now());
        acaoFiltrar(false);
        item.getStyleClass().add("semFoco");
        item.getStyleClass().add("BotaoMeio");
        barraSuperior.add(item, 1, 0);
        labelTitulo.setText(idioma.getMensagem("cadastro_multiplo"));
        labelDespesa.setText(idioma.getMensagem("adicionar_despesa")+":");
        labelDespesaValor.setText(idioma.getMensagem("valor")+":");
        labelData.setText(idioma.getMensagem("data")+":");
        labelConta.setText(idioma.getMensagem("conta")+":");
        labelDespesaAgendada.setText(idioma.getMensagem("agendar")+":");
        finalizar.setText(idioma.getMensagem("cadastrar"));
        item.setPromptText(idioma.getMensagem("autofiltro"));
        areaTransferencia.criarMenu(this, tabelaLista, false);
        prepararDespesaAgendada();
        checkDespesaAgendada.setOnAction(e -> {
            eventoDespesaAgendada();
        });
        checkCartaoCredito.setOnAction(e -> {
            eventoCartaoCredito();
        });
    }
    
    private void prepararDespesaAgendada(){
        qtdMeses.setPrefWidth(100);
        qtdMeses.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
        zerarMeses();
        Ajuda.estilizarBotaoDica(qtdMeses, ajuda, idioma.getMensagem("ajuda_parcela_agendada"), Duracao.MUITO_LONGA);
        new CartaoCredito().montarSelectCategoria(listaCartaoCredito);
        labelCartaoCredito.getStyleClass().add("paddingLabelRight");
        listaCartaoCredito.getStyleClass().add("ListaComBotao");
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
    }
    
    private void zerarMeses(){
        qtdMeses.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }
    
    private void eventoDespesaAgendada(){
        if(checkDespesaAgendada.isSelected()){
            grupoAgendar.getChildren().add(qtdMeses);
            grupoAgendar.getChildren().add(valorParcela);
            grupoAgendar.getChildren().add(ajuda);
            grupoAgendar.getChildren().add(labelCartaoCredito);
            grupoAgendar.getChildren().add(checkCartaoCredito);
        }else{
            grupoAgendar.getChildren().remove(qtdMeses);
            grupoAgendar.getChildren().remove(valorParcela);
            grupoAgendar.getChildren().remove(ajuda);
            grupoAgendar.getChildren().remove(labelCartaoCredito);
            grupoAgendar.getChildren().remove(checkCartaoCredito);
            grupoAgendar.getChildren().remove(listaCartaoCredito);
            listaCartaoCredito.getSelectionModel().select(null);
            checkCartaoCredito.setSelected(false);
            zerarMeses();
        }
    }
    
    private void eventoCartaoCredito(){
        if(checkCartaoCredito.isSelected()){
            grupoAgendar.getChildren().add(listaCartaoCredito);
        }else{
            grupoAgendar.getChildren().remove(listaCartaoCredito);
        }
    }
    
    private String[] modalDespesa(Acao acao, String campo_1, String valor_1, String campo_2, String valor_2){
        ModalDespesaControlador janela = Janela.abrir(Despesa.FXML_MODAL_DESPESA, TITULO, true);
        if(acao == Acao.CADASTRAR){
            janela.setTitulo(idioma.getMensagem("adicionar_item"));
        }else{
            janela.setTitulo(idioma.getMensagem("alterar_item"));
        }
        janela.setMensagem(campo_1, campo_2);
        janela.setValor(valor_1, valor_2);
        Kernel.palcoModal.showAndWait();
        return janela.getResultado();
    }
    
    @Override
    public void adicionar(String nome) {
        String[] texto;
        if(nome!=null && !nome.equals("")){
            texto = Item.getNomes(nome);
        }else{
            texto = Item.getNomes(item.getText());
        }
        item.setText("");
        if(texto!=null){
            DespesaItem i = new DespesaItem().setNome(texto[0]).setNomeCategoria(texto[1]).consultar();
            if(i!=null){
                String[] valor = modalDespesa(Acao.CADASTRAR, idioma.getMensagem("valor")+":", "0.00", idioma.getMensagem("quantidade")+":", "1");
                if(valor != null){
                    tabelaLista.getItems().add(new Despesa(i.getIdItem(), i.getNome(), i.getNomeCategoria(), valor[1], valor[0]));
                    calcularValorTotal();
                }
            }
            Kernel.palco.requestFocus();
        }
    }
    
    private boolean validarFormulario(){
        try {
            Validar.tableView(tabelaLista, finalizar);
            Validar.datePicker(data);
            Validar.comboBox(conta);
            return true;
        } catch (Erro e) {
            return false;
        }
    }
    
    private void calcularValorTotal(){
        itens = tabelaLista.getItems();
        BigDecimal valorTotal = new BigDecimal("0.00");
        if(itens!=null){
            for(Despesa d : itens){
                BigDecimal valor = d.getValor();
                valorTotal = valorTotal.add(valor);
            }
        }
        saldoTotal = valorTotal;
        labelValorTotal.setText(idioma.getMensagem("moeda")+" "+valorTotal.toString());
    }
    
    private void limparFormulario(){
        tabelaLista.getItems().removeAll(itens);
        data.setValue(LocalDate.now());
        acaoFiltrar(true);
    }

    @Override
    public void acaoCadastrar(int botao) {
        if(botao==1){
            String texto = item.getText();
            if(texto.equals("")){
                Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("campo_nao_informado"), item, Duracao.CURTA);
                item.requestFocus();
            }else{
                DespesaItemFormularioControlador Controlador = Janela.abrir(DespesaItem.FXML_FORMULARIO, TITULO);
                Controlador.cadastrar(null, this, texto);
            }
        }
    }

    public void acaoFinalizar() {
        if(validarFormulario()){
            itens = tabelaLista.getItems();
            LocalDate d = data.getValue();
            Categoria c = conta.getSelectionModel().getSelectedItem();
            if(checkDespesaAgendada.isSelected()){
                    int j = Integer.parseInt(qtdMeses.getValue().toString());
                    for(Despesa ia : itens){
                        if(!valorParcela.isSelected()){
                            BigDecimal valor = ia.getValor();
                            valor = valor.divide(new BigDecimal(j), 2, RoundingMode.HALF_UP);
                            ia.setValor(valor.toString());
                        }
                    }
                    for(int i=1;i<=j;i++){
                        String cartaoCredito = null;
                        if(checkCartaoCredito.isSelected()){
                            cartaoCredito = listaCartaoCredito.getSelectionModel().getSelectedItem().getIdCategoria();
                        }
                        for(Despesa ia : itens){
                            ia.setIdConta(c);
                            ia.setData(Datas.toSqlData(d));
                            ia.setHora(Datas.getHoraAtual());
                            ia.setAgendada("1");
                            ia.setIdCartaoCredito(cartaoCredito);
                            if(j>1){ ia.setParcela(i+"/"+j); }
                            ia.cadastrar();
                        }
                        d = d.plusMonths(1);
                    }
            }else{
                for(Despesa i : itens){
                    i.setIdConta(c);
                    i.setData(Datas.toSqlData(d));
                    i.setHora(Datas.getHoraAtual());
                    i.cadastrar();
                }
                new Conta().alterarSaldo(Operacao.DECREMENTAR, c.getIdCategoria(), saldoTotal.toString());
            }
            limparFormulario();
            Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
        }
    }
    
    @Override
    public void acaoExcluir(int botao) {
        itens = tabelaLista.getSelectionModel().getSelectedItems();
        if(Validar.exclusao(itens, excluir, false)){
            tabelaLista.getItems().removeAll(itens);
            calcularValorTotal();
        }
    }
    
    @Override
    public void acaoFiltrar(Boolean animacao) {  
        item.setItens(new DespesaItem().listar());
        new Conta().montarSelectCategoria(conta);
        listaContaController.atualizarTabela(animacao);
        calcularValorTotal();
        item.requestFocus();
    }
    
    @Override
    public void acaoAlterar(int tabela) {
        if(tabela==1){
            int index = tabelaLista.getSelectionModel().getSelectedIndex();
            Despesa despesa = tabelaLista.getSelectionModel().getSelectedItem();
            String[] valor = modalDespesa(Acao.ALTERAR, idioma.getMensagem("valor")+":", despesa.getValor().toString(), idioma.getMensagem("quantidade")+":", despesa.getQuantidade().toString());
            if(valor != null){
                despesa.setValor(valor[0]);
                despesa.setQuantidade(valor[1]);
                tabelaLista.getItems().set(index, despesa);
                calcularValorTotal();
            }
        }else if(tabela==2){
            listaContaController.alterarConta(TITULO);
        }
    }
    
    @Override
    public boolean sair(){
        if(tabelaLista.getItems().size()>0){
            return Janela.showPergunta(idioma.getMensagem("sair_despesa_cadastro_multiplo"));
        }else{
            return true;
        }
    }
    
    @Override
    public void acaoVoltar() {
        Kernel.principal.acaoVoltar();
    }
    
    @Override
    public void acaoGerenciar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoAjuda() {
        Ajuda.getInstance().setObjetos(voltar,labelTitulo,labelData,data,labelConta,conta,finalizar,labelDespesa,item,cadastrarItem,excluir,tabelaLista,barraInferior,listaContaController.getGridPane(),grupoAgendar);
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_desp_mult_1"));
        Ajuda.getInstance().capitulo(data, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_2"));
        Ajuda.getInstance().capitulo(item, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_3"));
        Ajuda.getInstance().capitulo(cadastrarItem, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_4"));
        Ajuda.getInstance().capitulo(excluir, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_5"));
        Ajuda.getInstance().capitulo(grupoAgendar, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_6"));
        Ajuda.getInstance().capitulo(Arrays.asList(tabelaLista, barraInferior), Posicao.CENTRO, idioma.getMensagem("tuto_desp_mult_7"));
        Ajuda.getInstance().capitulo(conta, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_8"));
        Ajuda.getInstance().capitulo(finalizar, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_9"));
        Ajuda.getInstance().apresentarProximo();
    }
    
}
