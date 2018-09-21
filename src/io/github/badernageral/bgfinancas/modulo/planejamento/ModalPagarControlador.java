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

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import io.github.badernageral.bgfinancas.modelo.Conta;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.stage.Stage;
    
public final class ModalPagarControlador implements Initializable, ControladorFormulario {
    
    @FXML private TitledPane formulario;
    @FXML private Text campo_1;
    @FXML private Text campo_2;
    @FXML private Label valor_total;
    @FXML private ComboBox<Categoria> entrada_1;
    @FXML private DatePicker entrada_2;
    @FXML private Button ok;
    @FXML private Button cancelar;
    private String[] resultado = new String[2];
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ok.setText(Linguagem.getInstance().getMensagem("confirmar"));
        cancelar.setText(Linguagem.getInstance().getMensagem("cancelar"));
        ok.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("ok")+" (ALT+ENTER)"));
        cancelar.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("cancelar")+" (ESCAPE)"));
        campo_1.setText(Linguagem.getInstance().getMensagem("conta"));
        campo_2.setText(Linguagem.getInstance().getMensagem("data"));
        new Conta().montarSelectCategoria(entrada_1);
    }
    
    public void setTitulo(String titulo){
        formulario.setText(titulo);
    }
    
    public void setValor(Categoria valor_1, LocalDate valor_2, String valor_total){
        this.entrada_1.getSelectionModel().select(valor_1);
        this.entrada_2.setValue(valor_2);
        this.valor_total.setText(idioma.getMensagem("valor_total")+": "+idioma.getMensagem("moeda")+" "+valor_total);
    }
    
    @FXML
    @Override
    public void acaoFinalizar() {
        try{
            Validar.comboBox(entrada_1);
            Validar.datePicker(entrada_2);
            resultado[0] = entrada_1.getSelectionModel().getSelectedItem().getIdCategoria();
            resultado[1] = entrada_2.getValue().toString();
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
