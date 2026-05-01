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

package io.github.badernageral.bgfinancas.modulo.despesa.item;

import io.github.badernageral.bgfinancas.modulo.despesa.categoria.DespesaCategoriaFormularioControlador;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Acao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import io.github.badernageral.bgfinancas.template.botao.BotaoFormulario;
import io.github.badernageral.bgfinancas.template.botao.BotaoListaCategoria;
import io.github.badernageral.bgfinancas.modelo.DespesaCategoria;
import io.github.badernageral.bgfinancas.modelo.DespesaItem;
import io.github.badernageral.bgfinancas.modulo.despesa.DespesaCadastroMultiploControlador;
import javafx.scene.control.TitledPane;

public final class DespesaItemFormularioControlador implements Initializable, ControladorFormulario {
       
    private Acao acao;
    
    @FXML private TitledPane formulario;
    @FXML private BotaoListaCategoria categoriaController;
    @FXML private BotaoFormulario botaoController;
    @FXML private Label labelCategoria;
    @FXML private Label labelNome;
    @FXML private TextField nome;
    
    private DespesaItem modelo;
    private ControladorFormulario controladorF = null;
    private DespesaCadastroMultiploControlador controlador = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("despesa")+" - "+idioma.getMensagem("item"));
        Botao.prepararBotaoModal(this, botaoController, categoriaController);
        labelCategoria.setText(idioma.getMensagem("categoria")+":");
        labelNome.setText(idioma.getMensagem("nome")+":");
        new DespesaCategoria().montarSelectCategoria(categoriaController.getComboCategoria());
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
        if(controladorF != null){
            controladorF.selecionarComboItem(0, null);
        }
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        Animacao.fadeOutInvisivel(nome, formulario);
        DespesaCategoriaFormularioControlador Controlador = Janela.abrir(DespesaCategoria.FXML_FORMULARIO, idioma.getMensagem("despesas"));
        Controlador.cadastrar(this);
    }
    
    @Override
    public void selecionarComboCategoria(int combo, Categoria depesaCategoria) {
        new DespesaCategoria().montarSelectCategoria(categoriaController.getComboCategoria());
        categoriaController.setCategoriaSelecionada(depesaCategoria);
        Animacao.fadeInInvisivel(nome, formulario);
    }
    
    public void cadastrar(Categoria despesaCategoria){
        acao = Acao.CADASTRAR;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("cadastrar"));
        if(despesaCategoria!=null && !despesaCategoria.getIdCategoria().equals("todas")){
            categoriaController.setCategoriaSelecionada(despesaCategoria);
        }
    }
    
    public void cadastrar(ControladorFormulario controladorF, DespesaCadastroMultiploControlador controlador, String nome){
        acao = Acao.CADASTRAR;
        this.nome.setText(nome);
        this.controlador = controlador;
        this.controladorF = controladorF;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("cadastrar"));
    }
    
    public void alterar(DespesaItem modelo){
        this.modelo = modelo;
        acao = Acao.ALTERAR;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("alterar"));
        DespesaCategoria despesaCategoria = new DespesaCategoria().setIdCategoria(modelo.getIdCategoria()).consultar();
        if(despesaCategoria != null){
            categoriaController.setCategoriaSelecionada(despesaCategoria);
        }
        nome.setText(modelo.getNome());
    }
    
    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            if(acao == Acao.CADASTRAR){
                DespesaItem item = new DespesaItem(null, categoriaController.getIdCategoria(), nome.getText(), null);
                if(item.consultar()!=null){
                    Janela.showTooltip(Status.ERRO, idioma.getMensagem("nome_igual"), Duracao.CURTA);
                }else{
                    item.cadastrar();
                    if(controlador==null && controladorF==null){
                        Kernel.controlador.acaoFiltrar(true);
                        Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                        Animacao.fadeInOutClose(formulario);
                    }else{
                        item = new DespesaItem().setNome(item.getNome()).setNomeCategoria(item.getNomeCategoria()).consultar();
                        if(controlador!=null){
                            controlador.acaoFiltrar(false);
                            Animacao.fadeInOutClose(formulario);
                            controlador.adicionar(item.toString());
                        }else{
                            Animacao.fadeOutClose(formulario);
                            controladorF.selecionarComboItem(0, item);
                        }
                    } 
                }
            }else if(acao == Acao.ALTERAR){
                DespesaItem item = new DespesaItem(modelo.getIdItem(), categoriaController.getIdCategoria(), nome.getText(), null);
                item.alterar();
                Kernel.controlador.acaoFiltrar(true);
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }
        }
    }
    
    private boolean validarFormulario(){
        try {
            Validar.comboBox(categoriaController.getComboCategoria());
            Validar.textField(nome);
            return true;
        } catch (Erro ex) {
            return false;
        }
    }

    @Override
    public void selecionarComboItem(int combo, Item item) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

}