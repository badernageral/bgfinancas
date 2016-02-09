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

package badernageral.bgfinancas.modulo.utilitario;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.sistema.Botao;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.modelo.Despesa;
import badernageral.bgfinancas.modelo.DespesaItem;
import badernageral.bgfinancas.modelo.Receita;
import badernageral.bgfinancas.modelo.ReceitaItem;
import badernageral.bgfinancas.modulo.despesa.DespesaFormularioControlador;
import badernageral.bgfinancas.modulo.despesa.item.DespesaItemFormularioControlador;
import badernageral.bgfinancas.modulo.receita.ReceitaFormularioControlador;
import badernageral.bgfinancas.modulo.receita.item.ReceitaItemFormularioControlador;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

public final class GuiaFormularioControlador implements Initializable, ControladorFormulario {
       
    @FXML private TitledPane formulario;
    @FXML private Label labelPergunta;
    @FXML private GridPane conteudo;
    private int etapa = 0;
    
    // Botões
    private final HBox grupoBotao = new HBox();
    private final Button finalizar = new Button();
    private final Button cancelar = new Button();
    
    // Etapa 1
    private final ToggleGroup grupoTipo = new ToggleGroup();
    private final RadioButton despesa = new RadioButton(idioma.getMensagem("despesa"));
    private final RadioButton receita = new RadioButton(idioma.getMensagem("receita"));
    private final HBox botoes = new HBox();
    
    // Etapa 2
    private Boolean tipoDespesa;
    private final ListView<DespesaItem> listaDespesas = new ListView<>();
    private final ListView<ReceitaItem> listaReceitas = new ListView<>();
    private final TextField filtro = new TextField();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("guia"));
        Botao.prepararBotaoModal(this, finalizar, cancelar, null);
        finalizar.setText(idioma.getMensagem("proximo"));
        cancelar.setText(idioma.getMensagem("cancelar"));
        grupoBotao.getChildren().add(finalizar);
        grupoBotao.getChildren().add(cancelar);
        grupoBotao.setSpacing(5);
        conteudo.add(grupoBotao, 1, 2);
        etapa1();
    }
    
    private void etapa1(){
        etapa = 1;
        labelPergunta.setText(idioma.getMensagem("o_que_lancar"));
        despesa.setTooltip(new Tooltip(idioma.getMensagem("retira_dinheiro")));
        receita.setTooltip(new Tooltip(idioma.getMensagem("coloca_dinheiro")));
        despesa.getStyleClass().add("BotaoInicio");
        receita.getStyleClass().add("BotaoFim");
        despesa.setSelected(true);
        despesa.setToggleGroup(grupoTipo);
        receita.setToggleGroup(grupoTipo);
        botoes.getChildren().add(despesa);
        botoes.getChildren().add(receita);
        botoes.setSpacing(10);
        conteudo.add(botoes,1,1);
    }
    
    private void etapa2(){
        etapa = 2;
        conteudo.getChildren().remove(botoes);        
        if(tipoDespesa){
            labelPergunta.setText(idioma.getMensagem("qual_despesa_lancar"));
            filtrarDespesas("");
            conteudo.add(listaDespesas,0,1,2,1);
        }else{
            filtrarReceitas("");
            labelPergunta.setText(idioma.getMensagem("qual_receita_lancar"));
            conteudo.add(listaReceitas,0,1,2,1);
        }
        conteudo.getRowConstraints().get(1).setMinHeight(120);
        conteudo.getRowConstraints().get(1).setPrefHeight(120);
        conteudo.getRowConstraints().get(1).setMaxHeight(120);
        Kernel.palcoModal.setHeight(300);
        RowConstraints linha = new RowConstraints();
        conteudo.getRowConstraints().add(2, linha);
        filtro.getStyleClass().add("Filtro");
        conteudo.add(filtro,0,2,2,1);
        conteudo.getChildren().remove(grupoBotao);
        conteudo.add(grupoBotao, 1, 3);
        filtro.setOnKeyReleased(e -> {
            if(tipoDespesa){
                filtrarDespesas(filtro.getText());
            }else{
                filtrarReceitas(filtro.getText());
            }
        });
        filtro.setOnAction(e -> acaoFinalizar());
        filtro.requestFocus();
    }
    
    private void etapa3(Item item){
        if(tipoDespesa){
            DespesaFormularioControlador controlador = Janela.abrir(Despesa.FXML_FORMULARIO, idioma.getMensagem("guia"));
            controlador.cadastrar(null);
            controlador.lancamentoGuiado(item);
        }else{
            ReceitaFormularioControlador controlador = Janela.abrir(Receita.FXML_FORMULARIO, idioma.getMensagem("guia"));
            controlador.cadastrar(null);
            controlador.lancamentoGuiado(item);
        }
        Animacao.fadeInOutClose(formulario);
    }
        
    private void filtrarDespesas(String filtro){
        listaDespesas.setItems(new DespesaItem().setFiltro(filtro).listar());
        listaDespesas.getItems().add(new DespesaItem(null, null, idioma.getMensagem("nao_esta_lista"), null));
    }
    
    private void filtrarReceitas(String filtro){
        listaReceitas.setItems(new ReceitaItem().setFiltro(filtro).listar());
        listaReceitas.getItems().add(new ReceitaItem(null, null, idioma.getMensagem("nao_esta_lista"), null));
    }
    
    @Override
    public void selecionarComboItem(int combo, Item item) {
        if(item!=null){
            etapa3(item);
        }else{
            Animacao.fadeInInvisivel(filtro, formulario);
        }
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
    }
    
    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            if(etapa==1){
                tipoDespesa = grupoTipo.getSelectedToggle().equals(despesa);
                etapa2();
            }else if(etapa==2){
                if(tipoDespesa && listaDespesas.getSelectionModel().getSelectedItem().getIdItem() == null){
                    Animacao.fadeOutInvisivel(listaDespesas, formulario);
                    DespesaItemFormularioControlador controladorItem = Janela.abrir(DespesaItem.FXML_FORMULARIO, idioma.getMensagem("despesa"));
                    controladorItem.cadastrar(this, null, filtro.getText());
                }else if(!tipoDespesa && listaReceitas.getSelectionModel().getSelectedItem().getIdItem() == null){
                    Animacao.fadeOutInvisivel(listaReceitas, formulario);
                    ReceitaItemFormularioControlador controladorItem = Janela.abrir(ReceitaItem.FXML_FORMULARIO, idioma.getMensagem("receita"));
                    controladorItem.cadastrar(this, filtro.getText());
                }else{
                    if(tipoDespesa){
                        etapa3(listaDespesas.getSelectionModel().getSelectedItem());
                    }else{
                        etapa3(listaReceitas.getSelectionModel().getSelectedItem());
                    }
                }
            }
        }
    }
    
    private boolean validarFormulario(){
        if(etapa==1){
            if(!despesa.isSelected() && !receita.isSelected()){
                Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("campo_nao_informado"), despesa, Duracao.CURTA);
                return false;
            }
        }else if(etapa==2){
            if(tipoDespesa){
                if(listaDespesas.getSelectionModel().getSelectedItem()==null){
                    Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("selecione_item_lista"), listaDespesas, Duracao.CURTA);
                    return false;
                }
            }else{
                if(listaReceitas.getSelectionModel().getSelectedItem()==null){
                    Janela.showTooltip(Status.ADVERTENCIA, idioma.getMensagem("selecione_item_lista"), listaReceitas, Duracao.CURTA);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void acaoCadastrar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

}
