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
package io.github.badernageral.bgfinancas.modulo.planejamento;

import io.github.badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Controlador;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Tabela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Operacao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Posicao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.AreaTransferencia;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Numeros;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.modelo.CartaoCredito;
import io.github.badernageral.bgfinancas.modelo.Conta;
import io.github.badernageral.bgfinancas.modelo.Despesa;
import io.github.badernageral.bgfinancas.modelo.Planejamento;
import io.github.badernageral.bgfinancas.modelo.Receita;
import io.github.badernageral.bgfinancas.modulo.despesa.DespesaFormularioControlador;
import io.github.badernageral.bgfinancas.modulo.receita.ReceitaFormularioControlador;
import io.github.badernageral.bgfinancas.template.modulo.ListaConta;
import io.github.badernageral.bgfinancas.template.modulo.ListaGrupo;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

public final class PlanejamentoControlador implements Initializable, Controlador {

    @FXML private Button voltar;
    @FXML private Label titulo;
    @FXML private Button anterior;
    @FXML private Button proximo;
    @FXML private Label labelCartaoCredito;
    @FXML private Button cadastrarDespesa;
    @FXML private Button cadastrarReceita;
    @FXML private Button alterar;
    @FXML private Button excluir;
    @FXML private Button pagar;
    @FXML private Label labelTotalDespesas;
    @FXML private Label labelTotalReceitas;
    @FXML private Label labelSaldo;
    @FXML private GridPane barraSuperior;
    @FXML private GridPane barraInferior;
    @FXML private ListaGrupo listaGrupoController;
    @FXML private ListaConta listaContaController;
    @FXML private ComboBox<Categoria> listaCartaoCredito;

    @FXML private StackPane tabelaPane;

    private LocalDate data = LocalDate.now();
    private Planejamento modelo;

    private final String TITULO = idioma.getMensagem("planejamento");

    private ObservableList<Planejamento> itens;
    private final Tabela<Planejamento> tabela = new Tabela<>();
    private final TableView<Planejamento> tabelaLista = new TableView<>();
    private final List<Planejamento> itensPlanejamento = new ArrayList<>();
    
