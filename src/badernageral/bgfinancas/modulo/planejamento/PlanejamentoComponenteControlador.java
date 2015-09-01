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

package badernageral.bgfinancas.modulo.planejamento;

import badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import badernageral.bgfinancas.biblioteca.contrato.Controlador;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFiltro;
import badernageral.bgfinancas.biblioteca.sistema.Botao;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.sistema.Tabela;
import badernageral.bgfinancas.biblioteca.tipo.Acao;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Posicao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.utilitario.AutoCompletarTextField;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import badernageral.bgfinancas.modelo.Planejamento;
import badernageral.bgfinancas.modelo.PlanejamentoComponente;
import badernageral.bgfinancas.modelo.PlanejamentoItem;
import badernageral.bgfinancas.template.modulo.ListaConta;
import badernageral.bgfinancas.template.modulo.ListaGrupo;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public final class PlanejamentoComponenteControlador implements Initializable, Controlador, ControladorFiltro {
    
    @FXML private Button voltar;
    @FXML private Label titulo;
    @FXML private Button anterior;
    @FXML private Button proximo;
    @FXML private Button atualizar;
    @FXML private Button cadastrar;
    @FXML private Button excluir;
    @FXML private TextField receita;
    @FXML private Label labelSalario;
    @FXML private Label labelItem;
    @FXML private Label labelPlanejamentoSalario;
    @FXML private Label labelPlanejamentoGastos;
    @FXML private Label labelPlanejamentoSaldo;
    @FXML private Button gerenciarItem;
    @FXML private GridPane barraSuperior;
    @FXML private GridPane barraInferior;
    private final AutoCompletarTextField<PlanejamentoItem> item = new AutoCompletarTextField<>(this,this);
    @FXML private ListaGrupo listaGrupoController;
    @FXML private ListaConta listaContaController;
    
    @FXML private StackPane tabelaPane;
    
    private LocalDate data = LocalDate.now();
    private Planejamento modelo;
       
    private final String TITULO = idioma.getMensagem("planejamento");
    
    private ObservableList<PlanejamentoComponente> itens;
    private final Tabela<PlanejamentoComponente> tabela = new Tabela<>();
    private final TableView<PlanejamentoComponente> tabelaLista = new TableView<>();
    
    private final List<PlanejamentoComponente> itensPlanejamento = new ArrayList<>();
    private final ObservableList<PlanejamentoComponente> areaTransferencia = FXCollections.observableList(itensPlanejamento);
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(TITULO);
        Botao.prepararBotao(new Button[]{cadastrar}, null, excluir, voltar);
        Botao.prepararBotaoGerenciar(new Button[]{gerenciarItem});
        Botao.prepararBotaoGerenciar(1, anterior, idioma.getMensagem("anterior"));
        Botao.prepararBotaoGerenciar(2, proximo, idioma.getMensagem("proximo"));
        labelSalario.setText(idioma.getMensagem("salario")+":");
        labelItem.setText(idioma.getMensagem("adicionar_item")+":");
        tabelaPane.getChildren().add(tabelaLista);
        tabela.prepararTabela(tabelaLista);
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("nome"), "nomeItem");
        tabela.setColunaDinheiro(tabela.adicionarColuna(tabelaLista, idioma.getMensagem("valor"), "valor"), false);
        item.getStyleClass().add("BotaoMeio");
        barraSuperior.add(item, 6, 0);
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
            tabelaLista.getSelectionModel().getSelectedItems().stream().forEach(pc -> {
               areaTransferencia.add(pc);
            });
        });
        colar.setOnAction(event -> {
            areaTransferencia.stream().forEach(pc -> {
                adicionarItem(pc);
            });
            acaoFiltrar(true);
        });
        menu.getItems().addAll(copiar,colar);
        tabelaLista.setContextMenu(menu);
    }
    
    public void acaoAtualizarSaldoReceita(){
        modelo.setValor(receita.getText());
        modelo.alterar();
        calcularSaldo();
    }
    
    @Override
    public void adicionar(String texto) {
        String nome;
        if(texto!=null && !texto.equals("")){
            nome = texto;
        }else{
            nome = item.getText();
        }
        item.setText("");
        PlanejamentoItem i = new PlanejamentoItem().setNome(nome).consultar();
        if(i!=null){
            try{
                for(PlanejamentoComponente p : tabelaLista.getItems()){
                    if(p.getIdItem().equals(i.getIdItem())){
                        throw new Erro();
                    }
                }
                String valor = new PlanejamentoItem().modal(Acao.CADASTRAR, idioma.getMensagem("valor")+":", "0.00");
                if(valor != null){
                    PlanejamentoComponente pc = new PlanejamentoComponente(modelo.getIdPlajenamento(), i.getIdItem(), valor);
                    pc.cadastrar();
                    acaoFiltrar(true);
                }
            }catch(Erro e){
                Janela.showTooltip(Status.ERRO, idioma.getMensagem("item_ja_existe"), Duracao.NORMAL);
            }
        }
    }
    
    private void adicionarItem(PlanejamentoComponente pc){
        try{
            for(PlanejamentoComponente p : tabelaLista.getItems()){
                if(p.getIdItem().equals(pc.getIdItem())){
                    throw new Erro();
                }
            }
            pc.setIdPlanejamento(modelo.getIdPlajenamento());
            pc.cadastrar();
        }catch(Erro e){
            Janela.showTooltip(Status.ERRO, idioma.getMensagem("item_ja_existe"), Duracao.NORMAL);
        }
    }
    
    private void atualizarTitulo(Boolean animacao){
        titulo.setText(TITULO+" - "+idioma.getNomeMes(data.getMonthValue()).substring(0, 3)+" / "+data.getYear());
        modelo = new Planejamento(data.getMonthValue(),data.getYear()).consultar();
        if(modelo==null){
            modelo = new Planejamento(data.getMonthValue(),data.getYear());
            modelo.cadastrar();
            modelo = modelo.consultar();
        }
        receita.setText(modelo.getValor());
        acaoFiltrar(animacao);
    }
    
    
    
    @Override
    public void acaoFiltrar(Boolean animacao){
        tabelaLista.setItems(new PlanejamentoComponente().setMes(data.getMonthValue()).setAno(data.getYear()).listar());
        listaGrupoController.atualizarTabela(animacao);
        listaContaController.atualizarTabela(animacao);
        item.setItens(new PlanejamentoItem().listar());
        calcularSaldo();
        if(animacao){
            Animacao.fadeOutIn(tabelaLista);
        }
    }
    
    private void calcularSaldo(){
        itens = tabelaLista.getItems();
        List<String> resultado = new PlanejamentoComponente().calcularSaldo(itens, modelo.getValor());
        labelPlanejamentoSalario.setText(idioma.getMensagem("salario")+": "+idioma.getMensagem("moeda")+" "+modelo.getValor()+"   ");
        labelPlanejamentoGastos.setText(idioma.getMensagem("gastos")+": "+idioma.getMensagem("moeda")+" "+resultado.get(0)+"   ");
        labelPlanejamentoSaldo.setText(idioma.getMensagem("saldo")+": "+idioma.getMensagem("moeda")+" "+resultado.get(1));
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        if(botao==1){
            String texto = item.getText();
            if(texto.equals("")){
                Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("campo_nao_informado"), item, Duracao.CURTA);
                item.requestFocus();
            }else{
                if(new PlanejamentoItem().setNome(texto).consultar()==null){
                    PlanejamentoItem p = new PlanejamentoItem(null, texto);
                    p.cadastrar();
                    Kernel.controlador.acaoFiltrar(true);
                }
                adicionar(texto);
            }
        }
    }
    
    @Override
    public void acaoAlterar(int tabela) {
        if(tabela==1){
            PlanejamentoComponente pc = tabelaLista.getSelectionModel().getSelectedItem();
            String valor = new PlanejamentoItem().modal(Acao.ALTERAR, idioma.getMensagem("valor")+":", pc.getValor());
            if(valor != null){
                pc.setValor(valor);
                pc.alterar();
                acaoFiltrar(false);
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
            itens.forEach((PlanejamentoComponente p) -> p.excluir());
            Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
            acaoFiltrar(true);
        }
    }

    @Override
    public void acaoGerenciar(int botao) {
        if(botao==1){
            Kernel.principal.acaoPlanejamentoItem();
        }else if(botao==2){
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
        Ajuda.getInstance().setObjetos(voltar,titulo,anterior,proximo,labelSalario,receita,atualizar,labelItem,item,cadastrar,excluir,gerenciarItem,tabelaLista,barraInferior,listaGrupoController.getGridPane(),listaContaController.getGridPane());
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_plan_1"));
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_plan_2"));
        Ajuda.getInstance().capitulo(titulo, Posicao.BAIXO, idioma.getMensagem("tuto_plan_3"));
        Ajuda.getInstance().capitulo(anterior, Posicao.BAIXO, idioma.getMensagem("tuto_plan_4"));
        Ajuda.getInstance().capitulo(proximo, Posicao.BAIXO, idioma.getMensagem("tuto_plan_5"));
        Ajuda.getInstance().capitulo(receita, Posicao.BAIXO, idioma.getMensagem("tuto_plan_6"));
        Ajuda.getInstance().capitulo(atualizar, Posicao.BAIXO, idioma.getMensagem("tuto_plan_7"));
        Ajuda.getInstance().capitulo(item, Posicao.BAIXO, idioma.getMensagem("tuto_plan_8"));
        Ajuda.getInstance().capitulo(cadastrar, Posicao.BAIXO, idioma.getMensagem("tuto_plan_9"));
        Ajuda.getInstance().capitulo(excluir, Posicao.BAIXO, idioma.getMensagem("tuto_plan_10"));
        Ajuda.getInstance().capitulo(gerenciarItem, Posicao.BAIXO, idioma.getMensagem("tuto_plan_11"));
        Ajuda.getInstance().capitulo(tabelaLista, Posicao.CENTRO, idioma.getMensagem("tuto_plan_12"));
        Ajuda.getInstance().capitulo(barraInferior, Posicao.TOPO, idioma.getMensagem("tuto_plan_13"));
        Ajuda.getInstance().capitulo(Arrays.asList(listaContaController.getGridPane(),listaGrupoController.getGridPane()), 
                Posicao.CENTRO, idioma.getMensagem("tuto_plan_14"));
        Ajuda.getInstance().apresentarProximo();
    }

}