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

package io.github.badernageral.bgfinancas.modulo.grupo;

import io.github.badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Controlador;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Tabela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Posicao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.modelo.DespesaCategoria;
import io.github.badernageral.bgfinancas.modelo.Grupo;
import io.github.badernageral.bgfinancas.modelo.GrupoItem;
import io.github.badernageral.bgfinancas.template.barra.BarraPadrao;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

public final class GrupoItemControlador implements Initializable, Controlador {
    
    @FXML private GridPane barraSuperior;
    @FXML private Label labelCategoria;
    @FXML private ComboBox<Categoria> categoria;
    @FXML private Button cadastrar;
    @FXML private Button excluir;
    @FXML private BarraPadrao barraController;
    
    @FXML private TableView<Grupo> listaGrupos;
    private ObservableList<Grupo> itensGrupo;
    private final Tabela<Grupo> tabelaGrupo = new Tabela<>();
    
    @FXML private StackPane tabelaPane;
    
    private final String TITULO = idioma.getMensagem("cotas_despesas");
    
    private ObservableList<GrupoItem> itens;
    private final Tabela<GrupoItem> tabela = new Tabela<>();
    private final TableView<GrupoItem> tabelaLista = new TableView<>();;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(TITULO);
        barraController.getLabelTitulo().setText(TITULO);
        Botao.prepararBotaoCadastrar(cadastrar, 2);
        Botao.prepararBotaoExcluir(excluir, 2);
        labelCategoria.setText(idioma.getMensagem("adicionar_categoria")+":");
        tabelaGrupo.prepararTabelaSelecao(listaGrupos, 1);
        tabelaGrupo.adicionarColuna(listaGrupos, idioma.getMensagem("cota"), "nome");
        tabelaGrupo.adicionarColunaNumero(listaGrupos, idioma.getMensagem("valor"), "valor");
        tabelaGrupo.setColunaColorida(tabelaGrupo.adicionarColunaNumero(listaGrupos, idioma.getMensagem("saldo"), "saldo"));
        tabelaPane.getChildren().add(tabelaLista);
        tabela.prepararTabela(tabelaLista, 2);
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("despesa")+" / "+idioma.getMensagem("categoria"), "nomeItem");
        acaoFiltrar(false);
        new DespesaCategoria().montarSelectCategoria(categoria);
    }
    
    public void acaoGrupoSelecionado(){
        Categoria c = listaGrupos.getSelectionModel().getSelectedItem();
        if(c!=null){
            labelCategoria.setDisable(false);
            categoria.setDisable(false);
            cadastrar.setDisable(false);
            excluir.setDisable(false);
            tabelaLista.setDisable(false);
            categoria.getSelectionModel().select(-1);
            tabelaLista.setItems(new GrupoItem().setIdCategoria(c.getIdCategoria()).listar());
        }else{
            labelCategoria.setDisable(true);
            categoria.setDisable(true);
            cadastrar.setDisable(true);
            excluir.setDisable(true);
            tabelaLista.setDisable(true);
        }
    }
    
    @Override
    public void acaoFiltrar(Boolean animacao){
        atualizarGrupos();
        acaoGrupoSelecionado();
        if(animacao){
            Animacao.fadeOutIn(tabelaLista);
        }
    }
    
    public void atualizarGrupos(){
        Grupo grupoSelecionado = listaGrupos.getSelectionModel().getSelectedItem();
        LocalDate hoje = LocalDate.now();
        listaGrupos.setItems(new Grupo().getRelatorio(hoje,barraController.getFiltro().getText()));
        listaGrupos.getSelectionModel().select(grupoSelecionado);
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        if(botao==1){
            GrupoFormularioControlador Controlador = Janela.abrir(Grupo.FXML_FORMULARIO, TITULO);
            Controlador.cadastrar(null);
        }else if(botao==2){
            try{
                Validar.comboBox(categoria);
                Categoria d = categoria.getSelectionModel().getSelectedItem();
                Categoria c = listaGrupos.getSelectionModel().getSelectedItem();
                if(d!=null && c!=null){
                    try{
                        for(GrupoItem p : tabelaLista.getItems()){
                            if(p.getIdItem().equals(d.getIdCategoria())){
                                throw new Erro();
                            }
                        }
                        GrupoItem gi = new GrupoItem(d.getIdCategoria(), c.getIdCategoria(), d.getNome());
                        gi.cadastrar();
                        acaoFiltrar(true);
                    }catch(Erro e){
                        Janela.showTooltip(Status.ERRO, idioma.getMensagem("item_ja_existe"), Duracao.NORMAL);
                    }
                }
            }catch(Erro e){
                //
            }
        }
    }
    
    @Override
    public void acaoAlterar(int tabela) {
        if(tabela==1){
            itensGrupo = listaGrupos.getSelectionModel().getSelectedItems();
            if(Validar.alteracao(itensGrupo, barraController.getBotaoAlterar())){
                GrupoFormularioControlador Controlador = Janela.abrir(Grupo.FXML_FORMULARIO, TITULO);
                Controlador.alterar(itensGrupo.get(0));
            }
        }else if(tabela==11){
            acaoGrupoSelecionado();
        }
    }
    
    @Override
    public void acaoExcluir(int botao) {
        if(botao==1){
            itensGrupo = listaGrupos.getSelectionModel().getSelectedItems();
            if(Validar.exclusao(itensGrupo, barraController.getBotaoExcluir())){
                try {
                    for(Grupo t : itensGrupo){
                        t.excluir();
                    }
                    Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                } catch (Erro ex) {
                    Janela.showTooltip(Status.ERRO, idioma.getMensagem("restricao_excluir"), Duracao.NORMAL);
                }
                acaoFiltrar(true);
            }
        }else if(botao==2){
            itens = tabelaLista.getSelectionModel().getSelectedItems();
            if(Validar.exclusao(itens, excluir)){
                itens.forEach((GrupoItem p) -> p.excluir());
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                acaoFiltrar(true);
            }
        }
    }

    @Override
    public void acaoGerenciar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }
    
    @Override
    public void acaoVoltar() {
        Kernel.principal.acaoVoltar();
    }

    @Override
    public void acaoAjuda() {
        Ajuda.getInstance().setObjetos(barraController,labelCategoria,cadastrar,categoria,excluir,listaGrupos,tabelaLista);
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_grupo_1"));
        Ajuda.getInstance().capitulo(listaGrupos, Posicao.CENTRO, idioma.getMensagem("tuto_grupo_2"));
        Ajuda.getInstance().capitulo(barraController.getBotaoCadastrar(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_cadastrar"));
        Ajuda.getInstance().capitulo(barraController.getBotaoAlterar(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_alterar"));
        Ajuda.getInstance().capitulo(barraController.getBotaoExcluir(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_excluir"));
        Ajuda.getInstance().capitulo(tabelaLista, Posicao.CENTRO, idioma.getMensagem("tuto_grupo_3"));
        Ajuda.getInstance().capitulo(cadastrar, Posicao.BAIXO, idioma.getMensagem("tuto_grupo_4"));
        Ajuda.getInstance().capitulo(excluir, Posicao.BAIXO, idioma.getMensagem("tuto_grupo_5"));
        Ajuda.getInstance().apresentarProximo();
    }

}