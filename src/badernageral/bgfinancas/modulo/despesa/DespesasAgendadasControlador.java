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

package badernageral.bgfinancas.modulo.despesa;

import badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.Controlador;
import badernageral.bgfinancas.biblioteca.sistema.Botao;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.sistema.Tabela;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Posicao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import badernageral.bgfinancas.modelo.CartaoCredito;
import badernageral.bgfinancas.modelo.Despesa;
import badernageral.bgfinancas.template.modulo.ListaConta;
import badernageral.bgfinancas.template.modulo.ListaGrupo;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

public final class DespesasAgendadasControlador implements Initializable, Controlador {
    
    @FXML private Button voltar;
    @FXML private Label titulo;
    @FXML private Button anterior;
    @FXML private Button proximo;
    @FXML private Label labelCartaoCredito;
    @FXML private Button cadastrar;
    @FXML private Button alterar;
    @FXML private Button excluir;
    @FXML private Label labelDespesasAgendadasTotal;
    @FXML private GridPane barraSuperior;
    @FXML private GridPane barraInferior;
    @FXML private ListaGrupo listaGrupoController;
    @FXML private ListaConta listaContaController;
    @FXML private ComboBox<Categoria> listaCartaoCredito;
    
    @FXML private StackPane tabelaPane;
    
    private LocalDate data = LocalDate.now();
    private Despesa modelo;
       
    private final String TITULO = idioma.getMensagem("despesas_agendadas");
    
