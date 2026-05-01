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

package io.github.badernageral.bgfinancas.modulo.transferencia.item;

import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Acao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Duracao;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import io.github.badernageral.bgfinancas.template.botao.BotaoFormulario;
import io.github.badernageral.bgfinancas.template.botao.BotaoListaCategoria;
import io.github.badernageral.bgfinancas.modelo.TransferenciaCategoria;
import io.github.badernageral.bgfinancas.modelo.TransferenciaItem;
import io.github.badernageral.bgfinancas.modulo.transferencia.TransferenciaFormularioControlador;
import io.github.badernageral.bgfinancas.modulo.transferencia.categoria.TransferenciaCategoriaFormularioControlador;
import javafx.scene.control.TitledPane;

public final class TransferenciaItemFormularioControlador implements Initializable, ControladorFormulario {
    
    private Acao acao;
       
    @FXML private TitledPane formulario;
    @FXML private BotaoListaCategoria categoriaController;
    @FXML private BotaoFormulario botaoController;
    @FXML private Label labelCategoria;
    @FXML private Label labelNome;
    @FXML private TextField nome;
    
    private TransferenciaItem modelo;
    private TransferenciaFormularioControlador controlador = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("transferencia")+" - "+idioma.getMensagem("item"));
        Botao.prepararBotaoModal(this, botaoController, categoriaController);
        labelCategoria.setText(idioma.getMensagem("categoria")+":");
        labelNome.setText(idioma.getMensagem("nome")+":");
        new TransferenciaCategoria().montarSelectCategoria(categoriaController.getComboCategoria());
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
        if(controlador != null){
            controlador.selecionarComboItem(0, null);
        }
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        Animacao.fadeOutInvisivel(nome, formulario);
        TransferenciaCategoriaFormularioControlador Controlador = Janela.abrir(TransferenciaCategoria.FXML_FORMULARIO, idioma.getMensagem("transferencias"));
        Controlador.cadastrar(this);
    }
    
    @Override
    public void selecionarComboCategoria(int combo, Categoria transferenciaCategoria) {
        new TransferenciaCategoria().montarSelectCategoria(categoriaController.getComboCategoria());
        categoriaController.setCategoriaSelecionada(transferenciaCategoria);
        Animacao.fadeInInvisivel(nome, formulario);
    }
    
    public void cadastrar(Categoria transferenciaCategoria){
        acao = Acao.CADASTRAR;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("cadastrar"));
        if(!transferenciaCategoria.getIdCategoria().equals("todas")){
            categoriaController.setCategoriaSelecionada(transferenciaCategoria);
        }
    }
    
    public void cadastrar(TransferenciaFormularioControlador controlador, String nome){
        acao = Acao.CADASTRAR;
        this.nome.setText(nome);
        this.controlador = controlador;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("cadastrar"));
    }
    
    public void alterar(TransferenciaItem modelo){
        this.modelo = modelo;
        acao = Acao.ALTERAR;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("alterar"));
        TransferenciaCategoria transferenciaCategoria = new TransferenciaCategoria().setIdCategoria(modelo.getIdCategoria()).consultar();
        if(transferenciaCategoria != null){
            categoriaController.setCategoriaSelecionada(transferenciaCategoria);
        }
        nome.setText(modelo.getNome());
    }
    
    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            if(acao == Acao.CADASTRAR){
                TransferenciaItem item = new TransferenciaItem(null, categoriaController.getIdCategoria(), nome.getText(), categoriaController.getNomeCategoria());
                if(item.consultar()!=null){
                    Janela.showTooltip(Status.ERRO, idioma.getMensagem("nome_igual"), Duracao.CURTA);
                }else{
                    item.cadastrar();
                    if(controlador==null){
                        Kernel.controlador.acaoFiltrar(true);
                        Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                        Animacao.fadeInOutClose(formulario);
                    }else{
                        Animacao.fadeOutClose(formulario);
                        item = new TransferenciaItem().setNome(item.getNome()).setNomeCategoria(item.getNomeCategoria()).consultar();
                        controlador.selecionarComboItem(0, item);
                    }
                }
            }else if(acao == Acao.ALTERAR){
                TransferenciaItem item = new TransferenciaItem(modelo.getIdItem(), categoriaController.getIdCategoria(), nome.getText(), null);
                item.alterar();
                Kernel.principal.acaoTransferenciaItem();
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
