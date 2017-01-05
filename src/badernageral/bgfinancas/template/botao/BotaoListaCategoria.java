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

package badernageral.bgfinancas.template.botao;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public final class BotaoListaCategoria implements Initializable {
    
    @FXML private ComboBox<Categoria> categoria;
    @FXML private Button cadastrar;
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Olá, eu sou nothing
    }
    
    public void setCategoriaSelecionada(Categoria categoria){
        this.categoria.getSelectionModel().select(categoria);
    }
    
    public String getIdCategoria(){
        return categoria.getSelectionModel().getSelectedItem().getIdCategoria();
    }
    
    public String getNomeCategoria(){
        return categoria.getSelectionModel().getSelectedItem().getNome();
    }
    
    public ComboBox<Categoria> getComboCategoria(){
        return categoria;
    }
    
    public Button getBotaoCadastrar(){
        return cadastrar;
    }
    
}