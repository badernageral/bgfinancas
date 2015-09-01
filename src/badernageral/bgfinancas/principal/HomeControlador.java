/*
Copyright 2015 Jose Robson Mariano Alves

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
import badernageral.bgfinancas.biblioteca.tipo.Acao;
import badernageral.bgfinancas.biblioteca.tipo.Posicao;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.idioma.Linguagem;
import badernageral.bgfinancas.modelo.Agenda;
import badernageral.bgfinancas.modelo.Conta;
import badernageral.bgfinancas.modelo.Despesa;
import badernageral.bgfinancas.modelo.Grupo;
import badernageral.bgfinancas.modelo.Planejamento;
import badernageral.bgfinancas.modelo.PlanejamentoComponente;
import badernageral.bgfinancas.modelo.PlanejamentoItem;
import badernageral.bgfinancas.modelo.Receita;
import badernageral.bgfinancas.modelo.Transferencia;
import badernageral.bgfinancas.modulo.agenda.AgendaFormularioControlador;
import badernageral.bgfinancas.modulo.conta.ContaFormularioControlador;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public final class HomeControlador implements Initializable, Controlador {
    
    @FXML private GridPane home;
    
    @FXML private GridPane gridContas;
    @FXML private GridPane gridRelatorio;
    @FXML private GridPane gridPlanejamento;
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
    @FXML private Tab tabRelatorioDespesas;
    @FXML private Tab tabRelatorioReceitas;
    @FXML private Tab tabRelatorioTransferencias;
    @FXML private Tab tabRelatorioGrupos;
    @FXML private TableView<Despesa> tabelaListaRelatorioDespesas;
    @FXML private TableView<Receita> tabelaListaRelatorioReceitas;
    @FXML private TableView<Transferencia> tabelaListaRelatorioTransferencias;
    @FXML private TableView<Grupo> tabelaListaRelatorioGrupos;
    private final Tabela<Despesa> tabelaRelatorioDespesas = new Tabela<>();
    private final Tabela<Receita> tabelaRelatorioReceitas = new Tabela<>();
    private final Tabela<Transferencia> tabelaRelatorioTransferencias = new Tabela<>();
    private final Tabela<Grupo> tabelaGrupos = new Tabela<>();
    
    // Planejamento
    @FXML private Label labelPlanejamento;
    @FXML private Label labelPlanejamentoSalario;
    @FXML private Label labelPlanejamentoGastos;
    @FXML private Label labelPlanejamentoSaldo;
    @FXML private TableView<PlanejamentoComponente> tabelaListaPlanejamento;
    private final Tabela<PlanejamentoComponente> tabelaPlanejamento = new Tabela<>();
    
    // Agenda
    @FXML private Label labelAgenda;
    @FXML private TableView<Agenda> tabelaListaAgenda;
    private final Tabela<Agenda> tabelaAgenda = new Tabela<>();
    
    // Outros
    private LocalDate data = LocalDate.now();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(Linguagem.getInstance().getMensagem("principal"));
        inicializarContas();
        inicializarRelatorio();
        inicializarPlanejamento();
        inicializarAgenda();
        atualizarTabela(true);
    }
    
    private void inicializarContas(){
        tabelaConta.prepararTabela(tabelaListaConta, 1);
        tabelaConta.adicionarColuna(tabelaListaConta, idioma.getMensagem("nome"), "nome");
        tabelaConta.setColunaDinheiro(tabelaConta.adicionarColuna(tabelaListaConta, idioma.getMensagem("saldo"), "valor"), true);
        labelContas.setText(idioma.getMensagem("contas"));
    }
    
    private void inicializarPlanejamento(){
         tabelaPlanejamento.prepararTabela(tabelaListaPlanejamento, 2);
        tabelaPlanejamento.adicionarColuna(tabelaListaPlanejamento, idioma.getMensagem("nome"), "nomeItem");
        tabelaPlanejamento.setColunaDinheiro(tabelaPlanejamento.adicionarColuna(tabelaListaPlanejamento, idioma.getMensagem("valor"), "valor"), false);
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
        labelRelatorio.setText(idioma.getMensagem("mini_relatorio_mensal"));
        labelRelatorioData.setText(idioma.getNomeMes(LocalDate.now().getMonthValue()).substring(0,3)+" / "+LocalDate.now().getYear());
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
    }
    
    public void calcularSaldoContas(){
        BigDecimal valorPoupanca = tabelaListaConta.getItems().stream()
                .filter(c -> c.getSaldoTotal().equals(idioma.getMensagem("poupanca")))
                .map(c -> new BigDecimal(c.getValor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valorCredito = tabelaListaConta.getItems().stream()
                .filter(c -> c.getSaldoTotal().equals(idioma.getMensagem("credito")))
                .map(c -> new BigDecimal(c.getValor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        labelPoupancaTotal.setText(idioma.getMensagem("total_poupanca")+": "+idioma.getMensagem("moeda")+" "+valorPoupanca);
        labelCreditoTotal.setText(idioma.getMensagem("total_credito")+": "+idioma.getMensagem("moeda")+" "+valorCredito);
    }
    
    public void atualizarTabela(boolean animacao){
        atualizarContas();
        atualizarRelatorio();
        atualizarPlanejamento();
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
        tabelaListaRelatorioDespesas.setItems(new Despesa().getRelatorioMensal());
        tabelaListaRelatorioReceitas.setItems(new Receita().getRelatorioMensal());
        tabelaListaRelatorioTransferencias.setItems(new Transferencia().getRelatorioMensal());
        tabelaListaRelatorioGrupos.setItems(new Grupo().getRelatorio(null));
    }
    
    public void atualizarPlanejamento(){
        tabelaListaPlanejamento.setItems(new PlanejamentoComponente().setMes(data.getMonthValue()).setAno(data.getYear()).listar());
        labelPlanejamento.setText(idioma.getMensagem("planejamento")+" - "+idioma.getNomeMes(data.getMonthValue())+" / "+data.getYear());
        Planejamento modelo = new Planejamento(data.getMonthValue(),data.getYear()).consultar();
        String valor = "0.00";
        if(modelo!=null){
            valor = modelo.getValor();
        }
        List<String> resultado = new PlanejamentoComponente().calcularSaldo(tabelaListaPlanejamento.getItems(), valor);
        labelPlanejamentoSalario.setText(idioma.getMensagem("salario")+": "+idioma.getMensagem("moeda")+" "+valor+"   ");
        labelPlanejamentoGastos.setText(idioma.getMensagem("gastos")+": "+idioma.getMensagem("moeda")+" "+resultado.get(0)+"   ");
        labelPlanejamentoSaldo.setText(idioma.getMensagem("saldo")+": "+idioma.getMensagem("moeda")+" "+resultado.get(1));
    }
    
    private void atualizarAgenda(){
        tabelaListaAgenda.setItems(new Agenda().listar());
    }
    
    public void proximoPlanejamento(){
        data = data.plusMonths(1);
        atualizarPlanejamento();
    }
    
    public void anteriorPlanejamento(){
        data = data.minusMonths(1);
        atualizarPlanejamento();
    }
    
    public void acaoContas(){
        Kernel.principal.acaoConta();
    }
    
    public void acaoRelatorios(){
        Kernel.principal.acaoRelatorios();
    }
    
    public void acaoPlanejamento(){
        Kernel.principal.acaoPlanejamentoComponente();
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
            PlanejamentoComponente pc = tabelaListaPlanejamento.getSelectionModel().getSelectedItem();
            String valor = new PlanejamentoItem().modal(Acao.ALTERAR, idioma.getMensagem("valor")+":", pc.getValor());
            if(valor != null){
                pc.setValor(valor);
                pc.alterar();
                acaoFiltrar(false);
            }
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
        Ajuda.getInstance().setObjetos(gridContas, gridRelatorio, gridPlanejamento, gridAgenda);
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_home_1"));
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_home_2"));
        Ajuda.getInstance().capitulo(Posicao.TOPO, idioma.getMensagem("tuto_home_3"));
        Ajuda.getInstance().capitulo(gridContas, Posicao.BAIXO, idioma.getMensagem("tuto_home_4"));
        Ajuda.getInstance().capitulo(gridRelatorio, Posicao.BAIXO, idioma.getMensagem("tuto_home_5"));
        Ajuda.getInstance().capitulo(gridPlanejamento, Posicao.TOPO, idioma.getMensagem("tuto_home_6"));
        Ajuda.getInstance().capitulo(gridAgenda, Posicao.TOPO, idioma.getMensagem("tuto_home_7"));
        Ajuda.getInstance().apresentarProximo();
    }
    
}