    private ObservableList<Despesa> itens;
    private final Tabela<Despesa> tabela = new Tabela<>();
    private final TableView<Despesa> tabelaLista = new TableView<>();
    private final List<Despesa> itensDespesasAgendadas = new ArrayList<>();
    private final ObservableList<Despesa> areaTransferencia = FXCollections.observableList(itensDespesasAgendadas);
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(TITULO);
        Botao.prepararBotao(new Button[]{cadastrar}, alterar, excluir, voltar);
        Botao.prepararBotaoGerenciar(1, anterior, idioma.getMensagem("anterior"));
        Botao.prepararBotaoGerenciar(2, proximo, idioma.getMensagem("proximo"));
        labelCartaoCredito.setText(idioma.getMensagem("cartao_credito")+":");
        tabelaPane.getChildren().add(tabelaLista);
        tabela.prepararTabela(tabelaLista);
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("categoria"), "nomeCategoria");
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("item"), "nomeItem").setMinWidth(120);
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("quantidade"), "quantidade");
        tabela.setColunaDinheiro(tabela.adicionarColuna(tabelaLista, idioma.getMensagem("valor"), "valor"), false);
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("conta"), "nomeConta");
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("data"), "data");
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("cartao_credito"), "nomeCartaoCredito");
        new CartaoCredito().montarSelectCategoria(listaCartaoCredito);
        CartaoCredito cartaoSemCartao = new CartaoCredito().setIdCategoria("NULL").setNome(idioma.getMensagem("sem_cartao_credito"));
        CartaoCredito cartaoTodos = new CartaoCredito().setNome(idioma.getMensagem("todos"));
        listaCartaoCredito.getItems().add(cartaoSemCartao);
        listaCartaoCredito.getItems().add(cartaoTodos);
        listaCartaoCredito.getSelectionModel().select(cartaoTodos);
        listaCartaoCredito.setOnAction(e -> { acaoFiltrar(true); });
        atualizarTitulo(false);
        criarMenu();
    }
    
    public void criarMenu(){
        ContextMenu menu = new ContextMenu();
        MenuItem copiar = new MenuItem("Copiar");
        MenuItem colar = new MenuItem("Colar");
        copiar.disableProperty().bind(Bindings.isEmpty(tabelaLista.getSelectionModel().getSelectedItems()));
        colar.disableProperty().bind(Bindings.isEmpty(areaTransferencia));
        copiar.setOnAction(event -> {
            areaTransferencia.clear();
            tabelaLista.getSelectionModel().getSelectedItems().stream().forEach(d -> {
               areaTransferencia.add(d);
            });
        });
        colar.setOnAction(event -> {
            areaTransferencia.stream().forEach(d -> {
                d.setData(LocalDate.of(data.getYear(), data.getMonthValue(), d.getDataLocal().getDayOfMonth()));
                d.cadastrar();
            });
            acaoFiltrar(true);
        });
        menu.getItems().addAll(copiar,colar);
        tabelaLista.setContextMenu(menu);
    }
    
    private void atualizarTitulo(Boolean animacao){
        titulo.setText(TITULO+" - "+idioma.getNomeMes(data.getMonthValue())+" / "+data.getYear());
        acaoFiltrar(animacao);
    }
    
    @Override
    public void acaoFiltrar(Boolean animacao){
        tabelaLista.setItems(new Despesa().setSomenteAgendamento().setMesAno(data.getMonthValue(),data.getYear()).setIdCartaoCredito(listaCartaoCredito.getSelectionModel().getSelectedItem()).listar());
        listaGrupoController.atualizarTabela(animacao);
        listaContaController.atualizarTabela(animacao);
        calcularSaldo();
        if(animacao){
            Animacao.fadeOutIn(tabelaLista);
        }
    }
    
    private void calcularSaldo(){
        BigDecimal valorTotal = tabelaLista.getItems().stream()
                .map(d -> new BigDecimal(d.getValor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        labelDespesasAgendadasTotal.setText(idioma.getMensagem("valor_total")+": "+idioma.getMensagem("moeda")+" "+valorTotal.toString());
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        if(botao==1){
            DespesaFormularioControlador Controlador = Janela.abrir(Despesa.FXML_FORMULARIO, TITULO);
            Controlador.cadastrar(this);
        }
    }
    
    @Override
    public void acaoAlterar(int tabela) {
        if(tabela==1){
            itens = tabelaLista.getSelectionModel().getSelectedItems();
            if(Validar.alteracao(itens, alterar)){
                DespesaFormularioControlador Controlador = Janela.abrir(Despesa.FXML_FORMULARIO, TITULO);
                Controlador.alterar(itens.get(0));
            }
        }else if(tabela==2){
            listaContaController.alterarConta(TITULO);
        }else if(tabela==3){
            listaGrupoController.alterarGrupo(TITULO);
        }
    }
    
    @Override
    public void acaoExcluir(int botao) {
        itens = tabelaLista.getSelectionModel().getSelectedItems();
        if(Validar.exclusao(itens, excluir)){
            itens.forEach((Despesa p) -> p.excluir());
            Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
            acaoFiltrar(true);
        }
    }
    
    public LocalDate getData(){
        return data;
    }
    
    public void setData(int mes, int ano){
        data = data.withMonth(mes);
        data = data.withYear(ano);
        atualizarTitulo(true);
    }

    @Override
    public void acaoGerenciar(int botao) {
        if(botao==2){
            data = data.minusMonths(1);
            atualizarTitulo(true);
        }else if(botao==3){
            data = data.plusMonths(1);
            atualizarTitulo(true);
        }
    }
    
    @Override
    public void acaoVoltar() {
        Kernel.principal.acaoVoltar();
    }

    @Override
    public void acaoAjuda() {
        Ajuda.getInstance().setObjetos(voltar,titulo,anterior,proximo,labelCartaoCredito,listaCartaoCredito,cadastrar,alterar,excluir,tabelaLista,barraInferior,listaGrupoController.getGridPane(),listaContaController.getGridPane());
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_desp_agen_1"));
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_desp_agen_2"));
        Ajuda.getInstance().capitulo(titulo, Posicao.BAIXO, idioma.getMensagem("tuto_desp_agen_3"));
        Ajuda.getInstance().capitulo(anterior, Posicao.BAIXO, idioma.getMensagem("tuto_desp_agen_4"));
        Ajuda.getInstance().capitulo(proximo, Posicao.BAIXO, idioma.getMensagem("tuto_desp_agen_5"));
        Ajuda.getInstance().capitulo(listaCartaoCredito, Posicao.BAIXO, idioma.getMensagem("tuto_desp_agen_6"));
        Ajuda.getInstance().capitulo(cadastrar, Posicao.BAIXO, idioma.getMensagem("tuto_desp_agen_7"));
        Ajuda.getInstance().capitulo(alterar, Posicao.BAIXO, idioma.getMensagem("tuto_desp_agen_8"));
        Ajuda.getInstance().capitulo(excluir, Posicao.BAIXO, idioma.getMensagem("tuto_desp_agen_9"));
        Ajuda.getInstance().capitulo(tabelaLista, Posicao.CENTRO, idioma.getMensagem("tuto_desp_agen_10"));
        Ajuda.getInstance().capitulo(barraInferior, Posicao.TOPO, idioma.getMensagem("tuto_desp_agen_11"));
        Ajuda.getInstance().capitulo(Arrays.asList(listaContaController.getGridPane(),listaGrupoController.getGridPane()), 
                Posicao.CENTRO, idioma.getMensagem("tuto_desp_agen_12"));
        Ajuda.getInstance().apresentarProximo();
    }

}