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

package badernageral.bgfinancas.modulo.despesa;

import badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.Controlador;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFiltro;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.Botao;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.sistema.Tabela;
import badernageral.bgfinancas.biblioteca.tipo.Acao;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Operacao;
import badernageral.bgfinancas.biblioteca.tipo.Posicao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.biblioteca.utilitario.AreaTransferencia;
import badernageral.bgfinancas.biblioteca.utilitario.AutoCompletarTextField;
import badernageral.bgfinancas.biblioteca.utilitario.Datas;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
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
import badernageral.bgfinancas.template.modulo.ListaConta;
import badernageral.bgfinancas.modelo.Conta;
import badernageral.bgfinancas.modelo.Despesa;
import badernageral.bgfinancas.modelo.DespesaItem;
import badernageral.bgfinancas.modulo.despesa.item.DespesaItemFormularioControlador;
import java.util.Arrays;
import javafx.scene.layout.GridPane;

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
    @FXML private ListaConta listaContaController;
    @FXML private TableView<Despesa> tabelaLista;
    private final Tabela<Despesa> tabela = new Tabela<>();
    private ObservableList<Despesa> itens;
    private BigDecimal saldoTotal;
    
    private AreaTransferencia areaTransferencia = new AreaTransferencia();
    
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
        finalizar.setText(idioma.getMensagem("cadastrar"));
        item.setPromptText(idioma.getMensagem("autofiltro"));
        areaTransferencia.criarMenu(this, tabelaLista, false);
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
                if(Janela.showPergunta(idioma.getMensagem("item_nao_encontrado"))){
                    DespesaItemFormularioControlador Controlador = Janela.abrir(DespesaItem.FXML_FORMULARIO, TITULO);
                    Controlador.cadastrar(null, this, texto);
                }
            }
        }
    }

    public void acaoFinalizar() {
        if(validarFormulario()){
            itens = tabelaLista.getItems();
            LocalDate d = data.getValue();
            Categoria c = conta.getSelectionModel().getSelectedItem();
            for(Despesa i : itens){
                i.setIdConta(c);
                i.setData(Datas.toSqlData(d));
                i.setHora(Datas.getHoraAtual());
                i.cadastrar();
            }
            new Conta().alterarSaldo(Operacao.DECREMENTAR, c.getIdCategoria(), saldoTotal.toString());
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
    public void acaoVoltar() {
        Kernel.principal.acaoVoltar();
    }
    
    @Override
    public void acaoGerenciar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoAjuda() {
        Ajuda.getInstance().setObjetos(voltar,labelTitulo,labelData,data,labelConta,conta,finalizar,labelDespesa,item,cadastrarItem,excluir,tabelaLista,barraInferior,listaContaController.getGridPane());
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_desp_mult_1"));
        Ajuda.getInstance().capitulo(data, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_2"));
        Ajuda.getInstance().capitulo(item, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_3"));
        Ajuda.getInstance().capitulo(cadastrarItem, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_4"));
        Ajuda.getInstance().capitulo(excluir, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_5"));
        Ajuda.getInstance().capitulo(Arrays.asList(tabelaLista, barraInferior), Posicao.CENTRO, idioma.getMensagem("tuto_desp_mult_6"));
        Ajuda.getInstance().capitulo(conta, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_7"));
        Ajuda.getInstance().capitulo(finalizar, Posicao.BAIXO, idioma.getMensagem("tuto_desp_mult_8"));
        Ajuda.getInstance().apresentarProximo();
    }
    
}
