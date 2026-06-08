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

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import io.github.badernageral.bgfinancas.modelo.DespesaItem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

public final class ModalSubstituirItemDespesaControlador implements Initializable, ControladorFormulario {

    @FXML private TitledPane formulario;
    @FXML private Label labelAviso;
    @FXML private ComboBox<Item> comboSubstituto;
    @FXML private Button ok;
    @FXML private Button cancelar;

    private String substitutoId = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("substituir_item"));
        labelAviso.setText(idioma.getMensagem("aviso_substituir_item"));
        ok.setText(Linguagem.getInstance().getMensagem("ok"));
        cancelar.setText(Linguagem.getInstance().getMensagem("cancelar"));
    }

    public void prepararSubstituicao(DespesaItem itemParaExcluir) {
        new DespesaItem().montarSelectItem(comboSubstituto);
        comboSubstituto.getItems().removeIf(i -> i.getIdItem().equals(itemParaExcluir.getIdItem()));
    }

    public String getSubstitutoId() {
        return substitutoId;
    }

    @FXML
    @Override
    public void acaoFinalizar() {
        try {
            Validar.comboBox(comboSubstituto);
            substitutoId = comboSubstituto.getSelectionModel().getSelectedItem().getIdItem();
            Animacao.fadeInOutClose(formulario);
        } catch (Erro ex) {
            // tooltip já exibido por Validar
        }
    }

    @FXML
    @Override
    public void acaoCancelar() {
        Stage palco = (Stage) formulario.getScene().getWindow();
        palco.setTitle("");
        Animacao.fadeInOutClose(formulario);
    }

    @Override
    public void acaoCadastrar(int botao) { }

    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) { }

    @Override
    public void selecionarComboItem(int combo, Item item) { }

}
