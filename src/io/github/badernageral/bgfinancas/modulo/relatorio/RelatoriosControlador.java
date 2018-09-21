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

package io.github.badernageral.bgfinancas.modulo.relatorio;

import io.github.badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Controlador;
import io.github.badernageral.bgfinancas.modelo.Extrato;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Grafico;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Tabela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Posicao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Numeros;
import io.github.badernageral.bgfinancas.modelo.CartaoCredito;
import io.github.badernageral.bgfinancas.modelo.Conta;
import io.github.badernageral.bgfinancas.modelo.Despesa;
import io.github.badernageral.bgfinancas.modelo.DespesaCategoria;
import io.github.badernageral.bgfinancas.modelo.Receita;
import io.github.badernageral.bgfinancas.modelo.Transferencia;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public final class RelatoriosControlador implements Initializable, Controlador {
    
    private final String TITULO = idioma.getMensagem("relatorios");
    
    @FXML private BorderPane painelRelatorio;
    @FXML private Button voltar;
    @FXML private Label labelRelatorio;
    @FXML private Label labelContaCartao;
    @FXML private Label labelInicio;
    @FXML private Label labelFim;
    @FXML private ComboBox<String> relatorio;
    @FXML private ComboBox<String> tipo;
    @FXML private ComboBox<Categoria> listaContaCartao;
    @FXML private DatePicker inicio;
    @FXML private DatePicker fim;
    @FXML private Button atualizar;
    @FXML private Button imprimir;
    @FXML private GridPane tabela;
    @FXML private GridPane barraSuperior;
    
    private BigDecimal valorTotal;
    
    private final NumberAxis xAxisPrincipal = new NumberAxis();
    private final CategoryAxis yAxisPrincipal = new CategoryAxis();
    private final BarChart<String,Number> graficoPrincipal = new BarChart<>(yAxisPrincipal,xAxisPrincipal);
    
    private final NumberAxis xAxisSecundario = new NumberAxis();
    private final CategoryAxis yAxisSecundario = new CategoryAxis();
    private final BarChart<String,Number> graficoSecundario = new BarChart<>(yAxisSecundario,xAxisSecundario);
    
    private final CategoryAxis xAxisTempoLinhas = new CategoryAxis();
    private final NumberAxis yAxisTempoLinhas = new NumberAxis();
    private final LineChart<String,Number> graficoTempoLinhas = new LineChart<>(xAxisTempoLinhas,yAxisTempoLinhas);
    
    private final CategoryAxis xAxisTempoBarras = new CategoryAxis();
    private final NumberAxis yAxisTempoBarras = new NumberAxis();
    private final StackedBarChart<Number,String> graficoTempoEmpilhado = new StackedBarChart<>(yAxisTempoBarras,xAxisTempoBarras);
    
    private final ListView<DespesaCategoria> listaCategorias = new ListView<>();
    
    private final TableView<Despesa> listaDespesas = new TableView<>();
    private final Tabela<Despesa> tabelaDespesas = new Tabela<>();
    
    private final TableView<Extrato> listaExtrato = new TableView<>();
    private final Tabela<Extrato> tabelaExtrato = new Tabela<>();
    
    private final HBox panelValores = new HBox();
    private final Label labelReceitasValor = new Label();
    private final Label labelDespesasValor = new Label();
    private final Label labelSaldoValor = new Label();
    private final Label labelTotalValor = new Label();
                   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(TITULO);
        Botao.prepararBotaoVoltar(voltar);
        labelRelatorio.setText(idioma.getMensagem("selecione_relatorio")+":");
        List<String> tipoRelatorio = Arrays.asList(idioma.getMensagem("despesas"),idioma.getMensagem("lista_despesas"),idioma.getMensagem("despesas_tempo"),idioma.getMensagem("despesas_agendadas"),idioma.getMensagem("receitas"),idioma.getMensagem("transferencias"),idioma.getMensagem("extrato"));
        relatorio.setItems(FXCollections.observableList(tipoRelatorio));
        relatorio.getSelectionModel().select(0);
        labelInicio.setText(idioma.getMensagem("inicio")+":");
        labelFim.setText(idioma.getMensagem("fim")+":");
        LocalDate hoje = LocalDate.now();
        inicio.setValue(hoje.withDayOfMonth(1));
        fim.setValue(hoje.withDayOfMonth(hoje.lengthOfMonth()));
        prepararTabelas();
        prepararTipoTempo();
        prepararFiltro();
        prepararTotais();
    }
    
    private void prepararTabelas(){
        tabelaDespesas.prepararTabela(listaDespesas);
        tabelaDespesas.adicionarColuna(listaDespesas, idioma.getMensagem("categoria"), "nomeCategoria");
        tabelaDespesas.adicionarColuna(listaDespesas, idioma.getMensagem("item"), "nomeItem");
        tabelaDespesas.adicionarColunaNumero(listaDespesas, idioma.getMensagem("quantidade"), "quantidade");
        tabelaDespesas.adicionarColunaNumero(listaDespesas, idioma.getMensagem("valor"), "valor");
        tabelaExtrato.prepararTabela(listaExtrato);
        TableColumn<Extrato,String> tipo = tabelaExtrato.adicionarColuna(listaExtrato, idioma.getMensagem("tipo"), "tipo");
        tabelaExtrato.adicionarColunaDataHora(listaExtrato, idioma.getMensagem("data"), "dataHora");
        tabelaExtrato.adicionarColuna(listaExtrato, idioma.getMensagem("categoria"), "nomeCategoria", 100);
        tabelaExtrato.adicionarColuna(listaExtrato, idioma.getMensagem("item"), "nomeItem", 120);
        tabelaExtrato.adicionarColunaNumero(listaExtrato, idioma.getMensagem("valor"), "valor");
        TableColumn<Extrato,String> status = tabelaExtrato.adicionarColuna(listaExtrato, idioma.getMensagem("status"), "status");
        status.setCellFactory(e -> new TableCell<Extrato, String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item==null ? null : item);
                if (item != null) {
                    setTextFill(item.equals(idioma.getMensagem("agendado")) ? Color.RED : Color.BLACK);
                }
            }
        });
        tipo.setCellFactory(e -> new TableCell<Extrato, String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item==null ? null : item);
                if (item != null) {
                    setTextFill(item.equals(idioma.getMensagem("despesa")) ? Color.GREEN : Color.BLUE);
                }
            }
        });
    }
    
    public void acaoImprimir() {
        try{
            PageLayout paisagem = Printer.getDefaultPrinter().createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);
            Double largura = paisagem.getPrintableWidth()-50;
            Double altura = paisagem.getPrintableHeight()-50;
            PrinterJob trabalho = PrinterJob.createPrinterJob();
            if(trabalho != null){
                if(trabalho.showPrintDialog(Kernel.palco)){
                    Tooltip aguarde = Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("aguarde_impressao"));
                    tabela.getChildren().remove(barraSuperior);
                    trabalho.getJobSettings().setPageLayout(paisagem);
                    tabela.setMaxSize(largura, altura);
                    ObservableList itens = listaExtrato.getItems();
                    for(int i=0; i<itens.size();i+=18){
                        int end = (i+18<itens.size()) ? i+18 : itens.size();
                        listaExtrato.setItems(FXCollections.observableList(itens.subList(i, end)));
                        trabalho.printPage(tabela);
                    }
                    tabela.add(barraSuperior, 0, 0);
                    tabela.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    listaExtrato.setItems(itens);
                    aguarde.hide();
                }
                trabalho.endJob();
            }
        }catch(Exception ex){
            Janela.showMensagem(Status.ERRO, idioma.getMensagem("erro_imprimir"));
        }
    }
    
    private void prepararTipoTempo(){
        tipo.getItems().add(idioma.getMensagem("semanal_linhas"));
        tipo.getItems().add(idioma.getMensagem("mensal_linhas"));
        tipo.getItems().add(idioma.getMensagem("anual_linhas"));
        tipo.getItems().add(idioma.getMensagem("semanal_barras"));
        tipo.getItems().add(idioma.getMensagem("mensal_barras"));
        tipo.getItems().add(idioma.getMensagem("anual_barras"));
        tipo.getSelectionModel().select(1);
        tipo.setOnAction(e -> { carregarRelatorio(); });
    }
    
    public void prepararFiltro(){
        barraSuperior.getChildren().remove(atualizar);
        barraSuperior.getChildren().remove(tipo);
        if(relatorio.getSelectionModel().getSelectedItem().equals(idioma.getMensagem("despesas_tempo"))){
            barraSuperior.add(tipo, 9, 0);
            barraSuperior.add(atualizar, 10, 0);
        }else{
            barraSuperior.add(atualizar, 9, 0);
        }
        listaContaCartao.setOnAction(null);
        if(relatorio.getSelectionModel().getSelectedItem().equals(idioma.getMensagem("despesas_agendadas"))){
            labelContaCartao.setText(idioma.getMensagem("cartao_credito")+":");
            new CartaoCredito().montarSelectCategoria(listaContaCartao);
            CartaoCredito cartaoTodos = new CartaoCredito().setNome(idioma.getMensagem("sem_filtro"));
            listaContaCartao.getItems().add(new CartaoCredito().setNome(idioma.getMensagem("qualquer_cartao_credito")));
            listaContaCartao.getItems().add(new CartaoCredito().setNome(idioma.getMensagem("sem_cartao_credito")));
            listaContaCartao.getItems().add(cartaoTodos);
            listaContaCartao.getSelectionModel().select(cartaoTodos);
        }else{
            labelContaCartao.setText(idioma.getMensagem("conta")+":");
            new Conta().montarSelectCategoria(listaContaCartao);
            Conta contasTodas = new Conta().setNome(idioma.getMensagem("todas"));
            listaContaCartao.getItems().add(contasTodas);
            listaContaCartao.getSelectionModel().select(contasTodas);
        }
        listaContaCartao.setOnAction(e -> { carregarRelatorio(); });
        carregarRelatorio();
    }
    
    private void prepararTotais(){
        GridPane.setHalignment(labelTotalValor, HPos.CENTER);
        labelTotalValor.getStyleClass().add("labelValorTotal");
        panelValores.setAlignment(Pos.CENTER);
        panelValores.setSpacing(15);
        panelValores.getChildren().add(labelReceitasValor);
        panelValores.getChildren().add(labelDespesasValor);
        labelReceitasValor.getStyleClass().add("labelValorReceita");
        labelDespesasValor.getStyleClass().add("labelValorDespesa");
        labelSaldoValor.getStyleClass().add("labelValorSaldo");
    }
    
    public void carregarRelatorio(){
        removerGraficos();
        imprimir.setVisible(false);
        switch(relatorio.getSelectionModel().getSelectedIndex()){
            case 0:
                relatorioGraficoBarras(new Despesa().setAgendada("0"));
                break;
            case 1:
                relatorioListaDespesas();
                break;
            case 2:
                relatorioGraficoTempo();
                break;
            case 3:
                relatorioGraficoBarras(new Despesa().setAgendada("1"));
                break;
            case 4:
                relatorioGraficoBarras(new Receita());
                break;
            case 5:
                relatorioGraficoBarras(new Transferencia());
                break;
            case 6:
                relatorioExtrato();
                imprimir.setVisible(true);
                break;
            default:
                Janela.showMensagem(Status.ERRO, idioma.getMensagem("nao_encontrado"));
        }
    }
    
    private void removerGraficos(){
        tabela.getChildren().remove(graficoPrincipal);
        tabela.getChildren().remove(graficoSecundario);
        tabela.getChildren().remove(graficoTempoLinhas);
        tabela.getChildren().remove(graficoTempoEmpilhado);
        tabela.getChildren().remove(listaCategorias);
        tabela.getChildren().remove(listaDespesas);
        tabela.getChildren().remove(listaExtrato);
        tabela.getChildren().remove(panelValores);
        tabela.getChildren().remove(labelTotalValor);
        panelValores.getChildren().remove(labelTotalValor);
    }
    
    private String getValorTotal(ObservableList<Series<String,Number>> series){
        valorTotal = new BigDecimal("0.0");
        series.stream().forEach((serie) -> {
            serie.getData().stream().forEach((item) -> {
                valorTotal = valorTotal.add(new BigDecimal(item.getYValue().toString()));
            });
        });
        return idioma.getMensagem("moeda")+" "+valorTotal;
    }
    
    private Integer getTipoCategoria(){
        if(relatorio.getSelectionModel().getSelectedItem().equals(idioma.getMensagem("despesas_agendadas"))){
            if(listaContaCartao.getSelectionModel().getSelectedItem().getNome().equals(idioma.getMensagem("qualquer_cartao_credito"))){
                return 4;
            }else if(listaContaCartao.getSelectionModel().getSelectedItem().getNome().equals(idioma.getMensagem("sem_cartao_credito"))){
                return 3;
            }else{
                return 2;
            }
        }
        return 1;
    }
    
    private void relatorioListaDespesas(){
        tabela.add(listaDespesas, 0, 1);
        tabela.add(labelTotalValor, 0, 2);
        GridPane.setColumnSpan(listaDespesas, GridPane.REMAINING);
        GridPane.setColumnSpan(labelTotalValor, GridPane.REMAINING);
        ObservableList<Despesa> despesas = new Despesa().listarPeriodoAgrupado(inicio.getValue(), fim.getValue(), listaContaCartao.getSelectionModel().getSelectedItem());
        listaDespesas.setItems(despesas);
        Double totalDespesas = despesas.stream().mapToDouble(d -> d.getValor().doubleValue()).sum();
        labelTotalValor.setText(idioma.getMensagem("valor_total")+": "+Numeros.arredondar(totalDespesas));
    }
    
    private void relatorioExtrato(){
        panelValores.getChildren().add(labelTotalValor);
        tabela.add(listaExtrato, 0, 1);
        tabela.add(panelValores, 0, 2);
        GridPane.setColumnSpan(listaExtrato, GridPane.REMAINING);
        GridPane.setColumnSpan(panelValores, GridPane.REMAINING);
        ObservableList<Extrato> despesas = new Despesa().getExtrato(inicio.getValue(), fim.getValue(), listaContaCartao.getSelectionModel().getSelectedItem());
        ObservableList<Extrato> receitas = new Receita().getExtrato(inicio.getValue(), fim.getValue(), listaContaCartao.getSelectionModel().getSelectedItem());
        Double totalReceitas = receitas.stream().mapToDouble(r -> r.getValor().doubleValue()).sum();
        Double totalDespesas = despesas.stream().filter(d -> !d.getStatus().equals(idioma.getMensagem("agendado"))).mapToDouble(d -> d.getValor().doubleValue()).sum();
        Double totalDespesasAgendadas = despesas.stream().filter(d -> d.getStatus().equals(idioma.getMensagem("agendado"))).mapToDouble(d -> d.getValor().doubleValue()).sum();
        listaExtrato.setItems(despesas);
        listaExtrato.getItems().addAll(receitas);
        listaExtrato.getItems().sort((Extrato a, Extrato b) -> {
            return a.getDataHora().compareTo(b.getDataHora());
        });
        labelReceitasValor.setText(idioma.getMensagem("receitas")+": "+Numeros.arredondar(totalReceitas));
        labelDespesasValor.setText(idioma.getMensagem("despesas_confirmadas")+": "+Numeros.arredondar(totalDespesas));
        labelSaldoValor.setText(idioma.getMensagem("saldo")+": "+Numeros.arredondar(totalReceitas-totalDespesas));
        labelTotalValor.setText(idioma.getMensagem("despesas_aguardando_confirmacao")+": "+Numeros.arredondar(totalDespesasAgendadas));
        if(listaContaCartao.getSelectionModel().getSelectedItem().getNome().equals(idioma.getMensagem("todas"))){
            panelValores.getChildren().add(2,labelSaldoValor);
        }else{
            panelValores.getChildren().remove(labelSaldoValor);
        }
    }
    
    private void relatorioGraficoBarras(Grafico objeto){
        xAxisPrincipal.setLabel(idioma.getMensagem("valores")+" ("+idioma.getMensagem("moeda")+")");
        xAxisSecundario.setLabel(idioma.getMensagem("valores")+" ("+idioma.getMensagem("moeda")+")");
        ajustarColunas(50,50);
        tabela.add(graficoPrincipal, 0, 1);
        tabela.add(graficoSecundario, 1, 1);
        String id_categoria = listaContaCartao.getSelectionModel().getSelectedItem().getIdCategoria();
        Integer tipo_categoria = getTipoCategoria();
        graficoPrincipal.getData().setAll(objeto.getRelatorioMensalBarras(inicio.getValue(), fim.getValue(), null, id_categoria, tipo_categoria));
        graficoPrincipal.getData().stream().forEach((serie) -> {
            serie.getData().stream().forEach((item) -> {
                item.getNode().setCursor(Cursor.HAND);
                item.getNode().setOnMouseClicked((MouseEvent event) -> {
                    updateGraficoSecundario(serie.getName(), objeto);
                });
                eventosGrafico(item.getNode(), serie.getName()+" - "+idioma.getMensagem("moeda")+" "+item.getYValue());
            });
        });
        graficoPrincipal.setTitle(idioma.getMensagem("categorias")+" - "+getValorTotal(graficoPrincipal.getData()));
        graficoPrincipal.lookupAll("Label.chart-legend-item").stream().forEach((Node legenda) -> {
            legenda.setCursor(Cursor.HAND);
            legenda.setOnMouseClicked((MouseEvent event) -> {
                if(legenda instanceof Label){
                    Label texto = (Label) legenda;
                    updateGraficoSecundario(texto.getText(), objeto);
                }
            });
        });
        try{
            Series<String,Number> primeiraCategoria = graficoPrincipal.getData().get(0);
            updateGraficoSecundario(primeiraCategoria.getName(), objeto);
        }catch(Exception e){
            updateGraficoSecundario("", null);
        }
    }
    
    private void updateGraficoSecundario(String nomeCategoria, Grafico objeto){
        if(objeto!=null){
            String id_categoria = listaContaCartao.getSelectionModel().getSelectedItem().getIdCategoria();
            Integer tipo_categoria = getTipoCategoria();
            graficoSecundario.getData().setAll(objeto.getRelatorioMensalBarras(inicio.getValue(), fim.getValue(), nomeCategoria, id_categoria, tipo_categoria));
            graficoSecundario.getData().stream().forEach((serie) -> {
                serie.getData().stream().forEach((item) -> {
                    eventosGrafico(item.getNode(), serie.getName()+" - "+idioma.getMensagem("moeda")+" "+item.getYValue());
                });
            });
            graficoSecundario.setTitle(idioma.getMensagem("itens")+" / "+nomeCategoria+" - "+getValorTotal(graficoSecundario.getData()));
        }else{
            graficoSecundario.getData().clear();
            graficoSecundario.setTitle(idioma.getMensagem("itens")+" - "+getValorTotal(graficoSecundario.getData()));
        }
    }
    
    private void relatorioGraficoTempo(){   
        listaCategorias.setEditable(true);
        if(listaCategorias.getItems().size()<=0){
            ObservableList<DespesaCategoria> categorias = new DespesaCategoria().listar();
            listaCategorias.setItems(categorias);
        }
        listaCategorias.setCellFactory(CheckBoxListCell.forListView((DespesaCategoria param) -> {
            BooleanProperty observable = param.getSelecao();
            observable.addListener((obs, wasSelected, isNowSelected) -> gerarGraficoTempo() );
            return observable;
        }));
        ajustarColunas(25,75);
        tabela.add(listaCategorias, 0, 1);
        if(isBarras()){
            tabela.add(graficoTempoEmpilhado, 1, 1);
        }else{
            tabela.add(graficoTempoLinhas, 1, 1);
        }
        gerarGraficoTempo();
    }
    
    public void gerarGraficoTempo(){
        List<String> categoriasSelecionadas = new ArrayList<>();
        listaCategorias.getItems().stream().forEach((categoria) -> {
            if(categoria.isSelecionado()){
                categoriasSelecionadas.add(categoria.getIdCategoria());
            }
        });
        if(isBarras()){
            new Despesa().preencherRelatorioMensalTempoEmpilhado(graficoTempoEmpilhado, inicio.getValue(), fim.getValue(), categoriasSelecionadas, listaContaCartao.getSelectionModel().getSelectedItem(), tipo.getSelectionModel().getSelectedItem());
            graficoTempoEmpilhado.getData().stream().forEach((serie) -> {
                serie.getData().stream().forEach((item) -> {
                    eventosGrafico(item.getNode(), serie.getName()+" - "+idioma.getMensagem("moeda")+" "+item.getXValue());
                });
            });
        }else{
            new Despesa().preencherRelatorioMensalTempoLinhas(graficoTempoLinhas, inicio.getValue(), fim.getValue(), categoriasSelecionadas, listaContaCartao.getSelectionModel().getSelectedItem(), tipo.getSelectionModel().getSelectedItem());
            graficoTempoLinhas.getData().stream().forEach((serie) -> {
                serie.getData().stream().forEach((item) -> {
                    eventosGrafico(item.getNode(), serie.getName()+" - "+idioma.getMensagem("moeda")+" "+item.getYValue());
                });
            });
        }
    }
    
    private boolean isBarras(){
        String _tipo = tipo.getSelectionModel().getSelectedItem();
        return _tipo.equals(idioma.getMensagem("semanal_barras")) || _tipo.equals(idioma.getMensagem("mensal_barras")) || _tipo.equals(idioma.getMensagem("anual_barras"));
    }
    
    private void ajustarColunas(int coluna1, int coluna2){
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(coluna1);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(coluna2);
        tabela.getColumnConstraints().setAll(col1,col2);
    }
    
    private void eventosGrafico(Node node, String nome){
        Tooltip tooltip = new Tooltip(nome);
        node.setOnMouseEntered((MouseEvent event) -> {
            tooltip.show(node, event.getScreenX()+15, event.getScreenY());
        });
        node.setOnMouseExited((MouseEvent event) -> {
            tooltip.hide();
        });
    }

    @Override
    public void acaoVoltar() {
        Kernel.principal.acaoVoltar();
    }

    @Override
    public void acaoCadastrar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoAlterar(int tabela) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoExcluir(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoGerenciar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoFiltrar(Boolean animacao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoAjuda() {
        Ajuda.getInstance().setObjetos(voltar,labelRelatorio,relatorio,labelContaCartao,listaContaCartao,labelInicio,inicio,labelFim,fim,atualizar,graficoPrincipal,graficoSecundario,listaCategorias,graficoTempoLinhas,graficoTempoEmpilhado);
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_relat_1"));
        Ajuda.getInstance().capitulo(relatorio, Posicao.BAIXO, idioma.getMensagem("tuto_relat_2"));
        Ajuda.getInstance().capitulo(listaContaCartao, Posicao.BAIXO, idioma.getMensagem("tuto_relat_3"));
        Ajuda.getInstance().capitulo(inicio, Posicao.BAIXO, idioma.getMensagem("tuto_relat_4"));
        Ajuda.getInstance().capitulo(fim, Posicao.BAIXO, idioma.getMensagem("tuto_relat_5"));
        Ajuda.getInstance().capitulo(atualizar, Posicao.BAIXO, idioma.getMensagem("tuto_relat_6"));
        Ajuda.getInstance().apresentarProximo();
    }
    
}
