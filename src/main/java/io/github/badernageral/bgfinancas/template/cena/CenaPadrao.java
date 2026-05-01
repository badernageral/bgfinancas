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

package io.github.badernageral.bgfinancas.template.cena;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import io.github.badernageral.bgfinancas.template.barra.BarraPadrao;
import javafx.scene.control.Label;

public final class CenaPadrao implements Initializable {
    
    @FXML private BarraPadrao barraController;
    @FXML private StackPane tabela;
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Olá, eu sou nothing
    }
    
    public void setTabela(Node tabelaLista){
        tabela.getChildren().add(tabelaLista);
    }
    
    public void setTitulo(String titulo){
        barraController.getLabelTitulo().setText(titulo);
    }
    
    public Label getLabelTitulo(){
        return barraController.getLabelTitulo();
    }
    
    public Button getBotaoVoltar(){
        return barraController.getBotaoVoltar();
    }
    
    public Button getBotaoCadastrar(){
        return barraController.getBotaoCadastrar();
    }
    
    public Button getBotaoAlterar(){
        return barraController.getBotaoAlterar();
    }
    
    public Button getBotaoExcluir(){
        return barraController.getBotaoExcluir();
    }
    
    public TextField getFiltro(){
        return barraController.getFiltro();
    }
    
}