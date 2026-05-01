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

package io.github.badernageral.bgfinancas.modulo.receita.categoria;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
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
import io.github.badernageral.bgfinancas.modelo.ReceitaCategoria;
import io.github.badernageral.bgfinancas.modulo.receita.item.ReceitaItemFormularioControlador;
import javafx.scene.control.TitledPane;

public final class ReceitaCategoriaFormularioControlador implements Initializable, ControladorFormulario {
       
    private Acao acao;
    
    @FXML private TitledPane formulario;
    @FXML private BotaoFormulario botaoController;
    @FXML private Label labelNome;
    @FXML private TextField nome;
    
    private ReceitaItemFormularioControlador controlador = null;
    private ReceitaCategoria modelo;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("receita")+" - "+idioma.getMensagem("categoria"));
        Botao.prepararBotaoModal(this, botaoController);
        labelNome.setText(idioma.getMensagem("nome")+":");
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
        if(controlador != null){
            controlador.selecionarComboCategoria(1, null);
        }
    }
    
    public void cadastrar(ReceitaItemFormularioControlador controlador){
        acao = Acao.CADASTRAR;
        this.controlador = controlador;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("cadastrar"));
    }
    
    public void alterar(ReceitaCategoria modelo){
        this.modelo = modelo;
        acao = Acao.ALTERAR;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("alterar"));
        nome.setText(modelo.getNome());
    }
    
    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            if(acao == Acao.CADASTRAR){
                ReceitaCategoria categoria = new ReceitaCategoria(null, nome.getText());
                categoria.cadastrar();
                if(controlador==null){
                    Kernel.principal.acaoReceitaCategoria();
                    Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                    Animacao.fadeInOutClose(formulario);
                }else{
                    Animacao.fadeOutClose(formulario);
                    categoria = new ReceitaCategoria().setNome(categoria.getNome()).consultar();
                    controlador.selecionarComboCategoria(1, categoria);
                }
            }else if(acao == Acao.ALTERAR){
                ReceitaCategoria categoria = new ReceitaCategoria(modelo.getIdCategoria(), nome.getText());
                categoria.alterar();
                Kernel.principal.acaoReceitaCategoria();
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }
        }
    }
    
    private boolean validarFormulario(){
        try {
            Validar.textField(nome);
            return true;
        } catch (Erro ex) {
            return false;
        }
    }

    @Override
    public void acaoCadastrar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void selecionarComboItem(int combo, Item item) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

}
