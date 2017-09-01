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

package io.github.badernageral.bgfinancas.template.botao;

import io.github.badernageral.bgfinancas.biblioteca.sistema.Botao;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public final class BotaoGerenciarCategoriaItem implements Initializable {
    
    @FXML private Label labelCategoria;
    @FXML private Label labelItem;
    @FXML private ChoiceBox<Categoria> categoria;
    @FXML private Button gerenciar;
    @FXML private Button gerenciarItem;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        labelCategoria.setText(Linguagem.getInstance().getMensagem("categoria")+":");
        labelItem.setText(Linguagem.getInstance().getMensagem("item")+":");
        Botao.prepararBotaoGerenciar(new Button[]{gerenciar,gerenciarItem});
        categoria.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends Categoria> o, Categoria oV, Categoria nV) -> {
                Kernel.controlador.acaoFiltrar(false);
            }
        );
    }
    
    public Label getLabelCategoria(){
        return labelCategoria;
    }
    
    public ChoiceBox<Categoria> getChoiceCategoria(){
        return categoria;
    }
    
    public Button getBotaoGerenciarCategoria(){
        return gerenciar;
    }
    
    public Label getLabelItem(){
        return labelItem;
    }
    
    public Button getBotaoGerenciarItem(){
        return gerenciarItem;
    }
    
}