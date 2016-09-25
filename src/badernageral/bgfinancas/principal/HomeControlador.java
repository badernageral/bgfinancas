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

package badernageral.bgfinancas.principal;

import badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import badernageral.bgfinancas.biblioteca.contrato.Controlador;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.sistema.Tabela;
import badernageral.bgfinancas.biblioteca.tipo.Posicao;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.idioma.Linguagem;
import badernageral.bgfinancas.modelo.Agenda;
import badernageral.bgfinancas.modelo.Conta;
import badernageral.bgfinancas.modelo.Despesa;
import badernageral.bgfinancas.modelo.Grupo;
import badernageral.bgfinancas.modelo.Receita;
import badernageral.bgfinancas.modelo.Transferencia;
import badernageral.bgfinancas.modulo.agenda.AgendaFormularioControlador;
import badernageral.bgfinancas.modulo.conta.ContaFormularioControlador;
import badernageral.bgfinancas.modulo.despesa.DespesaFormularioControlador;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public final class HomeControlador implements Initializable, Controlador {
    
    @FXML private GridPane home;
    
    @FXML private GridPane gridContas;
    @FXML private GridPane gridRelatorio;
    @FXML private GridPane gridDespesasAgendadas;
    @FXML private GridPane gridAgenda;
    
    // Contas
    @FXML private Label labelContas;
    @FXML private Label labelCreditoTotal;
    @FXML private Label labelPoupancaTotal;
    @FXML private TableView<Conta> tabelaListaConta;
    private final Tabela<Conta> tabelaConta = new Tabela<>();
    
    // Relatório
    @FXML private Label labelRelatorio;
    @FXML private Label labelRelatorioData;
    @FXML private Label labelValorTotalRelatorio;
    @FXML private TabPane painelRelatorio;
    @FXML private Tab tabRelatorioDespesas;
    @FXML private Tab tabRelatorioReceitas;
    @FXML private Tab tabRelatorioTransferencias;
    @FXML private Tab tabRelatorioGrupos;
    @FXML private Tab tabRelatorioDespesasAgendadas;
    @FXML private TableView<Despesa> tabelaListaRelatorioDespesas;
    @FXML private TableView<Receita> tabelaListaRelatorioReceitas;
    @FXML private TableView<Transferencia> tabelaListaRelatorioTransferencias;
    @FXML private TableView<Grupo> tabelaListaRelatorioGrupos;
    @FXML private TableView<Despesa> tabelaListaRelatorioDespesasAgendadas;
    private final Tabela<Despesa> tabelaRelatorioDespesas = new Tabela<>();
    private final Tabela<Receita> tabelaRelatorioReceitas = new Tabela<>();
    private final Tabela<Transferencia> tabelaRelatorioTransferencias = new Tabela<>();
    private final Tabela<Grupo> tabelaGrupos = new Tabela<>();
    private final Tabela<Despesa> tabelaRelatorioDespesasAgendadas = new Tabela<>();
    private LocalDate dataRelatorio = LocalDate.now();
    private BigDecimal valorTotalRelatorio = BigDecimal.ZERO;
    
    // Despesas Agendadas
    @FXML private Label labelDespesasAgendadas;
    @FXML private Label labelDespesasAgendadasData;
    @FXML private Label labelDespesasAgendadasTotal;
    @FXML private Label labelDespesasAgendadasCredito;
    @FXML private TableView<Despesa> tabelaListaDespesasAgendadas;
    private final Tabela<Despesa> tabelaDespesasAgendadas = new Tabela<>();
    
    // Agenda
    @FXML private Label labelAgenda;
    @FXML private Label labelValorTotalAgenda;
    @FXML private TableView<Agenda> tabelaListaAgenda;
    private BigDecimal valorTotalAgenda = BigDecimal.ZERO;
    private final Tabela<Agenda> tabelaAgenda = new Tabela<>();
    
    // Outros
    private LocalDate data = LocalDate.now();
    private BigDecimal valorCredito;
    private BigDecimal valorPoupanca;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(Linguagem.getInstance().getMensagem("principal"));
        inicializarContas();
        inicializarRelatorio();
        inicializarDespesasAgendadas();
        inicializarAgenda();
        atualizarTabela(true);
    }
    
    private void inicializarContas(){
        tabelaConta.prepararTabela(tabelaListaConta, 1);
        prepararColunaTipoConta(tabelaConta.adicionarColuna(tabelaListaConta, "", "saldoTotal"));
        tabelaConta.adicionarColuna(tabelaListaConta, idioma.getMensagem("nome"), "nome");
        tabelaConta.setColunaDinheiro(tabelaConta.adicionarColuna(tabelaListaConta, idioma.getMensagem("saldo"), "valor"), true);
        labelContas.setText(idioma.getMensagem("contas"));
        
    }
    
    private void prepararColunaTipoConta(TableColumn colunaTipoConta){
        colunaTipoConta.setMinWidth(35);
        colunaTipoConta.setMaxWidth(35);
        colunaTipoConta.setCellFactory(coluna -> {
            return new TableCell<Conta, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        if(item.equals(idioma.getMensagem("credito"))){
                            setStyle("-fx-background-image: url(\"badernageral/bgfinancas/recursos/imagem/outros/credito.png\");-fx-background-repeat: no-repeat;-fx-background-position: center center");
                        }else{
                            setStyle("-fx-background-image: url(\"badernageral/bgfinancas/recursos/imagem/outros/poupanca.png\");-fx-background-repeat: no-repeat;-fx-background-position: center center");
                        }
                    }
                }
            };
        });
    }
    
    private void inicializarDespesasAgendadas(){
        labelDespesasAgendadas.setText(idioma.getMensagem("despesas_agendadas"));
        tabelaDespesasAgendadas.prepararTabela(tabelaListaDespesasAgendadas, 2);
        tabelaDespesasAgendadas.adicionarColuna(tabelaListaDespesasAgendadas, idioma.getMensagem("nome"), "nomeItem").setMinWidth(150);
        tabelaDespesasAgendadas.setColunaDinheiro(tabelaDespesasAgendadas.adicionarColuna(tabelaListaDespesasAgendadas, idioma.getMensagem("valor"), "valor"), false);
        tabelaDespesasAgendadas.adicionarColuna(tabelaListaDespesasAgendadas, idioma.getMensagem("data"), "data");
    }
    
    private void inicializarAgenda(){
        tabelaAgenda.prepararTabela(tabelaListaAgenda, 6);
        tabelaAgenda.adicionarColuna(tabelaListaAgenda, idioma.getMensagem("tipo"), "nomeCategoria");
        tabelaAgenda.adicionarColuna(tabelaListaAgenda, idioma.getMensagem("descricao"), "nome");
        tabelaAgenda.adicionarColuna(tabelaListaAgenda, idioma.getMensagem("data"), "data");
        tabelaAgenda.setColunaDinheiro(tabelaAgenda.adicionarColuna(tabelaListaAgenda, idioma.getMensagem("valor"), "valor"), true);
        labelAgenda.setText(idioma.getMensagem("lembretes"));
    }
    
    private void inicializarRelatorio(){
        painelRelatorio.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            atualizarValorTotalRelatorio(newValue);
        });
        labelRelatorio.setText(idioma.getMensagem("mini_relatorio_mensal"));
        tabRelatorioDespesas.setText(idioma.getMensagem("despesas"));
        Image imagemDespesas = new Image("/badernageral/bgfinancas/recursos/imagem/modulo/icon/despesas.png");
        ImageView iconeDespesas = new ImageView(imagemDespesas);
        tabRelatorioDespesas.setGraphic(iconeDespesas);
        tabRelatorioReceitas.setText(idioma.getMensagem("receitas"));
        Image imagemReceitas = new Image("/badernageral/bgfinancas/recursos/imagem/modulo/icon/receitas.png");
        ImageView iconeReceitas = new ImageView(imagemReceitas);
        tabRelatorioReceitas.setGraphic(iconeReceitas);
        tabRelatorioTransferencias.setText(idioma.getMensagem("transferencias"));
        Image imagemTransferencias = new Image("/badernageral/bgfinancas/recursos/imagem/modulo/icon/transferencias.png");
        ImageView iconeTransferencias = new ImageView(imagemTransferencias);
        tabRelatorioTransferencias.setGraphic(iconeTransferencias);
        tabRelatorioGrupos.setText(idioma.getMensagem("cotas_despesas"));
        Image imagemGrupos = new Image("/badernageral/bgfinancas/recursos/imagem/modulo/icon/grupos.png");
        ImageView iconeGrupos = new ImageView(imagemGrupos);
        tabRelatorioGrupos.setGraphic(iconeGrupos);
        tabRelatorioDespesasAgendadas.setText(idioma.getMensagem("despesas_agendadas"));
        Image imagemDespesasAgendadas = new Image("/badernageral/bgfinancas/recursos/imagem/modulo/icon/despesa_agendada.png");
        ImageView iconeDespesasAgendadas = new ImageView(imagemDespesasAgendadas);
        tabRelatorioDespesasAgendadas.setGraphic(iconeDespesasAgendadas);
        tabelaRelatorioDespesas.prepararTabela(tabelaListaRelatorioDespesas, 3);
        tabelaRelatorioDespesas.adicionarColuna(tabelaListaRelatorioDespesas, idioma.getMensagem("categoria"), "nomeCategoria");
        tabelaRelatorioDespesas.setColunaDinheiro(tabelaRelatorioDespesas.adicionarColuna(tabelaListaRelatorioDespesas, idioma.getMensagem("valor"), "valor"), true);
        tabelaRelatorioReceitas.prepararTabela(tabelaListaRelatorioReceitas, 3);
        tabelaRelatorioReceitas.adicionarColuna(tabelaListaRelatorioReceitas, idioma.getMensagem("categoria"), "nomeCategoria");
        tabelaRelatorioReceitas.setColunaDinheiro(tabelaRelatorioReceitas.adicionarColuna(tabelaListaRelatorioReceitas, idioma.getMensagem("valor"), "valor"), true);
        tabelaRelatorioTransferencias.prepararTabela(tabelaListaRelatorioTransferencias, 3);
        tabelaRelatorioTransferencias.adicionarColuna(tabelaListaRelatorioTransferencias, idioma.getMensagem("categoria"), "nomeCategoria");
        tabelaRelatorioTransferencias.setColunaDinheiro(tabelaRelatorioTransferencias.adicionarColuna(tabelaListaRelatorioTransferencias, idioma.getMensagem("valor"), "valor"), true);
        tabelaGrupos.prepararTabela(tabelaListaRelatorioGrupos, 3);
        tabelaGrupos.adicionarColuna(tabelaListaRelatorioGrupos, idioma.getMensagem("cota"), "nome");
        tabelaGrupos.setColunaDinheiro(tabelaGrupos.adicionarColuna(tabelaListaRelatorioGrupos, idioma.getMensagem("valor"), "valor"), false);
        tabelaGrupos.setColunaDinheiro(tabelaGrupos.adicionarColuna(tabelaListaRelatorioGrupos, idioma.getMensagem("saldo"), "saldo"), true);
        tabelaRelatorioDespesasAgendadas.prepararTabela(tabelaListaRelatorioDespesasAgendadas, 3);
        tabelaRelatorioDespesasAgendadas.adicionarColuna(tabelaListaRelatorioDespesasAgendadas, idioma.getMensagem("categoria"), "nomeCategoria");
        tabelaRelatorioDespesasAgendadas.setColunaDinheiro(tabelaRelatorioDespesasAgendadas.adicionarColuna(tabelaListaRelatorioDespesasAgendadas, idioma.getMensagem("valor"), "valor"), true);
    }
    
    private void calcularSaldoContas(){
        valorPoupanca = tabelaListaConta.getItems().stream()
                .filter(c -> c.getSaldoTotal().equals(idioma.getMensagem("poupanca")))
                .map(c -> new BigDecimal(c.getValor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        valorCredito = tabelaListaConta.getItems().stream()
                .filter(c -> c.getSaldoTotal().equals(idioma.getMensagem("credito")))
                .map(c -> new BigDecimal(c.getValor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        labelPoupancaTotal.setText(idioma.getMensagem("total_poupanca")+": "+idioma.getMensagem("moeda")+" "+valorPoupanca);
        labelCreditoTotal.setText(idioma.getMensagem("total_credito")+": "+idioma.getMensagem("moeda")+" "+valorCredito);
    }
    
    private void calcularValorDespesasAgendadas(){
        BigDecimal valorTotal = tabelaListaDespesasAgendadas.getItems().stream()
                .map(d -> new BigDecimal(d.getValor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal creditoRestante = valorCredito.subtract(valorTotal);
        labelDespesasAgendadasTotal.setText(idioma.getMensagem("despesas")+": "+idioma.getMensagem("moeda")+" "+valorTotal.toString());
        labelDespesasAgendadasCredito.setText(idioma.getMensagem("credito_restante")+": "+idioma.getMensagem("moeda")+" "+creditoRestante.toString());
    }
    
    public void atualizarTabela(boolean animacao){
        atualizarContas();
        atualizarRelatorio();
        atualizarDespesasAgendadas();
        atualizarAgenda();
        if(animacao){
            Animacao.fadeOutIn(home);
        }
    }
    
    private void atualizarContas(){
        tabelaListaConta.setItems(new Conta().setAtivada("1").listar());
        calcularSaldoContas();
    }
    
    private void atualizarRelatorio(){
        labelRelatorioData.setText(idioma.getNomeMes(dataRelatorio.getMonthValue()).substring(0,3)+" / "+dataRelatorio.getYear());
        tabelaListaRelatorioDespesas.setItems(new Despesa().getRelatorioMensal(dataRelatorio));
        tabelaListaRelatorioReceitas.setItems(new Receita().getRelatorioMensal(dataRelatorio));
        tabelaListaRelatorioTransferencias.setItems(new Transferencia().getRelatorioMensal(dataRelatorio));
        tabelaListaRelatorioGrupos.setItems(new Grupo().getRelatorio(dataRelatorio, null));
        tabelaListaRelatorioDespesasAgendadas.setItems(new Despesa().getRelatorioMensal(dataRelatorio,true));
        atualizarValorTotalRelatorio(painelRelatorio.getSelectionModel().getSelectedIndex());
    }
    
    private void atualizarValorTotalRelatorio(Number aba){
        labelValorTotalRelatorio.setVisible(true);
        valorTotalRelatorio = BigDecimal.ZERO;
        switch(aba.intValue()){
            case 0:
                tabelaListaRelatorioDespesas.getItems().stream().forEach(item -> {
                    valorTotalRelatorio = valorTotalRelatorio.add(new BigDecimal(item.getValor()));
                });
                break;
            case 1:
                tabelaListaRelatorioReceitas.getItems().stream().forEach(item -> {
                    valorTotalRelatorio = valorTotalRelatorio.add(new BigDecimal(item.getValor()));
                });
                break;
            case 2:
                tabelaListaRelatorioTransferencias.getItems().stream().forEach(item -> {
                    valorTotalRelatorio = valorTotalRelatorio.add(new BigDecimal(item.getValor()));
                });
                break;
            default:
                labelValorTotalRelatorio.setVisible(false);
                break;
        }
        labelValorTotalRelatorio.setText(idioma.getMensagem("moeda")+" "+valorTotalRelatorio.toString());
    }
    
    public void atualizarDespesasAgendadas(){
        tabelaListaDespesasAgendadas.setItems(new Despesa().setSomenteAgendamento().setMesAno(data.getMonthValue(), data.getYear()).listar());
        labelDespesasAgendadasData.setText(idioma.getNomeMes(data.getMonthValue()).substring(0,3)+" / "+data.getYear());
        calcularValorDespesasAgendadas();
    }
    
    private void atualizarAgenda(){
        tabelaListaAgenda.setItems(new Agenda().listar());
        tabelaListaAgenda.getItems().stream().forEach(item -> {
            valorTotalAgenda = valorTotalAgenda.add(new BigDecimal(item.getValor()));
        });
        labelValorTotalAgenda.setText(idioma.getMensagem("moeda")+" "+valorTotalAgenda.toString());
    }
    
    public void proximoMesRelatorio(){
        dataRelatorio = dataRelatorio.plusMonths(1);
        atualizarRelatorio();
    }
    
    public void anteriorMesRelatorio(){
        dataRelatorio = dataRelatorio.minusMonths(1);
        atualizarRelatorio();
    }
    
    public void proximoMesDespesas(){
        data = data.plusMonths(1);
        atualizarDespesasAgendadas();
    }
    
    public void anteriorMesDespesas(){
        data = data.minusMonths(1);
        atualizarDespesasAgendadas();
    }
    
    public void acaoContas(){
        Kernel.principal.acaoConta();
    }
    
    public void acaoRelatorios(){
        Kernel.principal.acaoRelatorios();
    }
    
    public void acaoDespesasAgendadas(){
        Kernel.principal.acaoDespesasAgendadas();
    }
    
    public void acaoAgenda(){
        Kernel.principal.acaoAgenda();
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoAlterar(int tabela) {
        if(tabela==1){
            ObservableList<Conta> itens = tabelaListaConta.getSelectionModel().getSelectedItems();
            ContaFormularioControlador Controlador = Janela.abrir(Conta.FXML_FORMULARIO, idioma.getMensagem("contas"));
            Controlador.alterar(itens.get(0));
        }else if(tabela==6){
            ObservableList<Agenda> itens = tabelaListaAgenda.getSelectionModel().getSelectedItems();
            AgendaFormularioControlador Controlador = Janela.abrir(Agenda.FXML_FORMULARIO, idioma.getMensagem("lembretes"));
            Controlador.alterar(itens.get(0));
        }else if(tabela==2){
            ObservableList<Despesa> itens = tabelaListaDespesasAgendadas.getSelectionModel().getSelectedItems();
            DespesaFormularioControlador Controlador = Janela.abrir(Despesa.FXML_FORMULARIO, idioma.getMensagem("despesas_agendadas"));
            Controlador.alterar(itens.get(0));
        }else{
            System.out.println(idioma.getMensagem("nao_implementado")+tabela);
        }
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
    public void acaoVoltar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoFiltrar(Boolean animacao) {
        atualizarTabela(true);
    }

    @Override
    public void acaoAjuda() {
        Ajuda.getInstance().setObjetos(gridContas, gridRelatorio, gridDespesasAgendadas, gridAgenda);
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_home_1"));
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_home_2"));
        Ajuda.getInstance().capitulo(Posicao.TOPO, idioma.getMensagem("tuto_home_3"));
        Ajuda.getInstance().capitulo(gridContas, Posicao.BAIXO, idioma.getMensagem("tuto_home_4"));
        Ajuda.getInstance().capitulo(gridRelatorio, Posicao.BAIXO, idioma.getMensagem("tuto_home_5"));
        Ajuda.getInstance().capitulo(gridDespesasAgendadas, Posicao.TOPO, idioma.getMensagem("tuto_home_6"));
        Ajuda.getInstance().capitulo(gridAgenda, Posicao.TOPO, idioma.getMensagem("tuto_home_7"));
        Ajuda.getInstance().apresentarProximo();
    }
    
}
