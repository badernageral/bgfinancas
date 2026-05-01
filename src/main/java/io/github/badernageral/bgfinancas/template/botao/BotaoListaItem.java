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

package io.github.badernageral.bgfinancas.template.botao;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.AutoFiltro;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public final class BotaoListaItem implements Initializable {
    
    @FXML private ComboBox<Item> item;
    @FXML private Button cadastrar;
    private AutoFiltro<Item> autoFiltro;
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        autoFiltro = new AutoFiltro<>(item);
    }
        
    public void setItemSelecionado(Item item){
        autoFiltro.atualizarItens();
        this.item.getSelectionModel().select(item);
        autoFiltro.atualizar(false);
    }
    
    public String getIdItem(){
        if(autoFiltro.getItem()!=null){
            return autoFiltro.getItem().getIdItem();
        }else{
            return null;
        }
    }
    
    public ComboBox<Item> getComboItem(){
        return item;
    }
    
    public AutoFiltro<Item> getAutoFiltro(){
        return autoFiltro;
    }
    
    public Button getBotaoCadastrar(){
        return cadastrar;
    }
    
}