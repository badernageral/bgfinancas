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

package io.github.badernageral.bgfinancas.modulo.despesa;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Calculadora;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.stage.Stage;
    
public final class ModalDespesaControlador implements Initializable, ControladorFormulario {
    
    @FXML private TitledPane formulario;
    @FXML private Text campo_1;
    @FXML private Text campo_2;
    @FXML private TextField entrada_1;
    @FXML private TextField entrada_2;
    @FXML private Label ajuda1;
    @FXML private Label ajuda2;
    @FXML private Button ok;
    @FXML private Button cancelar;
    private String[] resultado;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Calculadora.preparar(entrada_1, ajuda1);
        Calculadora.preparar(entrada_2, ajuda2);
        ok.setText(Linguagem.getInstance().getMensagem("ok"));
        cancelar.setText(Linguagem.getInstance().getMensagem("cancelar"));
        ok.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("ok")+" (ALT+ENTER)"));
        cancelar.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("cancelar")+" (ESCAPE)"));
    }
    
    public void setTitulo(String titulo){
        formulario.setText(titulo);
    }
    
    public void setMensagem(String campo_1, String campo_2){
        this.campo_1.setText(campo_1);
        this.campo_2.setText(campo_2);
    }
    
    public void setValor(String valor_1, String valor_2){
        this.entrada_1.setText(valor_1);
        this.entrada_2.setText(valor_2);
    }
    
    @FXML
    @Override
    public void acaoFinalizar() {
        try{
            Validar.textFieldDecimal(entrada_1);
            Validar.textFieldDecimal(entrada_2);
            Stage palco = (Stage) formulario.getScene().getWindow();
            String valor_1 = entrada_1.getText();
            String valor_2 = entrada_2.getText();
            String[] fim = palco.getTitle().split("-");
            if(!valor_1.equals("")){
                fim[0] = valor_1;
            }
            if(!valor_2.equals("")){
                fim[1] = valor_2;
            }
            resultado = fim;
            Animacao.fadeInOutClose(formulario);
        }catch(Erro e){ }
    }
    
    public String[] getResultado(){
        return resultado;
    }
    
    @FXML
    @Override
    public void acaoCancelar() {
        Stage palco = (Stage) formulario.getScene().getWindow();
        palco.setTitle("");
        Animacao.fadeInOutClose(formulario);
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
