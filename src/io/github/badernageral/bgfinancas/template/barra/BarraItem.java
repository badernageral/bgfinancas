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

package io.github.badernageral.bgfinancas.template.barra;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import io.github.badernageral.bgfinancas.template.botao.BotaoGerenciarCategoria;
import io.github.badernageral.bgfinancas.template.botao.BotaoPadrao;

public final class BarraItem implements Initializable {
    
    @FXML private Filtro filtroController;
    @FXML private BotaoPadrao botaoController;
    @FXML private BotaoGerenciarCategoria botaoCategoriaController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Olá, eu sou nothing
    }
    
    public TextField getFiltro(){
        return filtroController.getFiltro();
    }
    
    public Button getBotaoCadastrar(){
        return botaoController.getBotaoCadastrar();
    }
    
    public Button getBotaoVoltar(){
        return botaoController.getBotaoVoltar();
    }
    
    public Button getBotaoAlterar(){
        return botaoController.getBotaoAlterar();
    }
    
    public Button getBotaoExcluir(){
        return botaoController.getBotaoExcluir();
    }
    
    public Label getLabelTitulo(){
        return botaoController.getLabelTitulo();
    }
    
    public Label getLabelCategoria(){
        return botaoCategoriaController.getLabelCategoria();
    }
    
    public ChoiceBox<Categoria> getChoiceCategoria(){
        return botaoCategoriaController.getChoiceCategoria();
    }
    
    public Button getBotaoGerenciarCategoria(){
        return botaoCategoriaController.getBotaoGerenciarCategoria();
    }
    
}