    private final AreaTransferencia<Planejamento> areaTransferencia = new AreaTransferencia();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(TITULO);
        Botao.prepararBotao(new Button[]{cadastrarDespesa}, alterar, excluir, voltar);
        Botao.prepararBotaoCadastrar(cadastrarReceita, 2);
        cadastrarDespesa.setTooltip(new Tooltip(idioma.getMensagem("cadastrar_despesa")+" (CTRL+I)"));
        cadastrarReceita.setTooltip(new Tooltip(idioma.getMensagem("cadastrar_receita")+" (CTRL+SHIFT+I)"));
        Botao.prepararBotaoGerenciar(1, anterior, idioma.getMensagem("anterior"));
        Botao.prepararBotaoGerenciar(2, proximo, idioma.getMensagem("proximo"));
        Botao.prepararBotaoGerenciar(3, pagar, idioma.getMensagem("confirmar"));
        labelCartaoCredito.setText(idioma.getMensagem("cartao_credito") + ":");
        tabelaPane.getChildren().add(tabelaLista);
        tabela.prepararTabela(tabelaLista);
        Planejamento.prepararColunaTipo(tabela.adicionarColuna(tabelaLista, "", "isDespesa"));
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("categoria"), "nomeCategoria");
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("item"), "nomeItem").setMinWidth(120);
        tabela.adicionarColunaNumero(tabelaLista, idioma.getMensagem("valor"), "valor");
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("conta"), "nomeConta");
        tabela.adicionarColunaData(tabelaLista, idioma.getMensagem("data"), "data");
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("cartao_credito"), "nomeCartaoCredito");
        new CartaoCredito().montarSelectCategoria(listaCartaoCredito);
        CartaoCredito cartaoSemCartao = new CartaoCredito().setIdCategoria("NULL").setNome(idioma.getMensagem("sem_cartao_credito"));
        CartaoCredito cartaoSomenteCartao = new CartaoCredito().setIdCategoria("NOTNULL").setNome(idioma.getMensagem("somente_cartao_credito"));
        CartaoCredito cartaoTodos = new CartaoCredito().setNome(idioma.getMensagem("todos"));
        listaCartaoCredito.getItems().add(cartaoSemCartao);
        listaCartaoCredito.getItems().add(cartaoSomenteCartao);
        listaCartaoCredito.getItems().add(cartaoTodos);
        listaCartaoCredito.getSelectionModel().select(cartaoTodos);
        listaCartaoCredito.setOnAction(e -> {
            acaoFiltrar(true);
        });
        atualizarTitulo(false);
        areaTransferencia.criarMenu(this, tabelaLista, true);
    }

    private void atualizarTitulo(Boolean animacao) {
        titulo.setText(TITULO + " - " + idioma.getNomeMes(data.getMonthValue()) + " / " + data.getYear());
        acaoFiltrar(animacao);
    }

    @Override
    public void acaoFiltrar(Boolean animacao) {
        tabelaLista.setItems(new Planejamento().listar(data.getMonthValue(), data.getYear(), listaCartaoCredito.getSelectionModel().getSelectedItem()));
        listaGrupoController.atualizarTabela(animacao);
        listaContaController.atualizarTabela(animacao);
        calcularSaldo();
        if (animacao) {
            Animacao.fadeOutIn(tabelaLista);
        }
    }

    private void calcularSaldo() {
        BigDecimal totalReceitas = tabelaLista.getItems().stream()
                .filter(p -> !p.isDespesa())
                .map(d -> d.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDespesas = tabelaLista.getItems().stream()
                .filter(p -> p.isDespesa())
                .map(d -> d.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        labelTotalReceitas.setText(idioma.getMensagem("receitas") + ": " + idioma.getMensagem("moeda") + " " + totalReceitas.toString());
        labelTotalDespesas.setText(idioma.getMensagem("despesas") + ": " + idioma.getMensagem("moeda") + " " + totalDespesas.toString());
        labelSaldo.setText(idioma.getMensagem("saldo") + ": " + idioma.getMensagem("moeda") + " " + Numeros.arredondar(totalReceitas.subtract(totalDespesas)));
    }

    @Override
    public void acaoCadastrar(int botao) {
        if (botao == 1) {
            DespesaFormularioControlador Controlador = Janela.abrir(Despesa.FXML_FORMULARIO, TITULO);
            Controlador.cadastrar(this);
        }else if(botao==2){
            ReceitaFormularioControlador Controlador = Janela.abrir(Receita.FXML_FORMULARIO, TITULO);
            Controlador.cadastrar(this);
        }
    }

    @Override
    public void acaoAlterar(int tabela) {
        switch (tabela) {
            case 1:
                itens = tabelaLista.getSelectionModel().getSelectedItems();
                if (Validar.alteracao(itens, alterar)) {
                    if(itens.get(0).isDespesa()){
                        DespesaFormularioControlador Controlador = Janela.abrir(Despesa.FXML_FORMULARIO, TITULO);
                        Controlador.alterar(itens.get(0).getDespesa());
                    }else{
                        ReceitaFormularioControlador Controlador = Janela.abrir(Receita.FXML_FORMULARIO, TITULO);
                        Controlador.alterar(itens.get(0).getReceita());
                    }
                }   break;
            case 2:
                listaContaController.alterarConta(TITULO);
                break;
            case 3:
                listaGrupoController.alterarGrupo(TITULO);
                break;
            default:
                break;
        }
    }

    @Override
    public void acaoExcluir(int botao) {
        itens = tabelaLista.getSelectionModel().getSelectedItems();
        if (Validar.exclusao(itens, excluir)) {
            itens.forEach((Planejamento p) -> p.excluir());
            Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
            acaoFiltrar(true);
        }
    }
    
    public Categoria getCartaoCredito(){
        return listaCartaoCredito.getSelectionModel().getSelectedItem();
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(int mes, int ano) {
        data = data.withMonth(mes);
        data = data.withYear(ano);
        atualizarTitulo(true);
    }

    @Override
    public void acaoGerenciar(int botao) {
        switch (botao) {
            case 2:
                data = data.minusMonths(1);
                atualizarTitulo(true);
                break;
            case 3:
                data = data.plusMonths(1);
                atualizarTitulo(true);
                break;
            case 4:
                itens = tabelaLista.getSelectionModel().getSelectedItems();
                if(itens.size()<=0 || itens.get(0)==null){
                    Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("selecione_item_pagar"), alterar, Duracao.NORMAL);
                }else{
                    String[] valor = modalPagar();
                    if(valor[0] != null && valor[1] != null){
                        itens.forEach((Planejamento p) -> {
                            p.setIdConta(valor[0]);
                            p.setData(valor[1]);
                            p.setAgendada("0");
                            p.alterar();
                            if(p.isDespesa()){
                                new Conta().alterarSaldo(Operacao.DECREMENTAR, p.getIdConta(), p.getValor().toString());
                            }else{
                                new Conta().alterarSaldo(Operacao.INCREMENTAR, p.getIdConta(), p.getValor().toString());
                            }
                        });
                        Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                        acaoFiltrar(true);
                    }
                }   
                break;
            default:
                break;
        }
        areaTransferencia.setData(data);
    }
    
    private String[] modalPagar(){
        ModalPagarControlador janela = Janela.abrir(Planejamento.FXML_MODAL_PAGAR, TITULO, true);
        janela.setTitulo(idioma.getMensagem("confirmar"));
        Planejamento p = tabelaLista.getSelectionModel().getSelectedItems().get(0);
        janela.setValor(new Conta().setIdCategoria(p.getIdConta()).setNome(p.getNomeConta()), p.getData());
        Kernel.palcoModal.showAndWait();
        return janela.getResultado();
    }

    @Override
    public void acaoVoltar() {
        Kernel.principal.acaoVoltar();
    }

    @Override
    public void acaoAjuda() {
        Ajuda.getInstance().setObjetos(voltar, titulo, anterior, proximo, labelCartaoCredito, listaCartaoCredito, cadastrarDespesa, cadastrarReceita, alterar, excluir, pagar, tabelaLista, barraInferior, listaGrupoController.getGridPane(), listaContaController.getGridPane());
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tudo_plan_1"));
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tudo_plan_2"));
        Ajuda.getInstance().capitulo(titulo, Posicao.BAIXO, idioma.getMensagem("tudo_plan_3"));
        Ajuda.getInstance().capitulo(anterior, Posicao.BAIXO, idioma.getMensagem("tudo_plan_4"));
        Ajuda.getInstance().capitulo(proximo, Posicao.BAIXO, idioma.getMensagem("tudo_plan_5"));
        Ajuda.getInstance().capitulo(listaCartaoCredito, Posicao.BAIXO, idioma.getMensagem("tudo_plan_6"));
        Ajuda.getInstance().capitulo(cadastrarDespesa, Posicao.BAIXO, idioma.getMensagem("tudo_plan_7"));
        Ajuda.getInstance().capitulo(cadastrarReceita, Posicao.BAIXO, idioma.getMensagem("tudo_plan_8"));
        Ajuda.getInstance().capitulo(alterar, Posicao.BAIXO, idioma.getMensagem("tudo_plan_9"));
        Ajuda.getInstance().capitulo(excluir, Posicao.BAIXO, idioma.getMensagem("tudo_plan_10"));
        Ajuda.getInstance().capitulo(pagar, Posicao.BAIXO, idioma.getMensagem("tudo_plan_11"));
        Ajuda.getInstance().capitulo(tabelaLista, Posicao.CENTRO, idioma.getMensagem("tudo_plan_12"));
        Ajuda.getInstance().capitulo(barraInferior, Posicao.TOPO, idioma.getMensagem("tudo_plan_13"));
        Ajuda.getInstance().capitulo(Arrays.asList(listaContaController.getGridPane(), listaGrupoController.getGridPane()),
                Posicao.CENTRO, idioma.getMensagem("tudo_plan_14"));
        Ajuda.getInstance().apresentarProximo();
    }

}
